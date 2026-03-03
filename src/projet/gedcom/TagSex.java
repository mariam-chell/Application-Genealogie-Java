package projet.gedcom;

import projet.modele.Individu;

/**
 * Tag SEX : sexe de l'individu (M ou F).
 */
public class TagSex extends TagSimple {

    public TagSex(String valeur) {
        super(valeur);
    }

    public String getNomTag() {
        return "SEX";
    }

    public void appliquerSurContexte(ContexteGedcom contexte) {
        Individu courant = contexte.getIndividuCourant();
        if (courant != null && valeur != null && valeur.length() > 0) {
            courant.setSexe(valeur.charAt(0));
        }
    }
}
