package tla.web.model.mappings;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import tla.domain.dto.LemmaDto;
import tla.domain.model.LemmaWord;
import tla.web.config.ApplicationProperties;
import tla.web.model.Glyphs;
import tla.web.model.Lemma;
import tla.web.model.Word;

import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;

@Configuration
public class MappingConfig {

    private class GlyphsConverter extends AbstractConverter<String, Glyphs> {
        @Override
        protected Glyphs convert(String source) {
            return Glyphs.of(source);
        }
    }

    @Autowired
    private ApplicationProperties properties;

    @Bean
    public ExternalReferencesConverter externalReferencesConverter() {
        return new ExternalReferencesConverter(properties);
    }

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        ExternalReferencesConverter externalReferencesConverter = externalReferencesConverter();
        modelMapper.createTypeMap(LemmaDto.class, Lemma.class).addMappings(
            m -> m.using(externalReferencesConverter).map(
                LemmaDto::getExternalReferences,
                Lemma::setExternalReferences
            )
        );
        modelMapper.createTypeMap(LemmaWord.class, Word.class).addMappings(
            m -> m.using(new GlyphsConverter()).map(
                LemmaWord::getGlyphs,
                Word::setGlyphs
            )
        );
        return modelMapper;
    }
}