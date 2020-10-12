package tla.web.mvc;

import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;

import org.jooq.lambda.Seq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.MessageSource;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
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
    private MessageSource l10n;

    @Autowired
    private EditorialRegistry editorialRegistry;

    @Autowired
    private RequestMappingHandlerMapping handlerMapping;

    @Value("${tla.editorials.path}")
    private String editorialsDir;

    /**
     * Select one of the languages in which a requested editorial page is available
     * under consideration of the <pre>Appect-Language</pre> values passed.
     */
    private String negotiateContentLanguage(String path, HttpHeaders header) {
        List<Locale.LanguageRange> requested = header.getAcceptLanguage();
        requested.sort(
            Comparator.comparing(
                Locale.LanguageRange::getWeight
            ).reversed()
        );
        var supported = getSupportedLanguages(path);
        for (Locale.LanguageRange lang : requested) {
            if (supported.contains(lang.getRange())) {
                return lang.getRange();
            }
        }
        return editorialRegistry.getLangDefault();
    }

    private Set<String> getSupportedLanguages(String path) {
        return  editorialRegistry.getSupportedLanguages(
            path
        );
    }

    /**
     * Add specified language with a weight of 1.0 to the language specifier list
     * in the accepted languages header field.
     */
    private HttpHeaders preferContentLanguage(String lang, HttpHeaders header) {
        List<Locale.LanguageRange> requested = header.getAcceptLanguage();
        requested.add(
            0,
            new Locale.LanguageRange(lang, 1.0f)
        );
        header.setAcceptLanguage(requested);
        return header;
    }

    /**
     * Looks up the page title as defined in <code>messages.properties</code>.
     *
     * Example: Title for <code>legal/imprint</code> is defined via message key
     * <code>editorial_title_legal_imprint</code>.
     */
    public String getPageTitle(String path, String lang) {
        String msgKey = "editorial_title_" + Seq.toString(
            Stream.of(
                path.split("/")
            ).filter(
                segm -> !segm.isBlank()
            ),
            "_"
        );
        return l10n.getMessage(
            msgKey,
            null,
            path,
            new Locale(lang)
        );
    }

    /**
     * Puts together the file system path where to find the negotiated template.
     *
     * TODO get subdir from ${tla.editorials.path}
     * TODO account for path not being prefixed by slash
     */
    private String templatePath(String lang, String path) {
        return Path.of(
            "..",
            editorialsDir,
            lang,
            path
        ).toString();
    }

    /**
     * Generic handler for semi-static editorial HTML page requests.
     */
    public String renderEditorial(
        HttpServletRequest request,
        @RequestParam(required = false) String lang,
        @RequestHeader HttpHeaders header,
        Model model
    ) throws Exception {
        String path = request.getRequestURI();
        if (TLALocaleResolver.isValidContentLanguage(lang)) {
            preferContentLanguage(lang, header);
        }
        String contentLang = negotiateContentLanguage(path, header);
        log.info(
            "serving request for static page {} with accepted languages {} with negotiated language {}.",
            path, header.getAcceptLanguage(), contentLang
        );
        var availableLanguages = getSupportedLanguages(path);
        model.addAttribute("templatePath", templatePath(contentLang, path));
        model.addAttribute("contentLang", contentLang);
        model.addAttribute("env.l10n", availableLanguages);
        model.addAttribute(
            "pageTitle",
            getPageTitle(path, contentLang)
        );
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
            String.class,
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
