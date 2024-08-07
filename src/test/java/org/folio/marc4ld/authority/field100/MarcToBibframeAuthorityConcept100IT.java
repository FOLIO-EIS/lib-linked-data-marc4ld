package org.folio.marc4ld.authority.field100;

import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.ld.dictionary.PredicateDictionary.FOCUS;
import static org.folio.ld.dictionary.PredicateDictionary.MAP;
import static org.folio.ld.dictionary.PredicateDictionary.SUB_FOCUS;
import static org.folio.ld.dictionary.ResourceTypeDictionary.CONCEPT;
import static org.folio.ld.dictionary.ResourceTypeDictionary.FORM;
import static org.folio.ld.dictionary.ResourceTypeDictionary.IDENTIFIER;
import static org.folio.ld.dictionary.ResourceTypeDictionary.ID_LCCN;
import static org.folio.ld.dictionary.ResourceTypeDictionary.PLACE;
import static org.folio.ld.dictionary.ResourceTypeDictionary.TEMPORAL;
import static org.folio.ld.dictionary.ResourceTypeDictionary.TOPIC;
import static org.folio.marc4ld.mapper.test.TestUtil.loadResourceAsString;
import static org.folio.marc4ld.mapper.test.TestUtil.validateEdge;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.collections4.CollectionUtils;
import org.folio.ld.dictionary.ResourceTypeDictionary;
import org.folio.ld.dictionary.model.Resource;
import org.folio.ld.dictionary.model.ResourceEdge;
import org.folio.marc4ld.Marc2LdTestBase;
import org.folio.marc4ld.mapper.test.TestUtil;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class MarcToBibframeAuthorityConcept100IT extends Marc2LdTestBase {

  @ParameterizedTest
  @CsvSource(value = {
    "PERSON , authority/100/marc_100_concept.jsonl",
    "FAMILY , authority/100/marc_100_concept_Ind1_equals3.jsonl",
  })
  void shouldMapField100(ResourceTypeDictionary resourceType, String file) {
    // given
    var marc = loadResourceAsString(file);

    //when
    var result = marcAuthorityToResources(marc);

    //then
    assertThat(result)
      .isNotNull()
      .isNotEmpty()
      .singleElement()
      .satisfies(resource -> validateRootResource(resource, List.of(CONCEPT, resourceType)))
      .satisfies(resource -> validateFocus(resource, resourceType))
      .satisfies(this::validateForm)
      .satisfies(this::validateTopic)
      .satisfies(this::validateTemporal)
      .satisfies(this::validatePlace)
      .satisfies(this::validateIdentifier);
  }

  private void validateRootResource(Resource resource, List<ResourceTypeDictionary> types) {
    TestUtil.validateResource(resource, types,
      Map.ofEntries(
        Map.entry("http://bibfra.me/vocab/lite/name", List.of("aValue")),
        Map.entry("http://bibfra.me/vocab/marc/numeration", List.of("bValue")),
        Map.entry("http://bibfra.me/vocab/marc/titles", List.of("cValue1", "cValue2")),
        Map.entry("http://bibfra.me/vocab/lite/date", List.of("dValue")),
        Map.entry("http://bibfra.me/vocab/marc/attribution", List.of("jValue1", "jValue2")),
        Map.entry("http://bibfra.me/vocab/lite/nameAlternative", List.of("qValue")),
        Map.entry("http://bibfra.me/vocab/marc/formSubdivision", List.of("vValue1", "vValue2")),
        Map.entry("http://bibfra.me/vocab/marc/generalSubdivision", List.of("xValue1", "xValue2")),
        Map.entry("http://bibfra.me/vocab/marc/chronologicalSubdivision", List.of("yValue1", "yValue2")),
        Map.entry("http://bibfra.me/vocab/marc/geographicSubdivision", List.of("zValue1", "zValue2")),
        Map.entry("http://library.link/vocab/resourcePreferred", List.of("true")),
        Map.entry("http://bibfra.me/vocab/lite/label", List.of("bValue, aValue, cValue1, cValue2, qValue,"
          + " dValue -- vValue1 -- vValue2 -- xValue1 -- xValue2 -- yValue1 -- yValue2 -- zValue1 -- zValue2"))
      ),
      "bValue, aValue, cValue1, cValue2, qValue, dValue"
        + " -- vValue1 -- vValue2 -- xValue1 -- xValue2 -- yValue1 -- yValue2 -- zValue1 -- zValue2");
  }

  private void validateFocus(Resource resource, ResourceTypeDictionary resourceType) {
    var resourceEdges = getEdges(resource, resourceType);
    assertThat(resourceEdges)
      .hasSize(1);
    validateEdge(resourceEdges.get(0), FOCUS, List.of(resourceType),
      Map.of(
        "http://bibfra.me/vocab/lite/name", List.of("aValue"),
        "http://bibfra.me/vocab/marc/numeration", List.of("bValue"),
        "http://bibfra.me/vocab/marc/titles", List.of("cValue1", "cValue2"),
        "http://bibfra.me/vocab/lite/date", List.of("dValue"),
        "http://bibfra.me/vocab/marc/attribution", List.of("jValue1", "jValue2"),
        "http://bibfra.me/vocab/lite/nameAlternative", List.of("qValue")
      ),
      "bValue, aValue, cValue1, cValue2, qValue, dValue");
  }

  private void validateForm(Resource resource) {
    var resourceEdges = getEdges(resource, FORM);
    assertThat(resourceEdges)
      .hasSize(2)
      .satisfies(edges -> validateEdge(edges.get(0), SUB_FOCUS, List.of(FORM),
        Map.of(
          "http://bibfra.me/vocab/lite/name", List.of("vValue1"),
          "http://bibfra.me/vocab/lite/label", List.of("vValue1")
        ),
        "vValue1"))
      .satisfies(edges ->
        validateEdge(edges.get(1), SUB_FOCUS, List.of(FORM),
          Map.of(
            "http://bibfra.me/vocab/lite/name", List.of("vValue2"),
            "http://bibfra.me/vocab/lite/label", List.of("vValue2")
          ),
          "vValue2"));
  }

  private void validateTopic(Resource resource) {
    var resourceEdges = getEdges(resource, TOPIC);
    assertThat(resourceEdges)
      .hasSize(2)
      .satisfies(edges -> validateEdge(edges.get(0), SUB_FOCUS, List.of(TOPIC),
        Map.of(
          "http://bibfra.me/vocab/lite/name", List.of("xValue1"),
          "http://bibfra.me/vocab/lite/label", List.of("xValue1")
        ),
        "xValue1"))
      .satisfies(edges ->
        validateEdge(edges.get(1), SUB_FOCUS, List.of(TOPIC),
          Map.of(
            "http://bibfra.me/vocab/lite/name", List.of("xValue2"),
            "http://bibfra.me/vocab/lite/label", List.of("xValue2")
          ),
          "xValue2"));
  }

  private void validateTemporal(Resource resource) {
    var resourceEdges = getEdges(resource, TEMPORAL);
    assertThat(resourceEdges)
      .hasSize(2)
      .satisfies(edges -> validateEdge(edges.get(0), SUB_FOCUS, List.of(TEMPORAL),
        Map.of(
          "http://bibfra.me/vocab/lite/name", List.of("yValue1"),
          "http://bibfra.me/vocab/lite/label", List.of("yValue1")
        ),
        "yValue1"))
      .satisfies(edges ->
        validateEdge(edges.get(1), SUB_FOCUS, List.of(TEMPORAL),
          Map.of(
            "http://bibfra.me/vocab/lite/name", List.of("yValue2"),
            "http://bibfra.me/vocab/lite/label", List.of("yValue2")
          ),
          "yValue2"));
  }

  private void validatePlace(Resource resource) {
    var resourceEdges = getEdges(resource, PLACE);
    assertThat(resourceEdges)
      .hasSize(2)
      .satisfies(edges -> validateEdge(edges.get(0), SUB_FOCUS, List.of(PLACE),
        Map.of(
          "http://bibfra.me/vocab/lite/name", List.of("zValue1"),
          "http://bibfra.me/vocab/lite/label", List.of("zValue1")
        ),
        "zValue1"))
      .satisfies(edges ->
        validateEdge(edges.get(1), SUB_FOCUS, List.of(PLACE),
          Map.of(
            "http://bibfra.me/vocab/lite/name", List.of("zValue2"),
            "http://bibfra.me/vocab/lite/label", List.of("zValue2")
          ),
          "zValue2"));
  }

  private void validateIdentifier(Resource resource) {
    var resourceEdges = getEdges(resource, ID_LCCN, IDENTIFIER);
    assertThat(resourceEdges)
      .hasSize(1)
      .satisfies(edges ->
        validateEdge(resourceEdges.get(0), MAP, List.of(ID_LCCN, IDENTIFIER),
          Map.of(
            "http://bibfra.me/vocab/lite/name", List.of("sh85121033"),
            "http://bibfra.me/vocab/lite/link", List.of("http://id.loc.gov/authorities/sh85121033"),
            "http://bibfra.me/vocab/lite/label", List.of("sh85121033")
          ),
          "sh85121033"));
  }

  private List<ResourceEdge> getEdges(Resource resource, ResourceTypeDictionary... resourceTypes) {
    return resource.getOutgoingEdges()
      .stream()
      .filter(edge -> Optional.of(edge.getTarget())
        .map(Resource::getTypes)
        .filter(types -> CollectionUtils.containsAll(types, Arrays.asList(resourceTypes)))
        .isPresent())
      .toList();
  }
}
