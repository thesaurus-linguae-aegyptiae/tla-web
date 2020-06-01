package tla.web.mvc;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import lombok.extern.slf4j.Slf4j;

import tla.domain.dto.extern.SingleDocumentWrapper;
import tla.domain.dto.meta.AbstractDto;
import tla.web.config.ApplicationProperties;
import tla.web.model.ObjectDetails;
import tla.web.model.TLAObject;
import tla.web.model.ThsEntry;
import tla.web.model.mappings.MappingConfig;
import tla.web.service.ThsService;

@Slf4j
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
    ThsController.class,
    MappingConfig.class,
    ApplicationProperties.class
})
public class ThsEntryDetailsTest {

    @MockBean
    private ThsService service;

    @Autowired
    private ThsController controller;

    private MockMvc mockMvc;


    @BeforeEach
    void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
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
    @ValueSource(strings = {"744HIAA6FRHBROALBB3DV4VV3I", "KQY2F5SJVBBN7GRO5WUXKG5M6M"})
    void getThsEntryDetails_exists(String id) throws Exception {
        assertNotNull(controller);
        doReturn(
            mapDetailsDTO(
                thsEntryDetailsDTO(id)
            )
        ).when(service).get(id);

        ObjectDetails<ThsEntry> details = service.get(id);
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
        );

        testResult.andExpect(
            status().isOk()
        ).andExpect(
            content().string(
                not(containsString(
                    String.format("_%s??", "en")
                ))
            )
        );
    }

}