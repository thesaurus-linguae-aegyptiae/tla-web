package tla.web.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tla.domain.dto.CorpusObjectDto;
import tla.domain.model.ObjectPath;
import tla.domain.model.ObjectReference;
import tla.domain.model.Passport;
import tla.domain.model.meta.BTSeClass;
import tla.domain.model.meta.Hierarchic;
import tla.domain.model.meta.TLADTO;
import tla.web.model.meta.BTSObject;
import tla.web.model.meta.BackendPath;

@Getter
@Setter
@NoArgsConstructor
@BackendPath("object")
@BTSeClass("BTSTCObject")
@TLADTO(CorpusObjectDto.class)
public class CorpusObject extends BTSObject implements Hierarchic {

	private List<ObjectPath> paths;
	
	private CorpusObject corpusobj;

	// Text object date

	public static final String PASSPORT_PROP_DATE = "date.date.date";
	@Setter(AccessLevel.NONE)
	private List<String> date;

	public List<String> getDate() {
		if (this.date == null) {
			this.date = extractDate(this);
		}
		return this.date;
	}

	private static List<String> extractDate(CorpusObject corpusobj) {
		List<String> dates = new ArrayList<String>();
		try {

			List<Passport> datesPassport = corpusobj.getPassport().extractProperty(PASSPORT_PROP_DATE);

			for (int i = 0; i < datesPassport.size(); i++) {
				for (int j = 0; j < datesPassport.get(i).extractObjectReferences().size(); j++) {

					dates.add(datesPassport.get(i).extractObjectReferences().get(j).getName());
				}
			}

		} catch (Exception e) {
			System.out.println("INFO: Could not extract date from object " + corpusobj.getId());
		}
		return dates;
	}

	// Text object date comment

	public static final String PASSPORT_PROP_DATECOMMENT = "date.date.comment";

	@Setter(AccessLevel.NONE)
	private List<String> dateComment;

	public List<String> getDateComment() {
		if (this.dateComment == null) {
			this.dateComment = extractDateComment(this);
		}
		return this.dateComment;
	}

	private static List<String> extractDateComment(CorpusObject corpusobj) {
		List<String> dateComment = new ArrayList<>();
		try {
			corpusobj.getPassport().extractProperty(PASSPORT_PROP_DATECOMMENT)
					.forEach(node -> dateComment.addAll(Arrays
							.asList(node.getLeafNodeValue().replaceAll("(\\r?\\n|^)[\\s\\-]+", "$1")
									.replaceAll("\\r?\\n[\\r?\\n\\s]*", "||").split("\\|\\|"))
							.stream().map(para -> para.strip()).collect(Collectors.toList())));
		} catch (Exception e) {
			System.out.println("INFO: Could not extract dating comment from object " + corpusobj.getId());
		}
		return dateComment;
	}

	// Synonyms

	public static final String PASSPORT_PROP_SYNONYMS = "synonyms.synonym_group";

	@Setter(AccessLevel.NONE)
	private List<SynonymGroup> synonymGroups;

	private static class SynonymGroup {

		private String synonym;
		private String language;

		public String getLanguage() {
			return this.language;
		}

		public String getSynonym() {
			return this.synonym;
		}
	}

	public static final String PASSPORT_PROP_SYNONYMGROUPS_SYNONYMS = "synonym";
	public static final String PASSPORT_PROP_SYNONYMGROUPS_LANGUAGE = "language";

	private static SynonymGroup addSynonymGroup(Passport passport) {
		SynonymGroup synonymGroup = new SynonymGroup();
		try {
			passport.extractProperty(PASSPORT_PROP_SYNONYMGROUPS_SYNONYMS)
					.forEach(node -> synonymGroup.synonym = node.getLeafNodeValue());
			passport.extractProperty(PASSPORT_PROP_SYNONYMGROUPS_LANGUAGE)
					.forEach(node -> synonymGroup.language = node.getLeafNodeValue());
		} catch (Exception e) {
			System.out.println("INFO:Could not extract synonym or language \n");
		}

		return synonymGroup;
	}

	public List<SynonymGroup> getSynonymGroups() {
		if (this.synonymGroups == null) {
			SynonymGroup synonymGroup = new SynonymGroup();
			this.synonymGroups = extractSynonyms(this);
		}
		return this.synonymGroups;
	}

