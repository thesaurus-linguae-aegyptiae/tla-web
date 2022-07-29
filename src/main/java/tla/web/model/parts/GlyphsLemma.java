package tla.web.model.parts;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tla.web.model.mappings.Util;
import lombok.AllArgsConstructor;

@Getter
@Setter
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class GlyphsLemma {

    public static final GlyphsLemma EMPTY = new GlyphsLemma();

    private String unicode;

    private String mdcCompact;
    private String svg;
    

    /**
     * Creates UI hieroglyphs representation of Manuel de Codage encoding.
     */
    public static GlyphsLemma of(String mdc) {
        return of(mdc, false);
    }

    /**
     * Creates UI hieroglyphs representation of Manuel de Codage encoding.
     * @param rubrum whether or not to render the entire thing in red
     */
    public static GlyphsLemma of(String mdc, boolean rubrum) {
        return GlyphsLemma.builder()
     
            .mdcCompact(mdc)
            .unicode(mdc)
            .svg(Util.jseshRender(mdc, rubrum))
            .build();
    }

    /**
     * Creates UI hieroglyphs representation for DTO glyphs object, i.e. renders SVG graphics
     * using JSesh with optional rubrum characteristics.
     */
    
    public static GlyphsLemma of(tla.domain.dto.LemmaDto.Glyphs dto, boolean rubrum) {
   
        if (dto != null) {
        	System.out.println("Lemma DTO");
            return GlyphsLemma.builder()
                .mdcCompact(dto.getMdcCompact())
                .unicode(dto.getUnicode())
                .svg(Util.jseshRender(dto.getMdcCompact(), rubrum))
                .build();
        } else {
        	System.out.println("Lemma DTO EMPTY");
            return GlyphsLemma.EMPTY;
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

