package org.folio.marc4ld.service.mapper.impl;

import static org.folio.ld.dictionary.PredicateDictionary.IS_DEFINED_BY;
import static org.folio.ld.dictionary.PredicateDictionary.TARGET_AUDIENCE;
import static org.folio.ld.dictionary.PropertyDictionary.LABEL;
import static org.folio.ld.dictionary.PropertyDictionary.LINK;
import static org.folio.ld.dictionary.PropertyDictionary.TERM;
import static org.folio.ld.dictionary.ResourceTypeDictionary.CATEGORY_SET;
import static org.folio.marc4ld.util.BibframeUtil.hash;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.folio.ld.dictionary.PredicateDictionary;
import org.folio.ld.dictionary.ResourceTypeDictionary;
import org.folio.ld.dictionary.model.Resource;
import org.folio.ld.dictionary.model.ResourceEdge;
import org.folio.marc4ld.dto.MarcData;
import org.folio.marc4ld.service.mapper.Marc4ldMapper;
import org.marc4j.marc.DataField;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TargetAudienceMapper implements Marc4ldMapper {

  private static final String TAG = "008";
  private static final Map<Character, String> MARC_CODE_TO_LINK_SUFFIX_MAP = Map.of(
    'a', "pre",
    'b', "pri",
    'c', "pad",
    'd', "ado",
    'e', "adu",
    'f', "spe",
    'g', "gen",
    'j', "juv"
  );
  private static final Map<Character, String> MARK_CODE_TO_TERM_MAP = Map.of(
    'a', "Preschool",
    'b', "Primary",
    'c', "Pre-adolescent",
    'd', "Adolescent",
    'e', "Adult",
    'f', "Specialized",
    'g', "General",
    'j', "Juvenile"
  );
  private static final String LINK_PREFIX = "http://id.loc.gov/vocabulary/maudience/";
  private static final Set<ResourceTypeDictionary> CATEGORY_SET_TYPES = Set.of(CATEGORY_SET);
  private static final List<String> CATEGORY_SET_LINK = List.of("https://id.loc.gov/vocabulary/maudience");
  private static final List<String> CATEGORY_SET_LABEL = List.of("Target audience");

  private final ObjectMapper objectMapper;

  @Override
  public String getTag() {
    return TAG;
  }

  @Override
  public boolean canMap2ld(PredicateDictionary predicate) {
    return predicate == TARGET_AUDIENCE;
  }

  @Override
  public void map2ld(MarcData marcData, Resource resource) {
    marcData.getControlFields()
      .stream()
      .filter(controlField -> TAG.equals(controlField.getTag()))
      .findFirst()
      .ifPresent(controlField -> {
        var code = controlField.getData().charAt(22);
        var link = LINK_PREFIX + MARC_CODE_TO_LINK_SUFFIX_MAP.get(code);
        var term = MARK_CODE_TO_TERM_MAP.get(code);
        var properties = objectMapper.convertValue(resource.getDoc(),
          new TypeReference<HashMap<String, List<String>>>() {
          });
        properties.put(LINK.getValue(), List.of(link));
        properties.put(TERM.getValue(), List.of(term));
        resource.setDoc(objectMapper.convertValue(properties, JsonNode.class));
        resource.setLabel(term);
        resource.getOutgoingEdges().add(new ResourceEdge(resource, getCategorySet(), IS_DEFINED_BY));
      });
  }

  @Override
  public boolean canMap2Marc(PredicateDictionary predicate, Resource resource) {
    //corresponding marc field will be generated using the yaml based configuration
    return false;
  }

  @Override
  public List<DataField> map2marc(Resource resource) {
    return Collections.emptyList();
  }

  private Resource getCategorySet() {
    var categorySet = new Resource();
    categorySet.setTypes(CATEGORY_SET_TYPES);
    categorySet.setDoc(objectMapper.convertValue(Map.of(
      LINK.getValue(), CATEGORY_SET_LINK,
      LABEL.getValue(), CATEGORY_SET_LABEL
    ), JsonNode.class));
    categorySet.setResourceHash(hash(categorySet, objectMapper));
    return categorySet;
  }
}
