package org.bcc.bloom.repository;

import org.bcc.bloom.exception.ErroBancoDadosException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = System.getenv("DB_BLOOM_URL");
    private static final String USER = System.getenv("DB_USER");
    private static final String PASSWORD = System.getenv("DB_PASSWORD");

    public static Connection getConnection() {
        if (URL == null || USER == null || PASSWORD == null) {
            throw new ErroBancoDadosException(
                    "Variáveis de ambiente do banco não configuradas. Defina DB_BLOOM_URL, DB_USER e DB_PASSWORD "
                    + "(ex.: DB_BLOOM_URL=jdbc:mysql://localhost:3306/bloom_db?useUnicode=true&characterEncoding=UTF-8).");
        }
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new ErroBancoDadosException("Driver do MySQL não encontrado: " + e.getMessage());
        } catch (SQLException e) {
            throw new ErroBancoDadosException("Erro ao conectar com a base de dados MySQL: " + e.getMessage());
        }
    }
}