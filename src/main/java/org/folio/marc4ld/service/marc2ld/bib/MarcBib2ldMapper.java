package org.folio.marc4ld.service.marc2ld.bib;

import java.util.Optional;
import org.folio.ld.dictionary.model.Resource;
import org.folio.marc4ld.service.marc2ld.MarcJson2ldMapper;

public interface MarcBib2ldMapper extends MarcJson2ldMapper<Optional<Resource>> {

}
