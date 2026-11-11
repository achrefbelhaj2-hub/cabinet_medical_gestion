package com.cabinet.controller.admin;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.stage.Modality;

import com.cabinet.dao.DBConnection;
import com.cabinet.dao.PatientDAO;
import com.cabinet.dao.MedecinDAO;
import com.cabinet.dao.RendezVousDAO;
import com.cabinet.dao.OrdonnanceDAO; 
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.sql.Date;

public class DashboardController {
    
    
    @FXML private Label labelPatientsCount;
    @FXML private Label labelMedecinsCount;
    @FXML private Label labelRdvTodayCount;
    @FXML private Label labelOrdonnancesCount; 
    @FXML private Button btnGestionPatients;
    @FXML private Button btnGestionMedecins;
    @FXML private Button btnVoirRdv;
    @FXML private Button btnVoirOrdonnances;
    
    @FXML private Button btnVoirStatistiques;
    
    
    @FXML
    public void initialize() {
        loadStatistics();
        setupButtonActions();
    }
    
    private void loadStatistics() {
        Connection connection = null;
        try {
            connection = DBConnection.getConnection();
            
            // Initialiser les DAOs avec la connexion
            PatientDAO patientDAO = new PatientDAO();
            MedecinDAO medecinDAO = new MedecinDAO();
            RendezVousDAO rendezVousDAO = new RendezVousDAO();
            OrdonnanceDAO ordonnanceDAO = new OrdonnanceDAO();
            
            // Compter les patients
            int patientsCount = patientDAO.getAllPatients().size();
            labelPatientsCount.setText(String.valueOf(patientsCount));
            
            // Compter les médecins
            int medecinsCount = medecinDAO.getAllMedecins().size();
            labelMedecinsCount.setText(String.valueOf(medecinsCount));
            
            // Compter les RDV d'aujourd'hui
            Date today = Date.valueOf(LocalDate.now());
            int rdvTodayCount = rendezVousDAO.countRendezVousByDate(today);
            labelRdvTodayCount.setText(String.valueOf(rdvTodayCount));
            
            // Compter les ordonnances
            int ordonnancesCount = ordonnanceDAO.getAllOrdonnances().size();
            labelOrdonnancesCount.setText(String.valueOf(ordonnancesCount));
            
        } catch (SQLException e) {
            e.printStackTrace();
            labelPatientsCount.setText("Erreur");
            labelMedecinsCount.setText("Erreur");
            labelRdvTodayCount.setText("Erreur");
            labelOrdonnancesCount.setText("Erreur");
            showAlert("Erreur", "Erreur de connexion à la base de données: " + e.getMessage());
        } finally {
            if (connection != null) {
                DBConnection.close(connection, null, null);
            }
        }
    }
    
    private void setupButtonActions() {
        btnGestionPatients.setOnAction(event -> openGestionPatients());
        btnGestionMedecins.setOnAction(event -> openGestionMedecins());
        btnVoirRdv.setOnAction(event -> openGestionRendezVous());
        btnVoirOrdonnances.setOnAction(event -> openGestionOrdonnances()); // Action Ordre
        btnVoirStatistiques.setOnAction(event -> openStatistiques()); // Action Stats
    }
    
    // Méthodes de navigation vers les autres vues (Gestion Patients, Médecins, RDV...)
    private void openGestionPatients() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/cabinet/view/admin/patient.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Gestion des Patients");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir la gestion des patients: " + e.getMessage());
        }
    }
    
    private void openGestionMedecins() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/cabinet/view/admin/medecin.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Gestion des Médecins");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir la gestion des médecins: " + e.getMessage());
        }
    }
    
    private void openGestionRendezVous() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/cabinet/view/admin/rendezvous.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Gestion des Rendez-vous");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir la gestion des rendez-vous: " + e.getMessage());
        }
    }
    
    /**
     * Méthode pour ouvrir la gestion des ordonnances (utilise ordonnance.fxml)
     */
    private void openGestionOrdonnances() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/cabinet/view/admin/ordonnance.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Gestion des Ordonnances");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir la gestion des ordonnances: " + e.getMessage());
        }
    }

    /**
     * Méthode pour ouvrir le module de Statistiques (utilise statistiques.fxml)
     */
    private void openStatistiques() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/cabinet/view/admin/statistiques.fxml"));
            Parent root = loader.load();
            
            Stage stage = new Stage();
            stage.setTitle("Statistiques du Cabinet");
            stage.setScene(new Scene(root, 950, 700)); // Taille adaptée
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
            
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir le module de statistiques: " + e.getMessage());
        }
    }
    
    private void showAlert(String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
            javafx.scene.control.Alert.AlertType.INFORMATION
        );
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    @FXML
    private void handleRefresh() {
        loadStatistics();
    }
}