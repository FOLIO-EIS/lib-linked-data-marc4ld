package org.folio.marc4ld.service.ld2marc.mapper.impl.classification;

import static org.folio.ld.dictionary.PropertyDictionary.CODE;
import static org.folio.ld.dictionary.PropertyDictionary.ITEM_NUMBER;
import static org.folio.ld.dictionary.PropertyDictionary.LINK;
import static org.folio.ld.dictionary.PropertyDictionary.SOURCE;
import static org.folio.ld.dictionary.ResourceTypeDictionary.CLASSIFICATION;
import static org.folio.marc4ld.util.Constants.A;
import static org.folio.marc4ld.util.Constants.B;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.folio.ld.dictionary.PredicateDictionary;
import org.folio.ld.dictionary.ResourceTypeDictionary;
import org.folio.ld.dictionary.model.Resource;
import org.folio.ld.dictionary.model.ResourceEdge;
import org.folio.marc4ld.service.ld2marc.mapper.Ld2MarcMapper;
import org.marc4j.marc.DataField;
import org.marc4j.marc.MarcFactory;

@RequiredArgsConstructor
public abstract class AbstractClassificationMapper implements Ld2MarcMapper {

  private static final Set<ResourceTypeDictionary> SUPPORTED_TYPES = Set.of(CLASSIFICATION);

  protected final ObjectMapper objectMapper;
  protected final MarcFactory marcFactory;

  private final String tag;
  private final String source;

  protected abstract char getIndicator1(Resource resource);

  protected abstract char getIndicator2(Resource resource);

  @Override
  public boolean canMap(PredicateDictionary predicate, Resource resource) {
    return predicate == PredicateDictionary.CLASSIFICATION
      && Objects.equals(resource.getTypes(), SUPPORTED_TYPES)
      && hasCorrespondingSource(resource);
  }

  @Override
  public DataField map(Resource resource) {
    var dataField = marcFactory.newDataField(tag, getIndicator1(resource), getIndicator2(resource));
    getPropertyValues(resource, CODE.getValue())
      .forEach(code -> dataField.addSubfield(marcFactory.newSubfield(A, code)));
    getPropertyValue(resource, ITEM_NUMBER.getValue())
      .ifPresent(itemNumber -> dataField.addSubfield(marcFactory.newSubfield(B, itemNumber)));
    return dataField;
  }

  protected boolean hasLinkInEdge(Resource resource, PredicateDictionary predicate, String linkValue) {
    return resource.getOutgoingEdges()
      .stream()
      .filter(resourceEdge -> predicate.equals(resourceEdge.getPredicate()))
      .map(ResourceEdge::getTarget)
      .map(r -> getPropertyValue(r, LINK.getValue()))
      .flatMap(Optional::stream)
      .anyMatch(linkValue::equals);
  }

  protected Optional<String> getPropertyValue(Resource resource, String property) {
    return resource.getDoc().get(property) != null
      ? Optional.of(resource.getDoc().get(property).get(0).asText())
      : Optional.empty();
  }

  private List<String> getPropertyValues(Resource resource, String property) {
    return resource.getDoc().get(property) != null
      ? objectMapper.convertValue(resource.getDoc().get(property), new TypeReference<>() {})
      : List.of();
  }

  private boolean hasCorrespondingSource(Resource resource) {
    return getPropertyValue(resource, SOURCE.getValue())
      .stream()
      .anyMatch(this.source::equals);
  }
}