	private static List<SynonymGroup> extractSynonyms(CorpusObject corpusobj) {
		List<SynonymGroup> synonymGroups = new ArrayList<>();
		try {
			corpusobj.getPassport().extractProperty(PASSPORT_PROP_SYNONYMS)
					.forEach(node -> synonymGroups.add(addSynonymGroup(node)));
			;
		} catch (Exception e) {
			System.out.println("INFO: Could not extract synonyms from object " + corpusobj.getId());
		}
		return synonymGroups;
	}
	


	// Bibliography
	public static final String PASSPORT_PROP_BIBL = "bibliography.bibliographical_text_field";

	@Setter(AccessLevel.NONE)
	private List<String> bibliography;

	public List<String> getBibliography() {
		if (this.bibliography == null) {
			this.bibliography = extractBibliography(this);
		}
		return this.bibliography;
	}

	/**
	 * Extract bibliographic information from coprus object passport.
	 *
	 * Bibliography is being copied from the
	 * <code>bibliography.bibliographical_text_field</code> passport field. The
	 * value(s) found under that locator are split at line breaks <code>\r\n</code>.
	 *
	 * @param corpusobj The corpus object instance from whose passport the
	 *                  bibliography is to be extracted.
	 * @return List of textual bibliographic references or an empty list
	 */
	private static List<String> extractBibliography(CorpusObject corpusobj) {
		List<String> bibliography = new ArrayList<>();
		try {
			corpusobj.getPassport().extractProperty(PASSPORT_PROP_BIBL)
					.forEach(node -> bibliography.addAll(Arrays
							.asList(node.getLeafNodeValue().replaceAll("(\\r?\\n|^)[\\s\\-]+", "$1")
									.replaceAll("\\r?\\n[\\r?\\n\\s]*", "||").split("\\|\\|"))
							.stream().map(bibref -> bibref.strip()).collect(Collectors.toList())));
		} catch (Exception e) {
			System.out.println("INFO: Could not extract bibliography from object " + corpusobj.getId());
		}
		return bibliography;
	}

	// Find spot

	public static final String PASSPORT_PROP_FINDSPOT = "find_spot.find_spot";
	public static final String PASSPORT_PROP_FINDSPOT_PLACE = "place";
	public static final String PASSPORT_PROP_FINDSPOT_FORMERPLACE = "former_place";

	@Setter(AccessLevel.NONE)
	private List<FindSpot> findSpots;
	
	abstract class abstractPlace{
	private String certainty;
	private String comment;
	private String is_origin;
	private ObjectReference place;
	
	public String getCertainty() {
		return this.certainty;
	}
	

	public abstract abstractPlace searchPlaces(Passport passport);
	private abstractPlace extractPlaces(Passport passport, abstractPlace place, String searchString) {
		System.out.println("BLUVBBBB \n");
		try {
			passport.extractObjectReferences(
					).forEach(
							node -> place.place = node);
	}catch (Exception e) {
		System.out.println("INFO: Could not extract object reference from object " + corpusobj.getId());
	}
try {
		passport.extractProperty("certainty"
				).forEach(
						node -> place.certainty = node.getLeafNodeValue());
}
catch (Exception e) {
	System.out.println("INFO: Could not extract certainty from object " + corpusobj.getId());
}
try {
	passport.extractProperty("comment"
			).forEach(
					node -> place.comment = node.getLeafNodeValue());
}
catch (Exception e) {
System.out.println("INFO: Could not extract comment from object " + corpusobj.getId());
}
try {
	passport.extractProperty("is_origin"
			).forEach(
					node -> place.is_origin = node.getLeafNodeValue());
}
catch (Exception e) {
System.out.println("INFO: Could not extract is_origin from object " + corpusobj.getId());
}

try {
	passport.extractProperty(searchString).get(1).extractObjectReferences();
}
catch (Exception e) {
System.out.println("INFO: Could not extract any object reference from object " + corpusobj.getId());
}
		
return place;	}
	}
		
	
	private class Place extends abstractPlace{
		
		@Override
		public abstractPlace searchPlaces(Passport passport ) {
			System.out.println("TTTTTTTTTTTTTTTTTTTT");
			
			return super.extractPlaces(passport,new Place(), PASSPORT_PROP_FINDSPOT_PLACE);
		}
		
		
	}
	
private class FormerPlace extends abstractPlace{
	@Override
	public abstractPlace searchPlaces(Passport passport ) {
		return super.extractPlaces(passport, new FormerPlace(),PASSPORT_PROP_FINDSPOT_FORMERPLACE);
	}
	
		
	}

