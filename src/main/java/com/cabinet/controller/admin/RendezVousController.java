package com.cabinet.controller.admin;

import com.cabinet.dao.RendezVousDAO;
import com.cabinet.dao.PatientDAO;
import com.cabinet.dao.MedecinDAO;
import com.cabinet.model.RendezVous;
import com.cabinet.model.Patient;
import com.cabinet.model.Medecin;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage; // Import nécessaire pour fermer la fenêtre
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Gère la planification, la modification et la consultation des Rendez-vous.
 */
public class RendezVousController {

    // --- Éléments de Saisie ---
    @FXML private DatePicker dateRdvPicker;
    @FXML private TextField heureRdvField;
    @FXML private ComboBox<String> medecinComboBox;
    @FXML private ComboBox<String> patientComboBox;
    @FXML private TextField motifField;
    @FXML private ComboBox<String> statutComboBox;
    @FXML private TextField searchField;
    @FXML private Label countLabel;
    @FXML private Button btnFermer; // Déclaration du bouton Fermer
    

    // --- TableView ---
    @FXML private TableView<RendezVous> rdvTable;
    @FXML private TableColumn<RendezVous, LocalDateTime> dateHeureColumn;
    @FXML private TableColumn<RendezVous, String> motifColumn;
    @FXML private TableColumn<RendezVous, String> statutColumn;
    @FXML private TableColumn<RendezVous, String> patientColumn;
    @FXML private TableColumn<RendezVous, String> medecinColumn;
    
    // --- Outil DAO et Liste Observable ---
    private RendezVousDAO rdvDAO = new RendezVousDAO();
    private PatientDAO patientDAO = new PatientDAO();
    private MedecinDAO medecinDAO = new MedecinDAO();
    private ObservableList<RendezVous> rdvList = FXCollections.observableArrayList();
    private List<Patient> allPatients;
    private List<Medecin> allMedecins;

