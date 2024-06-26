package org.folio.marc4ld.service.label.processor;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class UuidLabelProcessor implements LabelProcessor {

  @Override
  public String apply(Map<String, List<String>> properties) {
    return UUID.randomUUID().toString();
  }
}
