package tla.web.model.ui;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import tla.domain.dto.extern.PageInfo;

public class PaginationTest {

    @Test
    void nullInput() {
        Pagination pagination = new Pagination(null);
        assertAll("pagination should allow for creation from null",
            () -> assertEquals(1, pagination.getCurrentPage())
        );
    }

    @Test
    void firstPage() {
        PageInfo dto = new PageInfo();
        dto.setNumber(0);
        dto.setSize(20);
        dto.setTotalElements(199);
        dto.setTotalPages(10);
        dto.setNumberOfElements(20);
        Pagination p = new Pagination(dto);
        assertAll("test UI pagination representation object for first page",
            () -> assertEquals(1, p.getCurrentPage(), "first page"),
            () -> assertEquals(10, p.getPages().size(), "number pages"),
            () -> assertEquals(199, p.getTotalResults(), "total results"),
            () -> assertEquals(1, p.getFromResult(), "from result #"),
            () -> assertEquals(20, p.getToResult(), "to result #")
        );

    }
    
}