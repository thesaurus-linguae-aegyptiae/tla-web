package tla.web.mvc;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.ui.Model;

import tla.domain.model.ObjectPath;
import tla.domain.model.meta.Hierarchic;
import tla.web.model.meta.ObjectDetails;
import tla.web.model.meta.TLAObject;
import tla.web.model.ui.BreadCrumb;

public abstract class HierarchicObjectController<T extends TLAObject> extends ObjectController<T> {

    /**
     * converts the object reference chains by which an object can be located within the tree structure
     * it comes from (i.e. thesaurus, text corpus) to link containers that can be rendered in a template.
     *
     */
    public List<List<BreadCrumb>> createObjectPathLinks(Hierarchic object) {
        List<ObjectPath> paths = object.getPaths() != null ? object.getPaths() : new LinkedList<>();
        return paths.stream().map(
            path -> path.stream().map(
                ref -> createLink(ref)
            ).collect(
                Collectors.toList()
            )
        ).collect(
            Collectors.toList()
        );
    }

    @Override
    protected Model extendSingleObjectDetailsModel(Model model, ObjectDetails<T> container) {
        model.addAttribute(
            "objectPaths",
            createObjectPathLinks(
                (Hierarchic) container.getObject()
            )
        );
        return model;
    }

}