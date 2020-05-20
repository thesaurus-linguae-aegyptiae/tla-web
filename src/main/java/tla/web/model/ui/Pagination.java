package tla.web.model.ui;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
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

    public final static int OMISSION_THRESHOLD = 2;

    private long fromResult;
    private long toResult;
    private long totalResults;

    private List<Integer> pages;
    private int currentPage;
    private int lastPage;

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
            this.lastPage = Math.max(dtoPage.getTotalPages(), 1);
            this.pages = this.makePageLinks();
        } else {
            this.currentPage = 1;
            this.pages = List.of(this.currentPage);
        }
    }

    /**
     * Returns distance to first, last, or current page, depending on what is
     * closest.
     * @param page
     * @return
     */
    protected int distanceToRequiredPage(int page) {
        return Arrays.asList(
            1,
            this.currentPage,
            this.lastPage
        ).stream().map(
            requiredPage -> Math.abs(requiredPage - page)
        ).reduce(
            Math::min
        ).get();
    }

    /**
     * Creates a sequence of <code>Integer</code> values representing the pages
     * covered by this pagination object. <code>null</code> values represent
     * ellipsis placeholders (n pages omitted).
     */
    protected List<Integer> makePageLinks() {
        List<Integer> pages = new LinkedList<Integer>();
        List<Integer> omitted = new LinkedList<Integer>();
        IntStream.rangeClosed(1, this.lastPage).forEach(
            p -> {
                if (this.distanceToRequiredPage(p) > OMISSION_THRESHOLD) {
                    omitted.add(p);
                } else {
                    if (omitted.size() > 1) {
                        pages.add(null);
                    } else {
                        pages.addAll(omitted);
                    }
                    omitted.clear();
                    pages.add(p);
                }
            }
        );
        return pages;
    }

}