package projet.exceptions;

/**
 * Exception levée lorsqu'un lien bidirectionnel manque
 * entre individu et famille et a dû être corrigé.
 */
public class LienManquantException extends GenealogieException {

    public LienManquantException(String message) {
        super(message);
    }
}
