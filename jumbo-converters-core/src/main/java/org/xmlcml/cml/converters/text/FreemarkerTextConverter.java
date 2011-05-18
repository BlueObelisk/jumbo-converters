package org.xmlcml.cml.converters.text;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collections;
import java.util.List;

import nu.xom.Element;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public abstract class FreemarkerTextConverter extends XML2TextConverter {
	
	protected Configuration cfg;
	protected String templateName;
	
	public FreemarkerTextConverter(String templatePath, String templateName) {
		this.cfg = new Configuration();
		this.cfg.setClassForTemplateLoading(getClass(), templatePath);
		
		// Specify how templates will see the data-model. This is an advanced topic...
		// but just use this:
		this.cfg.setObjectWrapper(new DefaultObjectWrapper());
		
		this.templateName = templateName;
	}
		
	@Override
	public List<String> convertToText(Element xmlInput) {
		StringWriter stringWriter = new StringWriter();
		try {
			write(xmlInput, stringWriter);
		} catch (IOException e) {
			throw new RuntimeException("Error converting file", e);
		}
		return Collections.singletonList(stringWriter.toString());
	}

	protected Template getTemplate() throws IOException {
		return this.cfg.getTemplate(getTemplateName());
	}
	
	protected String getTemplateName() {
		return templateName;
	}
	
	protected abstract Object getDataModel(Element xmlInput);
	
	protected void write(Element xmlInput, Writer writer) throws IOException {
		/* ------------------------------------------------------------------- */    
        /* You usually do these for many times in the application life-cycle:  */    
                
        /* Get or create a template */
        Template ftlTemplate = getTemplate();
        
        /* Create a data-model */
        Object dataModel = getDataModel(xmlInput);
    	try {
			ftlTemplate.process(dataModel, writer);
		} catch (TemplateException e) {
			throw new IOException("Error processing template", e);
		}
        writer.flush();
	}
	
}
