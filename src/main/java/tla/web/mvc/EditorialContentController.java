package tla.web.mvc;

import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
            Comparator.comparing(Locale.LanguageRange::getWeight)
        );
        log.info("accepted languages: {}", requested);
        Set<String> supported = editorialRegistry.getSupportedLanguages(
            path
        );
        log.info("supported languages for {}: {}", path, supported);
        for (Locale.LanguageRange lang : requested) {
            log.info("lang: {}, weight: {}", lang, lang.getWeight());
            if (supported.contains(lang.toString())) {
                return lang.toString();
            }
        }
        return "en"; // TODO default
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
        HttpServletResponse response,
        Model model
    ) throws Exception {
        String path = request.getRequestURI();
        String contentLang = negiotiateContentLanguage(path, header);
        log.info("handling request for static page {}", path);
        log.info("accepted language: {}", request.getHeader(HttpHeaders.ACCEPT_LANGUAGE));
        log.info("negotiated language: {}", contentLang);
        String tp = templatePath(contentLang, path);
        log.info("template path: {}", tp);
        model.addAttribute("templatePath", tp);
        model.addAttribute("contentLang", contentLang);
        // setting content lang header value does not have any effect for whatever reason
        // response.addHeader(
        //     HttpHeaders.CONTENT_LANGUAGE,
        //     contentLang
        // );
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
            HttpServletResponse.class,
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