package com.cabinet.controller.admin;

import com.cabinet.dao.OrdonnanceDAO;
import com.cabinet.dao.PatientDAO;
import com.cabinet.dao.MedecinDAO;
import com.cabinet.model.Ordonnance;
import com.cabinet.model.Patient;
import com.cabinet.model.Medecin;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage; 
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class OrdonnanceController {

    @FXML private ComboBox<String> patientComboBox;
    @FXML private ComboBox<String> medecinComboBox;
    @FXML private TextArea contenuTextArea;
    @FXML private DatePicker datePrescriptionPicker;
    @FXML private Button btnFermer; 
    
    @FXML private TableView<Ordonnance> ordonnanceTable;
    @FXML private TableColumn<Ordonnance, Date> dateColumn;
    @FXML private TableColumn<Ordonnance, String> contenuColumn;
    @FXML private TableColumn<Ordonnance, String> patientColumn;
    @FXML private TableColumn<Ordonnance, String> medecinColumn;

    private OrdonnanceDAO ordonnanceDAO;
    private PatientDAO patientDAO;
    private MedecinDAO medecinDAO;
    private ObservableList<Ordonnance> ordonnanceList = FXCollections.observableArrayList();
    private List<Patient> allPatients;
    private List<Medecin> allMedecins;

    @FXML
    public void initialize() {
        try {
            ordonnanceDAO = new OrdonnanceDAO();
            patientDAO = new PatientDAO();
            medecinDAO = new MedecinDAO();
            
            
            dateColumn.setCellValueFactory(new PropertyValueFactory<>("date_prescription"));
            contenuColumn.setCellValueFactory(new PropertyValueFactory<>("contenu_textuel"));
            patientColumn.setCellValueFactory(cellData -> {
                int patientId = cellData.getValue().getId_patient();
                return new javafx.beans.property.SimpleStringProperty(getPatientNameById(patientId));
            });
            medecinColumn.setCellValueFactory(cellData -> {
                int medecinId = cellData.getValue().getId_medecin();
                return new javafx.beans.property.SimpleStringProperty(getMedecinNameById(medecinId));
            });
            
            loadPatientData();
            loadMedecinData();
            
            loadOrdonnanceData();
            
            ordonnanceTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    showOrdonnanceDetails(newSelection);
                }
            });
            
            datePrescriptionPicker.setValue(LocalDate.now());
            
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'initialisation: " + e.getMessage());
        }
    }

    private void loadPatientData() {
        try {
            allPatients = patientDAO.getAllPatients();
            List<String> patientNames = allPatients.stream()
                .map(p -> p.getNom() + " " + p.getPrenom())
                .collect(Collectors.toList());
            patientComboBox.setItems(FXCollections.observableArrayList(patientNames));
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger la liste des patients.");
            e.printStackTrace();
        }
    }
    
    private void loadMedecinData() {
        try {
            allMedecins = medecinDAO.getAllMedecins();
            List<String> medecinNames = allMedecins.stream()
                .map(m -> "Dr. " + m.getNom() + " " + m.getPrenom() + " - " + m.getSpecialite())
                .collect(Collectors.toList());
            medecinComboBox.setItems(FXCollections.observableArrayList(medecinNames));
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger la liste des médecins.");
            e.printStackTrace();
        }
    }
    
    private void loadOrdonnanceData() {
        try {
            ordonnanceList.setAll(ordonnanceDAO.getAllOrdonnances());
            ordonnanceTable.setItems(ordonnanceList);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger la liste des ordonnances.");
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleCreerOrdonnance() {
        if (!validateInput()) {
            return;
        }
        
        try {
            int idPatient = getPatientIdByName(patientComboBox.getValue());
            int idMedecin = getMedecinIdByName(medecinComboBox.getValue());
            
            if (idPatient == -1 || idMedecin == -1) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez sélectionner un patient et un médecin.");
                return;
            }
            
            Date datePrescription = Date.valueOf(datePrescriptionPicker.getValue());
            String contenuTextuel = contenuTextArea.getText().trim();
            
            Ordonnance ordonnance = new Ordonnance(
                0,
                datePrescription,
                contenuTextuel,
                idPatient,
                idMedecin
            );
            
            int idGenerated = ordonnanceDAO.addOrdonnance(ordonnance);
            
            if (idGenerated > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Ordonnance créée avec succès (ID: " + idGenerated + ")");
                loadOrdonnanceData();
                handleClearFields();
            } else {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Échec de la création de l'ordonnance.");
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la création de l'ordonnance: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleClearFields() {
        patientComboBox.setValue(null);
        medecinComboBox.setValue(null);
        contenuTextArea.clear();
        datePrescriptionPicker.setValue(LocalDate.now());
        ordonnanceTable.getSelectionModel().clearSelection();
    }
    
    @FXML
    private void handleFermer() {
        Stage stage = (Stage) btnFermer.getScene().getWindow();
        stage.close();
    }
    
    private void showOrdonnanceDetails(Ordonnance ordonnance) {
        String patientName = getPatientNameById(ordonnance.getId_patient());
        patientComboBox.setValue(patientName);
        
        String medecinName = getMedecinNameById(ordonnance.getId_medecin());
        medecinComboBox.setValue(medecinName);
        
        if (ordonnance.getDate_prescription() != null) {
            datePrescriptionPicker.setValue(ordonnance.getDate_prescription().toLocalDate());
        }
        contenuTextArea.setText(ordonnance.getContenu_textuel());
    }
    
    private boolean validateInput() {
        if (patientComboBox.getValue() == null || 
            medecinComboBox.getValue() == null ||
            datePrescriptionPicker.getValue() == null ||
            contenuTextArea.getText().trim().isEmpty()) {
            
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez remplir tous les champs.");
            return false;
        }
        return true;
    }
    
    private int getPatientIdByName(String fullName) {
        if (fullName == null || allPatients == null) return -1;
        return allPatients.stream()
            .filter(p -> (p.getNom() + " " + p.getPrenom()).equals(fullName))
            .map(Patient::getId_personne)
            .findFirst()
            .orElse(-1);
    }
    
    private int getMedecinIdByName(String fullName) {
        if (fullName == null || allMedecins == null) return -1;
        // Format: "Dr. Nom Prenom - Spécialité"
        String namePart = fullName.substring(4, fullName.indexOf(" - "));
        return allMedecins.stream()
            .filter(m -> (m.getNom() + " " + m.getPrenom()).equals(namePart))
            .map(Medecin::getId_personne)
            .findFirst()
            .orElse(-1);
    }
    
    private String getPatientNameById(int id) {
        if (allPatients == null) return "";
        return allPatients.stream()
            .filter(p -> p.getId_personne() == id)
            .map(p -> p.getNom() + " " + p.getPrenom())
            .findFirst()
            .orElse("Patient inconnu");
    }
    
    private String getMedecinNameById(int id) {
        if (allMedecins == null) return "";
        return allMedecins.stream()
            .filter(m -> m.getId_personne() == id)
            .map(m -> "Dr. " + m.getNom() + " " + m.getPrenom() + " - " + m.getSpecialite())
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
    
    @FXML
    private void handleSupprimerOrdonnance() {
        Ordonnance selected = ordonnanceTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Avertissement", "Veuillez sélectionner une ordonnance à supprimer.");
            return;
        }
        
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Supprimer l'ordonnance");
        confirm.setContentText("Êtes-vous sûr de vouloir supprimer cette ordonnance ?");
        
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                if (ordonnanceDAO.deleteOrdonnance(selected.getId_ordonnance())) {
                    showAlert(Alert.AlertType.INFORMATION, "Succès", "Ordonnance supprimée avec succès.");
                    loadOrdonnanceData();
                    handleClearFields();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Échec de la suppression de l'ordonnance.");
                }
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la suppression: " + e.getMessage());
            }
        }
    }
}