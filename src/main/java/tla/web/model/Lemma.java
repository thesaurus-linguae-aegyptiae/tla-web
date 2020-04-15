package tla.web.model;

import java.util.List;
import java.util.SortedMap;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Singular;
import lombok.experimental.SuperBuilder;
import tla.domain.model.Language;
import tla.domain.model.extern.AttestedTimespan;
import tla.domain.model.meta.BTSeClass;

@Data
@SuperBuilder
@NoArgsConstructor
@BTSeClass("BTSLemmaEntry")
@EqualsAndHashCode(callSuper = true)
public class Lemma extends TLAObject {

    @Singular
    private SortedMap<Language, List<String>> translations;

    @Singular
    private List<Word> words;

    @Singular
    private List<AttestedTimespan> attestations;

    public Long getAttestationCount() {
        return this.attestations.stream().mapToLong(
            timespan -> timespan.getAttestations().getCount()
        ).sum();
    }

    /**
    * Determines the language phase this lemma belongs to
    */
    public Dictionary getDictionaryName() {
        return Dictionary.ofLemma(this);
    }

    /**
    * Enum specifying the different language phases.
    */
    public enum Dictionary {

        HIERATIC("hieratic"),
        DEMOTIC("demotic"),
        COPTIC("coptic");

        private String id;

        private Dictionary(String id) {
            this.id = id;
        }

        @Override
        public String toString() {
            return this.id;
        }

        public static Dictionary ofLemma(Lemma lemma) {
            char c = lemma.getId().toLowerCase().charAt(0);
            if (c == 'c') {
                return Dictionary.COPTIC;
            } else if (c == 'd') {
                return Dictionary.DEMOTIC;
            } else {
                return Dictionary.HIERATIC;
            }
        }
    }
}
