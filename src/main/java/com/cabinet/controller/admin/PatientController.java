package com.cabinet.controller.admin;

import com.cabinet.dao.PatientDAO;
import com.cabinet.dao.UserDAO;
import com.cabinet.model.Patient;
import com.cabinet.model.Utilisateur;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.sql.Date;
import java.time.LocalDate;
import java.util.UUID;

public class PatientController {

    @FXML private TextField nomField;
    @FXML private TextField prenomField;
    @FXML private DatePicker dateNaissancePicker;
    @FXML private TextField adresseField;
    @FXML private TextField telephoneField;
    @FXML private TextField dossierMedicalField;
    @FXML private TextField mutuelleField;
    @FXML private TextField searchField;
    @FXML private Label countLabel;
    // NOUVELLE AJOUT: Référence au bouton Fermer
    @FXML private Button btnFermer;

    @FXML private TableView<Patient> patientTable;
    @FXML private TableColumn<Patient, Integer> idColumn;
    @FXML private TableColumn<Patient, String> nomColumn;
    @FXML private TableColumn<Patient, String> prenomColumn;
    @FXML private TableColumn<Patient, Date> dateNaissanceColumn;
    @FXML private TableColumn<Patient, String> telephoneColumn;
    @FXML private TableColumn<Patient, String> mutuelleColumn;

    private PatientDAO patientDAO = new PatientDAO();
    private UserDAO userDAO = new UserDAO();
    private ObservableList<Patient> patientList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Liaison des colonnes
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id_personne"));
        nomColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        prenomColumn.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        dateNaissanceColumn.setCellValueFactory(new PropertyValueFactory<>("date_naissance"));
        telephoneColumn.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        mutuelleColumn.setCellValueFactory(new PropertyValueFactory<>("mutuelle"));

        loadPatientData();

        patientTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                showPatientDetails(newSelection);
            }
        });
    }

    private void loadPatientData() {
        try {
            patientList.setAll(patientDAO.getAllPatients());
            patientTable.setItems(patientList);
            updateCountLabel();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger la liste des patients.");
            e.printStackTrace();
        }
    }
    
    private void updateCountLabel() {
        int count = patientList.size();
        countLabel.setText(count + " patient" + (count > 1 ? "s" : ""));
    }
    
    private void showPatientDetails(Patient patient) {
        nomField.setText(patient.getNom());
        prenomField.setText(patient.getPrenom());
        if (patient.getDate_naissance() != null) {
            dateNaissancePicker.setValue(patient.getDate_naissance().toLocalDate());
        } else {
            dateNaissancePicker.setValue(null);
        }
        adresseField.setText(patient.getAdresse());
        telephoneField.setText(patient.getTelephone());
        dossierMedicalField.setText(patient.getDossier_medical());
        mutuelleField.setText(patient.getMutuelle());
    }
    
    // NOUVELLE MÉTHODE: Gère l'action du bouton Fermer
    @FXML
    private void handleFermer() {
        // Ferme la fenêtre (Stage) qui contient ce bouton.
        if (btnFermer != null) {
            btnFermer.getScene().getWindow().hide();
        }
    }

    @FXML
    private void handleAddPatient() {
        if (!validateInput()) return;
        
        try {
            // 1. Générer un login unique
            String login = generateUniqueLogin(
                nomField.getText().trim(), 
                prenomField.getText().trim()
            );
            
            // 2. Générer un mot de passe temporaire
            String tempPassword = generateTemporaryPassword();
            
            // 3. Créer l'utilisateur dans la table utilisateur
            Utilisateur nouvelUtilisateur = new Utilisateur(
                login,
                tempPassword, // Dans un vrai système, il faudrait hacher ce mot de passe
                "PATIENT"
            );
            
            int idUserGenerated = userDAO.addUser(nouvelUtilisateur);
            
            if (idUserGenerated <= 0) {
                showAlert(Alert.AlertType.ERROR, "Erreur", 
                    "Échec de la création du compte utilisateur.");
                return;
            }
            
            // 4. Créer le patient avec l'ID utilisateur
            Patient patient = new Patient(
                0, // ID auto-généré
                nomField.getText().trim(),
                prenomField.getText().trim(),
                dateNaissancePicker.getValue() != null ? 
                    Date.valueOf(dateNaissancePicker.getValue()) : null,
                adresseField.getText().trim(),
                telephoneField.getText().trim(),
                dossierMedicalField.getText().trim(),
                mutuelleField.getText().trim(),
                idUserGenerated // ID de l'utilisateur créé
            );
            
            int idPatientGenerated = patientDAO.addPatient(patient);
            
            if (idPatientGenerated > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Succès", 
                    "✅ Patient ajouté avec succès !\n" +
                    "─────────────────────────────\n" +
                    "📋 Informations du patient:\n" +
                    "   ID Patient: " + idPatientGenerated + "\n" +
                    "   Nom: " + patient.getNom() + " " + patient.getPrenom() + "\n" +
                    "   Téléphone: " + patient.getTelephone() + "\n\n" +
                    "🔐 Compte utilisateur créé:\n" +
                    "   Login: " + login + "\n" +
                    "   Mot de passe temporaire: " + tempPassword + "\n" +
                    "   Rôle: PATIENT\n\n" +
                    "⚠️ IMPORTANT: Le patient doit changer son mot de passe à la première connexion !");
                
                loadPatientData();
                clearFields();
            } else {
                // Si échec de création du patient, supprimer l'utilisateur créé
                userDAO.deleteUser(idUserGenerated);
                showAlert(Alert.AlertType.ERROR, "Erreur", 
                    "Échec de l'ajout du patient. L'utilisateur créé a été supprimé.");
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", 
                "Erreur lors de l'ajout du patient: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleUpdatePatient() {
        Patient selectedPatient = patientTable.getSelectionModel().getSelectedItem();
        if (selectedPatient != null && validateInput()) {
            // Mettre à jour les informations du patient
            selectedPatient.setNom(nomField.getText().trim());
            selectedPatient.setPrenom(prenomField.getText().trim());
            selectedPatient.setDate_naissance(
                dateNaissancePicker.getValue() != null ? 
                    Date.valueOf(dateNaissancePicker.getValue()) : null
            );
            selectedPatient.setAdresse(adresseField.getText().trim());
            selectedPatient.setTelephone(telephoneField.getText().trim());
            selectedPatient.setDossier_medical(dossierMedicalField.getText().trim());
            selectedPatient.setMutuelle(mutuelleField.getText().trim());

            try {
                if (patientDAO.updatePatient(selectedPatient)) {
                    showAlert(Alert.AlertType.INFORMATION, "Succès", "Patient mis à jour avec succès.");
                    loadPatientData();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Échec de la mise à jour du patient.");
                }
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la mise à jour: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Attention", 
                "Veuillez sélectionner un patient à modifier et vérifier les champs.");
        }
    }
    
    @FXML
    private void handleDeletePatient() {
        Patient selectedPatient = patientTable.getSelectionModel().getSelectedItem();
        if (selectedPatient != null) {
            // Vérifier si le patient a un utilisateur associé
            boolean hasUserAccount = selectedPatient.getId_utilisateur() > 0;
            
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirmation de suppression");
            confirm.setHeaderText("Supprimer le patient");
            
            if (hasUserAccount) {
                confirm.setContentText("Êtes-vous sûr de vouloir supprimer " + 
                    selectedPatient.getNom() + " " + selectedPatient.getPrenom() + 
                    " ?\n\n⚠️ Le compte utilisateur associé sera également supprimé !");
            } else {
                confirm.setContentText("Êtes-vous sûr de vouloir supprimer " + 
                    selectedPatient.getNom() + " " + selectedPatient.getPrenom() + " ?");
            }
            
            if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                try {
                    // 1. Supprimer d'abord le patient
                    boolean patientDeleted = patientDAO.deletePatient(selectedPatient.getId_personne());
                    
                    if (patientDeleted && hasUserAccount) {
                        // 2. Supprimer l'utilisateur associé
                        boolean userDeleted = userDAO.deleteUser(selectedPatient.getId_utilisateur());
                        
                        if (userDeleted) {
                            showAlert(Alert.AlertType.INFORMATION, "Succès", 
                                "Patient et compte utilisateur supprimés avec succès.");
                        } else {
                            showAlert(Alert.AlertType.WARNING, "Avertissement", 
                                "Patient supprimé mais échec de la suppression du compte utilisateur.");
                        }
                    } else if (patientDeleted) {
                        showAlert(Alert.AlertType.INFORMATION, "Succès", "Patient supprimé avec succès.");
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Erreur", "Échec de la suppression du patient.");
                    }
                    
                    loadPatientData();
                    clearFields();
                    
                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "Erreur de suppression", 
                        "Impossible de supprimer le patient: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Attention", "Veuillez sélectionner un patient à supprimer.");
        }
    }
    
    @FXML
    private void handleSearchPatient() {
        String searchText = searchField.getText().trim();
        if (!searchText.isEmpty()) {
            try {
                patientList.setAll(patientDAO.searchPatientsByName(searchText));
                patientTable.setItems(patientList);
                updateCountLabel();
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la recherche: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            loadPatientData();
        }
    }
    
    @FXML
    private void handleRefresh() {
        loadPatientData();
        searchField.clear();
    }
    
    // Méthodes utilitaires
    private String generateUniqueLogin(String nom, String prenom) {
        String baseLogin = (prenom.charAt(0) + nom).toLowerCase()
            .replaceAll("[^a-z0-9]", ""); // Supprimer les caractères spéciaux
        
        // Vérifier si ce login existe déjà
        Utilisateur existingUser = userDAO.getUserByLogin(baseLogin);
        
        if (existingUser == null) {
            return baseLogin;
        } else {
            // Ajouter un numéro si le login existe déjà
            int counter = 1;
            String newLogin;
            do {
                newLogin = baseLogin + counter;
                existingUser = userDAO.getUserByLogin(newLogin);
                counter++;
            } while (existingUser != null && counter < 100);
            
            return newLogin;
        }
    }
    
    private String generateTemporaryPassword() {
        // Générer un mot de passe temporaire aléatoire
        return UUID.randomUUID().toString().substring(0, 8);
    }
    
    private boolean validateInput() { 
        if (nomField.getText().trim().isEmpty() || prenomField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Nom et prénom sont obligatoires.");
            return false;
        }
        
        String telephone = telephoneField.getText().trim();
        if (!telephone.isEmpty() && !telephone.matches("^[0-9]{8}$")) {
            showAlert(Alert.AlertType.ERROR, "Erreur", 
                "Le numéro de téléphone doit contenir 10 chiffres.");
            return false;
        }
        
        return true; 
    }
    
    @FXML
    private void clearFields() { 
        nomField.clear();
        prenomField.clear();
        dateNaissancePicker.setValue(null);
        adresseField.clear();
        telephoneField.clear();
        dossierMedicalField.clear();
        mutuelleField.clear();
        patientTable.getSelectionModel().clearSelection();
    }
    
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}