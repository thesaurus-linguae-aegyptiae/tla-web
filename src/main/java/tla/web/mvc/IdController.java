package tla.web.mvc;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import lombok.extern.slf4j.Slf4j;
import tla.web.repo.TlaClient;

@Slf4j
@Controller
@RequestMapping("/id")
public class IdController {
	/**
	 * Checks if ID exists and redirects to the corresponding page for details
	 */
	@Autowired
	protected TlaClient backend;

	@RequestMapping(value = "", method = RequestMethod.GET)
	public RedirectView getViewForRequestParamId(@RequestParam String id) {
		return redirectToViewForId(id);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public RedirectView getViewForPathVariable(@PathVariable String id) {
		return redirectToViewForId(id);
	}

	public RedirectView redirectToViewForId(String id) {
		String type = getTypeOfId(id);
		return new RedirectView(String.format("/%s/%s", getViewPathForType(type), id), true);
	}

	public String getTypeOfId(String id) {
		try {
			return backend.getTypeOfIdFromAllIndices(id);
		} catch (Exception e) {
			return "false";
		}
	}

	public String getViewPathForType(String type) {
		Map<String, String> indices = new HashMap<String, String>();
		indices.put("lemma", "lemma");
		indices.put("text", "text");
		indices.put("sentence", "sentence");
		indices.put("token", "sentence/token");
		indices.put("ths", "thesaurus");
		indices.put("object", "object");
		indices.put("false", "error");
		// Comment and Annotation later to come

		return indices.getOrDefault(type, "error");
	}
}