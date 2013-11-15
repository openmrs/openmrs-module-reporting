package org.openmrs.module.reporting.template;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import org.openmrs.messagesource.MessageSourceService;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.report.renderer.template.TemplateEvaluationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 *
 */
@Component
public class TemplateFactory {

    private MessageSourceService mss;

    private Handlebars handlebars = new Handlebars();

    @Autowired
    public TemplateFactory(@Qualifier("messageSourceService") MessageSourceService mss) {
        this.mss = mss;
        this.handlebars.registerHelpers(new HandlebarsHelpers(mss));
    }

    public Template compileHandlebarsTemplate(String template) {
        try {
            return handlebars.compileInline(template);
        } catch (IOException e) {
            throw new TemplateEvaluationException("Error compiling handlebars template: " + template, e); // this is in another package. Use this or another exception?
        }
    }

    public String evaluateHandlebarsTemplate(String template, Object context) throws EvaluationException {
        Template handlebarsTemplate = compileHandlebarsTemplate(template);
        try {
            return handlebarsTemplate.apply(context);
        } catch (IOException e) {
            throw new EvaluationException("template", e);
        }
    }
}
