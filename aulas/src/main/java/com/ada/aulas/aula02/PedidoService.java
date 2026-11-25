package com.ada.aulas.aula02;

import org.springframework.stereotype.Service;

/**
 * Serviço responsável pelo processamento de pedidos (utilizado nos exercícios práticos dos alunos).
 *
 * <p>Regras de negócio:
 * <ul>
 *   <li>Criação de pedido:
 *     <ul>
 *       <li>E-mail do cliente é obrigatório e quantidade deve ser maior que zero ({@link IllegalArgumentException}).</li>
 *       <li>O produto deve existir ({@link RecursoNaoEncontradoException}).</li>
 *       <li>O produto deve estar ativo ({@link RegraNegocioException}).</li>
 *       <li>Deve haver estoque suficiente ({@link RegraNegocioException}).</li>
 *       <li>Deve abater a quantidade do estoque do produto, salvar o produto atualizado, salvar o pedido com status
 *           {@link StatusPedido#CONFIRMADO} e enviar notificação de confirmação ao cliente.</li>
 *     </ul>
 *   </li>
 *   <li>Cancelamento de pedido:
 *     <ul>
 *       <li>O pedido deve existir ({@link RecursoNaoEncontradoException}).</li>
 *       <li>Não é permitido cancelar um pedido que já foi {@link StatusPedido#ENVIADO} ou que já está
 *           {@link StatusPedido#CANCELADO} ({@link RegraNegocioException}).</li>
 *       <li>Ao cancelar, deve devolver a quantidade de itens ao estoque do produto, alterar o status para
 *           {@link StatusPedido#CANCELADO}, salvar o pedido e enviar notificação de cancelamento.</li>
 *     </ul>
 *   </li>
 * </ul>
 */
@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ProdutoRepository produtoRepository;
    private final NotificacaoService notificacaoService;

    public PedidoService(
            PedidoRepository pedidoRepository,
            ProdutoRepository produtoRepository,
            NotificacaoService notificacaoService
    ) {
        this.pedidoRepository = pedidoRepository;
        this.produtoRepository = produtoRepository;
        this.notificacaoService = notificacaoService;
    }

    public Pedido criarPedido(String emailCliente, Long produtoId, int quantidade) {
        if (emailCliente == null || emailCliente.isBlank()) {
            throw new IllegalArgumentException("O e-mail do cliente é obrigatório.");
        }
        if (quantidade <= 0) {
            throw new IllegalArgumentException("A quantidade do pedido deve ser maior que zero.");
        }

        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto não encontrado com ID: " + produtoId));

        if (!produto.isAtivo()) {
            throw new RegraNegocioException("Não é possível criar pedido para um produto inativo.");
        }

        if (produto.getQuantidadeEstoque() < quantidade) {
            throw new RegraNegocioException("Estoque insuficiente para o produto: " + produto.getNome());
        }

        // Atualiza e persiste o novo saldo de estoque
        produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() - quantidade);
        produtoRepository.save(produto);

        // Calcula o valor total e cria o pedido confirmado
        double valorTotal = produto.getPreco() * quantidade;
        Pedido novoPedido = new Pedido(null, emailCliente, produto, quantidade, valorTotal, StatusPedido.CONFIRMADO);
        Pedido pedidoSalvo = pedidoRepository.save(novoPedido);

        // Notifica o cliente por e-mail
        notificacaoService.enviarConfirmacaoPedido(emailCliente, pedidoSalvo);

        return pedidoSalvo;
    }

    public Pedido cancelarPedido(Long pedidoId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pedido não encontrado com ID: " + pedidoId));

        if (pedido.getStatus() == StatusPedido.ENVIADO) {
            throw new RegraNegocioException("Não é possível cancelar um pedido que já foi enviado.");
        }

        if (pedido.getStatus() == StatusPedido.CANCELADO) {
            throw new RegraNegocioException("Este pedido já está cancelado.");
        }

        // Devolve os itens ao estoque do produto
        Produto produto = pedido.getProduto();
        produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() + pedido.getQuantidade());
        produtoRepository.save(produto);

        // Atualiza status do pedido e notifica o cancelamento
        pedido.setStatus(StatusPedido.CANCELADO);
        Pedido pedidoCancelado = pedidoRepository.save(pedido);

        notificacaoService.enviarCancelamentoPedido(pedidoCancelado.getEmailCliente(), pedidoCancelado);

        return pedidoCancelado;
    }
}
