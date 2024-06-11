package org.folio.marc4ld.service.marc2ld.preprocessor.impl;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.folio.marc4ld.util.Constants.GEOGRAPHIC_CODE_TO_NAME_DICTIONARY;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.folio.marc4ld.service.dictionary.DictionaryProcessor;
import org.folio.marc4ld.service.marc2ld.preprocessor.DataFieldPreprocessor;
import org.marc4j.marc.DataField;
import org.marc4j.marc.MarcFactory;
import org.marc4j.marc.Subfield;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataField043Preprocessor implements DataFieldPreprocessor {

  private static final char CODE_A = 'a';

  private final DictionaryProcessor dictionaryProcessor;
  private final MarcFactory marcFactory;

  @Override
  public Optional<DataField> preprocess(DataField dataField) {
    var result = marcFactory.newDataField(dataField.getTag(), dataField.getIndicator1(), dataField.getIndicator2());
    dataField.getSubfields()
      .forEach(sf -> {
        if (sf.getCode() == CODE_A) {
          result.addSubfield(marcFactory.newSubfield(CODE_A, sf.getData().replaceAll("-+$", EMPTY)));
        } else {
          result.addSubfield(sf);
        }
      });
    return Optional.of(result)
      .filter(this::isValid);
  }

  @Override
  public String getTag() {
    return "043";
  }

  public boolean isValid(DataField dataField) {
    return Optional.ofNullable(dataField.getSubfield(CODE_A))
      .map(Subfield::getData)
      .flatMap(data -> dictionaryProcessor.getValue(GEOGRAPHIC_CODE_TO_NAME_DICTIONARY, data))
      .isPresent();
  }
}
