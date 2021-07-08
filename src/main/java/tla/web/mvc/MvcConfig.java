package tla.web.mvc;

import java.util.Locale;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import nz.net.ultraq.thymeleaf.LayoutDialect;
import tla.web.config.ApplicationProperties;
import tla.web.model.mappings.BTSMarkupConverter;
import tla.web.model.mappings.LanguageFromStringConverter;
import tla.web.model.mappings.ScriptFromStringConverter;
import tla.web.model.mappings.URLDecodeConverter;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Bean
    @Autowired
    public GlobalControllerAdvisor globalAdvisoryController(ApplicationProperties applicationProperties) {
        return new GlobalControllerAdvisor(applicationProperties);
    }

    /**
     * Locale resolver is being called multiple times per request, mysteriously...
     */
    
   
    @Bean
    public LocaleResolver localeResolver() {
        return new TLALocaleResolver();
    }

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        return TLALocaleResolver.localeChangeInterceptor("lang");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        WebMvcConfigurer.super.addInterceptors(registry);
        registry.addInterceptor(localeChangeInterceptor());
    }

    @Bean
    public LayoutDialect layoutDialect() {
        return new LayoutDialect();
    }
    
    @Bean(name = "messageSource")
    public MessageSource getMessageResource()  {
        ReloadableResourceBundleMessageSource messageResource= new ReloadableResourceBundleMessageSource();
         
        // Read i18n/messages_xxx.properties file.
        // For example: i18n/messages_en.properties
        messageResource.setBasename("classpath:i18n/messages");
        messageResource.setDefaultLocale(Locale.ENGLISH);
        messageResource.setDefaultEncoding("UTF-8");
        return messageResource;
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        Stream.of(
            new ScriptFromStringConverter(),
            new LanguageFromStringConverter(),
            new BTSMarkupConverter(),
            new URLDecodeConverter()
        ).forEach(
            converter -> registry.addConverter(converter)
        );
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
            .addResourceHandler("/resources/**")
            .addResourceLocations("/resources/");
    }

}
