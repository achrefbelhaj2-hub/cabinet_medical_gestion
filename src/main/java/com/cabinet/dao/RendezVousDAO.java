package com.cabinet.dao;

import com.cabinet.model.RendezVous;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RendezVousDAO {

    // Requêtes SQL
    private static final String INSERT_RDV = 
        "INSERT INTO rendezvous (date_heure, motif, statut, id_patient, id_medecin) VALUES (?, ?, ?, ?, ?)";
    private static final String SELECT_RDV_BY_ID = 
        "SELECT * FROM rendezvous WHERE id_rdv = ?";
    private static final String SELECT_ALL_RDV = 
        "SELECT * FROM rendezvous ORDER BY date_heure";
    private static final String UPDATE_RDV = 
        "UPDATE rendezvous SET date_heure = ?, motif = ?, statut = ?, id_patient = ?, id_medecin = ? WHERE id_rdv = ?";
    private static final String DELETE_RDV = 
        "DELETE FROM rendezvous WHERE id_rdv = ?";
    private static final String SELECT_RDV_BY_DATE = 
        "SELECT * FROM rendezvous WHERE DATE(date_heure) = ? ORDER BY date_heure";
    private static final String SELECT_RDV_BY_PATIENT = 
        "SELECT * FROM rendezvous WHERE id_patient = ? ORDER BY date_heure";
    private static final String SELECT_RDV_BY_MEDECIN = 
        "SELECT * FROM rendezvous WHERE id_medecin = ? ORDER BY date_heure";
    private static final String SELECT_RDV_BY_STATUT = 
        "SELECT * FROM rendezvous WHERE statut = ? ORDER BY date_heure";
    private static final String COUNT_RDV_BY_DATE = 
        "SELECT COUNT(*) FROM rendezvous WHERE DATE(date_heure) = ?";
    private static final String UPDATE_STATUT = 
        "UPDATE rendezvous SET statut = ? WHERE id_rdv = ?";

    // --- Méthode d'aide pour mapper le ResultSet à l'objet RendezVous ---
    private RendezVous mapResultSetToRendezVous(ResultSet rs) throws SQLException {
        int idRdv = rs.getInt("id_rdv");
        Timestamp timestamp = rs.getTimestamp("date_heure");
        LocalDateTime dateHeure = timestamp.toLocalDateTime();
        String motif = rs.getString("motif");
        String statut = rs.getString("statut");
        int idPatient = rs.getInt("id_patient");
        int idMedecin = rs.getInt("id_medecin");
        
        return new RendezVous(idRdv, dateHeure, motif, statut, idPatient, idMedecin);
    }

    // --- CREATE ---
    public int addRendezVous(RendezVous rdv) {
        int idGenerated = 0;
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = DBConnection.getConnection();
            ps = connection.prepareStatement(INSERT_RDV, Statement.RETURN_GENERATED_KEYS);
            
            ps.setTimestamp(1, Timestamp.valueOf(rdv.getDate_heure()));
            ps.setString(2, rdv.getMotif());
            ps.setString(3, rdv.getStatut());
            ps.setInt(4, rdv.getId_patient());
            ps.setInt(5, rdv.getId_medecin());
            
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

    // --- READ ALL ---
    public List<RendezVous> getAllRendezVous() {
        List<RendezVous> rdvs = new ArrayList<>();
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = DBConnection.getConnection();
            ps = connection.prepareStatement(SELECT_ALL_RDV);
            rs = ps.executeQuery();

            while (rs.next()) {
                rdvs.add(mapResultSetToRendezVous(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(connection, ps, rs);
        }
        return rdvs;
    }
    
    // --- READ BY ID ---
    public RendezVous getRendezVousById(int idRdv) {
        RendezVous rdv = null;
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = DBConnection.getConnection();
            ps = connection.prepareStatement(SELECT_RDV_BY_ID);
            ps.setInt(1, idRdv);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                rdv = mapResultSetToRendezVous(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(connection, ps, rs);
        }
        return rdv;
    }
    
    // --- READ BY DATE ---
    public List<RendezVous> getRendezVousByDate(java.sql.Date date) {
        List<RendezVous> rdvs = new ArrayList<>();
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = DBConnection.getConnection();
            ps = connection.prepareStatement(SELECT_RDV_BY_DATE);
            ps.setDate(1, date);
            rs = ps.executeQuery();

            while (rs.next()) {
                rdvs.add(mapResultSetToRendezVous(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(connection, ps, rs);
        }
        return rdvs;
    }
    
    // --- READ BY PATIENT ID ---
    public List<RendezVous> getRendezVousByPatientId(int idPatient) {
        List<RendezVous> rdvs = new ArrayList<>();
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = DBConnection.getConnection();
            ps = connection.prepareStatement(SELECT_RDV_BY_PATIENT);
            ps.setInt(1, idPatient);
            rs = ps.executeQuery();

            while (rs.next()) {
                rdvs.add(mapResultSetToRendezVous(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(connection, ps, rs);
        }
        return rdvs;
    }
    
    // --- READ BY MEDECIN ID ---
    public List<RendezVous> getRendezVousByMedecinId(int idMedecin) {
        List<RendezVous> rdvs = new ArrayList<>();
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = DBConnection.getConnection();
            ps = connection.prepareStatement(SELECT_RDV_BY_MEDECIN);
            ps.setInt(1, idMedecin);
            rs = ps.executeQuery();

            while (rs.next()) {
                rdvs.add(mapResultSetToRendezVous(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(connection, ps, rs);
        }
        return rdvs;
    }
    
    // --- READ BY STATUT ---
    public List<RendezVous> getRendezVousByStatut(String statut) {
        List<RendezVous> rdvs = new ArrayList<>();
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = DBConnection.getConnection();
            ps = connection.prepareStatement(SELECT_RDV_BY_STATUT);
            ps.setString(1, statut);
            rs = ps.executeQuery();

            while (rs.next()) {
                rdvs.add(mapResultSetToRendezVous(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(connection, ps, rs);
        }
        return rdvs;
    }
    
    // --- COUNT BY DATE ---
    public int countRendezVousByDate(java.sql.Date date) {
        int count = 0;
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = DBConnection.getConnection();
            ps = connection.prepareStatement(COUNT_RDV_BY_DATE);
            ps.setDate(1, date);
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
    
    // --- UPDATE ---
    public boolean updateRendezVous(RendezVous rdv) {
        Connection connection = null;
        PreparedStatement ps = null;
        boolean rowUpdated = false;

        try {
            connection = DBConnection.getConnection();
            ps = connection.prepareStatement(UPDATE_RDV);
            
            ps.setTimestamp(1, Timestamp.valueOf(rdv.getDate_heure()));
            ps.setString(2, rdv.getMotif());
            ps.setString(3, rdv.getStatut());
            ps.setInt(4, rdv.getId_patient());
            ps.setInt(5, rdv.getId_medecin());
            ps.setInt(6, rdv.getId_rdv());

            rowUpdated = ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(connection, ps, null);
        }
        return rowUpdated;
    }
    
    // --- UPDATE STATUT SEULEMENT ---
    public boolean updateStatut(int idRdv, String statut) {
        Connection connection = null;
        PreparedStatement ps = null;
        boolean rowUpdated = false;

        try {
            connection = DBConnection.getConnection();
            ps = connection.prepareStatement(UPDATE_STATUT);
            
            ps.setString(1, statut);
            ps.setInt(2, idRdv);

            rowUpdated = ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(connection, ps, null);
        }
        return rowUpdated;
    }

    // --- DELETE ---
    public boolean deleteRendezVous(int idRdv) {
        Connection connection = null;
        PreparedStatement ps = null;
        boolean rowDeleted = false;

        try {
            connection = DBConnection.getConnection();
            ps = connection.prepareStatement(DELETE_RDV);
            ps.setInt(1, idRdv);
            
            rowDeleted = ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(connection, ps, null);
        }
        return rowDeleted;
    }
    
    // --- VALIDATION : Vérifier si un créneau est libre ---
    public boolean isCreneauDisponible(int idMedecin, LocalDateTime dateHeure) {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean disponible = true;
        
        try {
            connection = DBConnection.getConnection();
            String query = "SELECT COUNT(*) FROM rendezvous WHERE id_medecin = ? AND date_heure = ? AND statut != 'ANNULE'";
            ps = connection.prepareStatement(query);
            ps.setInt(1, idMedecin);
            ps.setTimestamp(2, Timestamp.valueOf(dateHeure));
            rs = ps.executeQuery();
            
            if (rs.next() && rs.getInt(1) > 0) {
                disponible = false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(connection, ps, rs);
        }
        return disponible;
    }
}