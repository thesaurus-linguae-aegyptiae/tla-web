package tla.web.mvc;

import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import lombok.extern.slf4j.Slf4j;
import tla.web.config.EditorialConfig.EditorialRegistry;

/**
 * Uses dynamic mappings to handle requests for quote unquote "<em>static</em>" pages.
 */
@Slf4j
@Controller
@RequestMapping("/")
public class EditorialContentController {

    @Autowired
    private EditorialRegistry editorialRegistry;

    @Autowired
    private RequestMappingHandlerMapping handlerMapping;

    /**
     * Select one of the languages in which a requested editorial page is available
     * under consideration of the <pre>Appect-Language</pre> values passed.
     */
    private String negiotiateContentLanguage(String path, HttpHeaders header) {
        List<Locale.LanguageRange> requested = header.getAcceptLanguage();
        requested.sort(
            Comparator.comparing(
                Locale.LanguageRange::getWeight
            ).reversed()
        );
        Set<String> supported = editorialRegistry.getSupportedLanguages(
            path
        );
        for (Locale.LanguageRange lang : requested) {
            if (supported.contains(lang.getRange())) {
                return lang.getRange();
            }
        }
        return editorialRegistry.getLangDefault();
    }

    /**
     * Puts together the path where to find the negoriated template.
     */
    private String templatePath(String lang, String path) {
        return "../pages/" + lang + path;
    }

    /**
     * Generic handler for semi-static editorial HTML page requests.
     */
    public String renderEditorial(
        HttpServletRequest request,
        @RequestHeader HttpHeaders header,
        Model model
    ) throws Exception {
        String path = request.getRequestURI();
        String contentLang = negiotiateContentLanguage(path, header);
        log.info(
            "serving request for static page {} with accepted languages {} with negotiated language",
            path, request.getHeader(HttpHeaders.ACCEPT_LANGUAGE), contentLang
        );
        model.addAttribute("templatePath", templatePath(contentLang, path));
        model.addAttribute("contentLang", contentLang);
        return "editorial";
    }

    /**
     * Creates request handler mappings for editorial pages previously registered in the
     * {@link EditorialRegistry}.
     */
    @EventListener
    public void onApplicationReady(ApplicationReadyEvent event) throws NoSuchMethodException {
        log.info("ready to create request handler mappings for editorial templates");
        Method handlerMethod = EditorialContentController.class.getDeclaredMethod(
            "renderEditorial",
            HttpServletRequest.class,
            HttpHeaders.class,
            Model.class
        );
        try {
            this.editorialRegistry.getLangSupport().forEach(
                (path, supportedLanguages) -> {
                    handlerMapping.registerMapping(
                        RequestMappingInfo.paths(path).methods(RequestMethod.GET).build(),
                        this,
                        handlerMethod
                    );
                }
            );
        } catch (Exception e) {}
    }

}