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
			/*System.out.println("###### in URLDecodeConverter: " + source);*/
			if (source != null) { 
				//if (source.contains("%")) { System.out.println("###### % found: "+ source +"################"); }
				source = source.replaceAll("%([^0-9])","%25$1"); // Fängt Fälle von % ohne folgende Ziffer ab, die den URLDecoder zum Absturz bringen
				//if (source.contains("%")) { System.out.println("###### % found: "+ source +"################"); }
			}
            return URLDecoder.decode(source, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return source;
        }
    }

}
