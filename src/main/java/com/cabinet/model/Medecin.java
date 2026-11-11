package com.cabinet.model;

import java.sql.Date;

public class Medecin extends Personne {
    private String specialite;
    private String licence;
    private int id_utilisateur; 

    public Medecin() {
        super();
    }

    public Medecin(int id_medecin, String nom, String prenom, Date date_naissance, String adresse, String telephone, String specialite, String licence, int id_utilisateur) {
        super(id_medecin, nom, prenom, date_naissance, adresse, telephone);
        this.specialite = specialite;
        this.licence = licence;
        this.id_utilisateur = id_utilisateur;
    }



    public String getSpecialite() {
        return specialite;
    }

    public void setSpecialite(String specialite) {
        this.specialite = specialite;
    }

    public String getLicence() {
        return licence;
    }

    public void setLicence(String licence) {
        this.licence = licence;
    }

    public int getId_utilisateur() {
        return id_utilisateur;
    }

    public void setId_utilisateur(int id_utilisateur) {
        this.id_utilisateur = id_utilisateur;
    }
}