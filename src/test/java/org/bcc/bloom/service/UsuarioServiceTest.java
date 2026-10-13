package org.bcc.bloom.service;

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
            "Momoniq", emailUnico, "123456", 1L, "123.456.789-00", "Rua B, 45"
        );
        
        assertNotNull(comprador);
        assertEquals("Momoniq", comprador.getNome());
        assertEquals(0L, comprador.getSaldo());
    }
}