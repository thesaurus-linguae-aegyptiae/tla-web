package tla.web.config;

import static org.junit.jupiter.api.Assertions.*;

import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;
import org.springframework.web.util.UriTemplate;

public class TestLinkFormatter {

    final static String EXPECT_URL = "http://domain.xyz/10070";

    @Test
    public void dontFormatNullIDs() {
        LinkFormatter f = new LinkFormatter();
        assertNull(f.format(null));
    }

    @Test
    public void formatIdOnly() {
        LinkFormatter f = new LinkFormatter();
        f.setDefaultFormat(new UriTemplate("http://domain.xyz/{id}"));
        assertAll("format ID works if only default format exists",
            () -> assertEquals(EXPECT_URL, f.format("10070")),
            () -> assertEquals(EXPECT_URL, f.format("10070", null)),
            () -> assertEquals(EXPECT_URL, f.format("10070", "type"), "don't care if typed is provided")
        );
    }

    @Test
    public void formatIdOnly_UseTypedDefault() {
        LinkFormatter f = new LinkFormatter();
        f.getTypeFormats().put(
            "default",
            new UriTemplate("http://domain.xyz/{id}")
        );
        assertAll("format ID works if 'default' is a template type",
            () -> assertEquals(EXPECT_URL, f.format("10070")),
            () -> assertEquals(EXPECT_URL, f.format("10070", null)),
            () -> assertEquals(EXPECT_URL, f.format("10070", "type"), "don't care if type provided")
        );
    }

    @Test
    public void formatIdAndType_NoTypedTemplates() {
        LinkFormatter f = new LinkFormatter();
        f.setDefaultFormat(new UriTemplate("http://domain.xyz/{id}"));
        assertEquals(EXPECT_URL, f.format("10070", "whatever"));
    }

    @Test
    public void formatIdAndType_TypeUndefined() {
        LinkFormatter f = new LinkFormatter();
        f.getTypeFormats().put(
            "default",
            new UriTemplate("http://domain.xyz/{id}")
        );
        assertEquals(EXPECT_URL, f.format("10070", "whatever"));
    }

    @Test
    public void formatIdAndType_DefaultFormat() {
        LinkFormatter f = new LinkFormatter();
        f.setDefaultFormat(
            new UriTemplate("http://domain.xyz/{type}/{id}")
        );
        assertEquals("http://domain.xyz/text/10070", f.format("10070", "text"));
    }

    @Test
    public void dontFormatIdMatchingURLPattern() {
        LinkFormatter f = new LinkFormatter();
        f.setDefaultFormat(
            new UriTemplate("http://domain.xyz/{id}")
        );
        f.setIdPattern(Pattern.compile("^[0-9]*$"));
        assertNull(f.format("http://domain.org/path/"));
    }

    @Test
    public void formatIdAndType_TypeUndefined_DefaultFormat() {
        LinkFormatter f = new LinkFormatter();
        f.getTypeFormats().put(
            "type",
            new UriTemplate("http://domain.xyz/{type}/{id}")
        );
        f.setDefaultFormat(new UriTemplate("http://domain.xyz/{id}"));
        assertEquals(EXPECT_URL, f.format("10070", "whatever"));
    }

    @Test
    public void formatIdAndType_TypeDefined() {
        LinkFormatter f = new LinkFormatter();
        f.getTypeFormats().put(
            "whatever",
            new UriTemplate("http://domain.xyz/{type}/{id}")
        );
        assertEquals("http://domain.xyz/whatever/10070", f.format("10070", "whatever"));
    }

    @Test
    public void noTemplate() {
        LinkFormatter f = new LinkFormatter();
        assertNull(f.format("10070"));
        assertNull(f.format("10070", "type"));
    }

    @Test
    public void noMatchingTemplate() {
        LinkFormatter f = new LinkFormatter();
        f.getTypeFormats().put(
            "whatever",
            new UriTemplate("http://domain.xyz/{type}/{id}")
        );
        assertNull(f.format("10070"));
        assertNull(f.format("10070", "type"));
    }

}
