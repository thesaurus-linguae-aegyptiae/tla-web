package tla.web.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Utility bean for use in HTML templates.
 */
@Controller
public class TemplateUtils {

    /**
     * Put the specified parameter into the current request's URL. If the parameter is
     * already in the URL, it gets replaced. The result is a URI components builder, meaning
     * that in order to use it as a URI or a String, its {@link UriComponentsBuilder#build()}
     * method needs to be called.
     */
    public UriComponentsBuilder replaceQueryParam(String name, String value) {
        return ServletUriComponentsBuilder.fromCurrentRequest().replaceQueryParam(
            name, value
        );
    }

    /**
     * Set/replace a URL parameter and return the resulting URL as a String, without any
     * URL-encoding applied. (Meaning that characters like square brackets <code>[]</code>
     * will be preserved.)
     */
    public String setQueryParam(String name, String value) {
        return this.replaceQueryParam(name, value).build().toString();
    }

    /**
     * Replace current request's URL path with another. Useful for links where you want to
     * keep the URL parameters.
     */
    public UriComponentsBuilder replacePath(String path) {
        return ServletUriComponentsBuilder.fromCurrentRequest().replacePath(path);
    }

}