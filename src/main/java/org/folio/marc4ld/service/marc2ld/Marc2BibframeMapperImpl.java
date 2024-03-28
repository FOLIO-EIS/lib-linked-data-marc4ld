package org.folio.marc4ld.service.marc2ld;

import static java.lang.Character.MIN_VALUE;
import static java.util.Objects.isNull;
import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.folio.ld.dictionary.PredicateDictionary.TITLE;
import static org.folio.ld.dictionary.ResourceTypeDictionary.INSTANCE;
import static org.folio.marc4ld.util.BibframeUtil.getFirstValue;
import static org.folio.marc4ld.util.BibframeUtil.isNotEmptyResource;
import static org.folio.marc4ld.util.Constants.DependencyInjection.DATA_FIELD_PREPROCESSORS_MAP;
import static org.folio.marc4ld.util.Constants.FIELD_UUID;
import static org.folio.marc4ld.util.Constants.SUBFIELD_INVENTORY_ID;
import static org.folio.marc4ld.util.Constants.SUBFIELD_SRS_ID;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import lombok.extern.log4j.Log4j2;
import org.folio.ld.dictionary.model.Resource;
import org.folio.ld.fingerprint.service.FingerprintHashService;
import org.folio.marc4ld.configuration.property.Marc4BibframeRules;
import org.folio.marc4ld.service.marc2ld.field.FieldMapper;
import org.folio.marc4ld.service.marc2ld.preprocessor.DataFieldPreprocessor;
import org.marc4j.MarcJsonReader;
import org.marc4j.marc.DataField;
import org.marc4j.marc.MarcFactory;
import org.marc4j.marc.Subfield;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class Marc2BibframeMapperImpl implements Marc2BibframeMapper {
  private final Marc4BibframeRules rules;
  private final FieldMapper fieldMapper;
  private final Map<String, DataFieldPreprocessor> dataFieldPreprocessorsMap;
  private final MarcFactory marcFactory;
  private final FingerprintHashService hashService;

  public Marc2BibframeMapperImpl(Marc4BibframeRules rules, FingerprintHashService hashService, FieldMapper fieldMapper,
                                 @Qualifier(DATA_FIELD_PREPROCESSORS_MAP)
                                 Map<String, DataFieldPreprocessor> dataFieldPreprocessorsMap,
                                 MarcFactory marcFactory) {
    this.rules = rules;
    this.hashService = hashService;
    this.fieldMapper = fieldMapper;
    this.dataFieldPreprocessorsMap = dataFieldPreprocessorsMap;
    this.marcFactory = marcFactory;
  }

  @Override
  public Resource fromMarcJson(String marc) {
    if (isEmpty(marc)) {
      log.warn("Given marc is empty [{}]", marc);
      return null;
    }
    var reader = new MarcJsonReader(new ByteArrayInputStream(marc.getBytes(StandardCharsets.UTF_8)));
    var instance = new Resource().addType(INSTANCE);
    while (reader.hasNext()) {
      var marcRecord = reader.next();
      marcRecord.getDataFields().forEach(dataField -> {
        handleField(dataField.getTag(), instance, dataField, marcRecord);
        if (FIELD_UUID.equals(dataField.getTag())) {
          instance.setInventoryId(readUuid(dataField.getSubfield(SUBFIELD_INVENTORY_ID)));
          instance.setSrsId(readUuid(dataField.getSubfield(SUBFIELD_SRS_ID)));
        }
      });
      marcRecord.getControlFields().forEach(controlField -> handleField(controlField.getTag(), instance,
        marcFactory.newDataField(EMPTY, MIN_VALUE, MIN_VALUE), marcRecord));
    }
    instance.setLabel(selectInstanceLabel(instance));
    cleanEmptyEdges(instance);
    instance.setId(hashService.hash(instance));
    return instance;
  }

  private void handleField(String tag, Resource instance, DataField dataField, org.marc4j.marc.Record marcRecord) {
    var localDataField = new AtomicReference<>(dataField);
    ofNullable(rules.getFieldRules().get(tag)).ifPresent(frs -> {
        var preprocessedOk = ofNullable(dataFieldPreprocessorsMap.get(dataField.getTag()))
          .map(preprocessor -> {
            localDataField.set(preprocessor.preprocess(dataField));
            return preprocessor.isValid(localDataField.get());
          })
          .orElse(true);
        if (Boolean.TRUE.equals(preprocessedOk)) {
          frs.forEach(fr -> fieldMapper.handleField(instance, localDataField.get(), marcRecord.getControlFields(), fr));
        }
      }
    );
  }

  private UUID readUuid(Subfield subfield) {
    if (isNull(subfield) || isNull(subfield.getData())) {
      return null;
    }
    var value = subfield.getData().strip();
    try {
      return UUID.fromString(value);
    } catch (Exception e) {
      log.warn("Incorrect UUID value from Marc field 999, subfield [{}]: {}", subfield.getCode(), value);
      return null;
    }
  }

  private String selectInstanceLabel(Resource instance) {
    return getFirstValue(() -> instance.getOutgoingEdges().stream()
      .filter(e -> TITLE.getUri().equals(e.getPredicate().getUri()))
      .map(re -> re.getTarget().getLabel()).toList());
  }

  private void cleanEmptyEdges(Resource resource) {
    resource.setOutgoingEdges(resource.getOutgoingEdges().stream()
      .map(re -> {
        cleanEmptyEdges(re.getTarget());
        return re;
      })
      .filter(re -> isNotEmptyResource(re.getTarget()))
      .collect(Collectors.toCollection(LinkedHashSet::new))
    );
  }

}
