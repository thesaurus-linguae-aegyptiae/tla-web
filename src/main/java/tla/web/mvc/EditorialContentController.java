package tla.web.mvc;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import lombok.extern.slf4j.Slf4j;
import tla.web.config.EditorialConfig.EditorialRegistry;

@Slf4j
@Controller
public class EditorialContentController {

    @Autowired
    private EditorialRegistry editorialRegistry;

    @Autowired
    private RequestMappingHandlerMapping handlerMapping;

    /**
     * Generic handler for semi-static editorial HTML page requests.
     */
    public String renderEditorial(HttpServletRequest request, Model model) throws Exception {
        String path = request.getRequestURI();
        log.info("handling request for static page {}", path);
        log.info("accepted language: {}", request.getHeader("Accept-Language"));
        model.addAttribute("templatePath", path);
        return "editorial";
    }

    @EventListener
    public void onApplicationReady(ApplicationReadyEvent event) throws NoSuchMethodException {
        log.info("ready to create request handler mappings for editorial templates");
        log.info("editorial language support registry: {}", editorialRegistry.getLangSupport());
        Method handlerMethod = EditorialContentController.class.getDeclaredMethod(
            "renderEditorial",
            HttpServletRequest.class,
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