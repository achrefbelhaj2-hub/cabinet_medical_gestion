
package com.cabinet.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.PreparedStatement;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/cabinet_medical";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Le driver MySQL JDBC est introuvable.");
            throw new SQLException("Driver MySQL introuvable.", e);
        }
        
        Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
        System.out.println("Connexion à la base de données réussie.");
        return connection;
    }

    // ❌ À CORRIGER : Ajouter des méthodes pour fermer toutes les ressources
    public static void closeConnection(Connection connection) {
        close(connection, null, null);
    }
    
    public static void close(Connection connection, PreparedStatement ps, ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                System.err.println("Erreur fermeture ResultSet: " + e.getMessage());
            }
        }
        
        if (ps != null) {
            try {
                ps.close();
            } catch (SQLException e) {
                System.err.println("Erreur fermeture PreparedStatement: " + e.getMessage());
            }
        }
        
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                    System.out.println("Connexion à la base de données fermée.");
                }
            } catch (SQLException e) {
                System.err.println("Erreur fermeture Connection: " + e.getMessage());
            }
        }
    }
}