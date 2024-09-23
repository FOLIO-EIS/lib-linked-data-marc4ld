package org.folio.marc4ld.service.ld2marc.mapper.custom.impl;

import static org.folio.ld.dictionary.PredicateDictionary.SUPPLEMENTARY_CONTENT;
import static org.folio.ld.dictionary.PropertyDictionary.CODE;
import static org.folio.marc4ld.service.marc2ld.mapper.custom.impl.SupplementaryContentMapper.SUPPORTED_CODES;
import static org.folio.marc4ld.util.BibframeUtil.getOutgoingEdges;
import static org.folio.marc4ld.util.BibframeUtil.getPropertyValue;
import static org.folio.marc4ld.util.BibframeUtil.getWork;
import static org.folio.marc4ld.util.Constants.TAG_008;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.folio.ld.dictionary.model.Resource;
import org.folio.ld.dictionary.model.ResourceEdge;
import org.folio.marc4ld.service.ld2marc.mapper.custom.Ld2MarcCustomMapper;
import org.springframework.stereotype.Component;

@Component
public class Ld2MarcSupplementaryContentMapper implements Ld2MarcCustomMapper {

  private static final String ONE = "1";

  @Override
  public void map(Resource resource, Context context) {
    getWork(resource)
      .ifPresent(work -> {
        var supplementaryContentEdges = getOutgoingEdges(work, SUPPLEMENTARY_CONTENT);
        var nonIndexCodes = getNonIndexCodes(supplementaryContentEdges);
        if (!nonIndexCodes.isEmpty()) {
          context.controlFieldsBuilder().addFieldValue(TAG_008, nonIndexCodes, 24, 28);
        }
        if (hasIndex(supplementaryContentEdges)) {
          context.controlFieldsBuilder().addFieldValue(TAG_008, ONE, 31, 32);
        }
      });
  }

  private String getNonIndexCodes(List<ResourceEdge> supplementaryContentEdges) {
    return supplementaryContentEdges.stream()
      .map(ResourceEdge::getTarget)
      .map(r -> getPropertyValue(r, CODE.getValue()))
      .flatMap(Optional::stream)
      .distinct()
      .map(code -> code.charAt(0))
      .filter(SUPPORTED_CODES::contains)
      .map(String::valueOf)
      .collect(Collectors.joining());
  }

  private boolean hasIndex(List<ResourceEdge> edges) {
    return edges.stream()
      .map(ResourceEdge::getTarget)
      .map(r -> getPropertyValue(r, CODE.getValue()))
      .flatMap(Optional::stream)
      .anyMatch(ONE::equals);
  }
}