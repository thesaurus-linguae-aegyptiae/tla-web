package tla.web.mvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import lombok.extern.slf4j.Slf4j;
import nz.net.ultraq.thymeleaf.LayoutDialect;
import tla.web.config.ApplicationProperties;
import tla.web.model.mappings.LanguageFromStringConverter;
import tla.web.model.mappings.ScriptFromStringConverter;

@Slf4j
@Configuration
@Import(ApplicationProperties.class)
public class MvcConfig  implements WebMvcConfigurer {

    @Autowired
    private ApplicationProperties applicationProperties;

    @Bean
    public TlaPageHeader tlaPageHeader() {
        return new TlaPageHeader(applicationProperties);
    }

    @Bean
    public LayoutDialect layoutDialect() {
        return new LayoutDialect();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        log.info("registering MVC handler interceptor");
        registry.addInterceptor(
            new RequestMappingInterceptor(applicationProperties)
        );
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

    /**
     * Contains information like static assets locations and such.
     * All obtained from applicationproperties (prefix <code>tla</code>).
     */
    public static class TlaPageHeader {

        private ApplicationProperties props;
        private ApplicationProperties.Assets assets;

        public TlaPageHeader(ApplicationProperties applicationProperties) {
            this.props = applicationProperties;
            this.assets = applicationProperties.getAssets();
        }

        public String getBootstrapCss() {
            return String.format(
                "%s/css/bootstrap.min.css",
                this.assets.getBootstrap()
            );
        }

        public String getFontawesomeCss() {
            return String.format(
                "%s/css/all.css",
                this.assets.getFontawesome()
            );
        }

        public String getBootstrapJs() {
            return String.format(
                "%s/js/bootstrap.min.js",
                this.assets.getBootstrap()
            );
        }

        public String getFontawesomeJs() {
            return String.format(
                "%s/js/all.js",
                this.assets.getFontawesome()
            );
        }

        public String getBaseUrl() {
            return props.getBaseUrl();
        }

        public String getAppName() {
            return props.getName();
        }
    }

    /**
     * Injects a TLA page header into every response model.
     */
    public class RequestMappingInterceptor extends HandlerInterceptorAdapter {

        private TlaPageHeader headerMetadata;

        public RequestMappingInterceptor(ApplicationProperties properties) {
            log.info(
                "instantiating MVC interceptor with {} application properties",
                properties != null ? "non-null" : "unavailable"
            );
            this.headerMetadata = new TlaPageHeader(properties);
        }

        @Override
        public void postHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            ModelAndView modelAndView
        ) throws Exception {
            if (modelAndView != null) {
                log.info(
                    "intercepting {} request to {} - extending view model",
                    request.getMethod(),
                    request.getRequestURI()
                );
                modelAndView.getModelMap().addAttribute("env", headerMetadata);
            }
            super.postHandle(request, response, handler, modelAndView);
        }
    }
}