    @FXML
    public void initialize() {
        // 1. Lier les colonnes
        dateHeureColumn.setCellValueFactory(new PropertyValueFactory<>("date_heure"));
        motifColumn.setCellValueFactory(new PropertyValueFactory<>("motif"));
        statutColumn.setCellValueFactory(new PropertyValueFactory<>("statut"));
        patientColumn.setCellValueFactory(cellData -> {
            int patientId = cellData.getValue().getId_patient();
            return new javafx.beans.property.SimpleStringProperty(getPatientNameById(patientId));
        });
        medecinColumn.setCellValueFactory(cellData -> {
            int medecinId = cellData.getValue().getId_medecin();
            return new javafx.beans.property.SimpleStringProperty(getMedecinNameById(medecinId));
        });
        
        // 2. Initialiser la ComboBox de Statut
        statutComboBox.setItems(FXCollections.observableArrayList("PLANIFIE", "ANNULE", "TERMINE"));

        // 3. Charger les données initiales dans les ComboBox
        loadPatientAndMedecinData();

        // 4. Charger les RDVs
        loadRendezVous();
        
        // 5. Ajouter un écouteur pour la sélection du tableau
        rdvTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                showRendezVousDetails(newSelection);
            } else {
                clearFields();
            }
        });
        
        // 6. Date par défaut = aujourd'hui
        dateRdvPicker.setValue(LocalDate.now());
        
        // 7. Heure par défaut = maintenant + 1 heure
        LocalTime now = LocalTime.now();
        LocalTime defaultTime = now.plusHours(1).withMinute(0);
        heureRdvField.setText(defaultTime.toString().substring(0, 5));
    }
    
    /**
     * Charge les listes de patients et de médecins depuis la base de données.
     */
    private void loadPatientAndMedecinData() {
        try {
            allPatients = patientDAO.getAllPatients();
            List<String> patientNames = allPatients.stream()
                .map(p -> p.getNom() + " " + p.getPrenom())
                .collect(Collectors.toList());
            patientComboBox.setItems(FXCollections.observableArrayList(patientNames));
            
            allMedecins = medecinDAO.getAllMedecins();
            List<String> medecinNames = allMedecins.stream()
                .map(m -> m.getNom() + " " + m.getPrenom())
                .collect(Collectors.toList());
            medecinComboBox.setItems(FXCollections.observableArrayList(medecinNames));

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de Chargement", "Impossible de charger les listes de patients/médecins.");
            e.printStackTrace();
        }
    }

    /**
     * Charge tous les rendez-vous dans la TableView.
     */
    private void loadRendezVous() {
        try {
            List<RendezVous> rdvs = rdvDAO.getAllRendezVous();
            rdvList.setAll(rdvs);
            rdvTable.setItems(rdvList);
            updateCountLabel();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger la liste des rendez-vous.");
            e.printStackTrace();
        }
    }
    
    private void updateCountLabel() {
        int count = rdvList.size();
        countLabel.setText(count + " rendez-vous" + (count > 1 ? "" : ""));
    }
    
    /**
     * Affiche les détails d'un RendezVous sélectionné dans les champs de saisie.
     * @param rdv L'objet RendezVous à afficher.
     */
    private void showRendezVousDetails(RendezVous rdv) {
        if (rdv.getDate_heure() != null) {
            dateRdvPicker.setValue(rdv.getDate_heure().toLocalDate());
            heureRdvField.setText(rdv.getDate_heure().toLocalTime().toString().substring(0, 5)); 
        } else {
            dateRdvPicker.setValue(null);
            heureRdvField.clear();
        }
        
        // Trouver le nom complet du patient
        String patientName = getPatientNameById(rdv.getId_patient());
        patientComboBox.setValue(patientName);

        // Trouver le nom complet du médecin
        String medecinName = getMedecinNameById(rdv.getId_medecin());
        medecinComboBox.setValue(medecinName);

        motifField.setText(rdv.getMotif());
        statutComboBox.setValue(rdv.getStatut());
    }
    
    // -------------------------------------------------------------------------
    // --- Méthodes de Gestion (CRUD) ---
    // -------------------------------------------------------------------------

    @FXML
    private void handlePlanifierRendezVous() {
        if (!validateInput()) {
            return;
        }

        try {
            // 1. Récupération et conversion des données
            LocalDate date = dateRdvPicker.getValue();
            LocalTime time = LocalTime.parse(heureRdvField.getText());
            LocalDateTime dateHeure = LocalDateTime.of(date, time);
            String motif = motifField.getText();
            String statut = statutComboBox.getValue();
            
            // 2. Trouver les IDs Patient et Médecin
            int idPatient = getPatientIdByName(patientComboBox.getValue());
            int idMedecin = getMedecinIdByName(medecinComboBox.getValue());
            
            if (idPatient == -1 || idMedecin == -1) {
                showAlert(Alert.AlertType.ERROR, "Erreur de sélection", "Sélection Patient/Médecin invalide.");
                return;
            }
            
            // 3. Vérifier si le créneau est disponible
            if (!rdvDAO.isCreneauDisponible(idMedecin, dateHeure)) {
                showAlert(Alert.AlertType.WARNING, "Créneau indisponible", 
                    "Le médecin a déjà un rendez-vous à cette heure.\nVeuillez choisir un autre créneau.");
                return;
            }

            // 4. Créer l'objet et insérer
            RendezVous nouveauRdv = new RendezVous(0, dateHeure, motif, statut, idPatient, idMedecin); 
            
            int idGenerated = rdvDAO.addRendezVous(nouveauRdv);
            if (idGenerated > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Succès", 
                    "Rendez-vous planifié avec succès.\nID: " + idGenerated + 
                    "\nDate: " + dateHeure.toLocalDate() + 
                    "\nHeure: " + dateHeure.toLocalTime());
                loadRendezVous(); 
                clearFields(); 
            } else {
                showAlert(Alert.AlertType.ERROR, "Échec", "Erreur lors de la planification du rendez-vous.");
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de Saisie", "Vérifiez le format de l'heure (ex: 14:30).");
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleUpdateRendezVous() {
        RendezVous selectedRdv = rdvTable.getSelectionModel().getSelectedItem();
        if (selectedRdv == null) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Veuillez sélectionner un rendez-vous à modifier.");
            return;
        }
        if (!validateInput()) {
            return;
        }

        try {
            // 1. Récupération et conversion des données
            LocalDate date = dateRdvPicker.getValue();
            LocalTime time = LocalTime.parse(heureRdvField.getText());
            LocalDateTime dateHeure = LocalDateTime.of(date, time);
            String motif = motifField.getText();
            String statut = statutComboBox.getValue();
            
            // 2. Trouver les IDs Patient et Médecin
            int idPatient = getPatientIdByName(patientComboBox.getValue());
            int idMedecin = getMedecinIdByName(medecinComboBox.getValue());
            
            if (idPatient == -1 || idMedecin == -1) {
                showAlert(Alert.AlertType.ERROR, "Erreur de sélection", "Sélection Patient/Médecin invalide.");
                return;
            }
            
            // 3. Vérifier si le créneau est disponible (sauf pour le RDV actuel)
            if (!dateHeure.equals(selectedRdv.getDate_heure()) && 
                !rdvDAO.isCreneauDisponible(idMedecin, dateHeure)) {
                showAlert(Alert.AlertType.WARNING, "Créneau indisponible", 
                    "Le médecin a déjà un rendez-vous à cette heure.\nVeuillez choisir un autre créneau.");
                return;
            }

            // 4. Mettre à jour l'objet sélectionné
            selectedRdv.setDate_heure(dateHeure);
            selectedRdv.setMotif(motif);
            selectedRdv.setStatut(statut);
            selectedRdv.setId_patient(idPatient);
            selectedRdv.setId_medecin(idMedecin);
            
            // 5. Mettre à jour en DB et recharger
            if (rdvDAO.updateRendezVous(selectedRdv)) {
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Rendez-vous mis à jour avec succès.");
                loadRendezVous(); 
                clearFields();
            } else {
                showAlert(Alert.AlertType.ERROR, "Échec", "Erreur lors de la mise à jour du rendez-vous.");
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de Saisie", "Vérifiez le format de l'heure (ex: 14:30).");
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleDeleteRendezVous() {
        RendezVous selectedRdv = rdvTable.getSelectionModel().getSelectedItem();

        if (selectedRdv != null) {
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Confirmation de Suppression");
            confirmation.setHeaderText("Supprimer le rendez-vous ?");
            confirmation.setContentText("Êtes-vous sûr de vouloir supprimer ce rendez-vous ?\n" +
                "Patient: " + getPatientNameById(selectedRdv.getId_patient()) + "\n" +
                "Date: " + selectedRdv.getDate_heure().toLocalDate() + "\n" +
                "Heure: " + selectedRdv.getDate_heure().toLocalTime());

            confirmation.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try {
                        if (rdvDAO.deleteRendezVous(selectedRdv.getId_rdv())) {
                            showAlert(Alert.AlertType.INFORMATION, "Succès", "Rendez-vous supprimé avec succès.");
                            loadRendezVous();
                            clearFields();
                        } else {
                            showAlert(Alert.AlertType.ERROR, "Échec", "Erreur lors de la suppression du rendez-vous.");
                        }
                    } catch (Exception e) {
                        showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la suppression: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            });
        } else {
            showAlert(Alert.AlertType.WARNING, "Attention", "Veuillez sélectionner un rendez-vous à supprimer.");
        }
    }
    
    @FXML
    private void handleSearchRendezVous() {
        String searchText = searchField.getText().trim();
        if (!searchText.isEmpty()) {
            try {
                // Recherche par nom de patient
                List<RendezVous> filteredRdvs = rdvList.stream()
                    .filter(rdv -> {
                        String patientName = getPatientNameById(rdv.getId_patient()).toLowerCase();
                        String medecinName = getMedecinNameById(rdv.getId_medecin()).toLowerCase();
                        String motif = rdv.getMotif().toLowerCase();
                        String searchLower = searchText.toLowerCase();
                        
                        return patientName.contains(searchLower) || 
                               medecinName.contains(searchLower) || 
                               motif.contains(searchLower);
                    })
                    .collect(Collectors.toList());
                
                rdvTable.setItems(FXCollections.observableArrayList(filteredRdvs));
                updateCountLabel();
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la recherche: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            loadRendezVous();
        }
    }
    
    @FXML
    private void handleRefresh() {
        loadRendezVous();
        searchField.clear();
        clearFields();
    }

    /**
     * Méthode pour fermer la fenêtre actuelle, liée au bouton Fermer.
     */
    @FXML
    private void handleFermer() {
        // Obtenir la fenêtre (Stage) à partir du bouton et la fermer
        Stage stage = (Stage) btnFermer.getScene().getWindow();
        stage.close();
    }
    
    // -------------------------------------------------------------------------
    // --- Méthodes utilitaires ---
    // -------------------------------------------------------------------------

    private boolean validateInput() {
        if (dateRdvPicker.getValue() == null ||
            heureRdvField.getText().trim().isEmpty() ||
            patientComboBox.getValue() == null ||
            medecinComboBox.getValue() == null ||
            motifField.getText().trim().isEmpty() ||
            statutComboBox.getValue() == null) {
            
            showAlert(Alert.AlertType.ERROR, "Erreur de Saisie", "Veuillez remplir tous les champs.");
            return false;
        }
        
        // Validation simple du format de l'heure (HH:mm)
        if (!heureRdvField.getText().matches("^([01]?[0-9]|2[0-3]):[0-5][0-9]$")) {
             showAlert(Alert.AlertType.ERROR, "Erreur de Saisie", "Veuillez entrer une heure valide au format HH:mm.");
             return false;
        }
        
        // Vérifier que la date n'est pas dans le passé
        LocalDate selectedDate = dateRdvPicker.getValue();
        if (selectedDate.isBefore(LocalDate.now())) {
            showAlert(Alert.AlertType.WARNING, "Date invalide", 
                "La date sélectionnée est dans le passé.\nVeuillez choisir une date future.");
            return false;
        }
        
        return true;
    }
    
    
    @FXML
    private void clearFields() {
        dateRdvPicker.setValue(LocalDate.now());
        // Réinitialiser l'heure
        LocalTime now = LocalTime.now();
        LocalTime defaultTime = now.plusHours(1).withMinute(0);
        heureRdvField.setText(defaultTime.toString().substring(0, 5));
        patientComboBox.setValue(null);
        medecinComboBox.setValue(null);
        motifField.clear();
        statutComboBox.setValue("PLANIFIE");
        rdvTable.getSelectionModel().clearSelection();
    }
    
    /**
     * Recherche l'ID du Patient par son Nom et Prénom complets.
     * @param fullName Nom et Prénom.
     * @return L'ID du patient ou -1 si non trouvé.
     */
    private int getPatientIdByName(String fullName) {
        if (fullName == null || allPatients == null) return -1;
        return allPatients.stream()
            .filter(p -> (p.getNom() + " " + p.getPrenom()).equals(fullName))
            .map(Patient::getId_personne)
            .findFirst()
            .orElse(-1);
    }
    
    /**
     * Recherche l'ID du Médecin par son Nom et Prénom complets.
     * @param fullName Nom et Prénom.
     * @return L'ID du médecin ou -1 si non trouvé.
     */
    private int getMedecinIdByName(String fullName) {
        if (fullName == null || allMedecins == null) return -1;
        return allMedecins.stream()
            .filter(m -> (m.getNom() + " " + m.getPrenom()).equals(fullName))
            .map(Medecin::getId_personne)
            .findFirst()
            .orElse(-1);
    }
    
    private String getPatientNameById(int id) {
        if (allPatients == null) return "Patient inconnu";
        return allPatients.stream()
            .filter(p -> p.getId_personne() == id)
            .map(p -> p.getNom() + " " + p.getPrenom())
            .findFirst()
            .orElse("Patient inconnu");
    }
    
    private String getMedecinNameById(int id) {
        if (allMedecins == null) return "Médecin inconnu";
        return allMedecins.stream()
            .filter(m -> m.getId_personne() == id)
            .map(m -> m.getNom() + " " + m.getPrenom())
            .findFirst()
            .orElse("Médecin inconnu");
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}