package tla.web.model.mappings;

import java.awt.geom.Rectangle2D;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

import org.apache.commons.lang3.RegExUtils;
import org.qenherkhopeshef.graphics.svg.SVGGraphics2D;

import jsesh.mdc.MDCParserModelGenerator;
import jsesh.mdc.MDCSyntaxError;
import jsesh.mdc.model.TopItem;
import jsesh.mdc.model.TopItemList;
import jsesh.mdcDisplayer.draw.MDCDrawingFacade;
import jsesh.utils.DoubleDimensions;
import lombok.extern.slf4j.Slf4j;

/**
 * Util
 */
@Slf4j
public class Util {

    public static final String TRANSLITERATION_FONT_MARKUP_REGEX = "\\$([^$]*?)\\$";
    public static final String GREEK_FONT_MARKUP_VITTMANN_REGEX = "#g\\+([^#]*?)#g\\-";
    public static final String GREEK_FONT_MARKUP_REGEX = "<gr>([<]*?)</gr>";
    public static final String HIERO_FONT_MARKUP_REGEX = "<hiero>([<]*?)</hiero>";

    public static final String MULTILING_FONT_MARKUP_REPLACEMENT = "<span class=\"bbaw-libertine\">$1</span>";
    public static final String HIERO_FONT_MARKUP_REPLACEMENT = "<span class=\"unicode-hieroglyphs\">$1</span>";

    public static final String XML_HEAD = "<?xml version='1.0' encoding='UTF-8' standalone='yes'?>";
    public static final String SVG_ATTR_REGEX = "width=.([0-9.]+). height=.([0-9.]+).";
    public static final String SVG_ATTR_REPLACEMENT = "viewBox=\"0 0 $1 $2\"";
    private static MDCDrawingFacade facade = new MDCDrawingFacade();

    public static String patchSVG(Writer writer) {
        var jsesh = writer.toString();
        if (jsesh.startsWith("<?xml")) {
            jsesh = jsesh.substring(
                XML_HEAD.length()
            );
        }
        return RegExUtils.replacePattern(
            jsesh, SVG_ATTR_REGEX, SVG_ATTR_REPLACEMENT
        );
    }

    private static TopItemList topItems(String mdc, boolean rubrum) throws MDCSyntaxError {
        TopItemList topItems;
        if (rubrum) {
            List<TopItem> parsed = new MDCParserModelGenerator().parse(mdc).asList();
            parsed.forEach(item -> item.setRed(rubrum));
            topItems = new TopItemList();
            topItems.addAll(parsed);
        } else  {
            topItems = new MDCParserModelGenerator().parse(mdc);
        }
        return topItems;
    }

    /**
     * Tries to use JSesh in order to render an MdC hieroglyph encoding
     * into an SVG vector graphic.
     * @param mdc hieroglyph sequence in Manuel de Codage (MdC)
     * @param rubrum whether to render entire MdC sequence in red
     * @return textual serialization of SVG vector graphic or null
     */
    public static String jseshRender(String mdc, boolean rubrum) {
        if (mdc != null && !mdc.isBlank()) {
            try (StringWriter writer = new StringWriter()) {
            	System.out.println("MDC "+mdc);
                Rectangle2D boundingBox = facade.getBounds(
                    mdc, 0, 0
                );
                var svg = new SVGGraphics2D(
                    writer,
                    new DoubleDimensions(
                        boundingBox.getWidth(),
                        boundingBox.getHeight()
                    )
                );
                facade.draw(
                    topItems(mdc, rubrum), svg, 0, 0
                );
                svg.dispose();
                return patchSVG(writer);
            } catch (Exception e) {
               // log.warn(
            	System.out.println(
                    "Jsesh could not render hieroglyph encoding '{}': {}"+
                    mdc+ " "+e.toString()
                );
            }
        }
       
        return null;
    }

    /**
     * Render Manuel de Codage hieroglyph encoding to SVG.
     * @param mdc
     * @return SVG serialization
     * @see #jseshRender(String, boolean)
     */
    public static String jseshRender(String mdc) {
        return jseshRender(mdc, false);
    }

    /**
     * Parses the input and replaces <code>$nfr$</code> and <code>#g+nfr#g-</code>markup
     * with HTML tags.
     */
    public static String escapeMarkup(String text) {
        if (text != null) {
			//System.out.println("###### in escapeMarkup: " + text);
				if (text.contains("#g")) {
					text = text.replaceAll("(?<=#g\\+[^#]*)w(?=[^#]*?#g\\-)", "s"); // End-Sigma in Vittmann's encoding ("w")
					text = text.replaceAll("(?<=#g\\+[^#]*)h(?=[^#]*?#g\\-)", "\u0113"); // Eta in Vittmann's encoding ("h")
					text = text.replaceAll("(?<=#g\\+[^#]*)H(?=[^#]*?#g\\-)", "\u0112"); 
					text = text.replaceAll("(?<=#g\\+[^#]*)v(?=[^#]*?#g\\-)", "\u014D"); // Omega in Vittmann's encoding ("w")
					text = text.replaceAll("(?<=#g\\+[^#]*)V(?=[^#]*?#g\\-)", "\u014C"); 
					text = text.replaceAll("(?<=#g\\+[^#]*)%\\?(?=[^#]*?#g\\-)", "\u0342"); // Greek perispomeni in Vittmann's encoding
					text = text.replaceAll("(?<=#g\\+[^#]*)%\\)(?=[^#]*?#g\\-)", "\u0313"); // Greek, psili; spiritus lenis in Vittmann's encoding("%)")
					text = text.replaceAll("(?<=#g\\+[^#]*)%\\((?=[^#]*?#g\\-)", "\u0314"); // Greek dasia; spiritus asper in Vittmann's encoding ("%(")
					text = text.replaceAll("(?<=#g\\+[^#]*)%\\-(?=[^#]*?#g\\-)", "\u0304"); // length in Vittmann's encoding ("%-")
					text = text.replaceAll(GREEK_FONT_MARKUP_VITTMANN_REGEX, MULTILING_FONT_MARKUP_REPLACEMENT);
				}
				text = text.replaceAll(GREEK_FONT_MARKUP_REGEX, MULTILING_FONT_MARKUP_REPLACEMENT);
				text = text.replaceAll(HIERO_FONT_MARKUP_REGEX, HIERO_FONT_MARKUP_REPLACEMENT);
				text = text.replaceAll(TRANSLITERATION_FONT_MARKUP_REGEX, MULTILING_FONT_MARKUP_REPLACEMENT);
				
				// line breaks to HTML
				text = text.replace("\\n", "<br/>");
				
				// Set style of non-Unicode glyphs in gyphs.unicode
				text = text.replace("<g>", "<span class=\"latin-in-hiero\">");
				text = text.replace("</g>", "</span>");
				
				// Cut out parts in 〈 ... 〉 in marked labels
				if (text.contains("<label>")) {
					text = text.replaceAll("(<label>.*?)〈.*?〉(.*</label>)", "$1$2");
					text = text.replaceAll("(<label>.*?)〈.*?〉(.*</label>)", "$1$2"); // sic, up to two instances
					text = text.replace("<label>", "");
					text = text.replace("</label>", "");
					
					// Treat triple point workaround
					//text = text.replace("\u205d", ":"); // not necessary anymore
				}
        }
        return text;
    }

}
