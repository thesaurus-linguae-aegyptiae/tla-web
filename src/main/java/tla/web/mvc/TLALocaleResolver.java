package tla.web.mvc;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

public class TLALocaleResolver extends AcceptHeaderLocaleResolver {

    /**
     * Determines whether a language specifier is even worth considering
     * during content negotiation (i.e. <em>approximate</em> <code>ISO 639-1</code> conformity).
     */
    static boolean isValidContentLanguage(String lang) {
        return lang != null && lang.matches("[A-Za-z]{2}");
    }

    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        if (isValidContentLanguage(request.getParameter("lang"))) {
            return new Locale(
                request.getParameter("lang")
            );
        } else {
            return super.resolveLocale(request);
        }
    }

}
