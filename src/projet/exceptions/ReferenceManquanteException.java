package projet.exceptions;

/**
 * Exception levée lorsqu'une famille ou un individu référencé n'existe pas
 * en tant qu'enregistrement explicite dans le fichier GEDCOM.
 */
public class ReferenceManquanteException extends GenealogieException {

    public ReferenceManquanteException(String message) {
        super(message);
    }
}
