package com.cabinet.dao;

import com.cabinet.model.Ordonnance;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrdonnanceDAO {

    private static final String INSERT_ORDONNANCE = 
        "INSERT INTO ordonnance (date_prescription, contenu_textuel, id_patient, id_medecin) VALUES (?, ?, ?, ?)";
    private static final String SELECT_ORDONNANCE_BY_ID = 
        "SELECT * FROM ordonnance WHERE id_ordonnance = ?";
    private static final String SELECT_ALL_ORDONNANCES = 
        "SELECT * FROM ordonnance ORDER BY date_prescription DESC";
    private static final String UPDATE_ORDONNANCE = 
        "UPDATE ordonnance SET date_prescription = ?, contenu_textuel = ?, id_patient = ?, id_medecin = ? WHERE id_ordonnance = ?";
    private static final String DELETE_ORDONNANCE = 
        "DELETE FROM ordonnance WHERE id_ordonnance = ?";
    private static final String SELECT_BY_PATIENT = 
        "SELECT * FROM ordonnance WHERE id_patient = ? ORDER BY date_prescription DESC";
    private static final String SELECT_BY_MEDECIN = 
        "SELECT * FROM ordonnance WHERE id_medecin = ? ORDER BY date_prescription DESC";
    private static final String SELECT_BY_DATE = 
        "SELECT * FROM ordonnance WHERE DATE(date_prescription) = ? ORDER BY date_prescription DESC";
    private static final String SELECT_BY_PATIENT_AND_MEDECIN = 
        "SELECT * FROM ordonnance WHERE id_patient = ? AND id_medecin = ? ORDER BY date_prescription DESC";
    private static final String COUNT_BY_PATIENT = 
        "SELECT COUNT(*) FROM ordonnance WHERE id_patient = ?";
    private static final String COUNT_BY_MEDECIN = 
        "SELECT COUNT(*) FROM ordonnance WHERE id_medecin = ?";

    private Ordonnance mapResultSetToOrdonnance(ResultSet rs) throws SQLException {
        int idOrdonnance = rs.getInt("id_ordonnance");
        java.sql.Date datePrescription = rs.getDate("date_prescription");
        String contenuTextuel = rs.getString("contenu_textuel");
        int idPatient = rs.getInt("id_patient");
        int idMedecin = rs.getInt("id_medecin");
        
        return new Ordonnance(idOrdonnance, datePrescription, contenuTextuel, idPatient, idMedecin);
    }

    public int addOrdonnance(Ordonnance ordonnance) {
        int idGenerated = 0;
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = DBConnection.getConnection();
            ps = connection.prepareStatement(INSERT_ORDONNANCE, Statement.RETURN_GENERATED_KEYS);
            
            ps.setDate(1, ordonnance.getDate_prescription());
            ps.setString(2, ordonnance.getContenu_textuel());
            ps.setInt(3, ordonnance.getId_patient());
            ps.setInt(4, ordonnance.getId_medecin());
            
            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    idGenerated = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(connection, ps, rs);
        }
        return idGenerated;
    }

    public List<Ordonnance> getAllOrdonnances() {
        List<Ordonnance> ordonnances = new ArrayList<>();
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = DBConnection.getConnection();
            ps = connection.prepareStatement(SELECT_ALL_ORDONNANCES);
            rs = ps.executeQuery();

            while (rs.next()) {
                ordonnances.add(mapResultSetToOrdonnance(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(connection, ps, rs);
        }
        return ordonnances;
    }

    public Ordonnance getOrdonnanceById(int idOrdonnance) {
        Ordonnance ordonnance = null;
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = DBConnection.getConnection();
            ps = connection.prepareStatement(SELECT_ORDONNANCE_BY_ID);
            ps.setInt(1, idOrdonnance);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                ordonnance = mapResultSetToOrdonnance(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(connection, ps, rs);
        }
        return ordonnance;
    }
    
    public List<Ordonnance> getOrdonnancesByPatientId(int idPatient) {
        List<Ordonnance> ordonnances = new ArrayList<>();
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            connection = DBConnection.getConnection();
            ps = connection.prepareStatement(SELECT_BY_PATIENT);
            ps.setInt(1, idPatient);
            rs = ps.executeQuery();
            
            while (rs.next()) {
                ordonnances.add(mapResultSetToOrdonnance(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(connection, ps, rs);
        }
        return ordonnances;
    }

    public List<Ordonnance> getOrdonnancesByMedecinId(int idMedecin) {
        List<Ordonnance> ordonnances = new ArrayList<>();
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            connection = DBConnection.getConnection();
            ps = connection.prepareStatement(SELECT_BY_MEDECIN);
            ps.setInt(1, idMedecin);
            rs = ps.executeQuery();
            
            while (rs.next()) {
                ordonnances.add(mapResultSetToOrdonnance(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(connection, ps, rs);
        }
        return ordonnances;
    }
    
    public List<Ordonnance> getOrdonnancesByDate(java.sql.Date date) {
        List<Ordonnance> ordonnances = new ArrayList<>();
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = DBConnection.getConnection();
            ps = connection.prepareStatement(SELECT_BY_DATE);
            ps.setDate(1, date);
            rs = ps.executeQuery();

            while (rs.next()) {
                ordonnances.add(mapResultSetToOrdonnance(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(connection, ps, rs);
        }
        return ordonnances;
    }
    
    public List<Ordonnance> getOrdonnancesByPatientAndMedecin(int idPatient, int idMedecin) {
        List<Ordonnance> ordonnances = new ArrayList<>();
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = DBConnection.getConnection();
            ps = connection.prepareStatement(SELECT_BY_PATIENT_AND_MEDECIN);
            ps.setInt(1, idPatient);
            ps.setInt(2, idMedecin);
            rs = ps.executeQuery();

            while (rs.next()) {
                ordonnances.add(mapResultSetToOrdonnance(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(connection, ps, rs);
        }
        return ordonnances;
    }
    
    public int countOrdonnancesByPatient(int idPatient) {
        int count = 0;
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = DBConnection.getConnection();
            ps = connection.prepareStatement(COUNT_BY_PATIENT);
            ps.setInt(1, idPatient);
            rs = ps.executeQuery();

            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(connection, ps, rs);
        }
        return count;
    }
    
    public int countOrdonnancesByMedecin(int idMedecin) {
        int count = 0;
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = DBConnection.getConnection();
            ps = connection.prepareStatement(COUNT_BY_MEDECIN);
            ps.setInt(1, idMedecin);
            rs = ps.executeQuery();

            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(connection, ps, rs);
        }
        return count;
    }

    public boolean updateOrdonnance(Ordonnance ordonnance) {
        Connection connection = null;
        PreparedStatement ps = null;
        boolean rowUpdated = false;

        try {
            connection = DBConnection.getConnection();
            ps = connection.prepareStatement(UPDATE_ORDONNANCE);
            
            ps.setDate(1, ordonnance.getDate_prescription());
            ps.setString(2, ordonnance.getContenu_textuel());
            ps.setInt(3, ordonnance.getId_patient());
            ps.setInt(4, ordonnance.getId_medecin());
            ps.setInt(5, ordonnance.getId_ordonnance());

            rowUpdated = ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(connection, ps, null);
        }
        return rowUpdated;
    }

    public boolean deleteOrdonnance(int idOrdonnance) {
        Connection connection = null;
        PreparedStatement ps = null;
        boolean rowDeleted = false;

        try {
            connection = DBConnection.getConnection();
            ps = connection.prepareStatement(DELETE_ORDONNANCE);
            ps.setInt(1, idOrdonnance);
            
            rowDeleted = ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(connection, ps, null);
        }
        return rowDeleted;
    }
    
    public List<Ordonnance> searchOrdonnances(String keyword) {
        List<Ordonnance> ordonnances = new ArrayList<>();
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = DBConnection.getConnection();
            String query = "SELECT * FROM ordonnance WHERE contenu_textuel LIKE ? ORDER BY date_prescription DESC";
            ps = connection.prepareStatement(query);
            ps.setString(1, "%" + keyword + "%");
            rs = ps.executeQuery();

            while (rs.next()) {
                ordonnances.add(mapResultSetToOrdonnance(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(connection, ps, rs);
        }
        return ordonnances;
    }
}