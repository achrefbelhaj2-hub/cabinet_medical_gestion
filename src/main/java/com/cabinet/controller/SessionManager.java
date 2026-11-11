package com.cabinet.controller;

import com.cabinet.model.Utilisateur;
import com.cabinet.model.Patient;
import com.cabinet.model.Medecin;

/**
 */
public class SessionManager {
    private static SessionManager instance;
    private Utilisateur currentUser;
    private Patient currentPatient;
    private Medecin currentMedecin;

    private SessionManager() {
    }

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public Utilisateur getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(Utilisateur currentUser) {
        this.currentUser = currentUser;
    }
    
    public Patient getCurrentPatient() {
        return currentPatient;
    }

    public void setCurrentPatient(Patient currentPatient) {
        this.currentPatient = currentPatient;
    }

    public Medecin getCurrentMedecin() {
        return currentMedecin;
    }

    public void setCurrentMedecin(Medecin currentMedecin) {
        this.currentMedecin = currentMedecin;
    }

    // --- Méthodes de vérification de rôle ---
    public boolean hasRole(String role) {
        return currentUser != null && 
               currentUser.getRole() != null && 
               currentUser.getRole().equals(role);
    }

    public String getUserRole() {
        return currentUser != null ? currentUser.getRole() : null;
    }

    public boolean isAdmin() {
        return hasRole("ADMIN");
    }

    public boolean isMedecin() {
        return hasRole("MEDECIN");
    }

    public boolean isPatient() {
        return hasRole("PATIENT");
    }

    // --- Méthodes utilitaires ---
    public String getCurrentUserName() {
        if (currentUser == null) return "Invité";
        
        String role = currentUser.getRole();
        if ("PATIENT".equals(role) && currentPatient != null) {
            return currentPatient.getPrenom() + " " + currentPatient.getNom();
        } else if ("MEDECIN".equals(role) && currentMedecin != null) {
            return "Dr. " + currentMedecin.getNom();
        } else if ("ADMIN".equals(role)) {
            return "Administrateur";
        }
        return currentUser.getLogin();
    }

    public int getCurrentUserId() {
        return currentUser != null ? currentUser.getId_utilisateur() : -1;
    }

    public void cleanSession() {
        this.currentUser = null;
        this.currentPatient = null;
        this.currentMedecin = null;
    }

    // --- Vérification d'accès ---
    public boolean canAccessPatientManagement() {
        return isAdmin();
    }

    public boolean canAccessMedecinManagement() {
        return isAdmin();
    }

    public boolean canAccessRendezVous() {
        return isAdmin() || isMedecin();
    }

    public boolean canAccessOrdonnances() {
        return isAdmin() || isMedecin();
    }

    public boolean canAccessPatientFeatures() {
        return isPatient();
    }

    @Override
    public String toString() {
        return String.format("SessionManager{user=%s, role=%s, patient=%s, medecin=%s}",
                currentUser != null ? currentUser.getLogin() : "null",
                getUserRole(),
                currentPatient != null ? "yes" : "no",
                currentMedecin != null ? "yes" : "no");
    }
}