package com.cabinet.controller.patient;

import com.cabinet.controller.SessionManager;
import com.cabinet.dao.RendezVousDAO;
import com.cabinet.dao.MedecinDAO;
import com.cabinet.model.Patient;
import com.cabinet.model.RendezVous;
import com.cabinet.model.Medecin;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Callback;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PatientRdvController {

    @FXML private TableView<RendezVous> rdvTable;
    @FXML private TableColumn<RendezVous, LocalDateTime> dateHeureColumn;
    @FXML private TableColumn<RendezVous, String> motifColumn;
    @FXML private TableColumn<RendezVous, String> statutColumn;
    @FXML private TableColumn<RendezVous, String> medecinColumn;
    @FXML private TableColumn<RendezVous, Void> actionsColumn;
    
    @FXML private Label detailDateLabel;
    @FXML private Label detailHeureLabel;
    @FXML private Label detailMedecinLabel;
    @FXML private Label detailMotifLabel;
    @FXML private Label detailStatutLabel;
    
    @FXML private Label statsTotalLabel;
    @FXML private Label statsPlanifiesLabel;
    @FXML private Label statsTerminesLabel;
    @FXML private Label statsAnnulesLabel;
    @FXML private Label nextRdvLabel;
    
    @FXML private RadioButton radioTous;
    @FXML private RadioButton radioPlanifies;
    @FXML private RadioButton radioTermines;
    @FXML private RadioButton radioAnnules;
    
    @FXML private ToggleGroup statusToggleGroup;
    @FXML private Button btnRefresh;
    @FXML private Button btnTakeNewRdv;
    @FXML private Button btnContactCabinet;
    @FXML private Button btnModifyRdv;
    @FXML private Button btnCancelRdv;
    
    private RendezVousDAO rdvDAO = new RendezVousDAO();
    private MedecinDAO medecinDAO = new MedecinDAO();
    private ObservableList<RendezVous> rdvList = FXCollections.observableArrayList();
    private ObservableList<RendezVous> filteredList = FXCollections.observableArrayList();
    private Map<Integer, Medecin> medecinsMap = new HashMap<>();
    
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML
    public void initialize() {
        Patient patient = SessionManager.getInstance().getCurrentPatient();
        if (patient == null) {
            showAlert(Alert.AlertType.WARNING, "Session", "Veuillez vous reconnecter.");
            return;
        }
        
        // Configuration des colonnes
        dateHeureColumn.setCellValueFactory(new PropertyValueFactory<>("date_heure"));
        motifColumn.setCellValueFactory(new PropertyValueFactory<>("motif"));
        statutColumn.setCellValueFactory(new PropertyValueFactory<>("statut"));
        medecinColumn.setCellValueFactory(cellData -> {
            int medecinId = cellData.getValue().getId_medecin();
            Medecin medecin = medecinsMap.get(medecinId);
            if (medecin != null) {
                return new javafx.beans.property.SimpleStringProperty(
                    "Dr. " + medecin.getNom() + " " + medecin.getPrenom()
                );
            }
            return new javafx.beans.property.SimpleStringProperty("Médecin #" + medecinId);
        });
        
        // Formatter pour la colonne date/heure
        dateHeureColumn.setCellFactory(column -> new TableCell<RendezVous, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime date, boolean empty) {
                super.updateItem(date, empty);
                if (empty || date == null) {
                    setText(null);
                } else {
                    setText(date.format(dateTimeFormatter));
                }
            }
        });
        
        // Cell factory pour le statut
        statutColumn.setCellFactory(column -> new TableCell<RendezVous, String>() {
            private final HBox hbox = new HBox(8);
            private final Circle circle = new Circle(5);
            private final Label label = new Label();
            
            {
                hbox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
                hbox.getChildren().addAll(circle, label);
                setGraphic(hbox);
            }
            
            @Override
            protected void updateItem(String statut, boolean empty) {
                super.updateItem(statut, empty);
                
                if (empty || statut == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    label.setText(statut);
                    
                    // Appliquer des styles selon le statut
                    switch (statut.toUpperCase()) {
                        case "PLANIFIE":
                            circle.setFill(Color.web("#3498db"));
                            label.setStyle("-fx-text-fill: #3498db; -fx-font-weight: bold;");
                            break;
                        case "TERMINE":
                            circle.setFill(Color.web("#2ecc71"));
                            label.setStyle("-fx-text-fill: #2ecc71; -fx-font-weight: bold;");
                            break;
                        case "ANNULE":
                            circle.setFill(Color.web("#e74c3c"));
                            label.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                            break;
                        default:
                            circle.setFill(Color.GRAY);
                            label.setStyle("-fx-text-fill: #7f8c8d;");
                    }
                }
            }
        });
        
        // Cell factory pour les actions
        actionsColumn.setCellFactory(column -> new TableCell<RendezVous, Void>() {
            private final HBox hbox = new HBox(5);
            private final Button btnView = new Button("👁️");
            private final Button btnCancel = new Button("❌");
            
            {
                hbox.setAlignment(javafx.geometry.Pos.CENTER);
                
                // Style des boutons
                btnView.getStyleClass().addAll("button-small", "button-outline-primary");
                btnCancel.getStyleClass().addAll("button-small", "button-gradient-danger");
                
                // Tooltips
                btnView.setTooltip(new Tooltip("Voir les détails"));
                btnCancel.setTooltip(new Tooltip("Annuler le RDV"));
                
                // Actions
                btnView.setOnAction(e -> {
                    RendezVous rdv = getTableView().getItems().get(getIndex());
                    rdvTable.getSelectionModel().select(rdv);
                    showRendezVousDetails(rdv);
                });
                
                btnCancel.setOnAction(e -> {
                    RendezVous rdv = getTableView().getItems().get(getIndex());
                    rdvTable.getSelectionModel().select(rdv);
                    handleCancelRdv();
                });
                
                setGraphic(hbox);
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    RendezVous rdv = getTableView().getItems().get(getIndex());
                    
                    // Montrer le bouton annuler seulement pour les RDV planifiés
                    if ("PLANIFIE".equals(rdv.getStatut())) {
                        hbox.getChildren().setAll(btnView, btnCancel);
                    } else {
                        hbox.getChildren().setAll(btnView);
                    }
                }
            }
        });
        
        // Configuration du toggle group
        radioTous.setToggleGroup(statusToggleGroup);
        radioPlanifies.setToggleGroup(statusToggleGroup);
        radioTermines.setToggleGroup(statusToggleGroup);
        radioAnnules.setToggleGroup(statusToggleGroup);
        radioTous.setSelected(true);
        
        // Écouteur pour les filtres
        statusToggleGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (newToggle != null) {
                filterRendezVous();
                updateStatistics();
            }
        });
        
        // Écouteur pour la sélection dans le tableau
        rdvTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                showRendezVousDetails(newSelection);
                updateButtonStates(newSelection);
            } else {
                clearDetails();
                disableButtons();
            }
        });
        
        // Configuration des boutons
        setupButtonActions();
        
        // Chargement initial
        loadMedecins();
        loadRendezVous();
        updateStatistics();
        disableButtons();
    }

    private void loadMedecins() {
        try {
            List<Medecin> medecins = medecinDAO.getAllMedecins();
            for (Medecin medecin : medecins) {
                medecinsMap.put(medecin.getId_personne(), medecin);
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger la liste des médecins.");
            e.printStackTrace();
        }
    }

    private void loadRendezVous() {
        Patient patient = SessionManager.getInstance().getCurrentPatient();
        if (patient != null) {
            try {
                List<RendezVous> rdvs = rdvDAO.getRendezVousByPatientId(patient.getId_personne());
                // Trier par date décroissante
                rdvs.sort((r1, r2) -> r2.getDate_heure().compareTo(r1.getDate_heure()));
                rdvList.setAll(rdvs);
                filterRendezVous();
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Erreur de chargement", "Impossible de charger vos rendez-vous.");
                e.printStackTrace();
            }
        }
    }
    
    private void filterRendezVous() {
        if (radioTous.isSelected()) {
            filteredList.setAll(rdvList);
        } else if (radioPlanifies.isSelected()) {
            filteredList.setAll(rdvList.stream()
                .filter(rdv -> "PLANIFIE".equals(rdv.getStatut()))
                .collect(Collectors.toList()));
        } else if (radioTermines.isSelected()) {
            filteredList.setAll(rdvList.stream()
                .filter(rdv -> "TERMINE".equals(rdv.getStatut()))
                .collect(Collectors.toList()));
        } else if (radioAnnules.isSelected()) {
            filteredList.setAll(rdvList.stream()
                .filter(rdv -> "ANNULE".equals(rdv.getStatut()))
                .collect(Collectors.toList()));
        }
        
        rdvTable.setItems(filteredList);
    }
    
    private void updateStatistics() {
        // Calculer les statistiques
        long total = rdvList.size();
        long planifies = rdvList.stream()
            .filter(rdv -> "PLANIFIE".equals(rdv.getStatut()))
            .count();
        long termines = rdvList.stream()
            .filter(rdv -> "TERMINE".equals(rdv.getStatut()))
            .count();
        long annules = rdvList.stream()
            .filter(rdv -> "ANNULE".equals(rdv.getStatut()))
            .count();
        
        // Mettre à jour les labels
        statsTotalLabel.setText(String.valueOf(total));
        statsPlanifiesLabel.setText(String.valueOf(planifies));
        statsTerminesLabel.setText(String.valueOf(termines));
        statsAnnulesLabel.setText(String.valueOf(annules));
        
        // Trouver le prochain RDV
        RendezVous nextRdv = rdvList.stream()
            .filter(rdv -> "PLANIFIE".equals(rdv.getStatut()))
            .filter(rdv -> rdv.getDate_heure().isAfter(LocalDateTime.now()))
            .sorted((r1, r2) -> r1.getDate_heure().compareTo(r2.getDate_heure()))
            .findFirst()
            .orElse(null);
        
        if (nextRdv != null) {
            String date = nextRdv.getDate_heure().format(dateFormatter);
            String time = nextRdv.getDate_heure().format(timeFormatter);
            nextRdvLabel.setText(date + " à " + time);
        } else {
            nextRdvLabel.setText("Aucun RDV à venir");
        }
    }
    
    private void showRendezVousDetails(RendezVous rdv) {
        if (rdv != null) {
            // Récupérer les infos du médecin
            Medecin medecin = medecinsMap.get(rdv.getId_medecin());
            String medecinNom = medecin != null ? 
                "Dr. " + medecin.getNom() + " " + medecin.getPrenom() : 
                "Médecin #" + rdv.getId_medecin();
            String specialite = medecin != null ? medecin.getSpecialite() : "";
            
            // Mettre à jour les détails
            detailDateLabel.setText(rdv.getDate_heure().format(dateFormatter));
            detailHeureLabel.setText(rdv.getDate_heure().format(timeFormatter));
            detailMedecinLabel.setText(medecinNom + (specialite.isEmpty() ? "" : " - " + specialite));
            detailMotifLabel.setText(rdv.getMotif());
            detailStatutLabel.setText(rdv.getStatut());
            
            // Appliquer un style selon le statut
            switch (rdv.getStatut().toUpperCase()) {
                case "PLANIFIE":
                    detailStatutLabel.setStyle("-fx-text-fill: #3498db; -fx-font-weight: bold;");
                    break;
                case "TERMINE":
                    detailStatutLabel.setStyle("-fx-text-fill: #2ecc71; -fx-font-weight: bold;");
                    break;
                case "ANNULE":
                    detailStatutLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                    break;
            }
        }
    }
    
    private void clearDetails() {
        detailDateLabel.setText("-");
        detailHeureLabel.setText("-");
        detailMedecinLabel.setText("-");
        detailMotifLabel.setText("-");
        detailStatutLabel.setText("-");
        detailStatutLabel.setStyle("");
    }
    
    private void setupButtonActions() {
        btnRefresh.setOnAction(event -> handleRefresh());
        btnTakeNewRdv.setOnAction(event -> handleTakeNewRdv());
        btnContactCabinet.setOnAction(event -> handleContactCabinet());
        btnModifyRdv.setOnAction(event -> handleModifyRdv());
        btnCancelRdv.setOnAction(event -> handleCancelRdv());
    }
    
    private void updateButtonStates(RendezVous rdv) {
        if (rdv != null && "PLANIFIE".equals(rdv.getStatut())) {
            btnModifyRdv.setDisable(false);
            btnCancelRdv.setDisable(false);
        } else {
            btnModifyRdv.setDisable(true);
            btnCancelRdv.setDisable(true);
        }
    }
    
    private void disableButtons() {
        btnModifyRdv.setDisable(true);
        btnCancelRdv.setDisable(true);
    }
    
    @FXML
    private void handleRefresh() {
        loadRendezVous();
        updateStatistics();
        showAlert(Alert.AlertType.INFORMATION, "Actualisation", "✅ Liste des rendez-vous actualisée.");
    }
    
    @FXML
    private void handleTakeNewRdv() {
        showAlert(Alert.AlertType.INFORMATION, "Nouveau RDV", 
            "📅 Fonctionnalité 'Prendre un nouveau RDV'\n\n" +
            "Cette fonctionnalité permettra de:\n" +
            "• Choisir un médecin\n" +
            "• Sélectionner une date et heure\n" +
            "• Spécifier le motif de la consultation\n" +
            "• Recevoir une confirmation par email");
    }
    
    @FXML
    private void handleContactCabinet() {
        showAlert(Alert.AlertType.INFORMATION, "Contacter le cabinet", 
            "📞 Téléphone: 01 23 45 67 89\n" +
            "📧 Email: contact@cabinetmedical.fr\n" +
            "🏥 Adresse: 123 Rue de la Santé, 75000 Paris\n" +
            "⏰ Horaires: Lundi-Vendredi 8h-19h, Samedi 9h-13h");
    }
    
    @FXML
    private void handleModifyRdv() {
        RendezVous selectedRdv = rdvTable.getSelectionModel().getSelectedItem();
        if (selectedRdv != null && "PLANIFIE".equals(selectedRdv.getStatut())) {
            showAlert(Alert.AlertType.INFORMATION, "Modifier RDV", 
                "✏️ Modification du RDV #" + selectedRdv.getId_rdv() + "\n\n" +
                "Vous pourrez modifier:\n" +
                "• La date et l'heure\n" +
                "• Le motif de consultation\n" +
                "• Le médecin (si disponibilité)\n\n" +
                "Une confirmation vous sera envoyée par email.");
        }
    }
    
    @FXML
    private void handleCancelRdv() {
        RendezVous selectedRdv = rdvTable.getSelectionModel().getSelectedItem();
        if (selectedRdv != null && "PLANIFIE".equals(selectedRdv.getStatut())) {
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Confirmation d'annulation");
            confirmation.setHeaderText("Annuler ce rendez-vous ?");
            confirmation.setContentText(
                "Êtes-vous sûr de vouloir annuler ce rendez-vous ?\n\n" +
                "📅 Date: " + selectedRdv.getDate_heure().format(dateFormatter) + "\n" +
                "⏰ Heure: " + selectedRdv.getDate_heure().format(timeFormatter) + "\n" +
                "👨‍⚕️ Médecin: " + getMedecinName(selectedRdv.getId_medecin()) + "\n" +
                "📝 Motif: " + selectedRdv.getMotif() + "\n\n" +
                "⚠️ Cette action est irréversible."
            );
            
            confirmation.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try {
                        // Mettre à jour le statut dans la base de données
                        selectedRdv.setStatut("ANNULE");
                        if (rdvDAO.updateRendezVous(selectedRdv)) {
                            showAlert(Alert.AlertType.INFORMATION, "Annulation réussie", 
                                "✅ Votre rendez-vous a été annulé avec succès.\n" +
                                "Vous recevrez une confirmation par email.");
                            loadRendezVous();
                            updateStatistics();
                        } else {
                            showAlert(Alert.AlertType.ERROR, "Erreur", 
                                "❌ Échec de l'annulation. Veuillez réessayer.");
                        }
                    } catch (Exception e) {
                        showAlert(Alert.AlertType.ERROR, "Erreur", 
                            "❌ Erreur lors de l'annulation: " + e.getMessage());
                    }
                }
            });
        }
    }
    
    private String getMedecinName(int medecinId) {
        Medecin medecin = medecinsMap.get(medecinId);
        return medecin != null ? 
            "Dr. " + medecin.getNom() + " " + medecin.getPrenom() : 
            "Médecin #" + medecinId;
    }
    
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}