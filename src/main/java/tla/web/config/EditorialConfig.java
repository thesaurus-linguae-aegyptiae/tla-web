package tla.web.config;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Maintains an inventory of semi-static templates written for editorial web pages.
 * Runs when the {@link ContextRefreshedEvent} is being broadcasted, and registers
 * all <pre>HTML</pre> files inside of the directory specified via the
 * <pre>tla.editorials.path</pre> application property, alongside their respective
 * available languages.
 * The registry is then added to the context in the {@link EditorialRegistry} bean.
 */
@Slf4j
@Configuration
public class EditorialConfig {

    @Value("classpath:${tla.editorials.path}")
    private Resource editorialsDir;

    @Value("classpath:${tla.editorials.path}/**/*.html")
    private Resource[] editorialFiles;

    private EditorialRegistry editorialRegistry = new EditorialRegistry();

    @Bean
    public EditorialRegistry EditorialRegistry() {
        return this.editorialRegistry;
    }

    /**
     * Contains an inventory of all editorial pages and the languages in which they
     * are available.
     */
    @Getter
    public class EditorialRegistry {

        private Map<String, Set<String>> langSupport = new HashMap<>();

        private Path getRelativeEditorialPath(Resource editorialResource) throws IOException {
            return Paths.get(
                editorialsDir.getURI()
            ).relativize(
                Paths.get(editorialResource.getURI())
            );
        }

        private String getEditorialPathLang(Path editorialPath) {
            return editorialPath.subpath(0, 1).toString();
        }

        private String getEditorialPathMapping(Path editorialPath) {
            Path path = editorialPath.subpath(
                1, editorialPath.getNameCount()
            );
            return path.toString().replaceAll("\\.[Hh][Tt][Mm][Ll]$", "");
        }

        private void registerEditorialFile(Resource editorialResource) {
            try {
                Path editorialPath = this.getRelativeEditorialPath(editorialResource);
                String editorialID = this.getEditorialPathMapping(editorialPath);
                String lang = this.getEditorialPathLang(editorialPath);
                this.langSupport.putIfAbsent(
                    editorialID,
                    new TreeSet<>()
                );
                this.langSupport.get(editorialID).add(lang);
            } catch (Exception e) {
                log.error("could not register editorial resource {}!", editorialResource);
                log.error("reason: ", e);
            }
        }

        /**
         * Registers a whole bunch of <pre>HTML</pre> files.
         */
        public Map<String, Set<String>> registerAll(Resource[] editorialResources) {
            for (Resource r : editorialResources) {
                this.registerEditorialFile(r);
            }
            return this.langSupport;
        }

        /**
         * Looks up the languages in which a given editorial page is available.
         */
        public Set<String> getSupportedLanguages(String path) {
            return this.langSupport.getOrDefault(
                path.substring(path.startsWith("/") ? 1 : 0),
                Set.of("en")
            ); // TODO default
        }
    }

    @EventListener
    public void onContextRefresh(ContextRefreshedEvent event) {
        log.info("register editorial templates");
        log.info("editorials dir: {}", editorialsDir);
        if (editorialFiles != null) {
            log.info(
                "registry: {}",
                this.editorialRegistry.registerAll(editorialFiles)
            );
        }
    }

}
