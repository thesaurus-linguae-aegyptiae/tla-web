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
public class Glyphs {

    public static final Glyphs EMPTY = new Glyphs();

    private String unicode;

    private String mdcCompact;
    private String mdcOriginal;
    private String mdcOriginalSafe;
    private String mdcTla;
    private String svg;
    
    private boolean mdcArtificiallyAligned;

    /**
     * Creates UI hieroglyphs representation of Manuel de Codage encoding.
     */
    public static Glyphs of(String mdc) {
        return of(mdc, false);
    }

    /**
     * Creates UI hieroglyphs representation of Manuel de Codage encoding.
     * @param rubrum whether or not to render the entire thing in red
     */
    public static Glyphs of(String mdc, boolean rubrum) {
        return Glyphs.builder()
     
            .mdcCompact(mdc)
            .unicode(mdc)
            .svg(Util.jseshRender(mdc, rubrum))
            .build();
    }

    /**
     * Creates UI hieroglyphs representation for DTO glyphs object, i.e. renders SVG graphics
     * using JSesh with optional rubrum characteristics.
     */
    public static Glyphs of(tla.domain.model.SentenceToken.Glyphs dto, boolean rubrum) {
        if (dto != null) {
        	//System.out.println("Sentence DTO");
            return Glyphs.builder()
                .mdcCompact(dto.getMdcCompact())
                .unicode(dto.getUnicodeTla())
                .mdcArtificiallyAligned(dto.isMdcArtificiallyAligned())
                .svg(Util.jseshRender(dto.getMdcCompact(), rubrum))
                .build();
        } else {
            return Glyphs.EMPTY;
        }
    }
    
 /*   public static Glyphs of(tla.domain.dto.LemmaDto.Glyphs dto, boolean rubrum) {
        if (dto != null) {
        	System.out.println("Lemma DTO");
            return Glyphs.builder()
                .mdcCompact(dto.getMdcCompact())
                .unicode(dto.getUnicode())
                .svg(Util.jseshRender(dto.getMdcCompact(), rubrum))
                .build();
        } else {
            return Glyphs.EMPTY;
        }
    }
*/
    /**
     * Returns true if all attributes are actually empty.
     */
    public boolean isEmpty() {
        return (this.unicode == null || this.unicode.isBlank())
            && (this.mdcCompact == null || this.mdcCompact.isBlank());
    }

}

