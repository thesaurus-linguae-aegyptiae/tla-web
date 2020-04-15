package tla.web.model.mappings;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import tla.domain.dto.DocumentDto;
import tla.domain.dto.LemmaDto;
import tla.domain.model.LemmaWord;
import tla.domain.model.meta.BTSeClass;
import tla.web.config.ApplicationProperties;
import tla.web.model.Glyphs;
import tla.web.model.Lemma;
import tla.web.model.TLAObject;
import tla.web.model.Word;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private static ModelMapper modelMapper;

    @Bean
    public ExternalReferencesConverter externalReferencesConverter() {
        return new ExternalReferencesConverter(properties);
    }

    @Bean
    public ModelMapper modelMapper() {
        return modelMapper != null ? modelMapper : initModelMapper();
    }

    private ModelMapper initModelMapper() {
        modelClasses = new HashMap<>();
        List.of(
            Lemma.class
        ).stream().forEach(
            modelClass -> registerModelClass(modelClass)
        );
        modelMapper = new ModelMapper();
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

    private static Map<String, Class<? extends TLAObject>> modelClasses;

    protected static void registerModelClass(Class<? extends TLAObject> modelClass) {
        for (Annotation a : modelClass.getAnnotations()) {
            if (a instanceof BTSeClass) {
                modelClasses.put(((BTSeClass) a).value(), modelClass);
            }
        }
    }

    public static Class<? extends TLAObject> getModelClass(String eclass) {
        return modelClasses.get(eclass);
    }

    public static TLAObject convertDTO(DocumentDto dto) {
        return modelMapper.map(
            dto,
            getModelClass(dto.getEclass())
        );
    }

}