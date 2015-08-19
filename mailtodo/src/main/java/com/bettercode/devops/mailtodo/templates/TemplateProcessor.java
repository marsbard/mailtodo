package com.bettercode.devops.mailtodo.templates;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

import com.bettercode.devops.mailtodo.App;

import freemarker.core.ParseException;
import freemarker.template.Configuration;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateNotFoundException;

public class TemplateProcessor {
	
	private Configuration cfg;



	public TemplateProcessor() throws TemplateProcessorException {
		cfg = new Configuration(Configuration.VERSION_2_3_22);

		// Specify the source where the template files come from. Here I set a
		// plain directory for it, but non-file-system sources are possible too:
		try {
			cfg.setDirectoryForTemplateLoading(new File(App.MAIL_TEMPLATES_PATH));
		} catch (IOException e) {
			throw new TemplateProcessorException(e);
		}

		// Set the preferred charset template files are stored in. UTF-8 is
		// a good choice in most applications:
		cfg.setDefaultEncoding("UTF-8");
	}



	public String process(String template, Map<String, String> model) throws TemplateProcessorException {
		
		Template temp;
		try {
			temp = cfg.getTemplate(template + ".ftl");
		} catch (IOException e) {
			throw new TemplateProcessorException(e);
		}
		
		Writer out = new StringWriter();
		
		try {
			temp.process(model, out);
		} catch (TemplateException | IOException e) {
			throw new TemplateProcessorException(e);
		}
	
		return out.toString();
	}

}
