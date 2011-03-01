package org.xmlcml.cml.converters.text;

import nu.xom.Element;

/** this is messay and is breaking away from LegacyProcessor machinery
 * However they still co-exist and so some subclassed methods are no-ops or override
 * @author pm286
 *
 */
public abstract class TemplateConverter extends Text2XMLConverter {

		protected Template template;

		public TemplateConverter(Element templateElement) {
			super();
			legacyProcessor = createLegacyProcessor();
			this.template = new Template(templateElement);
//			this.template.debug();
		}

}
