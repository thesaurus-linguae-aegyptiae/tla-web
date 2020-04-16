package tla.web.model;

import java.util.List;
import java.util.stream.Collectors;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import tla.domain.model.Passport;
import tla.domain.model.meta.BTSeClass;
import tla.web.model.mappings.Util;

@Data
@NoArgsConstructor
@BTSeClass("BTSAnnotation")
@EqualsAndHashCode(callSuper = true)
public class Annotation extends TLAObject {

    /**
     * Escape markup before returning value.
     */
    @Override
    public String getName() {
        return Util.escapeMarkup(super.getName());
    }

    /**
     * Lemma annotations have textual content in their passports
     * and we want to access that conveniently so just copy it
     * into here.
     * Might return null tho.
     * Escapes markup.
     */
    public String getBody() {
        if (this.getPassport() != null) {
            List<Passport> lemmaComments = this.getPassport().extractProperty(
                "annotation.lemma"
            );
            if (!lemmaComments.isEmpty()) {
                return Util.escapeMarkup(
                    String.join(
                        "\n",
                        lemmaComments.stream().map(
                            node -> node.getLeafNodeValue()
                        ).collect(
                            Collectors.toList()
                        )
                    )
                );
            }
        }
        return "";
    }

}