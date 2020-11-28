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

    private String unicode;

    private String mdc;

    private String svg;

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
            .mdc(mdc)
            .unicode(mdc)
            .svg(Util.jseshRender(mdc, rubrum))
            .build();
    }

    /**
     * Returns true if all attributes are actually empty.
     */
    public boolean isEmpty() {
        return (this.unicode == null || this.unicode.isBlank())
            && (this.mdc == null || this.mdc.isBlank());
    }

}

