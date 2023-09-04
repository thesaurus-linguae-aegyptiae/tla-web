package tla.web.model.mappings;

import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

import org.apache.commons.lang3.RegExUtils;
import org.qenherkhopeshef.graphics.svg.SVGGraphics2D;

import jsesh.hieroglyphs.graphics.DefaultHieroglyphicFontManager;
import jsesh.hieroglyphs.graphics.DirectoryHieroglyphicFontManager;
import jsesh.mdc.MDCParserModelGenerator;
import jsesh.mdc.MDCSyntaxError;
import jsesh.mdc.model.TopItem;
import jsesh.mdc.model.TopItemList;
import jsesh.mdcDisplayer.draw.MDCDrawingFacade;
import jsesh.mdcDisplayer.preferences.*;
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
	private static DrawingSpecification drawingSpecifications = new DrawingSpecificationsImplementation();
	
	public static void prepareFonts() {
		DefaultHieroglyphicFontManager manager = DefaultHieroglyphicFontManager.getInstance();
		manager.addHieroglyphicFontManager(new DirectoryHieroglyphicFontManager(new File("src/main/resources/static/jsesh")));
}
	

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
           	prepareFonts();
            	//System.out.println("MDC "+mdc);
					
					
					// Change a number of parameters, using the DrawingSpecificationsImplementation
					// class.
					drawingSpecifications.setSmallSignsCentered(true);
					drawingSpecifications.setMaxCadratHeight(18);
					drawingSpecifications.setMaxCadratWidth(18);
					facade.setDrawingSpecifications(drawingSpecifications);
		
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
				text = text.replaceAll("\\r?\\n", "<br/>");
				
				// Set style of non-Unicode glyphs in gyphs.unicode
				if (text.contains("</g>")) {
					text = text.replace("<g>", "<span class=\"latin-in-hiero\">");
					text = text.replace("</g>", "</span>");
				}
				
				// Set style of du./pl. markers
				text = text.replaceAll("([:\\.\\(\\[{〈⸮⸢])(DU|PL)", "$1<span class=\"ling-glossing-transliteration\">$2</span>");
				
				// Cut out parts in 〈 ... 〉 in marked labels
				if (text.trim().startsWith("<label>") && text.trim().endsWith("</label>")) {
               text = text.replace("<label>", "");
               text = text.replace("</label>", "");
               text = text.replace("〈", "");
               text = text.replace("〉", "");
               text = text.trim();
				}
				// Cut out 〈 and 〉 in marked titles
				if (text.trim().startsWith("<shortlabel>") && text.trim().endsWith("</shortlabel>")) {
               text = text.replace("<shortlabel>", "");
               text = text.replace("</shortlabel>", "");
               text = text.trim();
               if ((text.lastIndexOf("〈") == 0)  && (text.indexOf("〉") == text.length()-1) ){
                  // Typ "〈A〉" => "A"
                  text = text.replace("〈", "");
                  text = text.replace("〉", "");
               } else {
                  // Typ "〈A〉B〈C〉" => "B"
                  text = text.replaceAll("〈.*?〉", "");
               }
					
					// Treat triple point workaround
					//text = text.replace("\u205d", ":"); // not necessary anymore
				}
        }
        return text;
    }

}
