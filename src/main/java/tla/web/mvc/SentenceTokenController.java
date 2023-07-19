package tla.web.mvc;

import static tla.web.mvc.GlobalControllerAdvisor.BREADCRUMB_HOME;

import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import tla.domain.command.SentenceSearch;
import tla.domain.model.meta.Hierarchic;
import tla.error.ObjectNotFoundException;
import tla.web.model.Sentence;
import tla.web.model.meta.ObjectDetails;
import tla.web.model.meta.SearchResults;
import tla.web.model.meta.TemplateModelName;
import tla.web.model.ui.BreadCrumb;
import tla.web.model.ui.CorpusPathSegment;
import tla.web.model.ui.Pagination;
import tla.web.service.ObjectService;
import tla.web.service.SentenceService;
import tla.web.model.Sentence;
import java.util.Iterator;

@Controller
@RequestMapping("/token")
@TemplateModelName("sentence")
public class SentenceTokenController extends HierarchicObjectController<Sentence, SentenceSearch> {

	@Autowired
	private SentenceService service;

	@Override
	public ObjectService<Sentence> getService() {
		return service;
	}

	private String templatePath = "token";

	@Override
	public String getTemplatePath() {
		return this.templatePath;
	}

	@Override
	public List<List<BreadCrumb>> createObjectPathLinks(Hierarchic object) {
		var sentence = ((Sentence) object);
		var paths = super.createObjectPathLinks(sentence);
		paths.forEach(links -> {
			links.add(ObjectController.createLink(sentence.getText().toObjectReference()));
			if (sentence.getContext().getParagraph() != null) {
				links.add(new CorpusPathSegment(null, sentence.getContext().getParagraph(), null, "paragraph"));
			}
			links.add(new CorpusPathSegment(null, sentence.getContext().getLine(), null, "line"));
		});
		return paths;
	}

	@RequestMapping(value = "/lookup", method = RequestMethod.GET)
	public RedirectView lookup(@RequestParam String id) {
		return new RedirectView(String.format("search?tokens[0].id=%s", id), true);
	}

	/*
	 * this needs to be here for the sake of the procedural redundant route
	 * generation in {@link SearchController#onApplicationReady}
	 */
	@Override
	@RequestMapping(value = "/search", method = RequestMethod.GET)
	public String getSearchResultsPage(@ModelAttribute("tokenSearchForm") SentenceSearch form,
			@RequestParam(defaultValue = "1") String page, @RequestParam MultiValueMap<String, String> params,
			Model model) {

		SearchResults results = this.getService().search(form, Integer.parseInt(page)); // TODO validate page

		ObjectDetails<Sentence> container = new ObjectDetails(results.getObjects().get(0));
		model.addAttribute("breadcrumbs", List.of(BREADCRUMB_HOME,
				// BreadCrumb.of("/search", "menu_global_search"), // Lemma-Seite soll nicht
				// unter Breadcrumb-Pfad "search" erscheinen
				BreadCrumb.of(String.format("caption_details_%s", getTemplatePath()))));

		this.addHideableProperties(model);
		this.addHideable1LemmaProperties(model);
		this.addShowableProperties(model);
		this.addHideable2LemmaProperties(model);
		model.addAttribute("obj", container.getObject());
		model.addAttribute("passport", getPassportPropertyValues(container));
		model.addAttribute("caption", getService().getLabel(container.getObject()));
		model.addAttribute("related", container.getRelated());
		model.addAttribute("relations", container.extractRelatedObjects());
		model = extendSingleObjectDetailsModel(model, container);
		return String.format("%s/details", getTemplatePath());

	}

}