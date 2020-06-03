package tla.web.model;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import tla.domain.command.SearchCommand;
import tla.domain.dto.extern.PageInfo;
import tla.domain.dto.extern.SearchResultsWrapper;
import tla.domain.dto.meta.AbstractDto;
import tla.web.model.mappings.MappingConfig;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SearchResults {

    private List<TLAObject> objects;

    private SearchCommand<? extends AbstractDto> query;

    private PageInfo page;

    private Map<String, Map<String, Long>> facets;

    /**
     * Converts search results in a paged DTO to their respective frontend model
     * types.
     *
     * @param dto Search result page DTO.
     * @return Search result page
     */
    public static SearchResults from(SearchResultsWrapper<? extends AbstractDto> dto) {
        List<TLAObject> objects = dto.getResults().stream().map(
            d -> MappingConfig.convertDTO(d)
        ).collect(
            Collectors.toList()
        );
        return new SearchResults(
            objects,
            dto.getQuery(),
            dto.getPage(),
            dto.getFacets()
        );
    }
    
}