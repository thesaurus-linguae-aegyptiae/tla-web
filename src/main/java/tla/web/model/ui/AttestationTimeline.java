package tla.web.model.ui;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.Getter;
import tla.web.model.parts.extra.AttestedTimespan;

@Getter
public class AttestationTimeline {

    public final static int FROM = -5000;
    public final static int TO = 600;
    /**
     * where the tics are to be drawn on the x-axis
     */
    public final static List<Integer> XTICS = List.of(100, 500, 1000, 2000);

    private List<Rect> rectangles;
    private long maxCount;

    private tla.domain.model.extern.AttestedTimespan.Period totalTimespan;

    /**
     * x-axis tics
     */
    private List<Tic> tics;

    public AttestationTimeline(Collection<AttestedTimespan> attestations) {
        this.maxCount = attestations.stream().mapToLong(
            attestation -> attestation.getTotal().getCount()
        ).sum();
        this.totalTimespan = tla.domain.model.extern.AttestedTimespan.Period.builder().begin(
            attestations.stream().mapToInt(
                attestation -> attestation.getPeriod().getBegin()
            ).min().orElse(0)
        ).end(
            attestations.stream().mapToInt(
                attestation -> attestation.getPeriod().getEnd()
            ).max().orElse(0)
        ).build();
        this.rectangles = attestations.stream().flatMap(
            attestation -> this.renderAttestedTimespan(attestation)
        ).collect(Collectors.toList());
        Collections.sort(this.rectangles);
        this.tics = this.createXTics();
    }

    private int getHighestTicInterval(int year) {
        for (int i=XTICS.size()-1; i>=0; i--) {
            if (year % XTICS.get(i) == 0) {
                return i;
            }
        }
        return 0;
    }

    protected List<Tic> createXTics() {
        List<Tic> tics = new LinkedList<>();
        var s = XTICS.get(0);
        var start = FROM / s * s + 2 * s;
        var end = TO / s * s - 2 * s;
        for (int year=start; year <= end; year += XTICS.get(0)) {
            int i = getHighestTicInterval(year);
            tics.add(new Tic(year, i));
        }
        return tics;
    }

    public static AttestationTimeline from(List<AttestedTimespan> attestations) {
        return new AttestationTimeline(attestations);
    }

    protected Stream<Rect> renderAttestedTimespan(tla.domain.model.extern.AttestedTimespan attestation) {
        return Stream.concat(
            List.of(
                new Rect(attestation, 0, a -> a.getAttestations().getCount()),
                new Rect(attestation, 30, a -> AttestedTimespan.of(a).getTotal().getCount())
            ).stream(),
            attestation.getContains().stream().flatMap(
                child -> renderAttestedTimespan(child)
            )
        );
    }

    /**
     * project given year onto timeline, which is defined by {@link FROM} and {@link TO},
     * and return the result as a percentage indicating where to place that year from left
     * to right.
     */
    protected static double projectDate(int year) {
        return 100f * (year - FROM) / (TO - FROM);
    }

    @Getter
    public class Rect implements Comparable<Rect> {

        private double x;
        private double y;
        private double w;
        private double h;
        private double alpha;
        private long count;
        private tla.domain.model.extern.AttestedTimespan.Period period;

        public Rect(
            tla.domain.model.extern.AttestedTimespan attestation, double y,
            Function<tla.domain.model.extern.AttestedTimespan, Long> countFunc
        ) {
            this.period = attestation.getPeriod();
            var left = projectDate(attestation.getPeriod().getBegin());
            var right = projectDate(attestation.getPeriod().getEnd());
            this.alpha = .2f + .8f * countFunc.apply(attestation) / getMaxCount();
            this.count = attestation.getAttestations().getCount();
            this.x = left;
            this.w = right - left;
            this.y = y;
            this.h = 30f;
        }

        @Override
        public int compareTo(Rect o) {
            return Integer.compare((int) this.x, (int) o.x);
        }
    }

    @Getter
    public class Tic {
        private double x;
        /** tic length */
        private double l;
        /** tic stroke width */
        private double w;
        private int level;
        private String label;

        public Tic(int year, int level) {
            this.level = level;
            this.x = projectDate(year);
            this.l = 6 + (int) Math.pow(1.6, level + 1) * 4;
            this.w = 0.25 + Math.pow(1.9, level) / 8;
            this.label = String.format("%d", year);
        }

    }
}
