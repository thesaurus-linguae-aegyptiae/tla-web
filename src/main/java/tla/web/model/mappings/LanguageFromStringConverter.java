package tla.web.model.mappings;

import org.springframework.core.convert.converter.Converter;

import tla.domain.model.Language;

public class LanguageFromStringConverter implements Converter<String, Language> {

    @Override
    public Language convert(String source) {
        return Language.deserialize(source);
    }

}