package tla.web.repo;

import org.springframework.core.env.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RepoConfig {

    @Autowired
    private Environment env;

    @Bean
    public TlaClient tlaClient() {
        return new TlaClient(env.getProperty("tla.backend-url"));
    }


}
