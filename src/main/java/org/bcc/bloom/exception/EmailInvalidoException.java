package org.bcc.bloom.exception;

public class EmailInvalidoException extends RegraNegocioException {
    public EmailInvalidoException(String mensagem) {
        super(mensagem);
    }
}