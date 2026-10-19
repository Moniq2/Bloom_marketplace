package org.bcc.bloom.service;

import org.bcc.bloom.exception.RegraNegocioException;
import org.bcc.bloom.model.Comprador;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UsuarioServiceTest {

    private UsuarioService usuarioService;

    @BeforeEach
    public void setUp() {
        usuarioService = new UsuarioService();
    }

    @Test
    public void deveCadastrarCompradorComSucesso() {
        String emailUnico = "muniq_" + System.currentTimeMillis() + "@email.com";

        Comprador comprador = usuarioService.cadastrarComprador(
            "Momoniq", emailUnico, "Senha@123", "52998224725"
        );

        assertNotNull(comprador);
        assertEquals("Momoniq", comprador.getNome());
        assertEquals(0L, comprador.getSaldo());
        assertNotEquals("Senha@123", comprador.getSenha());
        assertEquals(60, comprador.getSenha().length());
    }

    @Test
    public void naoDeveCadastrarComCpfInvalido() {
        assertThrows(RegraNegocioException.class, () ->
            usuarioService.cadastrarComprador(
                "Momoniq", "teste@email.com", "Senha@123", "12345678900"
            )
        );
    }

    @Test
    public void naoDeveCadastrarComSenhaFraca() {
        assertThrows(RegraNegocioException.class, () ->
            usuarioService.cadastrarComprador(
                "Momoniq", "teste@email.com", "123456", "52998224725"
            )
        );
    }
}