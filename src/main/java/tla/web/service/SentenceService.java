package tla.web.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import tla.web.model.Sentence;
import tla.web.model.Text;
import tla.web.model.meta.ModelClass;
import tla.web.model.meta.ObjectDetails;

@Service
@ModelClass(Sentence.class)
public class SentenceService extends ObjectService<Sentence> {

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
            (Text) container.getRelated().get("BTSText").getOrDefault(
                sentence.getContext().getTextId(), null
            )
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

}