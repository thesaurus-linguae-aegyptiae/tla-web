package tla.web.model;

import java.util.List;
import java.util.SortedMap;
import java.util.stream.Collectors;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Singular;
import lombok.experimental.SuperBuilder;
import tla.domain.dto.LemmaDto;
import tla.domain.model.Language;
import tla.domain.model.Script;
import tla.domain.model.extern.AttestedTimespan;
import tla.domain.model.meta.BTSeClass;
import tla.domain.model.meta.TLADTO;

@Data
@SuperBuilder
@NoArgsConstructor
@BackendPath("lemma")
@TLADTO(LemmaDto.class)
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
    public Script getDictionaryName() {
        return Script.ofLemmaId(this.getId());
    }

    /**
     * Extract hieroglyphs from lemma words.
     * Return null if only empty hieroglyphs can be found.
     *
     * @return List of all lemma word hieroglyphs, or null if there are no hieroglyphs at all
     */
    public List<Glyphs> getHieroglyphs() {
        if (!this.getDictionaryName().equals(Script.DEMOTIC)) {
            List<Glyphs> hieroglyphs = this.getWords().stream().map(
                Word::getGlyphs
            ).collect(
                Collectors.toList()
            );
            if (hieroglyphs.stream().allMatch(
                glyphs -> glyphs == null || glyphs.isEmpty()
            )) {
                return null;
            } else {
                return hieroglyphs;
            }
        }
        return null;
    }

}
