package org.bcc.bloom.exception;

public class CredenciaisInvalidasException extends RegraNegocioException {
    public CredenciaisInvalidasException(String mensagem) {
        super(mensagem);
    }
}