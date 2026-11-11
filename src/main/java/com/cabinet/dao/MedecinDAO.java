package com.cabinet.dao;

import com.cabinet.model.Medecin;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MedecinDAO {

    private static final String INSERT_MEDECIN = 
        "INSERT INTO medecin (nom, prenom, date_naissance, adresse, telephone, specialite, licence, id_utilisateur) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String SELECT_MEDECIN_BY_ID = 
        "SELECT * FROM medecin WHERE id_medecin = ?";
    private static final String SELECT_ALL_MEDECINS = 
        "SELECT * FROM medecin";
    private static final String UPDATE_MEDECIN = 
        "UPDATE medecin SET nom = ?, prenom = ?, date_naissance = ?, adresse = ?, telephone = ?, specialite = ?, licence = ? WHERE id_medecin = ?";
    private static final String DELETE_MEDECIN = 
        "DELETE FROM medecin WHERE id_medecin = ?";
    private static final String SELECT_MEDECIN_BY_NOM = 
        "SELECT * FROM medecin WHERE nom LIKE ?";
    private static final String SELECT_MEDECIN_BY_USER_ID = 
        "SELECT * FROM medecin WHERE id_utilisateur = ?";

    private Medecin mapResultSetToMedecin(ResultSet rs) throws SQLException {
        int id = rs.getInt("id_medecin");
        String nom = rs.getString("nom");
        String prenom = rs.getString("prenom");
        java.sql.Date dateNaissance = rs.getDate("date_naissance");
        String adresse = rs.getString("adresse");
        String telephone = rs.getString("telephone");
        String specialite = rs.getString("specialite");
        String licence = rs.getString("licence");
        int idUtilisateur = rs.getInt("id_utilisateur");
        
        return new Medecin(id, nom, prenom, dateNaissance, adresse, telephone, specialite, licence, idUtilisateur);
    }
    
    public int addMedecin(Medecin medecin) {
        int idGenerated = 0;
        Connection connection = null;
        PreparedStatement ps = null;

        try {
            connection = DBConnection.getConnection();
            ps = connection.prepareStatement(INSERT_MEDECIN, Statement.RETURN_GENERATED_KEYS);
            
            ps.setString(1, medecin.getNom());
            ps.setString(2, medecin.getPrenom());
            ps.setDate(3, medecin.getDate_naissance());
            ps.setString(4, medecin.getAdresse());
            ps.setString(5, medecin.getTelephone());
            ps.setString(6, medecin.getSpecialite());
            ps.setString(7, medecin.getLicence());
            ps.setInt(8, medecin.getId_utilisateur());
            
            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        idGenerated = rs.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.closeConnection(connection);
        }
        return idGenerated;
    }

    public Medecin getMedecinById(int idMedecin) {
        Medecin medecin = null;
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = DBConnection.getConnection();
            ps = connection.prepareStatement(SELECT_MEDECIN_BY_ID);
            ps.setInt(1, idMedecin);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                medecin = mapResultSetToMedecin(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.closeConnection(connection);
        }
        return medecin;
    }

    public List<Medecin> getAllMedecins() {
        List<Medecin> medecins = new ArrayList<>();
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = DBConnection.getConnection();
            ps = connection.prepareStatement(SELECT_ALL_MEDECINS);
            rs = ps.executeQuery();

            while (rs.next()) {
                medecins.add(mapResultSetToMedecin(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.closeConnection(connection);
        }
        return medecins;
    }
    
    public List<Medecin> searchMedecinsByName(String nomRecherche) {
        List<Medecin> medecins = new ArrayList<>();
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = DBConnection.getConnection();
            ps = connection.prepareStatement(SELECT_MEDECIN_BY_NOM);
            ps.setString(1, "%" + nomRecherche + "%");
            rs = ps.executeQuery();

            while (rs.next()) {
                medecins.add(mapResultSetToMedecin(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.closeConnection(connection);
        }
        return medecins;
    }

    public boolean updateMedecin(Medecin medecin) {
        Connection connection = null;
        PreparedStatement ps = null;
        boolean rowUpdated = false;

        try {
            connection = DBConnection.getConnection();
            ps = connection.prepareStatement(UPDATE_MEDECIN);
            
            ps.setString(1, medecin.getNom());
            ps.setString(2, medecin.getPrenom());
            ps.setDate(3, medecin.getDate_naissance());
            ps.setString(4, medecin.getAdresse());
            ps.setString(5, medecin.getTelephone());
            ps.setString(6, medecin.getSpecialite());
            ps.setString(7, medecin.getLicence());
            ps.setInt(8, medecin.getId_personne());

            rowUpdated = ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.closeConnection(connection);
        }
        return rowUpdated;
    }
    
    public boolean deleteMedecin(int idMedecin) {
        Connection connection = null;
        PreparedStatement ps = null;
        boolean rowDeleted = false;

        try {
            connection = DBConnection.getConnection();
            ps = connection.prepareStatement(DELETE_MEDECIN);
            ps.setInt(1, idMedecin);
            
            rowDeleted = ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.closeConnection(connection);
        }
        return rowDeleted;
    }
    
    public Medecin getMedecinByUserId(int idUtilisateur) {
        Medecin medecin = null;
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = DBConnection.getConnection();
            ps = connection.prepareStatement(SELECT_MEDECIN_BY_USER_ID);
            ps.setInt(1, idUtilisateur);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                medecin = mapResultSetToMedecin(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.closeConnection(connection);
        }
        return medecin;
    }
}