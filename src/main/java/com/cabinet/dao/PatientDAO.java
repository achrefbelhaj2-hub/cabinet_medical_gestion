package com.cabinet.dao;

import com.cabinet.model.Patient;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PatientDAO {

    private static final String INSERT_PATIENT = 
        "INSERT INTO patient (nom, prenom, date_naissance, adresse, telephone, dossier_medical, mutuelle, id_utilisateur) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String SELECT_PATIENT_BY_ID = 
        "SELECT * FROM patient WHERE id_patient = ?";
    private static final String SELECT_ALL_PATIENTS = 
        "SELECT * FROM patient";
    private static final String UPDATE_PATIENT = 
        "UPDATE patient SET nom = ?, prenom = ?, date_naissance = ?, adresse = ?, telephone = ?, dossier_medical = ?, mutuelle = ? WHERE id_patient = ?";
    private static final String DELETE_PATIENT = 
        "DELETE FROM patient WHERE id_patient = ?";
    private static final String SELECT_PATIENT_BY_NOM = 
        "SELECT * FROM patient WHERE nom LIKE ?";
    private static final String SELECT_PATIENT_BY_USER_ID = 
        "SELECT * FROM patient WHERE id_utilisateur = ?";

    private Patient mapResultSetToPatient(ResultSet rs) throws SQLException {
        int id = rs.getInt("id_patient");
        String nom = rs.getString("nom");
        String prenom = rs.getString("prenom");
        java.sql.Date dateNaissance = rs.getDate("date_naissance");
        String adresse = rs.getString("adresse");
        String telephone = rs.getString("telephone");
        String dossierMedical = rs.getString("dossier_medical");
        String mutuelle = rs.getString("mutuelle");
        int idUtilisateur = rs.getInt("id_utilisateur");
        
        return new Patient(id, nom, prenom, dateNaissance, adresse, telephone, dossierMedical, mutuelle, idUtilisateur);
    }

    public int addPatient(Patient patient) {
        int idGenerated = 0;
        Connection connection = null;
        PreparedStatement ps = null;

        try {
            connection = DBConnection.getConnection();
            ps = connection.prepareStatement(INSERT_PATIENT, Statement.RETURN_GENERATED_KEYS);
            
            ps.setString(1, patient.getNom());
            ps.setString(2, patient.getPrenom());
            ps.setDate(3, patient.getDate_naissance());
            ps.setString(4, patient.getAdresse());
            ps.setString(5, patient.getTelephone());
            ps.setString(6, patient.getDossier_medical());
            ps.setString(7, patient.getMutuelle());
            ps.setInt(8, patient.getId_utilisateur());
            
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

    public Patient getPatientById(int idPatient) {
        Patient patient = null;
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = DBConnection.getConnection();
            ps = connection.prepareStatement(SELECT_PATIENT_BY_ID);
            ps.setInt(1, idPatient);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                patient = mapResultSetToPatient(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.closeConnection(connection);
        }
        return patient;
    }

    public Patient getPatientByUserId(int idUtilisateur) {
        Patient patient = null;
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = DBConnection.getConnection();
            ps = connection.prepareStatement(SELECT_PATIENT_BY_USER_ID);
            ps.setInt(1, idUtilisateur);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                patient = mapResultSetToPatient(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.closeConnection(connection);
        }
        return patient;
    }

    public List<Patient> getAllPatients() {
        List<Patient> patients = new ArrayList<>();
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = DBConnection.getConnection();
            ps = connection.prepareStatement(SELECT_ALL_PATIENTS);
            rs = ps.executeQuery();

            while (rs.next()) {
                patients.add(mapResultSetToPatient(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.closeConnection(connection);
        }
        return patients;
    }
    
    public List<Patient> searchPatientsByName(String nomRecherche) {
        List<Patient> patients = new ArrayList<>();
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = DBConnection.getConnection();
            ps = connection.prepareStatement(SELECT_PATIENT_BY_NOM);
            ps.setString(1, "%" + nomRecherche + "%");
            rs = ps.executeQuery();

            while (rs.next()) {
                patients.add(mapResultSetToPatient(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.closeConnection(connection);
        }
        return patients;
    }

    public boolean updatePatient(Patient patient) {
        Connection connection = null;
        PreparedStatement ps = null;
        boolean rowUpdated = false;

        try {
            connection = DBConnection.getConnection();
            ps = connection.prepareStatement(UPDATE_PATIENT);
            
            ps.setString(1, patient.getNom());
            ps.setString(2, patient.getPrenom());
            ps.setDate(3, patient.getDate_naissance());
            ps.setString(4, patient.getAdresse());
            ps.setString(5, patient.getTelephone());
            ps.setString(6, patient.getDossier_medical());
            ps.setString(7, patient.getMutuelle());
            ps.setInt(8, patient.getId_personne());

            rowUpdated = ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.closeConnection(connection);
        }
        return rowUpdated;
    }

    public boolean deletePatient(int idPatient) {
        Connection connection = null;
        PreparedStatement ps = null;
        boolean rowDeleted = false;

        try {
            connection = DBConnection.getConnection();
            ps = connection.prepareStatement(DELETE_PATIENT);
            ps.setInt(1, idPatient);
            
            rowDeleted = ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.closeConnection(connection);
        }
        return rowDeleted;
    }
}