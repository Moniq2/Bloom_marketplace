package org.bcc.bloom.exception;

public class SaldoInsuficienteException extends RegraNegocioException {
    public SaldoInsuficienteException(String mensagem) {
        super(mensagem);
    }
}