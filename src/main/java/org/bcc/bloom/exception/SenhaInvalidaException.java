package org.bcc.bloom.exception;

public class SenhaInvalidaException extends RegraNegocioException {
    public SenhaInvalidaException(String mensagem) {
        super(mensagem);
    }
}