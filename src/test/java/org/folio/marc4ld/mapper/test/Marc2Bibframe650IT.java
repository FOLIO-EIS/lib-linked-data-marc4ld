package org.folio.marc4ld.mapper.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.marc4ld.mapper.test.TestUtil.loadResourceAsString;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
import java.util.stream.StreamSupport;
import org.folio.ld.dictionary.ResourceTypeDictionary;
import org.folio.ld.dictionary.model.Resource;
import org.folio.ld.dictionary.model.ResourceEdge;
import org.folio.marc4ld.service.marc2ld.Marc2BibframeMapperImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;

@EnableConfigurationProperties
@SpringBootTest(classes = SpringTestConfig.class)
class Marc2Bibframe650IT {

  @Autowired
  private Marc2BibframeMapperImpl marc2BibframeMapper;

  @Test
  void map_shouldContains_severalRegion() {
    // given
    var marc = loadResourceAsString("fields/marc_650.jsonl");

    // when
    var resource = marc2BibframeMapper.fromMarcJson(marc);

    assertThat(resource)
      .isNotNull();

    var subject = geSubject(resource);
    assertThat(subject)
      .hasFieldOrPropertyWithValue("label", "Private libraries")
      .extracting(Resource::getDoc)
      .extracting(node -> node.get("http://bibfra.me/vocab/marc/geographicSubdivision"))
      .extracting(this::getValues)
      .asList()
      .containsOnly("Italy", "Florence");
  }

  @Test
  void map_shouldReturn_severalGeographicalNodes() {
    // given
    var marc = loadResourceAsString("fields/marc_650.jsonl");

    // when
    var resource = marc2BibframeMapper.fromMarcJson(marc);

    assertThat(resource)
      .isNotNull();

    var subject = geSubject(resource);

    var geographicNodes = getGeographicNodes(subject);

    assertThat(geographicNodes)
      .hasSize(2)
      .extracting(Resource::getLabel)
      .containsOnly("Italy", "Florence")
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

  private static List<Resource> getGeographicNodes(Resource subject) {
    return subject.getOutgoingEdges()
      .stream()
      .map(ResourceEdge::getTarget)
      .filter(resourceEdge -> resourceEdge.getTypes().contains(ResourceTypeDictionary.PLACE))
      .toList();
  }
}
