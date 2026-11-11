package com.cabinet.model;

import java.sql.Date;

public class Patient extends Personne {
    // Attributs spécifiques à Patient
    private String dossier_medical;
    private String mutuelle;
    private int id_utilisateur;

    public Patient() {
        super();
    }

    public Patient(String nom, String prenom, Date date_naissance, String adresse, 
                  String telephone, String dossier_medical, String mutuelle, int id_utilisateur) {
        super(0, nom, prenom, date_naissance, adresse, telephone);
        this.dossier_medical = dossier_medical;
        this.mutuelle = mutuelle;
        this.id_utilisateur = id_utilisateur;
    }

    public Patient(int id_patient, String nom, String prenom, Date date_naissance, 
                  String adresse, String telephone, String dossier_medical, 
                  String mutuelle, int id_utilisateur) {
        super(id_patient, nom, prenom, date_naissance, adresse, telephone);
        this.dossier_medical = dossier_medical;
        this.mutuelle = mutuelle;
        this.id_utilisateur = id_utilisateur;
    }


    public int getId_patient() {
        return getId_personne();
    }

    public void setId_patient(int id_patient) {
        setId_personne(id_patient);
    }

    public String getDossier_medical() {
        return dossier_medical;
    }

    public void setDossier_medical(String dossier_medical) {
        this.dossier_medical = dossier_medical;
    }

    public String getMutuelle() {
        return mutuelle;
    }

    public void setMutuelle(String mutuelle) {
        this.mutuelle = mutuelle;
    }

    public int getId_utilisateur() {
        return id_utilisateur;
    }

    public void setId_utilisateur(int id_utilisateur) {
        this.id_utilisateur = id_utilisateur;
    }

    @Override
    public String toString() {
        return getNom() + " " + getPrenom();
    }
}