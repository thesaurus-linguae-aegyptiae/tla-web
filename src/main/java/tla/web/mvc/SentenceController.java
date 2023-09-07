package tla.web.mvc;

import static tla.web.mvc.GlobalControllerAdvisor.BREADCRUMB_HOME;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

//import tla.domain.command.LemmaSearch;
import tla.domain.command.SentenceSearch;
import tla.domain.command.SentenceSearch.TokenSpec;
import tla.domain.model.meta.Hierarchic;

import tla.error.ObjectNotFoundException;
//import tla.web.config.LemmaSearchProperties;

import tla.web.model.Sentence;
import tla.web.model.meta.ObjectDetails;
import tla.web.model.meta.SearchResults;
//import tla.domain.dto.SentenceDto;
import tla.web.model.meta.TemplateModelName;
import tla.web.model.ui.BreadCrumb;
import tla.web.model.ui.CorpusPathSegment;
import tla.web.service.ObjectService;
import tla.web.service.SentenceService;
import tla.web.config.SentenceSearchProperties;

@Controller
@RequestMapping("/sentence")
@TemplateModelName("sentence")
public class SentenceController extends HierarchicObjectController<Sentence, SentenceSearch> {

	@Autowired
	private SentenceService service;
  
  @Autowired
  private SentenceSearchProperties searchConfig;
 
	@Override
	public ObjectService<Sentence> getService() {
		return service;
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

    /*
     * this needs to be here for the sake of the procedural redundant route generation
     * in {@link SearchController#onApplicationReady}
     */
    @Override
    @RequestMapping(value="/search", method=RequestMethod.GET)
    public String getSearchResultsPage(
        @ModelAttribute("sentenceSearchForm") SentenceSearch form,
        @RequestParam(defaultValue = "1") String page,
        @RequestParam MultiValueMap<String, String> params,
        Model model
    ) {
    	 model.addAttribute("contextInformation", searchConfig.getContextInformation());
        return super.getSearchResultsPage(form, page, params, model);
    }
  
	@RequestMapping(value = "/token/lookup", method = RequestMethod.GET)
	public RedirectView lookup(@RequestParam String id) {
		return new RedirectView(String.format("/sentence/token/" + id), true);
	}

	@RequestMapping(value = "/token/{id}", method = RequestMethod.GET)
	public String getSentenceForToken(@PathVariable String id, Model model) {
		SentenceSearch form = new SentenceSearch();
		TokenSpec tokenspec = new TokenSpec();
		tokenspec.setId(id);
		List<TokenSpec> tokens = new ArrayList<TokenSpec>();
		tokens.add(tokenspec);
		form.setTokens(tokens);

		SearchResults results = this.getService().search(form, 1); // TODO validate page
		ObjectDetails<Sentence> container= getService().getDetails(results.getObjects().get(0).getId()).orElseThrow(
	            () -> new ObjectNotFoundException(id, getTemplatePath())
	        );
      
      String sentenceID = container.getObject().getId();
		
		model.addAttribute("breadcrumbs",
				List.of(BREADCRUMB_HOME,
						BreadCrumb.of("/sentence/" + sentenceID,
								String.format("caption_details_%s", getTemplatePath())),
						BreadCrumb.of(String.format("caption_details_%s", "token"))));

		this.addHideableProperties(model);
		this.addHideable1LemmaProperties(model);
		this.addShowableProperties(model);
		this.addHideable2LemmaProperties(model);
		model.addAttribute("obj", container.getObject());
		model.addAttribute("passport", getPassportPropertyValues(container));
		model.addAttribute("caption", getService().getLabel(container.getObject()));
		model.addAttribute("related", container.getRelated());
		model.addAttribute("relations", container.extractRelatedObjects());
		model.addAttribute("searchTokenId", id);
		model = extendSingleObjectDetailsModel(model, container);
		return "token/details";
	}


}