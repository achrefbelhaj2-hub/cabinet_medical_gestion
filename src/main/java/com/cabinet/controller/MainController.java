package com.cabinet.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import java.io.IOException;

public class MainController {
    
    @FXML private StackPane mainContentArea;
    @FXML private Label userLabel;
    @FXML private Label statusLabel;
    @FXML private VBox adminMenu;
    @FXML private VBox patientMenu;

    @FXML
    public void initialize() {
        updateUserInfo();
        updateMenuVisibility();
        loadInitialView();
    }
    
    private void updateUserInfo() {
        if (SessionManager.getInstance().getCurrentUser() != null) {
            String userName = SessionManager.getInstance().getCurrentUserName();
            String role = SessionManager.getInstance().getUserRole();
            userLabel.setText(userName + " (" + role + ")");
            statusLabel.setText("Connecté");
        }
    }
    
    private void updateMenuVisibility() {
        SessionManager session = SessionManager.getInstance();
        
        if (adminMenu != null) {
            adminMenu.setVisible(session.isAdmin() || session.isMedecin());
            adminMenu.setManaged(session.isAdmin() || session.isMedecin());
        }
        
        if (patientMenu != null) {
            patientMenu.setVisible(session.isPatient());
            patientMenu.setManaged(session.isPatient());
        }
    }
    
    private void loadInitialView() {
        SessionManager session = SessionManager.getInstance();
        
        if (session.isAdmin() || session.isMedecin()) {
            loadView("/com/cabinet/view/admin/dashboard.fxml");
        } else if (session.isPatient()) {
            loadView("/com/cabinet/view/patient/patient_home.fxml");
        } else {
            loadView("/com/cabinet/view/welcome.fxml");
        }
    }
    
    private void loadView(String fxmlPath) {
        try {
            Parent view = FXMLLoader.load(getClass().getResource(fxmlPath));
            mainContentArea.getChildren().setAll(view);
        } catch (IOException e) {
            showAlert("Erreur", "Impossible de charger la vue : " + fxmlPath);
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleLoadAdminDashboard() {
        if (SessionManager.getInstance().isAdmin() || SessionManager.getInstance().isMedecin()) {
            loadView("/com/cabinet/view/admin/dashboard.fxml");
        }
    }
    
    @FXML
    private void handleLoadPatientManagement() {
        if (SessionManager.getInstance().isAdmin()) {
            loadView("/com/cabinet/view/admin/patient.fxml");
        }
    }
    
    @FXML
    private void handleLoadMedecinManagement() {
        if (SessionManager.getInstance().isAdmin()) {
            loadView("/com/cabinet/view/admin/medecin.fxml");
        }
    }
    
    @FXML
    private void handleLoadRendezVous() {
        if (SessionManager.getInstance().isAdmin() || SessionManager.getInstance().isMedecin()) {
            loadView("/com/cabinet/view/admin/rendezvous.fxml");
        }
    }
    
    @FXML
    private void handleLoadOrdonnanceManagement() {
        if (SessionManager.getInstance().isAdmin() || SessionManager.getInstance().isMedecin()) {
            loadView("/com/cabinet/view/admin/ordonnance.fxml");
        }
    }
    
    @FXML
    private void handleLoadPatientHome() {
        if (SessionManager.getInstance().isPatient()) {
            loadView("/com/cabinet/view/patient/patient_home.fxml");
        }
    }
    
    @FXML
    private void handleLoadMyProfile() {
        if (SessionManager.getInstance().isPatient()) {
            loadView("/com/cabinet/view/patient/patient_profile.fxml");
        }
    }
    
    @FXML
    private void handleLoadMyRendezVous() {
        if (SessionManager.getInstance().isPatient()) {
            loadView("/com/cabinet/view/patient/patient_rdv.fxml");
        }
    }
    
    @FXML
    private void handleLoadMyOrdonnances() {
        if (SessionManager.getInstance().isPatient()) {
            loadView("/com/cabinet/view/patient/patient_ordonnance.fxml");
        }
    }
    
    @FXML
    private void handleLogout() {
        SessionManager.getInstance().cleanSession();
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/cabinet/view/login.fxml"));
            mainContentArea.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void clearFields() {
        // Méthode appelée depuis les FXML pour effacer les champs
        System.out.println("Fields cleared - implémenter selon la vue");
    }
    
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}