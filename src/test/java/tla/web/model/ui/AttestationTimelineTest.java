package tla.web.model.ui;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import tla.domain.model.ObjectReference;
import tla.domain.model.extern.AttestedTimespan.AttestationStats;
import tla.domain.model.extern.AttestedTimespan.Period;
import tla.web.model.parts.extra.AttestedTimespan;

public class AttestationTimelineTest {

    @Test
    @DisplayName("place rectangles on timeline (only root nodes)")
    void timelineConstructorRectanglePlacement() {
        var att1 = AttestedTimespan.of(AttestedTimespan.builder().period(
            Period.builder().begin(-444).end(-222).ref(
                ObjectReference.builder().eclass("BTSThsEntry").id("p1").name("ancient period").type("date").build()
            ).build()
        ).attestations(
            AttestationStats.builder().count(11).build()
        ).build());
        var att2 = AttestedTimespan.of(AttestedTimespan.builder().period(
            Period.builder().begin(66).end(140).ref(
                ObjectReference.builder().eclass("BTSThsEntry").id("p2").name("recent period").type("date").build()
            ).build()
        ).attestations(
            AttestationStats.builder().count(13).build()
        ).build());
        // render timeline
        AttestationTimeline tl = AttestationTimeline.from(
            List.of(att1, att2)
        );
        assertAll("should be able to place timeline for attested timespans",
            () -> assertEquals(4, tl.getRectangles().size(), "should have placed 2x2 rectangles"),
            () -> assertTrue(tl.getRectangles().get(2).getX() > tl.getRectangles().get(0).getX(), "more recent period to the right of older one"),
            () -> assertEquals(-444, tl.getTotalTimespan().getBegin(), "total timespan covered determined by root attestation nodes (begin)"),
            () -> assertEquals(140, tl.getTotalTimespan().getEnd(), "total timespan covered determined by root attestation nodes (end)")
        );
    }

    @Test
    @DisplayName("place rectangles on timeline, including nested nodes")
    void recursiveRectanglePlacement() {
        var att3 = AttestedTimespan.of(AttestedTimespan.builder().period(
            Period.builder().begin(72).end(90).ref(
                ObjectReference.builder().eclass("BTSThsEntry").id("p2.1").name("recent sub-period").type("date").build()
            ).build()
        ).attestations(
            AttestationStats.builder().count(5).build()
        ).build());
        var att2 = AttestedTimespan.of(AttestedTimespan.builder().period(
            Period.builder().begin(66).end(140).ref(
                ObjectReference.builder().eclass("BTSThsEntry").id("p2").name("recent period").type("date").build()
            ).build()
        ).attestations(
            AttestationStats.builder().count(13).build()
        ).contains(
            List.of(att3)).build()
        );
        // render timeline
        AttestationTimeline tl = AttestationTimeline.from(
            List.of(att2)
        );
        assertAll("should be able to place nested attestation timespans",
            () -> assertEquals(4, tl.getRectangles().size(), "should have placed 2x2 rectangles")
        );
    }


}
