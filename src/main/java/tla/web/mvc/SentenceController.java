package tla.web.mvc;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import tla.domain.model.meta.Hierarchic;
import tla.web.model.Sentence;
import tla.web.model.meta.TemplateModelName;
import tla.web.model.ui.BreadCrumb;
import tla.web.model.ui.CorpusPathSegment;
import tla.web.service.ObjectService;
import tla.web.service.SentenceService;

@Controller
@RequestMapping("/sentence")
@TemplateModelName("sentence")
public class SentenceController extends HierarchicObjectController<Sentence> {

    @Autowired
    private SentenceService service;

    @Override
    public ObjectService<Sentence> getService() {
        return service;
    }

    @Override
    public List<List<BreadCrumb>> createObjectPathLinks(Hierarchic object) {
        var sentence = ((Sentence) object);
        var paths = super.createObjectPathLinks(sentence);
        paths.forEach(
            links -> {
                links.add(
                    ObjectController.createLink(
                        sentence.getText().toObjectReference()
                    )
                );
                if (sentence.getContext().getParagraph() != null) {
                    links.add(
                        new CorpusPathSegment(
                            null, sentence.getContext().getParagraph(),
                            null, "paragraph"
                        )
                    );
                }
                links.add(
                    new CorpusPathSegment(
                        null, sentence.getContext().getLine(),
                        null, "line"
                    )
                );
            }
        );
        return paths;
    }

}