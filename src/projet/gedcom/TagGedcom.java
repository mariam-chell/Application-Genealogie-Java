package projet.gedcom;

/**
 * Interface générale pour un tag GEDCOM.
 * Chaque tag sait appliquer sa valeur sur le contexte courant.
 */
public interface TagGedcom {
    String getNomTag();
    void appliquerSurContexte(ContexteGedcom contexte);
}
