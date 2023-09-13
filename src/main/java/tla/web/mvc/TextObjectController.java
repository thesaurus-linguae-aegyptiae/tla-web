package tla.web.mvc;

import static tla.web.mvc.GlobalControllerAdvisor.BREADCRUMB_HOME;

import java.util.List;

import javax.security.auth.message.callback.PrivateKeyCallback.Request;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import ch.qos.logback.classic.html.UrlCssBuilder;
import tla.web.mvc.SentenceController;
import tla.domain.command.SentenceSearch;
import tla.domain.dto.SentenceDto.SentenceContext;
import tla.domain.command.TextSearch;
import tla.web.config.SentenceSearchProperties;
import tla.web.model.Sentence;
import tla.web.model.Text;
import tla.web.model.meta.SearchResults;
import tla.web.model.meta.TemplateModelName;
import tla.web.model.ui.BreadCrumb;
import tla.web.model.ui.CorpusPathSegment;
import tla.web.model.ui.Pagination;
import tla.web.service.ObjectService;
import tla.web.service.SentenceService;
import tla.web.service.TextService;

@Controller
@RequestMapping("/text")
@TemplateModelName("text")
public class TextObjectController extends ObjectController<Text, TextSearch> {

    @Autowired
    private TextService service;

    @Override
    public ObjectService<Text> getService() {
        return service;
    }
      
    @Autowired
    private SentenceService sentenceService;
    
    public ObjectService<Sentence> getSentenceService(){
    	return sentenceService;
    }
      
    
    @Autowired
    private SentenceSearchProperties searchConfig;
    

    @RequestMapping(value = "/{id}/sentences", method = RequestMethod.GET)
    public String getTextSentencesSearchResultsPage(
            SentenceSearch form,
            @RequestParam(defaultValue = "1") String page,            
            @RequestParam MultiValueMap<String, String> params,
            Model model            
        ) {     
    	form.setSort("context.position");
        String textId = form.getId()[0];
        model.addAttribute("textId", textId);
	    model.addAttribute("contextInformation", searchConfig.getContextInformation());
	    
        //Set context!
        SentenceContext context = new SentenceContext();
        context.setTextId(textId);
        form.setContext(context);
        form.setId(null);
               
        //System.out.println("Submitted Form class "+form.getClass().toString());
        SearchResults results = this.getSentenceService().search(form, Integer.parseInt(page)); // TODO validate page           
            model.addAttribute("breadcrumbs",
                List.of(
                    BREADCRUMB_HOME,
                    BreadCrumb.of(
                    	modifySearchUrlbyPath("text/" + textId), "menu_global_text"
                    ),
                    BreadCrumb.of(
                        templateUtils.setQueryParam("page", "1"),
                        String.format("menu_global_search_text_sentences")
                    )
                )
            );
            var searchProperties = sentenceService.getSearchProperties();
            if (searchProperties != null) {
                model.addAttribute(
                    "hideableTextsentencesProperties",
                    searchProperties.getHideableTextsentencesProperties()
                );
               
            }
            // DW: die folgenden werden durch ObjectController ererbet? Hier nochmal nötig?
           
            model.addAttribute("objectType", "sentence");
            model.addAttribute("searchResults", results.getObjects());
            if(results.getQuery().getSort()==null ) results.getQuery().setSort("sortKey_asc");   
            model.addAttribute("searchQuery", results.getQuery());
            model.addAttribute("facets", results.getFacets());
            model.addAttribute("page", results.getPage());
            model.addAttribute("pagination", new Pagination(results.getPage()));
            model = extendSearchResultsPageModel(model, results, form);
            return String.format("text/sentences/search");
        }

    
  @Override
    @RequestMapping(value="/search", method=RequestMethod.GET)
    public String getSearchResultsPage(
        @ModelAttribute("textSearchForm") TextSearch form,
        @RequestParam(defaultValue = "1") String page,
        @RequestParam MultiValueMap<String, String> params,
        Model model
    ) {
        return super.getSearchResultsPage(form, page, params, model);
    } 
}