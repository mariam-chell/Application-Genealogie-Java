package projet.modele;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Représente une famille : un père, une mère et des enfants.
 */
public class Famille implements Serializable {

    private String identifiant;               // ex : @F1@
    private Individu pere;                    // HUSB
    private Individu mere;                    // WIFE
    private ArrayList<Individu> enfants;      // CHIL

    // Indique si la famille provient d'un enregistrement explicite FAM (true)
    // ou si elle a été créée seulement parce qu'elle était référencée (false).
    private boolean creeParRecord;

    public Famille(String identifiant) {
        this.identifiant = identifiant;
        this.enfants = new ArrayList<Individu>();
        this.creeParRecord = false;
    }

    public String getIdentifiant() {
        return identifiant;
    }

    public Individu getPere() {
        return pere;
    }

    public void setPere(Individu pere) {
        this.pere = pere;
    }

    public Individu getMere() {
        return mere;
    }

    public void setMere(Individu mere) {
        this.mere = mere;
    }

    public ArrayList<Individu> getEnfants() {
        return enfants;
    }

    public void ajouterEnfant(Individu enfant) {
        if (enfant != null && !enfants.contains(enfant)) {
            enfants.add(enfant);
        }
    }

    public boolean isCreeParRecord() {
        return creeParRecord;
    }

    public void setCreeParRecord(boolean creeParRecord) {
        this.creeParRecord = creeParRecord;
    }

    public String getDescriptionSimple() {
        String texte = "Famille " + identifiant + "\n";
        texte += "  Père : " + (pere != null ? pere.getDescriptionSimple() : "UNKNOWN") + "\n";
        texte += "  Mère : " + (mere != null ? mere.getDescriptionSimple() : "UNKNOWN") + "\n";
        texte += "  Enfants : ";
        if (enfants.isEmpty()) {
            texte += "aucun";
        } else {
            for (int i = 0; i < enfants.size(); i++) {
                texte += enfants.get(i).getDescriptionSimple();
                if (i < enfants.size() - 1) {
                    texte += ", ";
                }
            }
        }
        return texte;
    }

    @Override
    public String toString() {
        return getDescriptionSimple();
    }
}
