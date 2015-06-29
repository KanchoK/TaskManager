package taskManager.utils;

import java.io.StringWriter;
import java.util.Map;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

public class TemplateFIller {

	private static final String UTF8 = "UTF8";

	public static String fillTemplate(String templateName,
			Map<String, Object> mailParams) {
		
		// initialize velocity engine
		VelocityEngine ve = new VelocityEngine();
		initVelocityEngine(ve);

		// get mail template
		Template t = ve.getTemplate(templateName);
		t.setEncoding(UTF8);

		// make a context
		VelocityContext context = new VelocityContext(mailParams);

		// render a template
		StringWriter stringWriter = new StringWriter();
		ve.mergeTemplate(t.getName(), UTF8, context, stringWriter);
		String emailContent = stringWriter.toString();
		
		return emailContent;
	}

	private static void initVelocityEngine(VelocityEngine ve) {
		ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
		ve.setProperty("classpath.resource.loader.class",
				ClasspathResourceLoader.class.getName());
		ve.init();
	}
}
