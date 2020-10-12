package tla.web.mvc;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TLALocaleResolver extends AcceptHeaderLocaleResolver {

    /**
     * Determines whether a language specifier is even worth considering
     * during content negotiation (i.e. <code>ISO 639-1</code> conformity).
     */
    static boolean isValidContentLanguage(String lang) {
        return lang != null && lang.matches("[A-Za-z]{2}");
    }

    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        log.info("resolving locale {}", request.getHeader(HttpHeaders.ACCEPT_LANGUAGE));
        log.info("request parameters {}", request.getParameter("lang"));
        if (isValidContentLanguage(request.getParameter("lang"))) {
            return new Locale(
                request.getParameter("lang")
            );
        } else {
            return super.resolveLocale(request);
        }
    }

}
