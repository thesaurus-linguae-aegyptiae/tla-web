package tla.web.model.mappings;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import org.springframework.core.convert.converter.Converter;

/**
 * Required for correct binding of URL parameters in GET requests to search commands.
 */
public class URLDecodeConverter implements Converter<String, String> {

    @Override
    public String convert(String source) {
        try {
            return URLDecoder.decode(source, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return source;
        }
    }

}
