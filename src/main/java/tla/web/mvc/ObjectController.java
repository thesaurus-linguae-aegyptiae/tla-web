package tla.web.mvc;

import java.lang.annotation.Annotation;
import java.util.List;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import lombok.extern.slf4j.Slf4j;
import tla.error.ObjectNotFoundException;
import tla.web.model.ui.BreadCrumb;
import tla.web.model.meta.ObjectDetails;
import tla.web.model.meta.TemplateModelName;
import tla.web.model.meta.TLAObject;
import tla.web.service.ObjectService;

/**
 * Generic view controller with standard request handlers.
 *
 * Controllers implementing this class must be annotated with {@link Controller},
 * {@link TemplateModelName}, and {@link RequestMapping}.
 */
@Slf4j
public abstract class ObjectController<T extends TLAObject> {

    private String templatePath = null;

    /**
     * Extract template path from {@link TemplateModelName} annotation. Template path
     * is being used in order to locate the HTML template for the single object details
     * view, and for message properties for i18n.
     */
    @ModelAttribute("objectType") // TODO: objecttype und template path trennen
    public String getTemplatePath() {
        if (this.templatePath == null) {
            for (Annotation a : getClass().getAnnotations()) {
                if (a instanceof TemplateModelName) {
                    this.templatePath = ((TemplateModelName) a).value();
                }
            }
        }
        return this.templatePath;
    }

    /**
     * Must return an appropriate {@link ObjectService} instance for a particular controller
     * to be able to invoke operations targeting the entity model class it has been typed for.
     * @return An {@link ObjectService} instance providing access to entities of the specific type
     * required by this controller.
     */
    public abstract ObjectService<T> getService();

    /**
     * Retrieves the requested plus relevant related entites, and renders the results into the
     * single object details template defined for the entity type supported. To this end, it is
     * necessary that a template file <code>details.html</code> exists within a templates folder
     * subdirectory whose name matches the value given in the controller's {@link TemplateModelName}
     * annotation. So the template to be used for rendering the lemma details view must be in the file
     * <code>src/main/resources/templates/lemma/details.html</code> so that the controller
     * <code>@TemplateModelName("lemma") class LemmaController extends ObjectController<Lemma></code>
     * can use it.
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String getSingleObjectDetailsPage(@PathVariable String id, Model model) {
        log.debug("Compile lemma detail view data for {} {}", getTemplatePath(), id);
        ObjectDetails<T> container = getService().getDetails(id).orElseThrow(
            () -> new ObjectNotFoundException(id, getTemplatePath())
        );
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
        model.addAttribute("related", container.getRelated());
        model.addAttribute("relations", container.extractRelatedObjects());
        model = extendSingleObjectDetailsModel(model, container);
        return String.format("%s/details", getTemplatePath());
    }

    /**
     * Subclasses can extend the view model by overriding this method.
     */
    protected Model extendSingleObjectDetailsModel(Model model, ObjectDetails<T> container) {
        return model;
    }

}