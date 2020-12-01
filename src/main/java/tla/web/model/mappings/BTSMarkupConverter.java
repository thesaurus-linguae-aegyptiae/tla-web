package tla.web.model.mappings;

import org.springframework.core.convert.converter.Converter;
import org.springframework.web.util.HtmlUtils;

/**
 * Character sequences containing transliteration and thereby possibly symbols that are not
 * supported by every font are by convention marked up with <code>$</code>-charactes when working
 * in the BTS. This converter finds these sequences and replaces the <code>$</code>-charactes with
 * enclosing <code>&lt;span></code> tags with <code>class</code> attribute <code>bbaw-libertine</code>,
 * so that CSS can define an appropriate font. When registered as a formatter in a
 * <code>FormatterRegistry</code>, this converter will apply whereever templates extrapolate a string
 * value with the double-<code>{{</code> notation of thymeleaf, e.g.
 * <code>&lt;span th:utext="${{value}}"/></code>.
 *
 * @see Util#escapeMarkup(String)
 */
public class BTSMarkupConverter implements Converter<String, String> {

    @Override
    public String convert(String source) {
        return source != null ? Util.escapeMarkup(HtmlUtils.htmlEscapeDecimal(source)) : null;
    }

}