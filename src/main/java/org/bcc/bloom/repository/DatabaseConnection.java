package org.bcc.bloom.repository;

import org.bcc.bloom.exception.RegraNegocioException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "${DB_BLOOM_URL}";
    private static final String USER = "${DB_USER}";
    private static final String PASSWORD = "${DB_PASSWORD}";

    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new RegraNegocioException("Driver do MySQL não encontrado: " + e.getMessage());
        } catch (SQLException e) {
            throw new RegraNegocioException("Erro ao conectar com a base de dados MySQL: " + e.getMessage());
        }
    }
}