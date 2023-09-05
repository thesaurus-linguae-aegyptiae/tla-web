package tla.web.service;

import java.util.Optional;

//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tla.web.model.Sentence;
import tla.web.model.Text;
//import tla.web.model.ThsEntry;
import tla.web.model.meta.ModelClass;
import tla.web.model.meta.ObjectDetails;
import tla.web.model.meta.ObjectsContainer;
import tla.web.model.meta.SearchResults;
//import tla.web.repo.TlaClient;

//import java.util.List;

@Service
@ModelClass(Sentence.class)
public class SentenceService extends ObjectService<Sentence> {
	 //@Autowired
	 //protected TlaClient backend;
    
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
    Optional<ObjectDetails<Sentence>> injectTextObject(Optional<ObjectDetails<Sentence>> container) {
        if (container.isPresent()) {
            var sentence = container.get().getObject();
            sentence.setText(
                this.lookupSentenceText(sentence, container.get())
            );
        }
        return container;
    }

    @Override
    public Optional<ObjectDetails<Sentence>> getDetails(String id) {
        return this.injectTextObject(
            super.getDetails(id)
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
    
    /*public List<Sentence> searchSentencesByContextTextId(String textId) {
    	System.out.println("Search all sentences with "+textId);
        return backend.getSentencesByTextId(textId);
    }*/

}