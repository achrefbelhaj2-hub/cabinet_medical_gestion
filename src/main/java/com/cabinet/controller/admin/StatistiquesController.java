package com.cabinet.controller.admin;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;

import com.cabinet.dao.*;
import com.cabinet.model.*;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class StatistiquesController implements Initializable {
    
    // Labels simples
    @FXML private Label labelPatients;
    @FXML private Label labelMedecins;
    @FXML private Label labelRdvAujourdhui;
    @FXML private Label labelOrdonnances;
    
    @FXML private PieChart chartSimple;
    
    @FXML private TableView<RendezVous> tableSimple;
    @FXML private TableColumn<RendezVous, String> colDateSimple;
    @FXML private TableColumn<RendezVous, String> colPatientSimple;
    @FXML private TableColumn<RendezVous, String> colStatutSimple;
    
    // Boutons
    @FXML private Button btnRafraichir;
    @FXML private Button btnFermer;
    
    // DAOs
    private PatientDAO patientDAO = new PatientDAO();
    private MedecinDAO medecinDAO = new MedecinDAO();
    private RendezVousDAO rdvDAO = new RendezVousDAO();
    private OrdonnanceDAO ordonnanceDAO = new OrdonnanceDAO();
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTable();
        loadData();
        setupChart();
    }
    
    private void setupTable() {
        colDateSimple.setCellValueFactory(cellData -> {
            try {
                return new javafx.beans.property.SimpleStringProperty(
                    cellData.getValue().getDate_heure().format(
                        DateTimeFormatter.ofPattern("dd/MM/yy HH:mm")
                    )
                );
            } catch (Exception e) {
                return new javafx.beans.property.SimpleStringProperty("");
            }
        });
        
        colPatientSimple.setCellValueFactory(cellData -> {
            try {
                Patient patient = patientDAO.getPatientById(cellData.getValue().getId_patient());
                return new javafx.beans.property.SimpleStringProperty(
                    patient.getNom() + " " + patient.getPrenom()
                );
            } catch (Exception e) {
                return new javafx.beans.property.SimpleStringProperty("?");
            }
        });
        
        colStatutSimple.setCellValueFactory(new PropertyValueFactory<>("statut"));
    }
    
    private void loadData() {
        try {
            // 1. Patients
            int nbPatients = patientDAO.getAllPatients().size();
            labelPatients.setText(nbPatients + " patients");
            
            // 2. Médecins
            int nbMedecins = medecinDAO.getAllMedecins().size();
            labelMedecins.setText(nbMedecins + " médecins");
            
            // 3. RDV aujourd'hui
            java.sql.Date aujourdhui = java.sql.Date.valueOf(LocalDate.now());
            int nbRdvAujourdhui = rdvDAO.countRendezVousByDate(aujourdhui);
            labelRdvAujourdhui.setText(nbRdvAujourdhui + " RDV");
            
            // 4. Ordonnances
            int nbOrdonnances = ordonnanceDAO.getAllOrdonnances().size();
            labelOrdonnances.setText(nbOrdonnances + " ordonnances");
            
            // 5. Tableau des 5 derniers RDV
            List<RendezVous> tousRdvs = rdvDAO.getAllRendezVous();
            List<RendezVous> derniersRdvs = tousRdvs.stream()
                .sorted((r1, r2) -> r2.getDate_heure().compareTo(r1.getDate_heure()))
                .limit(5)
                .collect(Collectors.toList());
            
            tableSimple.setItems(FXCollections.observableArrayList(derniersRdvs));
            
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger les données");
        }
    }
    
    private void setupChart() {
        try {
            List<RendezVous> rdvs = rdvDAO.getAllRendezVous();
            
            // Compter par statut
            Map<String, Long> compteur = rdvs.stream()
                .collect(Collectors.groupingBy(
                    RendezVous::getStatut,
                    Collectors.counting()
                ));
            
            ObservableList<PieChart.Data> data = FXCollections.observableArrayList();
            
            // Ajouter seulement les statuts qui existent
            if (compteur.containsKey("PLANIFIE")) {
                data.add(new PieChart.Data("Planifiés", compteur.get("PLANIFIE")));
            }
            if (compteur.containsKey("TERMINE")) {
                data.add(new PieChart.Data("Terminés", compteur.get("TERMINE")));
            }
            if (compteur.containsKey("ANNULE")) {
                data.add(new PieChart.Data("Annulés", compteur.get("ANNULE")));
            }
            
            chartSimple.setData(data);
            chartSimple.setTitle("RDV par statut");
            
        } catch (Exception e) {
            e.printStackTrace();
            chartSimple.setTitle("Données indisponibles");
        }
    }
    
    @FXML
    private void handleRafraichir() {
        loadData();
        setupChart();
    }
    
    @FXML
    private void handleFermer() {
        Stage stage = (Stage) btnFermer.getScene().getWindow();
        stage.close();
    }
    
    private void showAlert(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}