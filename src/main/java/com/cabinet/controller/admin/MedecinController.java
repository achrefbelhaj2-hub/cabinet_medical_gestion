package com.cabinet.controller.admin;

import com.cabinet.dao.MedecinDAO;
import com.cabinet.dao.UserDAO;
import com.cabinet.model.Medecin;
import com.cabinet.model.Utilisateur;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.sql.Date;
import java.time.LocalDate;
import java.util.UUID;

public class MedecinController {

    @FXML private TextField nomField;
    @FXML private TextField prenomField;
    @FXML private DatePicker dateNaissancePicker;
    @FXML private TextField adresseField;
    @FXML private TextField telephoneField;
    @FXML private TextField specialiteField;
    @FXML private TextField licenceField;
    @FXML private TextField searchField;
    @FXML private Label countLabel;
    
    // NOUVELLE AJOUT: Référence au bouton Fermer
    @FXML private Button btnFermer; // 

    @FXML private TableView<Medecin> medecinTable; // [cite: 57]
    @FXML private TableColumn<Medecin, Integer> idColumn; // [cite: 58]
    @FXML private TableColumn<Medecin, String> nomColumn; // [cite: 58]
    @FXML private TableColumn<Medecin, String> prenomColumn; // [cite: 58]
    @FXML private TableColumn<Medecin, String> specialiteColumn; // [cite: 59]
    @FXML private TableColumn<Medecin, String> licenceColumn; // [cite: 59]
    @FXML private TableColumn<Medecin, String> telephoneColumn; // [cite: 59]

    private MedecinDAO medecinDAO = new MedecinDAO();
    private UserDAO userDAO = new UserDAO();
    private ObservableList<Medecin> medecinList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Liaison des colonnes [cite: 58, 59]
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id_personne"));
        nomColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        prenomColumn.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        specialiteColumn.setCellValueFactory(new PropertyValueFactory<>("specialite"));
        licenceColumn.setCellValueFactory(new PropertyValueFactory<>("licence"));
        telephoneColumn.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        
        loadMedecinData();

        medecinTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
             if (newSelection != null) {
                showMedecinDetails(newSelection);
            }
        });
    }

    private void loadMedecinData() {
        try {
            medecinList.setAll(medecinDAO.getAllMedecins());
            medecinTable.setItems(medecinList);
            updateCountLabel();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger la liste des médecins.");
            e.printStackTrace();
        }
    }
    
    private void updateCountLabel() {
        int count = medecinList.size();
        countLabel.setText(count + " médecin" + (count > 1 ? "s" : "")); // 
    }
    
    private void showMedecinDetails(Medecin medecin) {
        nomField.setText(medecin.getNom()); // [cite: 63]
        prenomField.setText(medecin.getPrenom()); // [cite: 64]
        if (medecin.getDate_naissance() != null) {
            dateNaissancePicker.setValue(medecin.getDate_naissance().toLocalDate()); // [cite: 65]
        } else {
            dateNaissancePicker.setValue(null);
        }
        adresseField.setText(medecin.getAdresse()); // [cite: 68]
        telephoneField.setText(medecin.getTelephone()); // [cite: 69]
        specialiteField.setText(medecin.getSpecialite()); // [cite: 66]
        licenceField.setText(medecin.getLicence()); // [cite: 67]
    }

    // NOUVELLE MÉTHODE: Gère l'action du bouton Fermer
    /**
     * Ferme la fenêtre (Stage) actuelle lorsque le bouton 'btnFermer' est cliqué.
     */
    @FXML
    private void handleFermer() {
        // Le bouton est lié à fx:id="btnFermer" et onAction="#handleFermer" dans medecin.fxml 
        if (btnFermer != null) {
            btnFermer.getScene().getWindow().hide();
        }
    }

    @FXML
    private void handleAddMedecin() { 
        if (!validateInput()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez remplir tous les champs obligatoires.");
            return;
        }
        
        try {
            // 1. Vérifier si la licence existe déjà
            String licence = licenceField.getText().trim();
            
            // 2. Générer un login unique
            String login = generateDoctorLogin(
                nomField.getText().trim(), 
                prenomField.getText().trim()
            );
            
            // 3. Générer un mot de passe temporaire
            String tempPassword = generateTemporaryPassword();
            
            // 4. Créer l'utilisateur dans la table utilisateur
            Utilisateur nouvelUtilisateur = new Utilisateur(
                login,
                tempPassword, // Dans un vrai système, il faudrait hacher ce mot de passe
                "MEDECIN"
            );
            
            int idUserGenerated = userDAO.addUser(nouvelUtilisateur);
            
            if (idUserGenerated <= 0) {
                showAlert(Alert.AlertType.ERROR, "Erreur", 
                    "Échec de la création du compte utilisateur.");
                return;
            }
            
            // 5. Créer le médecin avec l'ID utilisateur
            Medecin medecin = new Medecin(
                0, // ID auto-généré
                nomField.getText().trim(),
                prenomField.getText().trim(),
                dateNaissancePicker.getValue() != null ? 
                    Date.valueOf(dateNaissancePicker.getValue()) : null,
                adresseField.getText().trim(),
                telephoneField.getText().trim(),
                specialiteField.getText().trim(),
                licence,
                idUserGenerated // ID de l'utilisateur créé
            );
            
            int idMedecinGenerated = medecinDAO.addMedecin(medecin);
            
            if (idMedecinGenerated > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Succès", 
                    "✅ Médecin ajouté avec succès !\n" +
                    "─────────────────────────────\n" +
                    "📋 Informations du médecin:\n" +
                    "   ID Médecin: " + idMedecinGenerated + "\n" +
                    "   Nom: Dr. " + medecin.getNom() + " " + medecin.getPrenom() + "\n" +
                    "   Spécialité: " + medecin.getSpecialite() + "\n" +
                    "   Licence: " + medecin.getLicence() + "\n\n" +
                    "🔐 Compte utilisateur créé:\n" +
                    "   Login: " + login + "\n" +
                    "   Mot de passe temporaire: " + tempPassword + "\n" +
                    "   Rôle: MEDECIN\n\n" +
                    "⚠️ IMPORTANT: Le médecin doit changer son mot de passe à la première connexion !");
                
                loadMedecinData();
                clearFields();
            } else {
                // Si échec de création du médecin, supprimer l'utilisateur créé
                userDAO.deleteUser(idUserGenerated);
                showAlert(Alert.AlertType.ERROR, "Erreur", 
                    "Échec de l'ajout du médecin. L'utilisateur créé a été supprimé.");
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", 
                "Erreur lors de l'ajout du médecin: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleUpdateMedecin() { // [cite: 71]
        Medecin selectedMedecin = medecinTable.getSelectionModel().getSelectedItem();
        if (selectedMedecin != null && validateInput()) {
            // Vérifier la date
            if (dateNaissancePicker.getValue() == null) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "La date de naissance est requise.");
                return;
            }
            
            selectedMedecin.setNom(nomField.getText().trim());
            selectedMedecin.setPrenom(prenomField.getText().trim());
            selectedMedecin.setDate_naissance(Date.valueOf(dateNaissancePicker.getValue()));
            selectedMedecin.setAdresse(adresseField.getText().trim());
            selectedMedecin.setTelephone(telephoneField.getText().trim());
            selectedMedecin.setSpecialite(specialiteField.getText().trim());
            selectedMedecin.setLicence(licenceField.getText().trim());
            
            try {
                if (medecinDAO.updateMedecin(selectedMedecin)) {
                    showAlert(Alert.AlertType.INFORMATION, "Succès", "Médecin mis à jour avec succès.");
                    loadMedecinData();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Échec de la mise à jour du médecin.");
                }
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la mise à jour: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Attention", 
                "Veuillez sélectionner un médecin à modifier et vérifier les champs.");
        }
    }
    
    @FXML
    private void handleDeleteMedecin() { // [cite: 72]
        Medecin selectedMedecin = medecinTable.getSelectionModel().getSelectedItem();
        if (selectedMedecin != null) {
            // Vérifier si le médecin a un utilisateur associé
            boolean hasUserAccount = selectedMedecin.getId_utilisateur() > 0;
            
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Confirmation de Suppression");
            confirmation.setHeaderText("Supprimer le médecin ?");
            
            if (hasUserAccount) {
                confirmation.setContentText("Êtes-vous sûr de vouloir supprimer le Dr. " + 
                    selectedMedecin.getNom() + " " + selectedMedecin.getPrenom() + 
                    " ?\n\n⚠️ Le compte utilisateur associé sera également supprimé !");
            } else {
                confirmation.setContentText("Êtes-vous sûr de vouloir supprimer le Dr. " + 
                    selectedMedecin.getNom() + " " + selectedMedecin.getPrenom() + " ?");
            }
            
            confirmation.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try {
                        // 1. Supprimer d'abord le médecin
                        boolean medecinDeleted = medecinDAO.deleteMedecin(selectedMedecin.getId_personne());
                        
                        if (medecinDeleted && hasUserAccount) {
                            // 2. Supprimer l'utilisateur associé
                            boolean userDeleted = userDAO.deleteUser(selectedMedecin.getId_utilisateur());
                            
                            if (userDeleted) {
                                showAlert(Alert.AlertType.INFORMATION, "Succès", 
                                    "Médecin et compte utilisateur supprimés avec succès.");
                            } else {
                                showAlert(Alert.AlertType.WARNING, "Avertissement", 
                                    "Médecin supprimé mais échec de la suppression du compte utilisateur.");
                            }
                        } else if (medecinDeleted) {
                            showAlert(Alert.AlertType.INFORMATION, "Succès", "Médecin supprimé avec succès.");
                        } else {
                            showAlert(Alert.AlertType.ERROR, "Erreur", "Échec de la suppression du médecin.");
                        }
                        
                        loadMedecinData();
                        clearFields();
                        
                    } catch (Exception e) {
                        showAlert(Alert.AlertType.ERROR, "Erreur", 
                            "Erreur lors de la suppression: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            });
        } else {
            showAlert(Alert.AlertType.WARNING, "Attention", "Veuillez sélectionner un médecin à supprimer.");
        }
    }
    
    @FXML
    private void handleSearchMedecin() {
        String searchText = searchField.getText().trim(); // [cite: 56]
        if (!searchText.isEmpty()) {
            try {
                medecinList.setAll(medecinDAO.searchMedecinsByName(searchText));
                medecinTable.setItems(medecinList);
                updateCountLabel();
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la recherche: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            loadMedecinData();
        }
    }
    
    @FXML
    private void handleRefresh() {
        loadMedecinData();
        searchField.clear();
    }
    
    @FXML
    private void clearFields() { // [cite: 72]
        nomField.clear();
        prenomField.clear();
        dateNaissancePicker.setValue(null);
        adresseField.clear();
        telephoneField.clear();
        specialiteField.clear();
        licenceField.clear();
        medecinTable.getSelectionModel().clearSelection();
    }
    
    // Méthodes utilitaires
    private String generateDoctorLogin(String nom, String prenom) {
        String baseLogin = ("dr." + prenom.charAt(0) + nom).toLowerCase()
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
    
    private boolean validateInput() { // [cite: 74]
        if (nomField.getText().trim().isEmpty() || 
            prenomField.getText().trim().isEmpty() ||
            specialiteField.getText().trim().isEmpty() ||
            licenceField.getText().trim().isEmpty()) {
            return false;
        }
        
        // Validation de la licence (doit contenir uniquement des lettres et chiffres)
        String licence = licenceField.getText().trim();
        if (!licence.matches("[A-Za-z0-9]+")) {
            showAlert(Alert.AlertType.ERROR, "Erreur", 
                "Le numéro de licence doit contenir uniquement des lettres et chiffres.");
            return false;
        }
        String telephone = telephoneField.getText().trim();
        if (!telephone.isEmpty() && !telephone.matches("^[0-9]{8}$")) {
        showAlert(Alert.AlertType.ERROR, "Erreur", 
            "Le numéro de téléphone doit contenir 8 chiffres.");
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