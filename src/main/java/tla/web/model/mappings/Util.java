package tla.web.model.mappings;

import java.awt.geom.Rectangle2D;
import java.io.StringWriter;
import java.util.List;

import org.apache.commons.lang3.RegExUtils;
import org.qenherkhopeshef.graphics.svg.SVGGraphics2D;

import jsesh.mdc.MDCParserModelGenerator;
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

    public static final String SERIF_FONT_MARKUP_REGEX = "\\$([^$]+)\\$";
    public static final String SERIF_FONT_MARKUP_REPLACEMENT = "<span class=\"bbaw-libertine\">$1</span>";

    private static final String XML_HEAD = "<?xml version='1.0' encoding='UTF-8' standalone='yes'?>";

    /**
     * Tries to use JSesh in order to render an MdC hieroglyph encoding
     * into an SVG vector graphic.
     * @param mdc hieroglyph sequence in Manuel de Codage (MdC)
     * @param rubrum whether to render entire MdC sequence in red
     * @return textual serialization of SVG vector graphic or null
     */
    public static String jseshRender(String mdc, boolean rubrum) {
        if (mdc != null) {
            MDCDrawingFacade facade = new MDCDrawingFacade();
            StringWriter writer = new StringWriter();
            try {
                Rectangle2D boundingBox = facade.getBounds(
                    mdc, 0, 0
                );
                List<TopItem> parsed = new MDCParserModelGenerator().parse(mdc).asList();
                parsed.forEach(item -> item.setRed(rubrum));
                TopItemList topItems = new TopItemList();
                topItems.addAll(parsed);
                facade.draw(
                    topItems,
                    new SVGGraphics2D(
                        writer,
                        new DoubleDimensions(
                            boundingBox.getWidth(),
                            boundingBox.getHeight()
                        )
                    ),
                    0, 0
                );
            } catch (Exception e) {
                log.warn(
                    "Jsesh could not render hieroglyph encoding '{}': {}",
                    mdc,
                    e.toString()
                );
            }
            String jsesh = writer.toString();
            if (jsesh.length() > XML_HEAD.length()) {
                jsesh = String.format(
                    "%s</svg>",
                    jsesh.substring(XML_HEAD.length())
                );
            }
            return jsesh;
        } else {
            return null;
        }
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
     * Parses the input and replaces <code>$nfr$</code> markup
     * with HTML tags.
     */
    public static String escapeMarkup(String text) {
        String escaped = RegExUtils.replacePattern(
            text,
            SERIF_FONT_MARKUP_REGEX,
            SERIF_FONT_MARKUP_REPLACEMENT
        );
        if (escaped != null) {
            return escaped.replaceAll("\\n", "<br/>");
        }
        return escaped;
    }

}