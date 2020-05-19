package tla.web.model.ui;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;

import lombok.Getter;
import tla.domain.dto.extern.PageInfo;

/**
 * For display of pagination widget in UI.
 */
@Getter
public class Pagination {

    private long fromResult;
    private long toResult;
    private long totalResults;

    private List<Integer> pages;
    private int currentPage;

    /**
     * Takes the pagination info from DTO and turns it into something we can
     * render more easily.
     */
    public Pagination(PageInfo dtoPage) {
        if (dtoPage != null) {
            this.currentPage = dtoPage.getNumber() + 1;
            this.totalResults = dtoPage.getTotalElements();
            this.fromResult = (this.currentPage - 1) * dtoPage.getSize() + 1;
            this.toResult = (this.currentPage - 1) * dtoPage.getSize() + dtoPage.getNumberOfElements();
            this.pages = new LinkedList<Integer>();
            int lastPage = Math.max(dtoPage.getTotalPages(), 1);
            IntStream.rangeClosed(1, lastPage).forEach(
                p -> {
                    this.pages.add(Integer.valueOf(p));
                }
            );
            } else {
                this.currentPage = 1;
                this.pages = List.of(this.currentPage);
            }
    }

}