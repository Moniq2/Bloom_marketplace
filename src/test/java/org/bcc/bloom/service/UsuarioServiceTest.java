
package org.bcc.bloom.service;

import org.bcc.bloom.exception.CnpjInvalidoException;
import org.bcc.bloom.exception.RegraNegocioException;
import org.bcc.bloom.model.Comprador;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.security.SecureRandom;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class UsuarioServiceTest {

    private UsuarioService usuarioService;

    private static final SecureRandom RANDOM = new SecureRandom();

    @BeforeEach
    public void setUp() {
        usuarioService = new UsuarioService();
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "DB_BLOOM_URL", matches = ".+")
    public void deveCadastrarCompradorComSucesso() {

        String emailUnico = UUID.randomUUID() + "@email.com";
        String cpfUnico = gerarCpfValido();

        Comprador comprador = usuarioService.cadastrarComprador(
                "Momoniq",
                emailUnico,
                "Senha@123",
                cpfUnico
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
                        "Momoniq",
                        "teste@email.com",
                        "Senha@123",
                        "12345678900"
                )
        );
    }

    @Test
    public void naoDeveCadastrarComSenhaFraca() {
        assertThrows(RegraNegocioException.class, () ->
                usuarioService.cadastrarComprador(
                        "Momoniq",
                        "teste@email.com",
                        "123456",
                        "52998224725"
                )
        );
    }

    @Test
    public void naoDeveCadastrarVendedorComCnpjInvalido() {
        assertThrows(CnpjInvalidoException.class, () ->
                usuarioService.cadastrarVendedor(
                        "Zaio Hortifruti",
                        "sainho@email.com",
                        "Senha@123",
                        "12345678000199"
                )
        );
    }

    @Test
    public void deveAceitarCnpjValidoComPontuacao() {
        assertDoesNotThrow(() ->
                usuarioService.validarDadosVendedor(
                        "Zaio Hortifruti",
                        "sainho@email.com",
                        "11.222.333/0001-81",
                        "Senha@123"
                )
        );
    }

    private String gerarCpfValido() {

        int[] numeros = new int[11];

        boolean todosIguais;

        do {
            for (int i = 0; i < 9; i++) {
                numeros[i] = RANDOM.nextInt(10);
            }

            todosIguais = true;

            for (int i = 1; i < 9; i++) {
                if (numeros[i] != numeros[0]) {
                    todosIguais = false;
                    break;
                }
            }

        } while (todosIguais);

        int soma = 0;

        for (int i = 0; i < 9; i++) {
            soma += numeros[i] * (10 - i);
        }

        int resto = soma % 11;
        numeros[9] = resto < 2 ? 0 : 11 - resto;

        soma = 0;

        for (int i = 0; i < 10; i++) {
            soma += numeros[i] * (11 - i);
        }

        resto = soma % 11;
        numeros[10] = resto < 2 ? 0 : 11 - resto;

        StringBuilder cpf = new StringBuilder();

        for (int numero : numeros) {
            cpf.append(numero);
        }

        return cpf.toString();
    }
}
