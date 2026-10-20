package org.bcc.bloom.exception;

public class CampoObrigatorioException extends RegraNegocioException {
    public CampoObrigatorioException(String mensagem) {
        super(mensagem);
    }
}