package com.cabinet.dao;

import com.cabinet.model.Utilisateur;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class UserDAO {

    // Requêtes SQL
    private static final String SELECT_USER_BY_LOGIN = 
        "SELECT id_utilisateur, login, mot_de_passe, role FROM utilisateur WHERE login = ?";
    private static final String SELECT_USER_BY_ID = 
        "SELECT id_utilisateur, login, mot_de_passe, role FROM utilisateur WHERE id_utilisateur = ?";
    private static final String INSERT_USER = 
        "INSERT INTO utilisateur (login, mot_de_passe, role) VALUES (?, ?, ?)";
    private static final String UPDATE_USER = 
        "UPDATE utilisateur SET login = ?, mot_de_passe = ?, role = ? WHERE id_utilisateur = ?";
    private static final String DELETE_USER = 
        "DELETE FROM utilisateur WHERE id_utilisateur = ?";
    private static final String SELECT_ALL_USERS = 
        "SELECT id_utilisateur, login, mot_de_passe, role FROM utilisateur";
    private static final String COUNT_BY_ROLE = 
        "SELECT COUNT(*) FROM utilisateur WHERE role = ?";

    // --- Méthode d'aide pour mapper le ResultSet à l'objet Utilisateur ---
    private Utilisateur mapResultSetToUtilisateur(ResultSet rs) throws SQLException {
        int id = rs.getInt("id_utilisateur");
        String login = rs.getString("login");
        String motDePasse = rs.getString("mot_de_passe");
        String role = rs.getString("role");
        return new Utilisateur(id, login, motDePasse, role);
    }
    
    // ---------------------- CREATE (Ajouter) ----------------------
    /**
     * Ajoute un nouvel utilisateur.
     * @return L'ID auto-généré de l'utilisateur inséré.
     */
    public int addUser(Utilisateur user) {
        int idGenerated = 0;
        Connection connection = null;
        PreparedStatement ps = null;

        try {
            connection = DBConnection.getConnection();
            ps = connection.prepareStatement(INSERT_USER, Statement.RETURN_GENERATED_KEYS); 
            
            ps.setString(1, user.getLogin());
            ps.setString(2, user.getMot_de_passe()); // Pas de hachage
            ps.setString(3, user.getRole());
            
            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        idGenerated = rs.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout de l'utilisateur: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBConnection.closeConnection(connection);
        }
        return idGenerated;
    }

    // ---------------------- READ (Lecture) ----------------------
    /**
     * Récupère un utilisateur par son login (pour l'authentification).
     */
    public Utilisateur getUserByLogin(String login) {
        Utilisateur user = null;
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = DBConnection.getConnection();
            ps = connection.prepareStatement(SELECT_USER_BY_LOGIN);
            ps.setString(1, login);
            
            rs = ps.executeQuery();

            if (rs.next()) {
                user = mapResultSetToUtilisateur(rs);
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de la recherche de l'utilisateur par login: " + e.getMessage());
        } finally {
            DBConnection.closeConnection(connection);
        }
        return user;
    }
    
    /**
     * Récupère un utilisateur par son ID (pour la liaison Patient/Médecin).
     */
    public Utilisateur getUserById(int id) {
        Utilisateur user = null;
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = DBConnection.getConnection();
            ps = connection.prepareStatement(SELECT_USER_BY_ID);
            ps.setInt(1, id);
            
            rs = ps.executeQuery();

            if (rs.next()) {
                user = mapResultSetToUtilisateur(rs);
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de la recherche de l'utilisateur par ID: " + e.getMessage());
        } finally {
            DBConnection.closeConnection(connection);
        }
        return user;
    }
    
    /**
     * Authentifie un utilisateur avec login et mot de passe.
     * @return L'utilisateur si authentification réussie, null sinon.
     */
    public Utilisateur authenticate(String login, String password) {
        Utilisateur user = getUserByLogin(login);
        if (user != null && password.equals(user.getMot_de_passe())) { // Comparaison directe sans hachage
            return user;
        }
        return null;
    }
    
    /**
     * Récupère tous les utilisateurs.
     */
    public java.util.List<Utilisateur> getAllUsers() {
        java.util.List<Utilisateur> users = new java.util.ArrayList<>();
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = DBConnection.getConnection();
            ps = connection.prepareStatement(SELECT_ALL_USERS);
            rs = ps.executeQuery();

            while (rs.next()) {
                users.add(mapResultSetToUtilisateur(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de tous les utilisateurs: " + e.getMessage());
        } finally {
            DBConnection.closeConnection(connection);
        }
        return users;
    }
    
    // ---------------------- UPDATE (Modifier) ----------------------
    /**
     * Met à jour les informations de l'utilisateur (login, mot de passe, rôle).
     * @param user L'objet Utilisateur avec les nouvelles valeurs.
     * @return true si la mise à jour a réussi, false sinon.
     */
    public boolean updateUser(Utilisateur user) {
        Connection connection = null;
        PreparedStatement ps = null;
        boolean rowUpdated = false;

        try {
            connection = DBConnection.getConnection();
            ps = connection.prepareStatement(UPDATE_USER);
            
            ps.setString(1, user.getLogin());
            ps.setString(2, user.getMot_de_passe()); // Pas de hachage
            ps.setString(3, user.getRole());
            ps.setInt(4, user.getId_utilisateur());

            rowUpdated = ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour de l'utilisateur: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBConnection.closeConnection(connection);
        }
        return rowUpdated;
    }
    
    /**
     * Met à jour uniquement le mot de passe d'un utilisateur.
     * @param idUser ID de l'utilisateur
     * @param newPassword Nouveau mot de passe (en clair)
     * @return true si la mise à jour a réussi, false sinon.
     */
    public boolean updatePassword(int idUser, String newPassword) {
        Connection connection = null;
        PreparedStatement ps = null;
        boolean rowUpdated = false;
        
        String updatePasswordSQL = "UPDATE utilisateur SET mot_de_passe = ? WHERE id_utilisateur = ?";

        try {
            connection = DBConnection.getConnection();
            ps = connection.prepareStatement(updatePasswordSQL);
            
            ps.setString(1, newPassword); // Pas de hachage
            ps.setInt(2, idUser);

            rowUpdated = ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors du changement de mot de passe: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBConnection.closeConnection(connection);
        }
        return rowUpdated;
    }

    // ---------------------- DELETE (Supprimer) ----------------------
    /**
     * Supprime un utilisateur par son ID.
     * @param idUser L'ID de l'utilisateur à supprimer.
     * @return true si la suppression a réussi, false sinon.
     */
    public boolean deleteUser(int idUser) {
        Connection connection = null;
        PreparedStatement ps = null;
        boolean rowDeleted = false;

        try {
            connection = DBConnection.getConnection();
            ps = connection.prepareStatement(DELETE_USER);
            ps.setInt(1, idUser);
            
            rowDeleted = ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de l'utilisateur: " + e.getMessage());
            System.err.println("Cause: " + e.getCause());
            System.err.println("SQL State: " + e.getSQLState());
        } finally {
            DBConnection.closeConnection(connection);
        }
        return rowDeleted;
    }
    
    // ---------------------- UTILITAIRES ----------------------
    /**
     * Vérifie si un login existe déjà.
     * @param login Login à vérifier
     * @return true si le login existe, false sinon
     */
    public boolean loginExists(String login) {
        return getUserByLogin(login) != null;
    }
    
    /**
     * Compte le nombre d'utilisateurs par rôle.
     * @param role Rôle à compter
     * @return Nombre d'utilisateurs avec ce rôle
     */
    public int countUsersByRole(String role) {
        int count = 0;
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = DBConnection.getConnection();
            ps = connection.prepareStatement(COUNT_BY_ROLE);
            ps.setString(1, role);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors du comptage des utilisateurs: " + e.getMessage());
        } finally {
            DBConnection.closeConnection(connection);
        }
        return count;
    }
    
    /**
     * Vérifie si un utilisateur peut être supprimé (pas lié à un patient/médecin).
     * @param idUser ID de l'utilisateur
     * @return true si l'utilisateur peut être supprimé, false sinon
     */
    public boolean canDeleteUser(int idUser) {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean canDelete = true;
        
        String checkSQL = "SELECT COUNT(*) FROM patient WHERE id_utilisateur = ? " +
                         "UNION ALL SELECT COUNT(*) FROM medecin WHERE id_utilisateur = ?";

        try {
            connection = DBConnection.getConnection();
            ps = connection.prepareStatement(checkSQL);
            ps.setInt(1, idUser);
            ps.setInt(2, idUser);
            rs = ps.executeQuery();
            
            // Vérifier le patient
            if (rs.next() && rs.getInt(1) > 0) {
                canDelete = false;
            }
            
            // Vérifier le médecin
            if (rs.next() && rs.getInt(1) > 0) {
                canDelete = false;
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur lors de la vérification de suppression: " + e.getMessage());
        } finally {
            DBConnection.closeConnection(connection);
        }
        return canDelete;
    }
}