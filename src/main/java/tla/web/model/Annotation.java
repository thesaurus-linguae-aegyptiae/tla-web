package tla.web.model;

import java.util.List;
import java.util.stream.Collectors;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import tla.domain.model.Passport;
import tla.domain.model.meta.BTSeClass;

@Data
@NoArgsConstructor
@BTSeClass("BTSAnnotation")
@EqualsAndHashCode(callSuper = true)
public class Annotation extends TLAObject {

    /**
     * Lemma annotations have textual content in their passports
     * and we want to access that conveniently so just copy it
     * into here.
     * Might return null tho.
     */
    public String getBody() {
        if (this.getPassport() != null) {
            List<Passport> lemmaComments = this.getPassport().extractProperty(
                "annotation.lemma"
            );
            if (!lemmaComments.isEmpty()) {
                return String.join(
                    "\n",
                    lemmaComments.stream().map(
                        node -> node.getLeafNodeValue()
                    ).collect(
                        Collectors.toList()
                    )
                );
            }
        }
        return "";
    }

}