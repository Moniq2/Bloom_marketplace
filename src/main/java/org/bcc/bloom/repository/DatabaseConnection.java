package org.bcc.bloom.repository;

import org.bcc.bloom.exception.RegraNegocioException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    
    private static final String URL = System.getenv("DB_URL") != null 
        ? System.getenv("DB_URL") 
        : "jdbc:mysql://localhost:3306/bloom_db?useSSL=false&serverTimezone=UTC";
        
    private static final String USER = System.getenv("DB_USER") != null 
        ? System.getenv("DB_USER") 
        : "root";
        
    private static final String PASSWORD = System.getenv("DB_PASSWORD") != null 
        ? System.getenv("DB_PASSWORD") 
        : "";

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