package org.bcc.bloom.service;

import org.bcc.bloom.exception.CnpjInvalidoException;
import org.bcc.bloom.exception.RegraNegocioException;
import org.bcc.bloom.model.Comprador;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import static org.junit.jupiter.api.Assertions.*;

public class UsuarioServiceTest {

    private UsuarioService usuarioService;

    @BeforeEach
    public void setUp() {
        usuarioService = new UsuarioService();
    }

    // Teste de integracao: grava no MySQL, so roda quando as variaveis de ambiente do banco existem
    @Test
    @EnabledIfEnvironmentVariable(named = "DB_BLOOM_URL", matches = ".+")
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

    @Test
    public void naoDeveCadastrarVendedorComCnpjInvalido() {
        assertThrows(CnpjInvalidoException.class, () ->
            usuarioService.cadastrarVendedor(
                "Zaio Hortifruti", "sainho@email.com", "Senha@123", "12345678000199"
            )
        );
    }

    @Test
    public void deveAceitarCnpjValidoComPontuacao() {
        // 11.222.333/0001-81 e um CNPJ com digitos verificadores corretos
        assertDoesNotThrow(() ->
            usuarioService.validarDadosVendedor(
                "Zaio Hortifruti", "sainho@email.com", "11222333000181", "Senha@123"
            )
        );
    }
}