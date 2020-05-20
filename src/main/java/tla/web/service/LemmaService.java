package tla.web.service;

import tla.domain.command.LemmaSearch;
import tla.domain.dto.DocumentDto;
import tla.domain.dto.extern.SearchResultsWrapper;
import tla.domain.dto.extern.SingleDocumentWrapper;
import tla.web.model.Annotation;
import tla.web.model.Lemma;
import tla.web.model.ObjectDetails;
import tla.web.model.SearchResults;
import tla.web.model.TLAObject;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class LemmaService extends ObjectService<Lemma> {

    @Override
    protected SingleDocumentWrapper<DocumentDto> retrieveSingleDocument(String id) {
        return backend.retrieveObject(Lemma.class, id);
    }

    /**
     * Tries to extract a list of annotations from a object details container. Might return null.
     * TODO: should probably be a method of the object details container itself.
     */
    public List<Annotation> extractAnnotations(ObjectDetails<Lemma> container) {
        Map<String, Map<String, TLAObject>> related = container.getRelatedObjects();
        if (related != null && related.containsKey("BTSAnnotation") && !related.get("BTSAnnotation").isEmpty()) {
            return related.get("BTSAnnotation").values().stream().map(
                relatedObject -> relatedObject instanceof Annotation ? (Annotation) relatedObject : null
            ).filter(
                annotation -> annotation != null && (annotation.getName() != null && annotation.getBody() != null)
            ).collect(
                Collectors.toList()
            );
        } else {
            return null;
        }
    }

    /**
     * Extract bibliographic information from lemma.
     *
     * @param lemma Lemma object from internal model
     * @return list of textual bibliographic references or null
     */
    public List<String> extractBibliography(Lemma lemma) {
        try {
            List<String> bibliography = new ArrayList<>();
            lemma.getPassport().extractProperty(
                "bibliography.bibliographical_text_field"
            ).forEach(
                node -> bibliography.addAll(
                    Arrays.asList(
                        node.getLeafNodeValue().split(";")
                    ).stream().map(
                        bibref -> bibref.strip()
                    ).collect(
                        Collectors.toList()
                    )
                )
            );
            return bibliography;
        } catch (Exception e) {
            log.warn("could not extract bibliography from lemma {}", lemma.getId());
            return null;
        }
    }

    public SearchResults search(LemmaSearch command, Integer page) {
        SearchResultsWrapper<DocumentDto> response = backend.lemmaSearch(command, page);
        SearchResults container = SearchResults.from(response);
        return container;
    }

}