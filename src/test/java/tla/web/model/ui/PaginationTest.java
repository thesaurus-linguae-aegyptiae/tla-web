package tla.web.model.ui;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Collections;

import org.junit.jupiter.api.Test;

import tla.domain.dto.extern.PageInfo;

public class PaginationTest {

    private PageInfo makePageInfo(int pageSize, int totalElements, int totalPages, int currentPage) {
        PageInfo dto = new PageInfo();
        dto.setNumber(currentPage - 1);
        dto.setSize(pageSize);
        dto.setTotalElements(totalElements);
        dto.setTotalPages(totalPages);
        dto.setNumberOfElements(20);
        return dto;
    }

    @Test
    void nullInput() {
        Pagination pagination = new Pagination(null);
        assertAll("pagination should allow for creation from null",
            () -> assertEquals(1, pagination.getCurrentPage())
        );
    }

    @Test
    void firstPage() {
        PageInfo dto = makePageInfo(20, 199, 10, 1);
        Pagination p = new Pagination(dto);
        assertAll("test UI pagination representation object for first page",
            () -> assertEquals(1, p.getCurrentPage(), "first page"),
            () -> assertEquals(0, dto.getNumber(), "pages in DTO start at 0"),
            () -> assertEquals(7, p.getPages().size(), String.format("number pages. actual pages: %s", p.getPages())),
            () -> assertEquals(199, p.getTotalResults(), "total results"),
            () -> assertEquals(1, p.getFromResult(), "from result #"),
            () -> assertEquals(20, p.getToResult(), "to result #")
        );
    }

    @Test
    void pageLinks() {
        Pagination p = new Pagination(makePageInfo(20, 199, 14, 7));
        assertAll("test page to required page distance function",
            () -> assertEquals(2, p.distanceToRequiredPage(3), "page 3"),
            () -> assertEquals(2, p.distanceToRequiredPage(9), "page 9"),
            () -> assertEquals(3, p.distanceToRequiredPage(4), "page 4")
        );
        assertAll("test page range omission",
            () -> assertEquals(13, p.getPages().size(), "1 page should be omitted"),
            () -> assertEquals(
                1,
                Collections.frequency(p.getPages(), null),
                String.format("expect 1 omission range but got: %s", p.getPages())
            ),
            () -> assertEquals(null, p.getPages().get(9), "10th & 11th page omitted"),
            () -> assertEquals(12, p.getPages().get(10), "picking up after omission at 12th page")
        );
    }
    
}