package tla.web.model.mappings;

import org.springframework.core.convert.converter.Converter;

import tla.domain.model.Script;

public class ScriptFromStringConverter implements Converter<String, Script> {

    @Override
    public Script convert(String source) {
        return Script.fromString(source);
    }

}