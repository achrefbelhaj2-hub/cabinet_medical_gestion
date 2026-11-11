package com.cabinet.controller.patient;

import com.cabinet.controller.SessionManager;
import com.cabinet.dao.OrdonnanceDAO;
import com.cabinet.dao.MedecinDAO;
import com.cabinet.model.Patient;
import com.cabinet.model.Ordonnance;
import com.cabinet.model.Medecin;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Button;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class PatientOrdonnanceController {

    @FXML private TableView<Ordonnance> ordonnanceTable;
    @FXML private TableColumn<Ordonnance, Date> dateColumn;
    @FXML private TableColumn<Ordonnance, String> contenuColumn;
    @FXML private TableColumn<Ordonnance, String> medecinColumn;
    
    @FXML private Label detailTitreLabel;
    @FXML private Label detailInfoLabel;
    @FXML private Label detailStatutLabel;
    @FXML private Label detailMedecinLabel;
    @FXML private Label detailSpecialiteLabel;
    @FXML private TextArea detailContenuText;
    @FXML private TextArea detailInstructionsText;
    
    @FXML private ComboBox<String> periodeComboBox;
    @FXML private ComboBox<String> medecinComboBox;
    @FXML private Button btnActualiser;
    @FXML private Button btnDownloadPDF;
    @FXML private Button btnPrint;
    
    private OrdonnanceDAO ordonnanceDAO = new OrdonnanceDAO();
    private MedecinDAO medecinDAO = new MedecinDAO();
    private ObservableList<Ordonnance> ordonnanceList = FXCollections.observableArrayList();
    private Map<Integer, Medecin> medecinsMap = new HashMap<>();
    private Patient patient;

    @FXML
    public void initialize() {
        patient = SessionManager.getInstance().getCurrentPatient();
        if (patient == null) {
            showAlert(Alert.AlertType.WARNING, "Session", "Veuillez vous reconnecter.");
            return;
        }
        
        // Initialiser les colonnes du tableau
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date_prescription"));
        contenuColumn.setCellValueFactory(new PropertyValueFactory<>("contenu_textuel"));
        medecinColumn.setCellValueFactory(cellData -> {
            int medecinId = cellData.getValue().getId_medecin();
            Medecin medecin = medecinsMap.get(medecinId);
            if (medecin != null) {
                return new javafx.beans.property.SimpleStringProperty(
                    "Dr. " + medecin.getNom() + " " + medecin.getPrenom()
                );
            }
            return new javafx.beans.property.SimpleStringProperty("Médecin inconnu");
        });
        
        // Initialiser les ComboBox
        initComboBoxes();
        
        // Charger les médecins
        loadMedecins();
        
        // Charger les ordonnances
        loadOrdonnances();
        
        // Écouteur pour la sélection dans le tableau
        ordonnanceTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                showOrdonnanceDetails(newSelection);
            } else {
                clearDetails();
            }
        });
        
        // Configurer les actions des boutons
        setupButtonActions();
    }

    private void initComboBoxes() {
        // Initialiser la ComboBox des périodes
        periodeComboBox.getItems().addAll(
            "Toutes les périodes",
            "Dernier mois",
            "3 derniers mois", 
            "6 derniers mois",
            "Cette année"
        );
        periodeComboBox.setValue("Toutes les périodes");
        
        // Initialiser la ComboBox des médecins
        medecinComboBox.getItems().add("Tous les médecins");
        medecinComboBox.setValue("Tous les médecins");
        
        // Écouteurs pour les filtres
        periodeComboBox.setOnAction(event -> handleFilterByPeriode());
        medecinComboBox.setOnAction(event -> handleFilterByMedecin());
    }

    private void loadMedecins() {
        try {
            List<Medecin> medecins = medecinDAO.getAllMedecins();
            for (Medecin medecin : medecins) {
                medecinsMap.put(medecin.getId_personne(), medecin);
                medecinComboBox.getItems().add("Dr. " + medecin.getNom() + " " + medecin.getPrenom());
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger la liste des médecins.");
            e.printStackTrace();
        }
    }

    private void loadOrdonnances() {
        try {
            // Récupérer toutes les Ordonnances de ce Patient
            ordonnanceList.setAll(ordonnanceDAO.getOrdonnancesByPatientId(patient.getId_personne()));
            ordonnanceTable.setItems(ordonnanceList);
            
            // Mettre à jour le titre
            detailInfoLabel.setText(ordonnanceList.size() + " ordonnance(s) trouvée(s)");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de chargement", "Impossible de charger vos ordonnances.");
            e.printStackTrace();
        }
    }
    
    private void setupButtonActions() {
        btnActualiser.setOnAction(event -> handleRefresh());
        btnDownloadPDF.setOnAction(event -> handleDownloadPDF());
        btnPrint.setOnAction(event -> handlePrint());
    }
    
    @FXML
    private void handleRefresh() {
        loadOrdonnances();
        periodeComboBox.setValue("Toutes les périodes");
        medecinComboBox.setValue("Tous les médecins");
        clearDetails();
    }
    
    private void showOrdonnanceDetails(Ordonnance ordonnance) {
        if (ordonnance != null) {
            // Récupérer les infos du médecin
            Medecin medecin = medecinsMap.get(ordonnance.getId_medecin());
            String medecinNom = medecin != null ? 
                "Dr. " + medecin.getNom() + " " + medecin.getPrenom() : 
                "Médecin inconnu";
            String specialite = medecin != null ? medecin.getSpecialite() : "Spécialité inconnue";
            
            // Formater la date
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String dateFormatee = ordonnance.getDate_prescription().toLocalDate().format(dateFormatter);
            
            // Mettre à jour les détails
            detailTitreLabel.setText("Ordonnance #" + ordonnance.getId_ordonnance());
            detailInfoLabel.setText("Prescrite le " + dateFormatee);
            detailContenuText.setText(ordonnance.getContenu_textuel());
            detailMedecinLabel.setText(medecinNom);
            detailSpecialiteLabel.setText(specialite);
            
            // Ajouter des instructions génériques
            detailInstructionsText.setText(
                "Instructions générales:\n" +
                "1. Respectez scrupuleusement la posologie indiquée\n" +
                "2. Conservez l'ordonnance pendant toute la durée du traitement\n" +
                "3. Ne partagez pas vos médicaments\n" +
                "4. Consultez votre médecin en cas d'effets secondaires\n" +
                "5. Conservez les médicaments hors de portée des enfants"
            );
            
            // Vérifier si l'ordonnance est encore valide (3 mois)
            LocalDate datePrescription = ordonnance.getDate_prescription().toLocalDate();
            LocalDate dateExpiration = datePrescription.plusMonths(3);
            LocalDate aujourdhui = LocalDate.now();
            
            if (aujourdhui.isBefore(dateExpiration)) {
                long joursRestants = java.time.temporal.ChronoUnit.DAYS.between(aujourdhui, dateExpiration);
                detailStatutLabel.setText("ACTIVE (" + joursRestants + " jours restants)");
                detailStatutLabel.setStyle("-fx-background-color: #d5f4e6; -fx-text-fill: #27ae60; -fx-padding: 5 15; -fx-border-radius: 15; -fx-background-radius: 15; -fx-font-weight: bold;");
            } else {
                detailStatutLabel.setText("EXPIRÉE");
                detailStatutLabel.setStyle("-fx-background-color: #fadbd8; -fx-text-fill: #e74c3c; -fx-padding: 5 15; -fx-border-radius: 15; -fx-background-radius: 15; -fx-font-weight: bold;");
            }
        }
    }
    
    private void clearDetails() {
        detailTitreLabel.setText("Ordonnance");
        detailInfoLabel.setText("Sélectionnez une ordonnance pour voir les détails");
        detailContenuText.clear();
        detailInstructionsText.clear();
        detailMedecinLabel.setText("-");
        detailSpecialiteLabel.setText("-");
        detailStatutLabel.setText("-");
        detailStatutLabel.setStyle("");
    }
    
    @FXML
    private void handleDownloadPDF() {
        Ordonnance selectedOrdonnance = ordonnanceTable.getSelectionModel().getSelectedItem();
        if (selectedOrdonnance != null) {
            // Simuler le téléchargement
            showAlert(Alert.AlertType.INFORMATION, "Téléchargement PDF", 
                "✅ Téléchargement de l'ordonnance #" + selectedOrdonnance.getId_ordonnance() + 
                "\n\nLe fichier PDF sera sauvegardé dans votre dossier 'Téléchargements'.");
            
            // Ici, vous pourriez implémenter la génération de PDF réelle
            // Exemple: PDFGenerator.generateOrdonnancePDF(selectedOrdonnance);
        } else {
            showAlert(Alert.AlertType.WARNING, "Sélection requise", 
                "⚠️ Veuillez sélectionner une ordonnance à télécharger.");
        }
    }
    
    @FXML
    private void handlePrint() {
        Ordonnance selectedOrdonnance = ordonnanceTable.getSelectionModel().getSelectedItem();
        if (selectedOrdonnance != null) {
            // Simuler l'impression
            showAlert(Alert.AlertType.INFORMATION, "Impression", 
                "🖨️ Impression de l'ordonnance #" + selectedOrdonnance.getId_ordonnance() + 
                "\n\nVeuillez vérifier que votre imprimante est connectée et prête.");
            
            // Ici, vous pourriez implémenter l'impression réelle
            // Exemple: PrinterJob job = PrinterJob.createPrinterJob();
        } else {
            showAlert(Alert.AlertType.WARNING, "Sélection requise", 
                "⚠️ Veuillez sélectionner une ordonnance à imprimer.");
        }
    }
    
    @FXML
    private void handleFilterByPeriode() {
        String periode = periodeComboBox.getValue();
        if (periode != null && !periode.equals("Toutes les périodes")) {
            // Implémentation basique du filtrage
            LocalDate cutoffDate = LocalDate.now();
            switch (periode) {
                case "Dernier mois":
                    cutoffDate = cutoffDate.minusMonths(1);
                    break;
                case "3 derniers mois":
                    cutoffDate = cutoffDate.minusMonths(3);
                    break;
                case "6 derniers mois":
                    cutoffDate = cutoffDate.minusMonths(6);
                    break;
                case "Cette année":
                    cutoffDate = LocalDate.of(LocalDate.now().getYear(), 1, 1);
                    break;
            }
            
            final LocalDate finalCutoffDate = cutoffDate;
            List<Ordonnance> filtered = ordonnanceList.stream()
                .filter(o -> o.getDate_prescription().toLocalDate().isAfter(finalCutoffDate))
                .toList();
            
            ordonnanceTable.setItems(FXCollections.observableArrayList(filtered));
            detailInfoLabel.setText(filtered.size() + " ordonnance(s) pour la période: " + periode);
        } else {
            ordonnanceTable.setItems(ordonnanceList);
            detailInfoLabel.setText(ordonnanceList.size() + " ordonnance(s) trouvée(s)");
        }
    }
    
    @FXML
    private void handleFilterByMedecin() {
        String medecinSelection = medecinComboBox.getValue();
        if (medecinSelection != null && !medecinSelection.equals("Tous les médecins")) {
            // Extraire l'ID du médecin depuis la sélection
            String nomMedecin = medecinSelection.replace("Dr. ", "");
            for (Medecin medecin : medecinsMap.values()) {
                String fullName = medecin.getNom() + " " + medecin.getPrenom();
                if (fullName.equals(nomMedecin)) {
                    // Filtrer par ce médecin
                    List<Ordonnance> filtered = ordonnanceList.stream()
                        .filter(o -> o.getId_medecin() == medecin.getId_personne())
                        .toList();
                    
                    ordonnanceTable.setItems(FXCollections.observableArrayList(filtered));
                    detailInfoLabel.setText(filtered.size() + " ordonnance(s) pour " + medecinSelection);
                    return;
                }
            }
        } else {
            ordonnanceTable.setItems(ordonnanceList);
            detailInfoLabel.setText(ordonnanceList.size() + " ordonnance(s) trouvée(s)");
        }
    }
    
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}