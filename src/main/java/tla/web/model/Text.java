package tla.web.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tla.domain.dto.TextDto;
import tla.domain.model.meta.BTSeClass;
import tla.domain.model.meta.TLADTO;
import tla.web.model.meta.BackendPath;

@Getter
@Setter
@NoArgsConstructor
@BackendPath("text")
@BTSeClass("BTSText")
@TLADTO(TextDto.class)
public class Text extends CorpusObject {

    private TextDto.WordCount wordCount;
    public static final String PASSPORT_PROP_BIBL = "bibliography.bibliographical_text_field";
    
    @Setter(AccessLevel.NONE)
    private List<String> bibliography;
    public List<String> getBibliography() {
        if (this.bibliography == null) {
            this.bibliography = extractBibliography(this);
        }
        return this.bibliography;
    }

    /**
     * Extract bibliographic information from text passport.
     *
     * Bibliography is being copied from the <code>bibliography.bibliographical_text_field</code>
     * passport field. The value(s) found under that locator are split at line breaks <code>\r\n</code>.
     *
     * @param text The text instance from whose passport the bibliography is to be extracted.
     * @return List of textual bibliographic references or an empty list
     */
    private static List<String> extractBibliography(Text text) {
        List<String> bibliography = new ArrayList<>();
        try {
            text.getPassport().extractProperty(
                PASSPORT_PROP_BIBL
            ).forEach(
                node -> bibliography.addAll(
                    Arrays.asList(
                        node.getLeafNodeValue().replaceAll("(\\r\\n|^)[\\s\\-]+", "$1").replaceAll("\\r\\n[\\r\\n\\s]*", "<br/>||").split("\\|\\|")
                    ).stream().map(
                        bibref -> bibref.strip()
                    ).collect(
                        Collectors.toList()
                    )
                )
            );
        } catch (Exception e) {
          System.out.println("could not extract bibliography from text {} "+text.getId());
        }
        return bibliography;
    }


}
