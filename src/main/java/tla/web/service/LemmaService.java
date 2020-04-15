package tla.web.service;

import tla.domain.model.Passport;
import tla.web.model.Glyphs;
import tla.web.model.Lemma;
import tla.web.model.ObjectDetails;
import tla.web.model.TLAObject;
import tla.web.model.Word;
import tla.web.repo.TlaClient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class LemmaService {

    @Autowired
    private TlaClient api;

    public ObjectDetails<Lemma> getLemma(String id) {
        ObjectDetails<TLAObject> container = ObjectDetails.from(
            api.getLemma(id)
        );
        if (container.getObject() instanceof Lemma) {
            return new ObjectDetails<Lemma>(
                (Lemma) container.getObject(),
                container.getRelatedObjects()
            );
        } else {
            log.error("expected container with lemma in it, but it was {}", container.getObject().getClass());
            return null;
        }
    }

    /**
     * Extract hieroglyphs from lemma words.
     * Return null if only empty hieroglyphs can be found.
     *
     * @param lemma Lemma object from internal model
     * @return List of all lemma word hieroglyphs, or null if there are no hieroglyphs at all
     */
    public List<Glyphs> extractHieroglyphs(Lemma lemma) {
        if (!lemma.getDictionaryName().equals(Lemma.Dictionary.DEMOTIC)) {
            List<Glyphs> hieroglyphs = lemma.getWords().stream().map(
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

    /**
     * Extract bibliographic information from lemma passport.
     *
     * @param lemma Lemma object from internal model
     * @return list of {@link Passport} leaf nodes containing textual bibliographic references
     */
    public List<Passport> extractBibliography(Lemma lemma) {
        try {
            return lemma.getPassport().extractProperty(
                "bibliography.bibliographical_text_field"
            );
        } catch (Exception e) {
            log.warn("could not extract bibliography from lemma {}", lemma.getId());
            return Collections.emptyList();
        }
    }

}
