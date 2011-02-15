package gigadot.semsci.converters.chem.exception;

/**
 *
 * @author Weerapong
 */
public class UnexpectedCompChemSchema extends RuntimeException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 6932355389408123334L;

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
