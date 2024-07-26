package org.folio.marc4ld.service.marc2ld;

public interface MarcJson2ldMapper<R> {

  R fromMarcJson(String marc);
}
