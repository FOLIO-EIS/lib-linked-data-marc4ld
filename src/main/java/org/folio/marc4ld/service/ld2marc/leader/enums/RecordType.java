package org.folio.marc4ld.service.ld2marc.leader.enums;

public enum RecordType {
  LANGUAGE_MATERIAL('a'),
  NOTATED_MUSIC('c'),
  MANUSCRIPT_NOTATED_MUSIC('d'),
  CARTOGRAPHIC_MATERIAL('e'),
  MANUSCRIPT_CARTOGRAPHIC_MATERIAL('f'),
  PROJECTED_MEDIUM('g'),
  NONMUSICAL_SOUND_RECORDING('i'),
  MUSICAL_SOUND_RECORDING('j'),
  TWO_DIMENSIONAL_NONPROJECTABLE_GRAPHIC('k'),
  COMPUTER_FILE('m'),
  KIT('o'),
  MIXED_MATERIALS('p'),
  THREE_DIMENSIONAL_ARTIFACT_OR_NATURALLY_OCCURRING_OBJECT('r'),
  MANUSCRIPT_LANGUAGE_MATERIAL('t');

  public final char value;

  RecordType(char value) {
    this.value = value;
  }

}
