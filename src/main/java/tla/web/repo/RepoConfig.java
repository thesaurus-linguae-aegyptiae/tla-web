package tla.web.repo;

import tla.web.config.ApplicationProperties;

import org.springframework.core.env.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(ApplicationProperties.class)
public class RepoConfig {

    @Autowired
    private Environment env;

    @Bean
    public TlaClient tlaClient() {
        return new TlaClient(env.getProperty("tla.backend-url"));
    }


}
