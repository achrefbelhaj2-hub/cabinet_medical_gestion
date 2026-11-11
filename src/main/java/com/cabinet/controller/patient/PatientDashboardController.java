package com.cabinet.controller.patient;

import com.cabinet.controller.SessionManager;
import com.cabinet.dao.RendezVousDAO;
import com.cabinet.dao.OrdonnanceDAO;
import com.cabinet.model.Patient;
import com.cabinet.model.RendezVous;
import com.cabinet.model.Ordonnance;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Affiche les informations de synthèse pour le Patient (prochain RDV, messages...).
 */
public class PatientDashboardController {

    @FXML private Label welcomeLabel;
    @FXML private Label rdvCountLabel;
    @FXML private Label nextRdvLabel;
    
    // Boutons du menu
    @FXML private Button btnDashboard;
    @FXML private Button btnProfil;
    @FXML private Button btnRendezVous;
    @FXML private Button btnOrdonnances;
    @FXML private Button btnDeconnexion;
    
    // Boutons d'actions rapides
    @FXML private Button btnPrendreRdv;
    @FXML private Button btnVoirOrdonnances;
    @FXML private Button btnModifierProfil;
    @FXML private Button btnContacterCabinet;
    
    private RendezVousDAO rdvDAO = new RendezVousDAO();
    private OrdonnanceDAO ordonnanceDAO = new OrdonnanceDAO();
    private Patient patient;

    @FXML
    public void initialize() {
        patient = SessionManager.getInstance().getCurrentPatient();
        if (patient != null) {
            welcomeLabel.setText("Bienvenue, " + patient.getPrenom() + " " + patient.getNom());
            loadPatientStatistics();
            setupButtonActions();
            activateNavButton(btnDashboard);
        } else {
            welcomeLabel.setText("Bienvenue - Session non trouvée");
            showAlert(Alert.AlertType.WARNING, "Session", "Veuillez vous reconnecter.");
        }
    }
    
    private void loadPatientStatistics() {
        try {
            // 1. Compter le nombre total de RDV
            List<RendezVous> rdvs = rdvDAO.getRendezVousByPatientId(patient.getId_personne());
            int totalRdvs = rdvs.size();
            rdvCountLabel.setText(String.valueOf(totalRdvs));
            
            // 2. Trouver le prochain RDV
            if (!rdvs.isEmpty()) {
                RendezVous prochainRdv = rdvs.stream()
                    .filter(rdv -> rdv.getStatut().equals("PLANIFIE"))
                    .filter(rdv -> rdv.getDate_heure().isAfter(LocalDateTime.now()))
                    .sorted((rdv1, rdv2) -> rdv1.getDate_heure().compareTo(rdv2.getDate_heure()))
                    .findFirst()
                    .orElse(null);
                
                if (prochainRdv != null) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm");
                    String dateFormatee = prochainRdv.getDate_heure().format(formatter);
                    nextRdvLabel.setText(dateFormatee);
                } else {
                    nextRdvLabel.setText("Aucun RDV à venir");
                }
            } else {
                nextRdvLabel.setText("Aucun RDV planifié");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            rdvCountLabel.setText("Erreur");
            nextRdvLabel.setText("Données non disponibles");
        }
    }
    
    private void setupButtonActions() {
        // Actions du menu latéral
        btnDashboard.setOnAction(event -> activateNavButton(btnDashboard));
        btnProfil.setOnAction(event -> openProfil());
        btnRendezVous.setOnAction(event -> openRendezVous());
        btnOrdonnances.setOnAction(event -> openOrdonnances());
        btnDeconnexion.setOnAction(event -> handleDeconnexion());
        
        // Actions rapides
        btnPrendreRdv.setOnAction(event -> prendreRendezVous());
        btnVoirOrdonnances.setOnAction(event -> openOrdonnances());
        btnModifierProfil.setOnAction(event -> openProfil());
        btnContacterCabinet.setOnAction(event -> contacterCabinet());
    }
    
    private void activateNavButton(Button activeButton) {
        // Réinitialiser tous les boutons
        btnDashboard.getStyleClass().remove("nav-item-active");
        btnProfil.getStyleClass().remove("nav-item-active");
        btnRendezVous.getStyleClass().remove("nav-item-active");
        btnOrdonnances.getStyleClass().remove("nav-item-active");
        
        // Activer le bouton sélectionné
        if (!activeButton.getStyleClass().contains("nav-item-active")) {
            activeButton.getStyleClass().add("nav-item-active");
        }
    }
    
    private void openProfil() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/cabinet/view/patient/patient_profile.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) btnDashboard.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Mon Profil - Espace Patient");
            
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir le profil.");
        }
    }
    
    private void openRendezVous() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/cabinet/view/patient/patient_rdv.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) btnDashboard.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Mes Rendez-vous - Espace Patient");
            
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir les rendez-vous.");
        }
    }
    
    private void openOrdonnances() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/cabinet/view/patient/patient_ordonnance.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) btnDashboard.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Mes Ordonnances - Espace Patient");
            
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir les ordonnances.");
        }
    }
    
    private void prendreRendezVous() {
        showAlert(Alert.AlertType.INFORMATION, "Information", 
            "Fonctionnalité 'Prendre un RDV' à implémenter.\n" +
            "Cette fonctionnalité permettra de sélectionner un médecin et une date pour planifier un rendez-vous.");
    }
    
    private void contacterCabinet() {
        showAlert(Alert.AlertType.INFORMATION, "Contacter le cabinet", 
            "📞 Téléphone: 01 23 45 67 89\n" +
            "📧 Email: contact@cabinetmedical.com\n" +
            "🏥 Adresse: 123 Rue de la Santé, 75000 Paris\n" +
            "⏰ Horaires: Lundi-Vendredi 8h-19h, Samedi 9h-13h");
    }
    
    private void handleDeconnexion() {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Déconnexion");
        confirmation.setHeaderText("Êtes-vous sûr de vouloir vous déconnecter ?");
        
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    // CORRECTION ICI : Utiliser cleanSession() au lieu de logout()
                    SessionManager.getInstance().cleanSession();
                    
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/cabinet/view/login.fxml"));
                    Parent root = loader.load();
                    
                    Stage stage = (Stage) btnDashboard.getScene().getWindow();
                    stage.setScene(new Scene(root));
                    stage.setTitle("Connexion - Cabinet Médical");
                    
                } catch (Exception e) {
                    e.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la déconnexion.");
                }
            }
        });
    }
    
    @FXML
    private void handleRefresh() {
        loadPatientStatistics();
    }
    
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}