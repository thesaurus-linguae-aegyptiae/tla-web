package tla.web.mvc;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.context.WebApplicationContext;

import lombok.extern.slf4j.Slf4j;

import tla.domain.dto.extern.SingleDocumentWrapper;
import tla.domain.dto.meta.AbstractDto;
import tla.error.ObjectNotFoundException;
import tla.web.model.ObjectDetails;
import tla.web.model.TLAObject;
import tla.web.model.ThsEntry;
import tla.web.service.ThsService;

@Slf4j
@SpringBootTest
public class ThsEntryDetailsTest extends ViewTest {

    @MockBean
    private ThsService service;

    @Autowired
    private ThsController controller;

    @Autowired
    private WebApplicationContext ctx;

    @BeforeEach
    void init() {
        for (String s : ctx.getBeanDefinitionNames()) {
            log.warn("TEST BEAN: " + s);
        }
    }

    @SuppressWarnings("unchecked")
    private SingleDocumentWrapper<AbstractDto> thsEntryDetailsDTO(String id) throws Exception {
        return tla.domain.util.IO.loadFromFile(
            String.format(
                "src/test/resources/sample/data/ths/details/%s.json",
                id
            ),
            SingleDocumentWrapper.class
        );
    }

    private ObjectDetails<ThsEntry> mapDetailsDTO(SingleDocumentWrapper<AbstractDto> dto) {
        log.info("map object details DTO to domain model");
        assertNotNull(dto);
        assertNotNull(dto.getDoc());
        ObjectDetails<TLAObject> container = ObjectDetails.from(
            dto
        );
        return new ObjectDetails<ThsEntry>(
            (ThsEntry) container.getObject(),
            container.getRelated()
        );
    }


    @ParameterizedTest
    @ValueSource(strings = {
        "744HIAA6FRHBROALBB3DV4VV3I",
        "KQY2F5SJVBBN7GRO5WUXKG5M6M"
    })
    void getThsEntryDetails_exists(String id) throws Exception {
        assertNotNull(controller);
        when(
            service.getDetails(id)
        ).thenReturn(
            Optional.of(
                mapDetailsDTO(
                    thsEntryDetailsDTO(id)
                )
            )
        );

        ObjectDetails<ThsEntry> details = service.getDetails(id).orElseThrow(
            () -> new ObjectNotFoundException(id, "BTSThsEntry")
        );
        assertAll("assume mock service does as told",
            () -> assertNotNull(details),
            () -> assertNotNull(details.getObject())
        );
        ResultActions testResult = mockMvc.perform(
            get(
                "/thesaurus/" + id
            ).header(
                HttpHeaders.ACCEPT_LANGUAGE, "en"
            )
        ).andDo(print());

        testResult.andExpect(
            status().isOk()
        ).andExpect(
            content().string(
                not(containsString(
                    String.format("_%s??", "en")
                ))
            )
        ).andExpect(
            xpath("//span[@id='type-subtype']").exists()
        ).andExpect(
            xpath("//div[@id='details-content']/div[@id='translations']").exists()
        ).andExpect(
            xpath("//div[@id='translations']//span[contains(@class, 'translation')]").exists()
        ).andExpect(
            xpath("//div[@id='translations']/span[@class='translation-de']").doesNotExist()
        );
    }

    @Test
    void getThsEntryDetails_unknown() throws Exception {
        doReturn(
            Optional.empty()
        ).when(
            service
        ).getDetails("x");
        mockMvc.perform(
            get("/thesaurus/x")
        ).andDo(print()).andExpect(status().isOk())
        .andExpect(
            view().name("error/404")
        )
        .andExpect(
            model().attribute("env", aMapWithSize(greaterThan(2)))
        )
        .andExpect(
            model().attribute("env", hasEntry(containsString("baseUrl"), containsString("")))
        )
        .andExpect(
            model().attribute("env", hasKey("l10n"))
        )
        .andExpect(
            model().attribute("env", hasEntry(containsString("l10n"), hasItem("en")))
        );
    }

}