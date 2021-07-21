package tla.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcProperties.LocaleResolver;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.SpringProperties;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
public class App {

    public static void main(String[] args) {
        SpringProperties.setFlag("spring.xml.ignore");
        ApplicationContext context = SpringApplication.run(
            App.class,
            args
        );
        log.info(
            "bean definitions: {}",
            context.getBeanDefinitionNames().length
        );
    }
   
   

}
