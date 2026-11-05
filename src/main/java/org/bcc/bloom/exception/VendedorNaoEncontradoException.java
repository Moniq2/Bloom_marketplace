package org.bcc.bloom.exception;

public class VendedorNaoEncontradoException extends RegraNegocioException {
    public VendedorNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
