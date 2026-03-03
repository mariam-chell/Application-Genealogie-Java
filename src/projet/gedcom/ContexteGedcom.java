package projet.gedcom;

import projet.modele.Famille;
import projet.modele.Genealogie;
import projet.modele.Individu;

/**
 * Contexte utilisé pendant la lecture du fichier GEDCOM.
 * Contient la généalogie et les entrées courantes.
 */
public class ContexteGedcom {

    private Genealogie genealogie;
    private Individu individuCourant;
    private Famille familleCourante;

    public ContexteGedcom(Genealogie genealogie) {
        this.genealogie = genealogie;
    }

    public Genealogie getGenealogie() {
        return genealogie;
    }

    public Individu getIndividuCourant() {
        return individuCourant;
    }

    public void setIndividuCourant(Individu individuCourant) {
        this.individuCourant = individuCourant;
        this.familleCourante = null;
    }

    public Famille getFamilleCourante() {
        return familleCourante;
    }

    public void setFamilleCourante(Famille familleCourante) {
        this.familleCourante = familleCourante;
        this.individuCourant = null;
    }
}
