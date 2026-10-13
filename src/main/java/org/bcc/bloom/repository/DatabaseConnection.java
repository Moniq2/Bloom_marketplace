package org.bcc.bloom.repository;

import org.bcc.bloom.exception.RegraNegocioException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:sqlite:bloom.db";

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL);
        } catch (SQLException e) {
            throw new RegraNegocioException("Erro ao conectar com a base de dados: " + e.getMessage());
        }
    }
}