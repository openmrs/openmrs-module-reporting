package org.openmrs.module.reporting.template;

import com.github.jknack.handlebars.Options;
import org.openmrs.messagesource.MessageSourceService;

/**
 * Any method in this class that returns a String (or CharSequence) will be exposed as a helper for handlebars templates
 */
public class HandlebarsHelpers {

    private MessageSourceService mss;

    public HandlebarsHelpers(MessageSourceService mss) {
        this.mss = mss;
    }

    /**
     * This will be exposed as a handlebars helper
     * @param key
     * @param options
     * @return
     */
    public String message(String key, Options options) {
        String fullKey = options.hash("prefix", "") + key + options.hash("suffix", "");
        String localized = mss.getMessage(fullKey);
        return localized;
    }

}
