package gigadot.semsci.converters.chem.exception;

/**
 *
 * @author Weerapong
 */
public class UnexpectedCompChemSchema extends RuntimeException {

    public UnexpectedCompChemSchema(Throwable cause) {
        super(cause);
    }

    public UnexpectedCompChemSchema(String message, Throwable cause) {
        super(message, cause);
    }

    public UnexpectedCompChemSchema(String message) {
        super(message);
    }

    public UnexpectedCompChemSchema() {
        super();
    }

}
