package projet.gedcom;

import projet.modele.Individu;

/**
 * Tag NAME : prénoms et nom de l'individu (nom entre / /).
 */
public class TagName extends TagSimple {

    public TagName(String valeur) {
        super(valeur);
    }

    public String getNomTag() {
        return "NAME";
    }

    public void appliquerSurContexte(ContexteGedcom contexte) {
        Individu courant = contexte.getIndividuCourant();
        if (courant != null) {
            String prenom = valeur;
            String nom = "";
            int debut = valeur.indexOf('/');
            int fin = valeur.lastIndexOf('/');
            if (debut >= 0 && fin > debut) {
                prenom = valeur.substring(0, debut).trim();
                nom = valeur.substring(debut + 1, fin).trim();
            }
            courant.setPrenom(prenom);
            courant.setNom(nom);
        }
    }
}
