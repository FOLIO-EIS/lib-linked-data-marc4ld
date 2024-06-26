package org.folio.marc4ld.service.ld2marc.field.impl;

import static org.folio.marc4ld.util.Constants.SPACE;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.folio.ld.dictionary.PropertyDictionary;
import org.folio.marc4ld.service.condition.ConditionCheckerImpl;
import org.folio.marc4ld.service.ld2marc.field.IndicatorRuleApplier;

public class IndicatorRuleApplierImpl implements IndicatorRuleApplier {

  private final String property;
  private final char defaultIndicator;

  public IndicatorRuleApplierImpl(String indProperty, String indCondition) {
    this.property = Optional.ofNullable(indProperty)
      .map(PropertyDictionary::valueOf)
      .map(PropertyDictionary::getValue)
      .orElse(null);
    this.defaultIndicator = Optional.ofNullable(indCondition)
      .filter(StringUtils::isNotBlank)
      .filter(ic -> !ic.startsWith(ConditionCheckerImpl.NOT))
      .filter(ic -> !ic.equals(ConditionCheckerImpl.PRESENTED))
      .map(c -> c.charAt(0))
      .orElse(SPACE);
  }

  @Override
  public char map(JsonNode node) {
    return Optional.ofNullable(property)
      .map(prop -> node.get(property))
      .filter(n -> !n.isEmpty())
      .map(n -> n.get(0))
      .map(JsonNode::asText)
      .map(s -> s.charAt(0))
      .orElse(defaultIndicator);
  }
}
