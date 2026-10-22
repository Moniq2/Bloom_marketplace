package org.bcc.bloom.exception;

public class EstoqueInsuficienteException extends RegraNegocioException {
    public EstoqueInsuficienteException(String mensagem) {
        super(mensagem);
    }
}