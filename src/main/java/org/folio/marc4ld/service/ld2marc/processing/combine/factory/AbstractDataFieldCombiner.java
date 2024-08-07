package org.folio.marc4ld.service.ld2marc.processing.combine.factory;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.ObjectUtils.notEqual;
import static org.folio.marc4ld.util.Constants.SPACE;
import static org.folio.marc4ld.util.MarcUtil.orderSubfields;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.folio.marc4ld.service.ld2marc.processing.combine.DataFieldCombiner;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Subfield;

@RequiredArgsConstructor
abstract class AbstractDataFieldCombiner implements DataFieldCombiner {

  private final Comparator<Subfield> comparator;

  private DataField combinedField;

  protected abstract Collection<Character> getNonRepeatableFields();

  protected abstract Collection<Character> getRepeatableFields();

  @Override
  public Collection<DataField> build() {
    if (isNull(combinedField)) {
      return Collections.emptyList();
    }
    orderSubfields(combinedField, comparator);
    return List.of(combinedField);
  }

  @Override
  public void add(DataField dataField) {
    if (isNull(combinedField)) {
      this.combinedField = dataField;
      return;
    }
    putNonRepeatableFields(dataField);
    putRepeatableFields(dataField);
    setIndicators(dataField);
  }

  private void putNonRepeatableFields(DataField dataField) {
    getNonRepeatableFields()
      .stream()
      .filter(this::notContainsInCombined)
      .forEach(fieldTag -> putFieldIfPresent(fieldTag, dataField));
  }

  private boolean notContainsInCombined(char fieldTag) {
    return Optional.ofNullable(combinedField.getSubfield(fieldTag))
      .map(Subfield::getData)
      .filter(StringUtils::isNotBlank)
      .isEmpty();
  }

  private void putRepeatableFields(DataField dataField) {
    getRepeatableFields()
      .forEach(fieldTag -> putFieldIfPresent(fieldTag, dataField));
  }

  private void putFieldIfPresent(char fieldTag, DataField dataField) {
    dataField.getSubfields(fieldTag)
      .forEach(combinedField::addSubfield);
  }

  private void setIndicators(DataField dataField) {
    if (Objects.equals(combinedField.getIndicator1(), SPACE) && notEqual(dataField.getIndicator1(), SPACE)) {
      combinedField.setIndicator1(dataField.getIndicator1());
    }
    if (Objects.equals(combinedField.getIndicator2(), SPACE) && notEqual(dataField.getIndicator2(), SPACE)) {
      combinedField.setIndicator2(dataField.getIndicator2());
    }
  }
}
