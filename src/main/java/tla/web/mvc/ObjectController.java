package tla.web.mvc;

import java.lang.annotation.Annotation;
import java.util.List;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import lombok.extern.slf4j.Slf4j;
import tla.web.model.ObjectDetails;
import tla.web.model.TLAObject;
import tla.web.model.ui.BreadCrumb;
import tla.web.model.ui.TemplateModelName;
import tla.web.service.ObjectService;

/**
 * Controllers implementing this class must be annotated with {@link Controller}, {@link TemplateModelName}, and {@link RequestMapping}.
 */
@Slf4j
public abstract class ObjectController<T extends TLAObject> {

    private String templatePath = null;

    /**
     * Extract template path from {@link TemplateModelName} annotation. Template path
     * is being used in order to locate the HTML template for the single object details
     * view, and for message properties for i18n.
     */
    protected String getTemplatePath() {
        if (this.templatePath == null) {
            for (Annotation a : getClass().getAnnotations()) {
                if (a instanceof TemplateModelName) {
                    this.templatePath = ((TemplateModelName) a).value();
                }
            }
        }
        return this.templatePath;
    }

    public abstract ObjectService<T> getService();

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String getSingleObjectDetailsPage(Model model, @PathVariable String id) {
        log.debug("Compile lemma detail view data for {} {}", getTemplatePath(), id);
        ObjectDetails<T> container = getService().get(id);
        model = compileSingleObjectDetailsModel(model, container);
        model.addAttribute(
            "breadcrumbs",
            List.of(
                BreadCrumb.of("/", "menu_global_home"),
                BreadCrumb.of("/search", "menu_global_search"),
                BreadCrumb.of(
                    String.format("caption_details_%s", getTemplatePath())
                )
            )
        );
        model.addAttribute("obj", container.getObject());
        model.addAttribute("related", container.getRelatedObjects());
        return String.format("%s/details", getTemplatePath());
    }

    /**
     * Subclasses must return model object, regardless of whether they add attributes to it.
     */
    protected abstract Model compileSingleObjectDetailsModel(Model model, ObjectDetails<T> container);

}