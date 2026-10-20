package org.bcc.bloom.exception;

public class ErroBancoDadosException extends RuntimeException {
    public ErroBancoDadosException(String mensagem) {
        super(mensagem);
    }
}