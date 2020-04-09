package tla.web.model.mappings;

import java.io.StringWriter;

import java.awt.geom.Rectangle2D;
import org.qenherkhopeshef.graphics.svg.SVGGraphics2D;

import jsesh.mdcDisplayer.draw.MDCDrawingFacade;
import jsesh.utils.DoubleDimensions;
import lombok.extern.slf4j.Slf4j;

/**
 * Util
 */
@Slf4j
public class Util {

    /**
     * Tries to use JSesh in order to render an MdC hieroglyph encoding
     * into an SVG vector graphic.
     * @param mdc hieroglyph sequence in Manuel de Codage (MdC)
     * @return textual serialization of SVG vector graphic
     */
    public static String jseshRender(String mdc) {
        MDCDrawingFacade facade = new MDCDrawingFacade();
        StringWriter writer = new StringWriter();
        try {
            Rectangle2D boundingBox = facade.getBounds(
                mdc, 0, 0
            );
            facade.draw(
                mdc,
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
        return writer.toString();
    }

}