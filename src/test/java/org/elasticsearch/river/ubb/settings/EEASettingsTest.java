package org.elasticsearch.river.ubb.settings;

import org.junit.Test;

import static org.junit.Assert.*;

public class EEASettingsTest {

    @Test
    public void getTimeFormatAsString() {
        assertEquals("1.0 seconds", RiverUtils.getTimeString(1000));
        assertEquals("1.0 minutes", RiverUtils.getTimeString(60*1000));
    }


    @Test
    public void removeSpecialChars() {
        String s = "jabas, na leo.";
        String s2 = "'Norge fremstillet i tegninger'";
        assertNull(RiverUtils.removeSpecialChars(null));
        assertEquals("", RiverUtils.removeSpecialChars("      "));
        assertEquals("s", RiverUtils.removeSpecialChars("{s}"));
        assertEquals("anakonda", RiverUtils.removeSpecialChars("[ \"anakonda]"));
        assertEquals("anakonda", RiverUtils.removeSpecialChars("   [ \"anakonda]"));
        assertEquals("Norge fremstillet s i tegninger", RiverUtils.removeSpecialChars("'Norge fremstillet's i tegninger'"));
        assertEquals("jabas na leo.", RiverUtils.removeSpecialChars("\"jabas, na leo.\";"));
        System.out.println(RiverUtils.removeSpecialChars(s2));
    }

    @Test
    public void getSortLabel(){
        assertEquals("mama" , RiverUtils.constructLabelSort("http://purl.org/dc/terms/title", "mama"));
        assertEquals("" , RiverUtils.constructLabelSort("http://purl.org/dc/terms/title", ""));
    }
}