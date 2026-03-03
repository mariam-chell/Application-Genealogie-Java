package projet.exceptions;

/**
 * Exception levée lorsqu'un cycle est détecté dans la généalogie,
 * par exemple lorsqu'un individu est son propre ancêtre.
 */
public class CycleGenealogiqueException extends GenealogieException {

    public CycleGenealogiqueException(String message) {
        super(message);
    }
}