	private static class FindSpot{
		private static List<Place> places;
		private static List<FormerPlace> formerPlaces;
		private static Place place;
		private static FormerPlace formerPlace;
		
		public List<Place> getPlaces(){		
		return this.places;	
		}
		public static void extractPlace(Passport passport) {
			System.out.println(passport.toString());
			place = (Place) place.searchPlaces(passport);
			places.add(place);
		}
		
		public String test = "test";
		
public List<FormerPlace> getFormerPlaces(){
			
	return this.formerPlaces;
		}
public static void extractFormerPlace(Passport passport) {
	formerPlace = (FormerPlace) formerPlace.searchPlaces(passport);
	formerPlaces.add(formerPlace);
}
	}

	public List<FindSpot> getFindSpots() {
		if (this.findSpots == null) {
			this.findSpots = extractFindSpot(this);
		}
		return this.findSpots;
	}
	
	private static FindSpot addFindSpot() {
		FindSpot findSpot = new FindSpot();
		
		return findSpot;
	}

	private static List<FindSpot> extractFindSpot(CorpusObject corpusobj) {
		List<FindSpot> findSpots = new ArrayList<FindSpot>();
		try {

			List<Passport> findSpotPassports = corpusobj.getPassport().extractProperty(PASSPORT_PROP_FINDSPOT);
			for (int i = 0; i < findSpotPassports.size(); i++) {
				FindSpot findSpot = new FindSpot();
				List<Passport> places = new ArrayList<Passport>();
				try{
					places.addAll(findSpotPassports.get(i).extractProperty(PASSPORT_PROP_FINDSPOT_PLACE));
				System.out.println(places.size());
				places.forEach(
						node->System.out.println("Blub\n") );
				}catch (Exception e) {
					System.out.println("INFO: Could not extract find spots from object " + corpusobj.getId());
				}
		List<Passport> formerPlaces = new ArrayList<Passport>();
		try {
		formerPlaces.addAll(findSpotPassports.get(i).extractProperty(PASSPORT_PROP_FINDSPOT_FORMERPLACE));
		formerPlaces.forEach(
				node->FindSpot.extractFormerPlace(node));
		}catch(Exception e) {
			System.out.println("INFO: Could not extract find spots from object " + corpusobj.getId());
		}
				findSpots.add(findSpot);
				}
				
				
			
	
		} catch (Exception e) {
			System.out.println("INFO: Could not extract find spots from object " + corpusobj.getId());
		}
		return findSpots;
	}
	

	
	
	
	
	

	// Object location

	public static final String PASSPORT_PROP_PRESLOC = "present_location.location.location";

	@Setter(AccessLevel.NONE)
	private List<String> locations;

	public List<String> getLocations() {
		if (this.locations == null) {
			this.locations = extractLocations(this);
		}
		return this.locations;
	}

	private static List<String> extractLocations(CorpusObject corpusobj) {
		List<String> locations = new ArrayList<String>();
		try {
			List<Passport> locationsPassports = corpusobj.getPassport().extractProperty(PASSPORT_PROP_PRESLOC);

			for (int i = 0; i < locationsPassports.size(); i++) {
				for (int j = 0; j < locationsPassports.get(i).extractObjectReferences().size(); j++) {

					locations.add(locationsPassports.get(i).extractObjectReferences().get(j).getName());
				}
			}
		} catch (Exception e) {
			System.out.println("INFO: Could not extract locations from object " + corpusobj.getId());
		}
		return locations;
	}

	// Object Inv.nos.

	public static final String PASSPORT_PROP_PRESLOC_ID = "present_location.location.inventory_number";

	@Setter(AccessLevel.NONE)
	private List<String> invNos;

	public List<String> getInvNos() {
		if (this.invNos == null) {
			this.invNos = extractInvNos(this);
		}
		return this.invNos;
	}

	private static List<String> extractInvNos(CorpusObject corpusobj) {
		List<String> invNos = new ArrayList<String>();
		try {
			corpusobj.getPassport().extractProperty(PASSPORT_PROP_PRESLOC_ID)
					.forEach(node -> invNos.add(node.getLeafNodeValue()));
		} catch (Exception e) {
			System.out.println("INFO: Could not extract location inventory nos. from object " + corpusobj.getId());
		}

		return invNos;
	}

	// Protocol

	public static final String PASSPORT_PROP_PROTOCOL = "definition.main_group.protocol";

	@Setter(AccessLevel.NONE)
	private List<String> protocol;

