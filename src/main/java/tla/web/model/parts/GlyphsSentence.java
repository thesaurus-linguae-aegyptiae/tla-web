package tla.web.model.parts;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tla.web.model.mappings.Util;
import lombok.AllArgsConstructor;
//import tla.domain.dto.SentenceDto;

@Getter
@Setter
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class GlyphsSentence {

    public static final GlyphsSentence EMPTY = new GlyphsSentence();

    private String unicode;

    private String mdcCompact;
    private String svg;
    

    /**
     * Creates UI hieroglyphs representation of Manuel de Codage encoding.
     */
    public static GlyphsSentence of(String mdc) {
        return of(mdc, false);
    }

    /**
     * Creates UI hieroglyphs representation of Manuel de Codage encoding.
     * @param rubrum whether or not to render the entire thing in red
     */
    public static GlyphsSentence of(String mdc, boolean rubrum) {
        return GlyphsSentence.builder()
     
            .mdcCompact(mdc)
            .unicode(mdc)
            .svg(Util.jseshRender(mdc, rubrum))
            .build();
    }

    /**
     * Creates UI hieroglyphs representation for DTO glyphs object, i.e. renders SVG graphics
     * using JSesh with optional rubrum characteristics.
     */
    
    public static GlyphsSentence of(tla.domain.model.Glyphs dto, boolean rubrum) {
                                                      // unituitive: tla.domain.dto.SentenceDto.Glyphs analogous 
                                                      // to tla.domain.dto.LemmaDto.Glyphs in GlyphsLemma does not work
        if (dto != null) {
        	//System.out.println("Sentence DTO");
            return GlyphsSentence.builder()
                .mdcCompact(dto.getMdcCompact())
                .unicode(dto.getUnicode())
                .svg(Util.jseshRender(dto.getMdcCompact(), rubrum))
                .build();
        } else {
        	//System.out.println("Sentence DTO EMPTY");
            return GlyphsSentence.EMPTY;
        }
    }

    /**
     * Returns true if all attributes are actually empty.
     */
    public boolean isEmpty() {
        return (this.unicode == null || this.unicode.isBlank())
            && (this.mdcCompact == null || this.mdcCompact.isBlank());
    }

}

