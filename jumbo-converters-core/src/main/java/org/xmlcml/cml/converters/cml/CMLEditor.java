package org.xmlcml.cml.converters.cml;

import org.xmlcml.cml.base.CMLElement;

/** edits one of CMLMolecule, Spect, React, etc.
 * May later need to be mapped onto conventions
 * @author pm286
 *
 */
public interface CMLEditor {

	void executeCommand(CMLElement cmlIn);

}
