 package com.cabinet.controller;

import com.cabinet.dao.UserDAO;
import com.cabinet.dao.PatientDAO;
import com.cabinet.dao.MedecinDAO;
import com.cabinet.model.Utilisateur;
import com.cabinet.model.Patient;
import com.cabinet.model.Medecin;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;

public class LoginController {

    @FXML private TextField loginField;
    @FXML private PasswordField passwordField;
    
    private UserDAO userDAO = new UserDAO();
    private PatientDAO patientDAO = new PatientDAO();
    private MedecinDAO medecinDAO = new MedecinDAO();

    @FXML
    public void initialize() {
        loginField.requestFocus();
        passwordField.setOnAction(this::handleLogin);
    }

    @FXML
    public void handleLogin(ActionEvent event) {
        String login = loginField.getText().trim();
        String password = passwordField.getText();

        if (login.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez entrer votre identifiant et mot de passe.");
            return;
        }

        try {
            Utilisateur user = userDAO.getUserByLogin(login);
            
            if (user == null) {
                showAlert(Alert.AlertType.ERROR, "Échec de connexion", "Identifiant non trouvé.");
                return;
            }

            boolean passwordMatch = user.getMot_de_passe().equals(password);
            
            if (!passwordMatch) {
                showAlert(Alert.AlertType.ERROR, "Échec de connexion", "Mot de passe incorrect.");
                return;
            }

            SessionManager session = SessionManager.getInstance();
            session.setCurrentUser(user);
            System.out.println("Utilisateur connecté: " + user.getLogin() + " (" + user.getRole() + ")");

            String role = user.getRole();
            
            if ("PATIENT".equals(role)) {
                loadPatientData(user.getId_utilisateur());
            } else if ("MEDECIN".equals(role)) {
                loadMedecinData(user.getId_utilisateur());
            } else if ("ADMIN".equals(role)) {
                System.out.println("Connexion ADMIN réussie");
                showAlert(Alert.AlertType.INFORMATION, "Bienvenue", 
                         "Bienvenue Administrateur!");
            } else {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Rôle utilisateur inconnu.");
                session.cleanSession();
                return;
            }

            redirectToMainInterface(event);

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur Système", 
                     "Une erreur est survenue: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadPatientData(int userId) {
        try {
            Patient patient = patientDAO.getPatientByUserId(userId);
            if (patient != null) {
                SessionManager.getInstance().setCurrentPatient(patient);
                System.out.println("Patient chargé: " + patient.getNom() + " " + patient.getPrenom());
                
                showAlert(Alert.AlertType.INFORMATION, "Bienvenue", 
                         "Bienvenue " + patient.getPrenom() + " " + patient.getNom() + "!");
            } else {
                showAlert(Alert.AlertType.WARNING, "Information", 
                         "Aucun profil patient trouvé pour ce compte.");
                SessionManager.getInstance().cleanSession();
            }
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement du patient: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Erreur", 
                     "Impossible de charger votre profil.");
        }
    }

    private void loadMedecinData(int userId) {
        try {
            Medecin medecin = medecinDAO.getMedecinByUserId(userId);
            if (medecin != null) {
                SessionManager.getInstance().setCurrentMedecin(medecin);
                System.out.println("Médecin chargé: " + medecin.getNom() + " " + medecin.getPrenom());
                
                showAlert(Alert.AlertType.INFORMATION, "Bienvenue", 
                         "Bienvenue Dr. " + medecin.getNom() + "!");
            } else {
                showAlert(Alert.AlertType.WARNING, "Information", 
                         "Aucun profil médecin trouvé pour ce compte.");
            }
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement du médecin: " + e.getMessage());
        }
    }

    private void redirectToMainInterface(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/cabinet/view/main.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root, 1200, 800);
            
            String role = SessionManager.getInstance().getUserRole();
            String title = "Cabinet Médical";
            if ("ADMIN".equals(role)) {
                title += " - Administration";
            } else if ("MEDECIN".equals(role)) {
                title += " - Espace Médecin";
            } else if ("PATIENT".equals(role)) {
                title += " - Espace Patient";
            }
            
            stage.setTitle(title);
            stage.setScene(scene);
            stage.setMinWidth(1000);
            stage.setMinHeight(700);
            stage.centerOnScreen();
            stage.show();
            
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de Navigation", 
                     "Impossible de charger l'interface principale.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSwitchToSignup(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/cabinet/view/signup.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Inscription Patient - Cabinet Médical");
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger la page d'inscription.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleEnterPressed(ActionEvent event) {
        handleLogin(event);
    }

    @FXML
    private void handleQuickAdminLogin(ActionEvent event) {
        loginField.setText("admin");
        passwordField.setText("adminpasswordhash");
        handleLogin(event);
    }

    @FXML
    private void handleQuickPatientLogin(ActionEvent event) {
        showAlert(Alert.AlertType.INFORMATION, "Patient Test", 
                 "Veuillez utiliser le formulaire d'inscription.");
    }

    @FXML
    private void handleClearForm() {
        loginField.clear();
        passwordField.clear();
        loginField.requestFocus();
    }

    @FXML
    private void handleShowHelp() {
        showAlert(Alert.AlertType.INFORMATION, "Aide - Connexion",
                 "Identifiants de test disponibles:\n\n" +
                 "Administrateur:\n" +
                 "Login: admin\n" +
                 "Mot de passe: adminpasswordhash\n\n" +
                 "Pour créer un compte patient, cliquez sur 'S'inscrire'.");
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}