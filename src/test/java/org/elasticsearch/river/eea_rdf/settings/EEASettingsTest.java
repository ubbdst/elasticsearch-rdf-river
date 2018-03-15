package org.elasticsearch.river.eea_rdf.settings;

import org.junit.Test;

import static org.junit.Assert.*;

public class EEASettingsTest {

    @Test
    public void getTimeFormatAsString() {
        assertEquals("1.0 seconds", EEASettings.getTimeFormatAsString(1000));
        assertEquals("1.0 minutes", EEASettings.getTimeFormatAsString(60*1000));
    }


    @Test
    public void removeSpecialChars() {
        assertNull(EEASettings.removeSpecialChars(null));
        assertEquals("", EEASettings.removeSpecialChars("      "));
        assertEquals("s", EEASettings.removeSpecialChars("{s}"));
        assertEquals("anakonda", EEASettings.removeSpecialChars("[ \"anakonda]"));
        System.out.println(EEASettings.removeSpecialChars("hamad, ali rashid's masauni"));
    }
}