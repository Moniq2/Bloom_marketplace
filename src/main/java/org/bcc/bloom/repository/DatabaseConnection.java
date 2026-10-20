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