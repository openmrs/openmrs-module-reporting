package org.openmrs.module.reporting.template;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Options;
import org.openmrs.Concept;
import org.openmrs.api.ConceptService;
import org.openmrs.messagesource.MessageSourceService;
import org.openmrs.module.reporting.ReportingConstants;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Any method in this class that returns a String (or CharSequence) will be exposed as a helper for handlebars templates
 */
public class HandlebarsHelpers {

    private MessageSourceService mss;

    private ConceptService conceptService;

    public HandlebarsHelpers(MessageSourceService mss, ConceptService conceptService) {
        this.mss = mss;
        this.conceptService = conceptService;
    }

    /**
     * This will be exposed as a handlebars helper.
     * Usage is like: {{ message location.uuid prefix="ui.i18n.Location.name." }}
     * @param key
     * @param options
     * @return
     */
    public CharSequence message(String key, Options options) {
        String fullKey = options.hash("prefix", "") + key + options.hash("suffix", "");
        String localized = mss.getMessage(fullKey);
        if (localized == null) {
            return "";
        } else {
            return new Handlebars.SafeString(localized);
        }
    }

    /**
     * This will be exposed as a handlebars helper.
     * Usage is like: {{ conceptName "PIH:Lab only" }}
     * @param sourceAndCode
     * @return
     */
    public CharSequence conceptName(String sourceAndCode) {
        try {
            String[] split = sourceAndCode.split(":");
            Concept concept = conceptService.getConceptByMapping(split[1], split[0]);
            return new Handlebars.SafeString(concept.getName().getName());
        } catch (Exception ex) {
            return sourceAndCode;
        }
    }

    /**
     * This will be exposed as a handlebars helper.
     * Usage is like: {{ formatDate request.evaluateStartDatetime "yyyyMMdd" }}
     * @param date
     * @param dateFormat defaults to "yyyy-MM-dd"
     * @return
     */
    public CharSequence formatDate(Date date, String dateFormat) {
        if (date == null) {
            return "";
        }
        if (dateFormat == null) {
            dateFormat = "yyyy-MM-dd";
        }
        Locale locale = ReportingConstants.GLOBAL_PROPERTY_DEFAULT_LOCALE();
        SimpleDateFormat df = locale == null ? new SimpleDateFormat(dateFormat) : new SimpleDateFormat(dateFormat, locale);
        return df.format(date);
    }

}
