package projet.exceptions;

/**
 * Exception levée lorsqu'un individu a un sexe incohérent
 * avec son rôle (père / mère).
 */
public class SexeIncoherentException extends GenealogieException {

    public SexeIncoherentException(String message) {
        super(message);
    }
}
