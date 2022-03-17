package tla.web.model.parts.extra;

public class AttestedTimespan extends tla.domain.model.extern.AttestedTimespan {

    public static AttestedTimespan of(tla.domain.model.extern.AttestedTimespan dto) {
        AttestedTimespan o = new AttestedTimespan();
        o.setAttestations(dto.getAttestations());
        o.setPeriod(dto.getPeriod());
        o.setContains(dto.getContains());
        return o;
    }

    public AttestationStats getTotal() {
    	
        AttestationStats stats = this.getContains().stream().map(
            child -> child.getAttestations()
        ).reduce(
            new AttestationStats(),
            (total, current) -> total.add(current)
        );
        return stats.add(this.getAttestations());
    }

}
