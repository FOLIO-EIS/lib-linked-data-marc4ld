package org.folio.marc4ld.mapper.field650;

import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.marc4ld.mapper.test.TestUtil.loadResourceAsString;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
import java.util.stream.StreamSupport;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.folio.ld.dictionary.ResourceTypeDictionary;
import org.folio.ld.dictionary.model.Resource;
import org.folio.ld.dictionary.model.ResourceEdge;
import org.folio.marc4ld.Marc2LdTestBase;
import org.junit.jupiter.api.Test;

class Marc2Bibframe650IT extends Marc2LdTestBase {

  @Test
  void map_shouldContains_severalForms() {
    // given
    var marc = loadResourceAsString("fields/650/marc_650_several_forms.jsonl");

    // when
    var resource = marcBibToResource(marc);

    var subject = geSubject(resource);
    assertThat(subject)
      .hasFieldOrPropertyWithValue("label", "Private libraries")
      .extracting(Resource::getDoc)
      .extracting(node -> node.get("http://bibfra.me/vocab/marc/formSubdivision"))
      .extracting(this::getValues)
      .asInstanceOf(InstanceOfAssertFactories.LIST)
      .containsOnly("form 1", "form 2");
  }

  @Test
  void map_shouldReturn_severalFormNodes() {
    // given
    var marc = loadResourceAsString("fields/650/marc_650_several_forms.jsonl");

    // when
    var resource = marcBibToResource(marc);

    var subject = geSubject(resource);

    var formNodes = getNodes(subject, ResourceTypeDictionary.FORM);

    assertThat(formNodes)
      .hasSize(2)
      .extracting(Resource::getLabel)
      .containsOnly("form 1", "form 2")
    ;
  }

  @Test
  void map_shouldContains_severalRegions() {
    // given
    var marc = loadResourceAsString("fields/650/marc_650_several_places.jsonl");

    // when
    var resource = marcBibToResource(marc);
    var subject = geSubject(resource);
    assertThat(subject)
      .hasFieldOrPropertyWithValue("label", "Private libraries")
      .extracting(Resource::getDoc)
      .extracting(node -> node.get("http://bibfra.me/vocab/marc/geographicSubdivision"))
      .extracting(this::getValues)
      .asInstanceOf(InstanceOfAssertFactories.LIST)
      .containsOnly("Italy", "Florence");
  }

  @Test
  void map_shouldReturn_severalGeographicalNodes() {
    // given
    var marc = loadResourceAsString("fields/650/marc_650_several_places.jsonl");

    // when
    var resource = marcBibToResource(marc);
    var subject = geSubject(resource);

    var geographicNodes = getNodes(subject, ResourceTypeDictionary.PLACE);

    assertThat(geographicNodes)
      .hasSize(2)
      .extracting(Resource::getLabel)
      .containsOnly("Italy", "Florence")
    ;
  }

  @Test
  void map_shouldContains_severalTopics() {
    // given
    var marc = loadResourceAsString("fields/650/marc_650_several_topics.jsonl");

    // when
    var resource = marcBibToResource(marc);
    var subject = geSubject(resource);
    assertThat(subject)
      .hasFieldOrPropertyWithValue("label", "Private libraries")
      .extracting(Resource::getDoc)
      .extracting(node -> node.get("http://bibfra.me/vocab/marc/generalSubdivision"))
      .extracting(this::getValues)
      .asInstanceOf(InstanceOfAssertFactories.LIST)
      .containsOnly("topic 1", "topic 2");
  }

  @Test
  void map_shouldReturn_severalTopicNodes() {
    // given
    var marc = loadResourceAsString("fields/650/marc_650_several_topics.jsonl");

    // when
    var resource = marcBibToResource(marc);
    var subject = geSubject(resource);

    var topicNodes = getNodes(subject, ResourceTypeDictionary.TOPIC);

    assertThat(topicNodes)
      .hasSize(3)
      .extracting(Resource::getLabel)
      .containsOnly("Private libraries", "topic 1", "topic 2")
    ;
  }

  private List<String> getValues(JsonNode node) {
    return StreamSupport.stream(node.spliterator(), false)
      .map(JsonNode::asText)
      .toList();
  }

  private Resource geSubject(Resource resource) {
    return resource.getOutgoingEdges()
      .stream()
      .findFirst()
      .orElseThrow()
      .getTarget()
      .getOutgoingEdges()
      .stream()
      .findFirst()
      .orElseThrow()
      .getTarget();
  }

  private static List<Resource> getNodes(Resource subject, ResourceTypeDictionary type) {
    return subject.getOutgoingEdges()
      .stream()
      .map(ResourceEdge::getTarget)
      .filter(resourceEdge -> resourceEdge.getTypes().contains(type))
      .toList();
  }
}
