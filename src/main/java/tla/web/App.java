package tla.web;

import java.util.Properties;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;

import tla.web.config.ApplicationProperties;

@SpringBootApplication
public class App {

    /**
     * Create application properties from a {@link Properties} map which can be
     * obtained via a {@link YamlPropertiesFactoryBean}.
     *
     * @param properties
     * @return
     */
    public static ApplicationProperties bindProperties(Properties properties) {
        ConfigurationPropertySource configSource = new MapConfigurationPropertySource(
            properties
        );
        return new Binder(configSource).bind(
            "tla",
            ApplicationProperties.class
        ).get();
    }

    public static void main(String[] args) {
        SpringApplication.run(
            App.class,
            args
        );
    }

}
