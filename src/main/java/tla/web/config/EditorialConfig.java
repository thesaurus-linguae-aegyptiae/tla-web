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
 * all <code>.html</code> files inside of the directory specified via the
 * <code>tla.editorials.path</code> application property, alongside their respective
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
    public EditorialRegistry editorialRegistry() {
        return this.editorialRegistry;
    }

    /**
     * Contains an inventory of editorial pages and the languages in which they
     * are available.
     */
    @Getter
    public class EditorialRegistry {

        /**
         * Fallback language for editorials, configured in <code>tla.editorials.lang-default</code>
         * application property.
         */
        @Value("${tla.editorials.lang-default}")
        private String langDefault;

        /**
         * Registry of editorial's URL paths and the languages in which they can be offered.
         */
        private Map<String, Set<String>> langSupport = new HashMap<>();

        /**
         * Return a classpath resource's path relative to that of the editorials folder
         * (configured in <code>tla.editorials.path</code> application property).
         */
        protected Path getRelativeEditorialPath(Resource editorialResource) throws IOException {
            return Paths.get(
                editorialsDir.getURI()
            ).relativize(
                Paths.get(editorialResource.getURI())
            );
        }

        /**
         * Extracts the language identifier from an editorial file's path.
         *
         * <p>Path must be <strong>relative to editorials directory</strong>
         * (specified via <code>tla.editorials.path</code> application property),
         * so that first path segment can be used as language identifier!
         * </p>
         *
         * @param editorialPath path to editorial file (relative to editorials template folder)
         * @return two-char identifier (e.b. <code>en</code>, <code>de</code>, ...)
         * @see #getRelativeEditorialPath(Resource)
         */
        protected String extractLanguageID(Path editorialPath) {
            return editorialPath.subpath(0, 1).toString();
        }

        /**
         * Extract the URL path used for mapping requests to an editorial file.
         * The URL path for an editorial file is its location relative to the editorials folder
         * (<code>tla.editorials.path</code> application property), with the first path segment
         * (the language identifier) and the file extension (<code>.html</code>) removed.
         * (And, in the unfortunate case that we're running on windoze, backslashs flipped)
         *
         * @param editorialPath relative path to editorials dir: <code>en/legal/imprint.html</code>
         * @return mappable URL path (<code>legal/imprint</code>)
         * @see #getRelativeEditorialPath(Resource)
         */
        String createURLPath(Path editorialPath) {
            Path path = editorialPath.subpath(
                1, editorialPath.getNameCount()
            );
            return path.toString().replaceAll(
                "\\.[Hh][Tt][Mm][Ll]?$", ""
            ).replace(
                "\\", "/"
            );
        }

        /**
         * Register the language assigned to an editorial file under the URL path where its
         * handler's gonna be mapped to.
         *
         * <p>For instance, existance of an editorial file with the relative path
         * <code>en/legal/imprint.html</code> means registration of <code>legal/imprint</code>
         * and language identifier <code>en</code>.
         *
         * @see #getSupportedLanguages(String)
         * @see #getLangSupport()
         */
        private void registerEditorialFile(Resource editorialResource) {
            try {
                Path editorialPath = getRelativeEditorialPath(editorialResource);
                String editorialID = createURLPath(editorialPath);
                String lang = extractLanguageID(editorialPath);
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
         * Registers a whole bunch of <code>HTML</code> files.
         *
         * @see #getLangSupport()
         * @see #getSupportedLanguages(String)
         */
        public Map<String, Set<String>> registerAll(Resource[] editorialResources) {
            for (Resource r : editorialResources) {
                this.registerEditorialFile(r);
            }
            return this.langSupport;
        }

        /**
         * Looks up the languages in which a given editorial page is available.
         * @param path <code>legal/imprint</code>
         * @return {@literal ['en', 'de']}
         * @see #getLangSupport()
         */
        public Set<String> getSupportedLanguages(String path) {
            return this.langSupport.getOrDefault(
                path.substring(
                    path.startsWith("/") ? 1 : 0
                ),
                Set.of(
                    this.langDefault
                )
            );
        }
    }

    /**
     * Hook for the {@link ContextRefreshedEvent} (taking place when application context
     * has been initialized) scanning editorials folder and registering its contents and the
     * languages in which they are available.
     * @param event
     */
    @EventListener
    public void onContextRefresh(ContextRefreshedEvent event) {
        log.info("register editorial templates inside of editorials dir {}.", editorialsDir);
        if (editorialFiles != null) {
            log.info(
                "registry: {}",
                this.editorialRegistry.registerAll(editorialFiles)
            );
        }
    }

}
