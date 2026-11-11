package com.cabinet.model;

import java.sql.Date;

public class Ordonnance {
    private int id_ordonnance;
    private Date date_prescription; 
    private String contenu_textuel;
    private int id_patient; 
    private int id_medecin; 

    public Ordonnance() {}

    public Ordonnance(int id_ordonnance, Date date_prescription, String contenu_textuel, int id_patient, int id_medecin) {
        this.id_ordonnance = id_ordonnance;
        this.date_prescription = date_prescription;
        this.contenu_textuel = contenu_textuel;
        this.id_patient = id_patient;
        this.id_medecin = id_medecin;
    }


    public int getId_ordonnance() {
        return id_ordonnance;
    }

    public void setId_ordonnance(int id_ordonnance) {
        this.id_ordonnance = id_ordonnance;
    }

    public Date getDate_prescription() {
        return date_prescription;
    }

    public void setDate_prescription(Date date_prescription) {
        this.date_prescription = date_prescription;
    }

    public String getContenu_textuel() {
        return contenu_textuel;
    }

    public void setContenu_textuel(String contenu_textuel) {
        this.contenu_textuel = contenu_textuel;
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