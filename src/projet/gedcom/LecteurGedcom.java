package projet.gedcom;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import projet.modele.Famille;
import projet.modele.Genealogie;
import projet.modele.Individu;

/**
 * Lecteur très simple de fichier GEDCOM.
 * Cette version gère uniquement les tags imposés par le sujet,
 * et ignore les autres.
 */
public class LecteurGedcom {

    /**
     * Lit un fichier GEDCOM et construit une Genealogie.
     */
    public Genealogie lireFichier(String cheminFichier) throws IOException {
        Genealogie genealogie = new Genealogie();
        ContexteGedcom contexte = new ContexteGedcom(genealogie);

        BufferedReader in = new BufferedReader(new FileReader(cheminFichier));

        String ligne = in.readLine();
        while (ligne != null) {
            ligne = ligne.trim();
            if (ligne.length() > 0) {
                traiterLigne(ligne, contexte);
            }
            ligne = in.readLine();
        }

        in.close();
        return genealogie;
    }

    private void traiterLigne(String ligne, ContexteGedcom contexte) {
        String[] morceaux = ligne.split("\\s+", 3);
        if (morceaux.length < 2) {
            return;
        }

        int niveau = Integer.parseInt(morceaux[0]);
        String second = morceaux[1];

        // Cas des enregistrements de niveau 0 avec identifiant (@I...@ ou @F...@)
        if (niveau == 0 && second.startsWith("@") && morceaux.length >= 3) {
            String identifiant = second;
            String tag = morceaux[2];

            if ("INDI".equals(tag)) {
                Individu ind = contexte.getGenealogie()
                                       .creerIndividuDepuisRecord(identifiant);
                contexte.setIndividuCourant(ind);
            } else if ("FAM".equals(tag)) {
                Famille fam = contexte.getGenealogie()
                                      .creerFamilleDepuisRecord(identifiant);
                contexte.setFamilleCourante(fam);
            } else {
                // Autre enregistrement (HEAD, TRLR, OBJE...) : on réinitialise le contexte.
                contexte.setIndividuCourant(null);
                contexte.setFamilleCourante(null);
            }
        } else {
            // Niveau > 0 ou ligne 0 sans identifiant : tags de description.
            traiterTagNiveauPlusGrand(ligne, contexte);
        }
    }

    private void traiterTagNiveauPlusGrand(String ligne, ContexteGedcom contexte) {
        String[] morceaux = ligne.split("\\s+", 3);
        if (morceaux.length < 2) {
            return;
        }

        String tag = morceaux[1];
        String valeur = (morceaux.length == 3) ? morceaux[2] : "";

        if ("NAME".equals(tag)) {
            TagGedcom t = new TagName(valeur);
            t.appliquerSurContexte(contexte);
        } else if ("SEX".equals(tag)) {
            TagGedcom t = new TagSex(valeur);
            t.appliquerSurContexte(contexte);
        } else if ("FAMC".equals(tag)) {
            traiterFAMC(valeur, contexte);
        } else if ("FAMS".equals(tag)) {
            traiterFAMS(valeur, contexte);
        } else if ("HUSB".equals(tag)) {
            traiterHUSB(valeur, contexte);
        } else if ("WIFE".equals(tag)) {
            traiterWIFE(valeur, contexte);
        } else if ("CHIL".equals(tag)) {
            traiterCHIL(valeur, contexte);
        } else {
            // Tag non traité : on l'ignore, ainsi que ses sous-lignes
            // (dans cette version simple, nous ne gérons pas les sous-niveaux).
        }
    }

    /**
     * FAMC : famille d'origine de l'individu courant.
     */
    private void traiterFAMC(String valeur, ContexteGedcom contexte) {
        Individu enfant = contexte.getIndividuCourant();
        if (enfant == null) {
            return;
        }
        String idFamille = valeur.trim();
        if (idFamille.length() == 0) {
            return;
        }

        Genealogie g = contexte.getGenealogie();
        Famille famille = g.obtenirOuCreerFamilleParReference(idFamille);

        // Lien dans les deux sens
        enfant.setFamilleOrigine(famille);
        famille.ajouterEnfant(enfant);
    }

    /**
     * FAMS : familles dans lesquelles l'individu courant est parent.
     */
    private void traiterFAMS(String valeur, ContexteGedcom contexte) {
        Individu parent = contexte.getIndividuCourant();
        if (parent == null) {
            return;
        }
        String idFamille = valeur.trim();
        if (idFamille.length() == 0) {
            return;
        }

        Genealogie g = contexte.getGenealogie();
        Famille famille = g.obtenirOuCreerFamilleParReference(idFamille);

        parent.ajouterFamilleParent(famille);

        // On essaie de fixer père/mère selon le sexe,
        // sans écraser une information déjà présente.
        if (parent.getSexe() == 'M') {
            if (famille.getPere() == null) {
                famille.setPere(parent);
            }
        } else if (parent.getSexe() == 'F') {
            if (famille.getMere() == null) {
                famille.setMere(parent);
            }
        }
    }

    /**
     * HUSB : père (mari) dans la famille courante.
     */
    private void traiterHUSB(String valeur, ContexteGedcom contexte) {
        Famille famille = contexte.getFamilleCourante();
        if (famille == null) {
            return;
        }
        String idIndividu = valeur.trim();
        if (idIndividu.length() == 0) {
            return;
        }

        Genealogie g = contexte.getGenealogie();
        Individu pere = g.obtenirOuCreerIndividuParReference(idIndividu);
        famille.setPere(pere);
        pere.ajouterFamilleParent(famille);
    }

    /**
     * WIFE : mère (épouse) dans la famille courante.
     */
    private void traiterWIFE(String valeur, ContexteGedcom contexte) {
        Famille famille = contexte.getFamilleCourante();
        if (famille == null) {
            return;
        }
        String idIndividu = valeur.trim();
        if (idIndividu.length() == 0) {
            return;
        }

        Genealogie g = contexte.getGenealogie();
        Individu mere = g.obtenirOuCreerIndividuParReference(idIndividu);
        famille.setMere(mere);
        mere.ajouterFamilleParent(famille);
    }

    /**
     * CHIL : enfant dans la famille courante.
     */
    private void traiterCHIL(String valeur, ContexteGedcom contexte) {
        Famille famille = contexte.getFamilleCourante();
        if (famille == null) {
            return;
        }
        String idEnfant = valeur.trim();
        if (idEnfant.length() == 0) {
            return;
        }

        Genealogie g = contexte.getGenealogie();
        Individu enfant = g.obtenirOuCreerIndividuParReference(idEnfant);
        famille.ajouterEnfant(enfant);

        if (enfant.getFamilleOrigine() == null) {
            enfant.setFamilleOrigine(famille);
        }
    }
}
