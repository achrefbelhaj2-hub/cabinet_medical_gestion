package com.cabinet.model;

import java.sql.Date;

public abstract class Personne {
    protected int id_personne;
    protected String nom;
    protected String prenom;
    protected Date date_naissance;
    protected String adresse;
    protected String telephone;
    protected int id_utilisateur;

    public Personne() {}

    public Personne(int id_personne, String nom, String prenom, Date date_naissance, String adresse, String telephone) {
        this.id_personne = id_personne;
        this.nom = nom;
        this.prenom = prenom;
        this.date_naissance = date_naissance;
        this.adresse = adresse;
        this.telephone = telephone;
    }

    public Personne(int id_personne, String nom, String prenom, Date date_naissance, 
                   String adresse, String telephone, int id_utilisateur) {
        this.id_personne = id_personne;
        this.nom = nom;
        this.prenom = prenom;
        this.date_naissance = date_naissance;
        this.adresse = adresse;
        this.telephone = telephone;
        this.id_utilisateur = id_utilisateur;
    }


    public int getId_personne() {
        return id_personne;
    }

    public void setId_personne(int id_personne) {
        this.id_personne = id_personne;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public Date getDate_naissance() {
        return date_naissance;
    }

    public void setDate_naissance(Date date_naissance) {
        this.date_naissance = date_naissance;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public int getId_utilisateur() {
        return id_utilisateur;
    }

    public void setId_utilisateur(int id_utilisateur) {
        this.id_utilisateur = id_utilisateur;
    }
}