package org.bcc.bloom.exception;

public class CpfInvalidoException extends RegraNegocioException {
    public CpfInvalidoException(String mensagem) {
        super(mensagem);
    }
}