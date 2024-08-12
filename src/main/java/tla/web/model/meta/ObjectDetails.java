package tla.web.model.meta;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.Comparator;
import java.util.HashMap;

import lombok.Getter;
import tla.domain.dto.extern.SingleDocumentWrapper;
import tla.domain.dto.meta.AbstractDto;
import tla.web.model.Lemma;
import tla.web.model.mappings.MappingConfig;

@Getter
public class ObjectDetails<T extends TLAObject> extends ObjectsContainer {

	private T object;

	public ObjectDetails(T object) {
		this.object = object;
	}

	@SuppressWarnings("unchecked")
	public ObjectDetails(SingleDocumentWrapper<?> dto) {
		super(dto);
		this.object = (T) MappingConfig.convertDTO(dto.getDoc());
	}

	public ObjectDetails(T object, Map<String, Map<String, TLAObject>> related) {
		super(related);
		this.object = object;
	}

	/**
	 * Map payload and related objects from DTO to domain model types.
	 */
	public static ObjectDetails<? extends TLAObject> from(SingleDocumentWrapper<? extends AbstractDto> wrapper) {
		var container = new ObjectDetails<>(wrapper);
		return container;
	}

	/**
	 * Projects objects in a single object details container's <code>related</code>
	 * map to the wrapped object's <code>relations</code> (which are
	 * {@link TLAObject} stubs in objects freshly converted from DTO).
	 */

	public Map<String, List<TLAObject>> extractRelatedObjects() {
		Map<String, List<TLAObject>> extractedRelations = new HashMap<String, List<TLAObject>>();

		extractedRelations = this.getObject().getRelations().entrySet().stream()
				.collect(Collectors.toMap(entry -> entry.getKey(),
						entry -> entry.getValue().stream().map(reference -> this.expandRelatedObject(reference))
								.filter(o -> o != null).collect(Collectors.toList())));
		for (String key : extractedRelations.keySet()) {
			List<TLAObject> value = extractedRelations.get(key);
			value.sort(new NameComparator());
		}
		return extractedRelations;
	}

	class NameComparator implements Comparator<TLAObject> {
		@Override
		public int compare(TLAObject a, TLAObject b) {			
			return a.compareObjects(b);
		}
	}
}
