package projet.modele;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Représente un individu dans la base généalogique.
 * Classe volontairement simple.
 */
public class Individu implements Serializable {

    private String identifiant;              // ex : @I1@
    private String prenom;
    private String nom;                      // sans les /.../
    private char sexe;                       // 'M', 'F' ou 'U' pour inconnu

    private Famille familleOrigine;          // FAMC : famille où l'individu est enfant
    private ArrayList<Famille> famillesParent; // FAMS : familles où l'individu est parent

    // Indique si l'individu provient d'un enregistrement explicite INDI (true)
    // ou s'il a été créé seulement parce qu'il était référencé (false).
    private boolean creeParRecord;

    public Individu(String identifiant) {
        this.identifiant = identifiant;
        this.famillesParent = new ArrayList<Famille>();
        this.sexe = 'U';
        this.creeParRecord = false;
    }

    public String getIdentifiant() {
        return identifiant;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public char getSexe() {
        return sexe;
    }

    public void setSexe(char sexe) {
        this.sexe = sexe;
    }

    public Famille getFamilleOrigine() {
        return familleOrigine;
    }

    public void setFamilleOrigine(Famille familleOrigine) {
        this.familleOrigine = familleOrigine;
    }

    public ArrayList<Famille> getFamillesParent() {
        return famillesParent;
    }

    public void ajouterFamilleParent(Famille famille) {
        if (famille != null && !famillesParent.contains(famille)) {
            famillesParent.add(famille);
        }
    }

    public boolean isCreeParRecord() {
        return creeParRecord;
    }

    public void setCreeParRecord(boolean creeParRecord) {
        this.creeParRecord = creeParRecord;
    }

    /**
     * Retourne une description texte simple de l'individu.
     */
    public String getDescriptionSimple() {
        return (prenom != null ? prenom : "UNKNOWN")
            + " "
            + (nom != null ? nom : "UNKNOWN")
            + " ("
            + identifiant
            + ", "
            + sexe
            + ")";
    }

    @Override
    public String toString() {
        return getDescriptionSimple();
    }
}
