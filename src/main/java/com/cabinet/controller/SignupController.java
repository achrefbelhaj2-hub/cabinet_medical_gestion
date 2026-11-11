package com.cabinet.controller;

import com.cabinet.dao.UserDAO;
import com.cabinet.dao.PatientDAO;
import com.cabinet.model.Utilisateur;
import com.cabinet.model.Patient;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;

public class SignupController {

    @FXML private TextField loginField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private TextField nomField;
    @FXML private TextField prenomField;
    @FXML private DatePicker dateNaissancePicker;
    @FXML private TextField adresseField;
    @FXML private TextField telephoneField;
    @FXML private TextField mutuelleField;

    private UserDAO userDAO = new UserDAO();
    private PatientDAO patientDAO = new PatientDAO();

    @FXML
    public void handleSignup(ActionEvent event) {
        if (!validateInput()) {
            return;
        }

        String login = loginField.getText().trim();
        String password = passwordField.getText();
        String nom = nomField.getText().trim();
        String prenom = prenomField.getText().trim();
        Date dateNaissance = Date.valueOf(dateNaissancePicker.getValue());
        String adresse = adresseField.getText().trim();
        String telephone = telephoneField.getText().trim();
        String mutuelle = mutuelleField.getText().trim();

        try {
            if (userDAO.getUserByLogin(login) != null) {
                showAlert(Alert.AlertType.ERROR, "Erreur d'Inscription", "Ce nom d'utilisateur est déjà pris.");
                return;
            }

            Utilisateur newUser = new Utilisateur(login, password, "PATIENT");

            int newUserId = userDAO.addUser(newUser);
            if (newUserId <= 0) {
                showAlert(Alert.AlertType.ERROR, "Erreur de base de données", "Échec de la création du compte utilisateur.");
                return;
            }

            Patient newPatient = new Patient(
                0,
                nom,
                prenom,
                dateNaissance,
                adresse,
                telephone,
                "",
                mutuelle,
                newUserId
            );

            int newPatientId = patientDAO.addPatient(newPatient);
            if (newPatientId > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Succès", 
                         "Votre compte Patient a été créé avec succès!\n\n" +
                         "Login: " + login + "\n" +
                         "Vous pouvez maintenant vous connecter.");
                switchToLogin(event);
            } else {
                userDAO.deleteUser(newUserId);
                showAlert(Alert.AlertType.ERROR, "Erreur de base de données", 
                         "Échec de l'enregistrement du profil patient.");
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur Système", 
                     "Une erreur inattendue est survenue: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSwitchToLogin(ActionEvent event) {
        switchToLogin(event);
    }

    private void switchToLogin(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/cabinet/view/login.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de Navigation", "Impossible de charger la vue de connexion.");
            e.printStackTrace();
        }
    }

    private boolean validateInput() {
        if (loginField.getText().trim().isEmpty() || passwordField.getText().trim().isEmpty() ||
            confirmPasswordField.getText().trim().isEmpty() || nomField.getText().trim().isEmpty() ||
            prenomField.getText().trim().isEmpty() || dateNaissancePicker.getValue() == null) {

            showAlert(Alert.AlertType.ERROR, "Erreur de Saisie", "Veuillez remplir tous les champs obligatoires.");
            return false;
        }

        if (!passwordField.getText().equals(confirmPasswordField.getText())) {
            showAlert(Alert.AlertType.ERROR, "Erreur de Saisie", "Les mots de passe ne correspondent pas.");
            return false;
        }

        if (dateNaissancePicker.getValue().isAfter(LocalDate.now())) {
            showAlert(Alert.AlertType.ERROR, "Erreur de Saisie", "La date de naissance est invalide.");
            return false;
        }

        return true;
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}