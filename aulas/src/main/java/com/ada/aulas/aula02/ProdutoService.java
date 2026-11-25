package com.ada.aulas.aula02;

import org.springframework.stereotype.Service;

/**
 * Serviço de domínio para gerenciamento de produtos e estoque (utilizado no Live Coding do professor).
 *
 * <p>Regras de negócio:
 * <ul>
 *   <li>Cadastro: nome e SKU são obrigatórios; preço deve ser maior que zero; estoque inicial não pode ser negativo.</li>
 *   <li>Cadastro: não é permitido cadastrar dois produtos com o mesmo SKU ({@link RegraNegocioException}).</li>
 *   <li>Busca por ID: caso o produto não exista no banco, lança {@link RecursoNaoEncontradoException}.</li>
 *   <li>Baixa de estoque: quantidade solicitada deve ser maior que zero; produto deve estar ativo e possuir saldo suficiente.</li>
 * </ul>
 */
@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;

    public ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    public Produto cadastrar(Produto produto) {
        if (produto == null) {
            throw new IllegalArgumentException("O produto não pode ser nulo.");
        }
        if (produto.getNome() == null || produto.getNome().isBlank()) {
            throw new IllegalArgumentException("O nome do produto é obrigatório.");
        }
        if (produto.getSku() == null || produto.getSku().isBlank()) {
            throw new IllegalArgumentException("O SKU do produto é obrigatório.");
        }
        if (produto.getPreco() <= 0) {
            throw new IllegalArgumentException("O preço do produto deve ser maior que zero.");
        }
        if (produto.getQuantidadeEstoque() < 0) {
            throw new IllegalArgumentException("A quantidade em estoque não pode ser negativa.");
        }

        if (produtoRepository.existsBySku(produto.getSku())) {
            throw new RegraNegocioException("Já existe um produto cadastrado com o SKU: " + produto.getSku());
        }

        // Dica para o live coding: comente a linha abaixo ou retorne 'produto' sem chamar 'save'
        // para demonstrar como o assertEquals passaria, mas o verify(produtoRepository).save(produto) pega o bug!
        return produtoRepository.save(produto);
    }

    public Produto buscarPorId(Long id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto não encontrado com ID: " + id));
    }

    public Produto baixarEstoque(Long produtoId, int quantidade) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("A quantidade para baixa deve ser maior que zero.");
        }

        Produto produto = buscarPorId(produtoId);

        if (!produto.isAtivo()) {
            throw new RegraNegocioException("Não é possível baixar estoque de um produto inativo.");
        }

        if (produto.getQuantidadeEstoque() < quantidade) {
            throw new RegraNegocioException("Estoque insuficiente para o produto: " + produto.getNome());
        }

        produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() - quantidade);
        return produtoRepository.save(produto);
    }
}
