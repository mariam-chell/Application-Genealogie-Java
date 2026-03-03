package projet.modele;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import projet.exceptions.CycleGenealogiqueException;
import projet.exceptions.GenealogieException;
import projet.exceptions.LienManquantException;
import projet.exceptions.ReferenceManquanteException;
import projet.exceptions.SexeIncoherentException;

/**
 * Représente l'ensemble de la base généalogique.
 * Utilise des collections simples (HashMap) vues en TP.
 */
public class Genealogie implements Serializable {

    private HashMap<String, Individu> individus;
    private HashMap<String, Famille> familles;

    public Genealogie() {
        individus = new HashMap<String, Individu>();
        familles = new HashMap<String, Famille>();
    }

    // Méthodes d'accès simples

    public Individu getIndividu(String id) {
        return individus.get(id);
    }

    public Famille getFamille(String id) {
        return familles.get(id);
    }

    /**
     * Crée ou récupère un individu à partir d'un enregistrement INDI.
     * Marque l'individu comme provenant d'un record explicite.
     */
    public Individu creerIndividuDepuisRecord(String id) {
        Individu ind = individus.get(id);
        if (ind == null) {
            ind = new Individu(id);
            individus.put(id, ind);
        }
        ind.setCreeParRecord(true);
        return ind;
    }

    /**
     * Crée ou récupère une famille à partir d'un enregistrement FAM.
     * Marque la famille comme provenant d'un record explicite.
     */
    public Famille creerFamilleDepuisRecord(String id) {
        Famille fam = familles.get(id);
        if (fam == null) {
            fam = new Famille(id);
            familles.put(id, fam);
        }
        fam.setCreeParRecord(true);
        return fam;
    }

    /**
     * Crée ou récupère un individu qui apparaît seulement dans une référence
     * (par exemple dans FAMC, FAMS, HUSB, WIFE, CHIL).
     */
    public Individu obtenirOuCreerIndividuParReference(String id) {
        Individu ind = individus.get(id);
        if (ind == null) {
            ind = new Individu(id);
            // creeParRecord reste à false : on saura qu'il manque un enregistrement INDI
            individus.put(id, ind);
        }
        return ind;
    }

    /**
     * Crée ou récupère une famille qui apparaît seulement dans une référence.
     */
    public Famille obtenirOuCreerFamilleParReference(String id) {
        Famille fam = familles.get(id);
        if (fam == null) {
            fam = new Famille(id);
            // creeParRecord reste à false
            familles.put(id, fam);
        }
        return fam;
    }

    public Individu chercherIndividuParNomComplet(String prenom, String nom) {
        Collection<Individu> tous = individus.values();
        for (Individu i : tous) {
            if (prenom != null && nom != null
                && prenom.equals(i.getPrenom())
                && nom.equals(i.getNom())) {
                return i;
            }
        }
        return null;
    }

    public Collection<Individu> getTousIndividus() {
        return individus.values();
    }

    public Collection<Famille> getToutesFamilles() {
        return familles.values();
    }

