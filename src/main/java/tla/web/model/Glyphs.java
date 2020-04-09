package tla.web.model;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import tla.web.model.mappings.Util;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Glyphs {

    private String unicode;

    private String mdc;

    private String svg;

    public static Glyphs of(String mdc) {
        return Glyphs.builder()
            .mdc(mdc)
            .svg(Util.jseshRender(mdc))
            .build();
    }

}

