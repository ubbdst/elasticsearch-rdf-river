package org.elasticsearch.river.ubb.settings;

import org.junit.Test;

import static org.junit.Assert.*;

public class EEASettingsTest {

    @Test
    public void getTimeFormatAsString() {
        assertEquals("1.0 seconds", RiverUtils.getTimeFormatAsString(1000));
        assertEquals("1.0 minutes", RiverUtils.getTimeFormatAsString(60*1000));
    }


    @Test
    public void removeSpecialChars() {
        assertNull(RiverUtils.removeSpecialChars(null));
        assertEquals("", RiverUtils.removeSpecialChars("      "));
        assertEquals("s", RiverUtils.removeSpecialChars("{s}"));
        assertEquals("anakonda", RiverUtils.removeSpecialChars("[ \"anakonda]"));
        System.out.println(RiverUtils.removeSpecialChars("hamad, ali rashid's masauni"));
    }

    @Test
    public void getSortLabel(){
        assertEquals("mama" , RiverUtils.getLabelCoalesce("http://purl.org/dc/terms/title", "mama"));
        assertEquals("" , RiverUtils.getLabelCoalesce("http://purl.org/dc/terms/title", ""));
    }
}