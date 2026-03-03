package projet.gedcom;

/**
 * Tag simple avec une valeur texte.
 */
public abstract class TagSimple implements TagGedcom {

    protected String valeur;

    public TagSimple(String valeur) {
        this.valeur = valeur;
    }

    public String getValeur() {
        return valeur;
    }
}