	public List<String> getProtocol() {
		if (this.protocol == null) {
			this.protocol = extractProtocol(this);
		}
		return this.protocol;
	}

	private static List<String> extractProtocol(CorpusObject corpusobj) {
		List<String> protocol = new ArrayList<>();
		try {
			corpusobj.getPassport().extractProperty(PASSPORT_PROP_PROTOCOL)
					.forEach(node -> protocol.addAll(Arrays
							.asList(node.getLeafNodeValue().replaceAll("(\\r?\\n|^)[\\s\\-]+", "$1")
									.replaceAll("\\r?\\n[\\r?\\n\\s]*", "||").split("\\|\\|"))
							.stream().map(bibref -> bibref.strip()).collect(Collectors.toList())));
		} catch (Exception e) {
			System.out.println("INFO: Could not extract protocol from object " + corpusobj.getId());
		}
		return protocol;
	}

	// Description

	public static final String PASSPORT_PROP_DESCR = "definition.main_group.definition";

	@Setter(AccessLevel.NONE)
	private List<String> description;

	public List<String> getDescription() {
		if (this.description == null) {
			this.description = extractDescription(this);
		}
		return this.description;
	}

	private static List<String> extractDescription(CorpusObject corpusobj) {
		List<String> description = new ArrayList<>();
		try {
			corpusobj.getPassport().extractProperty(PASSPORT_PROP_DESCR)
					.forEach(node -> description.addAll(Arrays
							.asList(node.getLeafNodeValue().replaceAll("(\\r?\\n|^)[\\s\\-]+", "$1")
									.replaceAll("\\r?\\n[\\r?\\n\\s]*", "||").split("\\|\\|"))
							.stream().map(bibref -> bibref.strip()).collect(Collectors.toList())));
		} catch (Exception e) {
			System.out.println("INFO: Could not extract description from object " + corpusobj.getId());
		}
		return description;
	}

	// File comment

	public static final String PASSPORT_PROP_FILECOMMENT = "definition.main_group.comment";

	@Setter(AccessLevel.NONE)
	private List<String> fileComment;

	public List<String> getFileComment() {
		if (this.fileComment == null) {
			this.fileComment = extractFileComment(this);
		}
		return this.fileComment;
	}

	private static List<String> extractFileComment(CorpusObject corpusobj) {
		List<String> fileComment = new ArrayList<>();
		try {
			corpusobj.getPassport().extractProperty(PASSPORT_PROP_FILECOMMENT)
					.forEach(node -> fileComment.addAll(Arrays
							.asList(node.getLeafNodeValue().replaceAll("(\\r?\\n|^)[\\s\\-]+", "$1")
									.replaceAll("\\r?\\n[\\r?\\n\\s]*", "||").split("\\|\\|"))
							.stream().map(bibref -> bibref.strip()).collect(Collectors.toList())));
		} catch (Exception e) {
			System.out.println("INFO: Could not extract file comment from object " + corpusobj.getId());
		}
		return fileComment;
	}

	// Object description
	///////////////////////////
	public static final String PASSPORT_PROP_OBJ_MODEL = "object.description_of_object.model";
	public static final String PASSPORT_PROP_OBJ_IMITATION = "object.description_of_object.imitation";
	public static final String PASSPORT_PROP_OBJ_MINIATURE = "object.description_of_object.miniature";
	public static final String PASSPORT_PROP_OBJ_SKEUO = "object.description_of_object.skeuomorph";
	public static final String PASSPORT_PROP_OBJ_OWNER = "object.description_of_object.owner";
	public static final String PASSPORT_PROP_OBJ_DESCR = "object.description_of_object.description";
	public static final String PASSPORT_PROP_OBJ_COMMENT = "object.description_of_object.comment";
	public static final String PASSPORT_PROP_OBJ_CONDITION = "object.technical_details.condition";
	public static final String PASSPORT_PROP_OBJ_TECH = "object.technical_details.technique";
	public static final String PASSPORT_PROP_OBJ_TECH_COMMENT = "object.technical_details.comment";
	public static final String PASSPORT_PROP_OBJ_GROUPING = "object.description_of_object.context.grouping";
	public static final String PASSPORT_PROP_OBJ_CONTEXT = "object.description_of_object.context.context";
	public static final String PASSPORT_PROP_OBJ_ARCH_COMMENT = "object.description_of_object.context.comment";

	// Object type

	public static final String PASSPORT_PROP_OBJ_TYPE = "object.description_of_object.type";

