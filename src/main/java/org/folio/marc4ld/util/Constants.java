package org.folio.marc4ld.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Constants {

  public static final String GEOGRAPHIC_CODE_TO_NAME_DICTIONARY = "GEOGRAPHIC_CODE_TO_NAME";
  public static final String FIELD_UUID = "999";
  public static final char SUBFIELD_INVENTORY_ID = 'i';
  public static final char SPACE = ' ';
  public static final char ZERO = '0';
  public static final char ONE = '1';
  public static final char TWO = '2';
  public static final char FOUR = '4';
  public static final char SEVEN = '7';
  public static final char A = 'a';
  public static final char B = 'b';
  public static final char Q = 'q';
  public static final char S = 's';
  public static final char T = 't';
  public static final String TAG_008 = "008";
  public static final String TAG_043 = "043";
  public static final String TAG_245 = "245";
  public static final String TAG_776 = "776";

  @UtilityClass
  public static class DependencyInjection {

    public static final String DICTIONARY_MAP = "dictionaryMap";
  }

  @UtilityClass
  public static class Classification {

    public static final String TAG_050 = "050";
    public static final String TAG_082 = "082";
    public static final String DLC = "http://id.loc.gov/vocabulary/organizations/dlc";
    public static final String UBA = "http://id.loc.gov/vocabulary/mstatus/uba";
    public static final String NUBA = "http://id.loc.gov/vocabulary/mstatus/nuba";
    public static final String FULL = "Full";
    public static final String ABRIDGED = "Abridged";
    public static final String DDC = "ddc";
    public static final String LC = "lc";
  }
}
