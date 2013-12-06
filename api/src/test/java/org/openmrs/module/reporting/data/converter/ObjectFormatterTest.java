package org.openmrs.module.reporting.data.converter;

import java.util.Locale;

import junit.framework.Assert;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.test.BaseModuleContextSensitiveTest;

public class ObjectFormatterTest extends BaseModuleContextSensitiveTest {

    @Test
    public void shouldLocalizeObsBasedOnDefaultLocale() {
        addLocalizedNamesToYesConcept();
        Assert.assertEquals("YES", new ObjectFormatter().convert(createObsWithValueCodedYes()));
    }

    @Test
    public void shouldLocalizeObsBasedOnSetLocale() {
        addLocalizedNamesToYesConcept();
        Assert.assertEquals("Oui", new ObjectFormatter(new Locale("fr")).convert(createObsWithValueCodedYes()));
    }


    // hack to add a few localized names to a concept
    private void addLocalizedNamesToYesConcept() {

        Concept yes = Context.getConceptService().getConcept(7);

        ConceptName oui = new ConceptName();
        oui.setName("Oui");
        oui.setLocale(new Locale("fr"));

        ConceptName si = new ConceptName();
        si.setName("Si");
        si.setLocale(new Locale("es"));

        yes.addName(oui);
        yes.addName(si);

        Context.getConceptService().saveConcept(yes);
    }

    private Obs createObsWithValueCodedYes() {
        Obs yes = new Obs();
        yes.setConcept(Context.getConceptService().getConcept(12));
        yes.setValueCoded(Context.getConceptService().getConcept(7));
        return yes;
    }


}
