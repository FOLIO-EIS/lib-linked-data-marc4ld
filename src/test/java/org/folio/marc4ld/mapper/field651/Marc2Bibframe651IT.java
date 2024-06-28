package org.folio.marc4ld.mapper.field651;

import static org.folio.marc4ld.mapper.test.TestUtil.loadResourceAsString;
import static org.folio.marc4ld.mapper.test.TestUtil.validateEdge;

import java.util.List;
import java.util.Map;
import org.folio.ld.dictionary.PredicateDictionary;
import org.folio.ld.dictionary.ResourceTypeDictionary;
import org.folio.marc4ld.Marc2LdTestBase;
import org.junit.jupiter.api.Test;

class Marc2Bibframe651IT extends Marc2LdTestBase {

  @Test
  void whenMarcField257WithMultipleSubfield_a_map_shouldConvertAll() {
    // given
    var marc = loadResourceAsString("fields/651/marc_651_info_multiply.jsonl");

    // when
    var result = marcBibToResource(marc);

    // then
    var work = result.getOutgoingEdges().iterator().next().getTarget();
    var resourceEdge = work.getOutgoingEdges().iterator().next();

    validateEdge(
      resourceEdge,
      PredicateDictionary.SUBJECT,
      List.of(ResourceTypeDictionary.CONCEPT, ResourceTypeDictionary.PLACE),
      Map.of(
        "http://bibfra.me/vocab/marc/miscInfo", List.of("some info", "some more info"),
        "http://bibfra.me/vocab/lite/name", List.of("Italy")
      ),
      "Italy"
    );
  }
}
