package tla.web.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import tla.web.config.ApplicationProperties;

@Configuration
@Import(ApplicationProperties.class)
public class MvcConfig {

    @Autowired
    private ApplicationProperties applicationProperties;

    @Bean
    public TlaPageHeader tlaPageHeader() {
        return new TlaPageHeader(applicationProperties);
    }

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
}