package tla.web.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import tla.web.model.Sentence;
import tla.web.model.Text;
import tla.web.model.meta.ModelClass;
import tla.web.model.meta.ObjectDetails;
import tla.web.model.meta.ObjectsContainer;
import tla.web.model.meta.SearchResults;

@Service
@ModelClass(Sentence.class)
public class SentenceService extends ObjectService<Sentence> {

    protected Text lookupSentenceText(Sentence sentence, ObjectsContainer container) {
        return (Text) container.getRelated().get("BTSText").getOrDefault(
            sentence.getContext().getTextId(), null
        );
    }

    /**
     * Looks up the text to which a sentence belongs from its object details container's
     * related objects ({@link ObjectDetails#getRelated()}) and writes it into the sentence
     * object itself so that is has access to text metadata.
     *
     * @see ObjectDetails#getRelated()
     */
    ObjectDetails<Sentence> injectTextObject(ObjectDetails<Sentence> container) {
        var sentence = container.getObject();
        sentence.setText(
            this.lookupSentenceText(sentence, container)
        );
        return container;
    }

    @Override
    public Optional<ObjectDetails<Sentence>> getDetails(String id) {
        return Optional.of(
            this.injectTextObject(
                super.getDetails(id).orElseThrow()
            )
        );
    }

    @Override
    public String getLabel(Sentence object) {
        return object.getName();
    }

    @Override
    protected SearchResults preProcess(SearchResults searchResults) {
        super.preProcess(searchResults).getObjects().forEach(
            object -> {
                var sentence = (Sentence) object;
                sentence.setText(
                    this.lookupSentenceText(sentence, searchResults)
                );
            }
        );
        return searchResults;
    }

}