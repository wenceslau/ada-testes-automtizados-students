package com.ada.aulas.aula02;

/**
 * Contrato para envio de notificações externas (e-mail, SMS, mensageria).
 */
public interface NotificacaoService {

    void enviarConfirmacaoPedido(String emailCliente, Pedido pedido);

    void enviarCancelamentoPedido(String emailCliente, Pedido pedido);
}
