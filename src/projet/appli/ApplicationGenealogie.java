package projet.appli;

import java.io.IOException;
import java.util.Scanner;

import projet.gedcom.LecteurGedcom;
import projet.modele.Famille;
import projet.modele.Genealogie;
import projet.modele.Individu;

/**
 * Application principale simple pour tester la généalogie en mode texte.
 * Utilise une interface très simple dans le terminal.
 */
public class ApplicationGenealogie {

    public static void main(String[] args) {
        Scanner clavier = new Scanner(System.in);

        System.out.println("Chemin du fichier GED à charger : ");
        String chemin = clavier.nextLine();

        LecteurGedcom lecteur = new LecteurGedcom();

        try {
            Genealogie genealogie = lecteur.lireFichier(chemin);

            // Vérification de base de la cohérence.
            try {
                genealogie.verifierCoherence();
            } catch (Exception e) {
                System.out.println("Attention : " + e.getMessage());
            }

            System.out.println("Base chargée. Entrez une requête (EXIT pour quitter).");
            System.out.println("Exemples :");
            System.out.println("  INFO John Begood");
            System.out.println("  CHILD John Begood");
            System.out.println("  SIBLINGS John Begood");
            System.out.println("  John Begood MARRIED Ada Begood");
            System.out.println("  FAMC Fred Begood");

            String requete = clavier.nextLine();
            while (!"EXIT".equalsIgnoreCase(requete)) {
                traiterRequete(requete, genealogie);
                System.out.println();
                System.out.println("Nouvelle requête (EXIT pour quitter) : ");
                requete = clavier.nextLine();
            }

        } catch (IOException e) {
            System.out.println("Erreur de lecture : " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Erreur : " + e.getMessage());
        } finally {
            clavier.close();
        }
    }

    /**
     * Traite les requêtes saisies par l'utilisateur.
     * Gère les mots-clés :
     *   INFO, CHILD, SIBLINGS, FAMC
     * et la forme : "<prenom1> <nom1> MARRIED <prenom2> <nom2>".
     */
    private static void traiterRequete(String requete, Genealogie genealogie) {
        if (requete == null) {
            return;
        }
        requete = requete.trim();
        if (requete.length() == 0) {
            System.out.println("Requête vide.");
            return;
        }

        String[] mots = requete.split("\\s+");
        if (mots.length == 0) {
            System.out.println("Requête vide.");
            return;
        }

        String premier = mots[0].toUpperCase();

        if ("INFO".equals(premier)) {
            if (mots.length < 3) {
                System.out.println("Format attendu : INFO prenom nom");
                return;
            }
            String prenom = mots[1];
            String nom = mots[2];
            afficherInfo(genealogie, prenom, nom);

        } else if ("CHILD".equals(premier)) {
            if (mots.length < 3) {
                System.out.println("Format attendu : CHILD prenom nom");
                return;
            }
            String prenom = mots[1];
            String nom = mots[2];
            afficherEnfants(genealogie, prenom, nom);

        } else if ("SIBLINGS".equals(premier)) {
            if (mots.length < 3) {
                System.out.println("Format attendu : SIBLINGS prenom nom");
                return;
            }
            String prenom = mots[1];
            String nom = mots[2];
            afficherFratrie(genealogie, prenom, nom);

        } else if ("FAMC".equals(premier)) {
            if (mots.length < 3) {
                System.out.println("Format attendu : FAMC prenom nom");
                return;
            }
            String prenom = mots[1];
            String nom = mots[2];
            afficherFamilleOrigine(genealogie, prenom, nom);

        } else {
            // On tente l'autre forme :
            // "<prenom1> <nom1> MARRIED <prenom2> <nom2>"
            if (mots.length == 5 && "MARRIED".equalsIgnoreCase(mots[2])) {
                String prenom1 = mots[0];
                String nom1 = mots[1];
                String prenom2 = mots[3];
                String nom2 = mots[4];
                testerMariage(genealogie, prenom1, nom1, prenom2, nom2);
            } else {
                System.out.println("Commande inconnue ou format invalide.");
            }
        }
    }

    private static void afficherInfo(Genealogie genealogie, String prenom, String nom) {
        Individu ind = genealogie.chercherIndividuParNomComplet(prenom, nom);
        if (ind == null) {
            System.out.println("Individu non trouvé : " + prenom + " " + nom);
            return;
        }

        System.out.println("Informations sur l'individu :");
        System.out.println(ind.getDescriptionSimple());

        Famille origine = ind.getFamilleOrigine();
        if (origine != null) {
            System.out.println("Famille d'origine :");
            System.out.println(origine.getDescriptionSimple());
        } else {
            System.out.println("Famille d'origine : inconnue");
        }

        if (ind.getFamillesParent().isEmpty()) {
            System.out.println("Aucune famille où l'individu est parent.");
        } else {
            System.out.println("Familles où l'individu est parent :");
            for (Famille f : ind.getFamillesParent()) {
                System.out.println(f.getDescriptionSimple());
            }
        }
    }

    private static void afficherEnfants(Genealogie genealogie, String prenom, String nom) {
        Individu parent = genealogie.chercherIndividuParNomComplet(prenom, nom);
        if (parent == null) {
            System.out.println("Individu non trouvé : " + prenom + " " + nom);
            return;
        }

        boolean auMoinsUnEnfant = false;
        for (Famille f : parent.getFamillesParent()) {
            if (!f.getEnfants().isEmpty()) {
                auMoinsUnEnfant = true;
                System.out.println("Enfants dans " + f.getIdentifiant() + " :");
                for (Individu e : f.getEnfants()) {
                    System.out.println("  " + e.getDescriptionSimple());
                }
            }
        }
        if (!auMoinsUnEnfant) {
            System.out.println("Aucun enfant trouvé pour " + parent.getDescriptionSimple());
        }
    }

    private static void afficherFratrie(Genealogie genealogie, String prenom, String nom) {
        Individu ind = genealogie.chercherIndividuParNomComplet(prenom, nom);
        if (ind == null) {
            System.out.println("Individu non trouvé : " + prenom + " " + nom);
            return;
        }

        Famille origine = ind.getFamilleOrigine();
        if (origine == null) {
            System.out.println("Famille d'origine inconnue pour " + ind.getDescriptionSimple());
            return;
        }

        boolean auMoinsUnFrereSoeur = false;
        System.out.println("Frères et soeurs de " + ind.getDescriptionSimple() + " :");
        for (Individu e : origine.getEnfants()) {
            if (e != ind) {
                auMoinsUnFrereSoeur = true;
                System.out.println("  " + e.getDescriptionSimple());
            }
        }
        if (!auMoinsUnFrereSoeur) {
            System.out.println("Aucun frère ou soeur trouvé.");
        }
    }

    private static void afficherFamilleOrigine(Genealogie genealogie, String prenom, String nom) {
        Individu ind = genealogie.chercherIndividuParNomComplet(prenom, nom);
        if (ind == null) {
            System.out.println("Individu non trouvé : " + prenom + " " + nom);
            return;
        }

        Famille origine = ind.getFamilleOrigine();
        if (origine == null) {
            System.out.println("Famille d'origine inconnue pour " + ind.getDescriptionSimple());
        } else {
            System.out.println("Famille d'origine de " + ind.getDescriptionSimple() + " :");
            System.out.println(origine.getDescriptionSimple());
        }
    }

    private static void testerMariage(Genealogie genealogie,
                                      String prenom1, String nom1,
                                      String prenom2, String nom2) {
        Individu a = genealogie.chercherIndividuParNomComplet(prenom1, nom1);
        Individu b = genealogie.chercherIndividuParNomComplet(prenom2, nom2);

        if (a == null || b == null) {
            System.out.println("Au moins un des deux individus n'existe pas.");
            return;
        }

        boolean maries = false;
        for (Famille f : a.getFamillesParent()) {
            if (f.getPere() == a && f.getMere() == b) {
                maries = true;
            } else if (f.getPere() == b && f.getMere() == a) {
                maries = true;
            }
        }

        if (maries) {
            System.out.println("Oui : " + a.getDescriptionSimple()
                    + " est marié avec " + b.getDescriptionSimple());
        } else {
            System.out.println("Non : ces deux individus ne sont pas mariés ensemble.");
        }
    }
}