	@Setter(AccessLevel.NONE)
	private List<String> objType;

	public List<String> getObjType() {
		if (this.objType == null) {
			this.objType = extractObjType(this);
		}
		return this.objType;
	}

	private static List<String> extractObjType(CorpusObject corpusobj) {
		List<String> objType = new ArrayList<String>();
		try {

			List<Passport> objTypePassports = corpusobj.getPassport().extractProperty(PASSPORT_PROP_OBJ_TYPE);

			for (int i = 0; i < objTypePassports.size(); i++) {
				for (int j = 0; j < objTypePassports.get(i).extractObjectReferences().size(); j++) {

					objType.add(objTypePassports.get(i).extractObjectReferences().get(j).getName());
				}
			}
		} catch (Exception e) {
			System.out.println("INFO: Could not extract object type from object " + corpusobj.getId());
		}
		return objType;
	}

	// Object component

	public static final String PASSPORT_PROP_OBJ_COMPONENT = "object.description_of_object.component";

	@Setter(AccessLevel.NONE)
	private List<String> components;

	public List<String> getComponents() {
		if (this.components == null) {
			this.components = extractComponents(this);
		}
		return this.components;
	}

	private static List<String> extractComponents(CorpusObject corpusobj) {
		List<String> components = new ArrayList<String>();
		try {

			List<Passport> componentsPassports = corpusobj.getPassport().extractProperty(PASSPORT_PROP_OBJ_COMPONENT);

			for (int i = 0; i < componentsPassports.size(); i++) {
				for (int j = 0; j < componentsPassports.get(i).extractObjectReferences().size(); j++) {

					components.add(componentsPassports.get(i).extractObjectReferences().get(j).getName());
				}
			}
		} catch (Exception e) {
			System.out.println("INFO: Could not extract components from object " + corpusobj.getId());
		}
		return components;
	}

	// Material

	public static final String PASSPORT_PROP_OBJ_MATERIAL = "object.technical_details.material";

	@Setter(AccessLevel.NONE)
	private List<String> materials;

	public List<String> getMaterials() {
		if (this.materials == null) {
			this.materials = extractMaterials(this);
		}
		return this.materials;
	}

	private static List<String> extractMaterials(CorpusObject corpusobj) {
		List<String> materials = new ArrayList<String>();
		try {

			List<Passport> materialsPassports = corpusobj.getPassport().extractProperty(PASSPORT_PROP_OBJ_MATERIAL);

			for (int i = 0; i < materialsPassports.size(); i++) {
				for (int j = 0; j < materialsPassports.get(i).extractObjectReferences().size(); j++) {
					materials.add(materialsPassports.get(i).extractObjectReferences().get(j).getName());
				}
			}
		} catch (Exception e) {
			System.out.println("INFO: Could not extract materials from object " + corpusobj.getId());
		}
		return materials;
	}

	// Dimensions

	public static final String PASSPORT_PROP_OBJ_HEIGHT = "object.technical_details.dimensions.h";
	public static final String PASSPORT_PROP_OBJ_LENGTH = "object.technical_details.dimensions.l";
	public static final String PASSPORT_PROP_OBJ_WIDTH = "object.technical_details.dimensions.w";

	@Setter(AccessLevel.NONE)
	private List<String> dimensions;

	public List<String> getDimensions() {
		if (this.dimensions == null) {
			this.dimensions = extractDimensions(this);
		}
		return this.dimensions;
	}

	private static List<String> extractDimensions(CorpusObject corpusobj) {
		List<String> dimensions = new ArrayList<>();
		try {
			corpusobj.getPassport().extractProperty(PASSPORT_PROP_OBJ_HEIGHT)
					.forEach(node -> dimensions.add(node.getLeafNodeValue()));
		} catch (Exception e) {
			System.out.println("INFO: Could not extract length from object " + corpusobj.getId());
		}
		try {
			corpusobj.getPassport().extractProperty(PASSPORT_PROP_OBJ_LENGTH)
					.forEach(node -> dimensions.add(node.getLeafNodeValue()));
		} catch (Exception e) {
			System.out.println("INFO: Could not extract width from object " + corpusobj.getId());
		}
		try {
			corpusobj.getPassport().extractProperty(PASSPORT_PROP_OBJ_WIDTH)
					.forEach(node -> dimensions.add(node.getLeafNodeValue()));
		} catch (Exception e) {
			System.out.println("INFO: Could not extract thickness from object " + corpusobj.getId());
		}
		return dimensions;
	}

}