    /**
     * Vérifie la cohérence de la base.
     *  - références manquantes (individus/familles créés uniquement par référence)
     *  - liens non bidirectionnels entre individus et familles
     *  - sexe incohérent père/mère
     *  - cycles généalogiques simples
     *
     * Lance une exception générique si au moins un problème important est trouvé.
     */
    public void verifierCoherence() throws GenealogieException {
        boolean referenceManquante = false;
        boolean lienCorrige = false;
        boolean sexeProbleme = false;

        // 1) Références manquantes : individus ou familles sans record explicite
        for (Individu ind : getTousIndividus()) {
            if (!ind.isCreeParRecord()) {
                System.out.println("Référence manquante : individu "
                        + ind.getIdentifiant() + " n'a pas d'enregistrement INDI.");
                referenceManquante = true;
            }
        }
        for (Famille fam : getToutesFamilles()) {
            if (!fam.isCreeParRecord()) {
                System.out.println("Référence manquante : famille "
                        + fam.getIdentifiant() + " n'a pas d'enregistrement FAM.");
                referenceManquante = true;
            }
        }

        // 2) Liens non bidirectionnels pour la famille d'origine et les enfants
        for (Individu ind : getTousIndividus()) {
            Famille origine = ind.getFamilleOrigine();
            if (origine != null) {
                if (!origine.getEnfants().contains(ind)) {
                    origine.ajouterEnfant(ind);
                    System.out.println("Lien manquant corrigé : ajout de "
                            + ind.getIdentifiant() + " dans les enfants de "
                            + origine.getIdentifiant());
                    lienCorrige = true;
                }
            }
        }
        for (Famille fam : getToutesFamilles()) {
            for (Individu enfant : fam.getEnfants()) {
                if (enfant.getFamilleOrigine() == null) {
                    enfant.setFamilleOrigine(fam);
                    System.out.println("Lien manquant corrigé : famille d'origine de "
                            + enfant.getIdentifiant() + " fixée à "
                            + fam.getIdentifiant());
                    lienCorrige = true;
                }
            }
        }

        // 3) Sexe incohérent : père Femme ou mère Homme
        for (Famille fam : getToutesFamilles()) {
            Individu pere = fam.getPere();
            Individu mere = fam.getMere();
            if (pere != null && pere.getSexe() == 'F') {
                System.out.println("Sexe incohérent : le père "
                        + pere.getIdentifiant() + " est de sexe F.");
                sexeProbleme = true;
            }
            if (mere != null && mere.getSexe() == 'M') {
                System.out.println("Sexe incohérent : la mère "
                        + mere.getIdentifiant() + " est de sexe M.");
                sexeProbleme = true;
            }
        }

        // 4) Détection très simple de cycles (un individu devient son propre ancêtre)
        for (Individu ind : getTousIndividus()) {
            verifierAbsenceCyclePour(ind);
        }

        // Synthèse des problèmes
        if (referenceManquante) {
            throw new ReferenceManquanteException(
                "Au moins une référence manquante a été détectée (voir messages ci-dessus).");
        }
        if (lienCorrige) {
            throw new LienManquantException(
                "Au moins un lien manquant a été corrigé (voir messages ci-dessus).");
        }
        if (sexeProbleme) {
            throw new SexeIncoherentException(
                "Au moins une incohérence de sexe a été détectée (voir messages ci-dessus).");
        }
    }

    /**
     * Vérifie qu'il n'existe pas de cycle pour un individu donné.
     */
    private void verifierAbsenceCyclePour(Individu depart)
            throws CycleGenealogiqueException {
        HashSet<Individu> visites = new HashSet<Individu>();
        explorerAncetres(depart, depart, visites);
    }

    /**
     * Parcourt récursivement les ancêtres de "courant" et vérifie
     * que l'on ne retombe pas sur "cible".
     */
    private void explorerAncetres(Individu courant,
                                  Individu cible,
                                  HashSet<Individu> visites)
            throws CycleGenealogiqueException {
        if (courant == null) {
            return;
        }
        if (visites.contains(courant)) {
            return;
        }
        visites.add(courant);

        Famille origine = courant.getFamilleOrigine();
        if (origine != null) {
            Individu pere = origine.getPere();
            Individu mere = origine.getMere();

            if (pere != null) {
                if (pere == cible) {
                    throw new CycleGenealogiqueException(
                        "Cycle détecté : " + cible.getDescriptionSimple()
                        + " est son propre ancêtre.");
                }
                explorerAncetres(pere, cible, visites);
            }
            if (mere != null) {
                if (mere == cible) {
                    throw new CycleGenealogiqueException(
                        "Cycle détecté : " + cible.getDescriptionSimple()
                        + " est son propre ancêtre.");
                }
                explorerAncetres(mere, cible, visites);
            }
        }
    }

    /**
     * Sauvegarde la généalogie dans un fichier binaire (sérialisation).
     */
    public void sauvegarder(String chemin) throws IOException {
        ObjectOutputStream out =
            new ObjectOutputStream(new FileOutputStream(chemin));
        out.writeObject(this);
        out.close();
    }

    /**
     * Charge une généalogie depuis un fichier binaire (sérialisation).
     */
    public static Genealogie charger(String chemin)
            throws IOException, ClassNotFoundException {
        ObjectInputStream in =
            new ObjectInputStream(new FileInputStream(chemin));
        Genealogie g = (Genealogie) in.readObject();
        in.close();
        return g;
    }
}
