package com.cabinet.model;

import java.time.LocalDateTime;

public class RendezVous {
    private int id_rdv;
    private LocalDateTime date_heure;
    private String motif;
    private String statut; 
    private int id_patient; 
    private int id_medecin; 

    public RendezVous() {}

    public RendezVous(int id_rdv, LocalDateTime date_heure, String motif, String statut, int id_patient, int id_medecin) {
        this.id_rdv = id_rdv;
        this.date_heure = date_heure;
        this.motif = motif;
        this.statut = statut;
        this.id_patient = id_patient;
        this.id_medecin = id_medecin;
    }


    public int getId_rdv() {
        return id_rdv;
    }

    public void setId_rdv(int id_rdv) {
        this.id_rdv = id_rdv;
    }

    public LocalDateTime getDate_heure() {
        return date_heure;
    }

    public void setDate_heure(LocalDateTime date_heure) {
        this.date_heure = date_heure;
    }

    public String getMotif() {
        return motif;
    }

    public void setMotif(String motif) {
        this.motif = motif;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public int getId_patient() {
        return id_patient;
    }

    public void setId_patient(int id_patient) {
        this.id_patient = id_patient;
    }

    public int getId_medecin() {
        return id_medecin;
    }

    public void setId_medecin(int id_medecin) {
        this.id_medecin = id_medecin;
    }
}