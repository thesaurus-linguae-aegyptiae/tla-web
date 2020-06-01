package tla.web.mvc;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import lombok.extern.slf4j.Slf4j;
import nz.net.ultraq.thymeleaf.LayoutDialect;
import tla.web.config.ApplicationProperties;
import tla.web.model.mappings.LanguageFromStringConverter;
import tla.web.model.mappings.ScriptFromStringConverter;

@Slf4j
@Configuration
@ControllerAdvice
public class MvcConfig implements WebMvcConfigurer {

    @Autowired
    private ApplicationProperties applicationProperties;

    @ModelAttribute("env")
    public Map<String, String> appVars() {
        log.info("app properties available? {}", applicationProperties != null);
        return Map.of(
            "baseUrl", applicationProperties.getBaseUrl(),
            "appName", applicationProperties.getName()
        );
    }

    @Bean
    public LayoutDialect layoutDialect() {
        return new LayoutDialect();
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new ScriptFromStringConverter());
        registry.addConverter(new LanguageFromStringConverter());
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
            .addResourceHandler("/resources/**")
            .addResourceLocations("/resources/");
    }

}