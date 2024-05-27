package org.folio.marc4ld.configuration.property;

import static java.util.Optional.ofNullable;

import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Data
@Configuration
@ConfigurationProperties
@PropertySource(value = "classpath:marc4bibframe.yml", factory = YamlPropertySourceFactory.class)
public class Marc4BibframeRules {

  private Map<String, List<FieldRule>> fieldRules;
  private Map<String, FieldRule> sharedRules;

  @Data
  public static class FieldRule {
    private Set<String> types;
    private String parent;
    private String predicate;
    private Marc2ldCondition marc2ldCondition;
    private Ld2marcCondition ld2marcCondition;
    private FieldRelation relation;
    private Map<Character, String> subfields;
    private String ind1;
    private String ind2;
    private String concat;
    private boolean append;
    private boolean multiply;
    private Map<String, String> constants;
    private Map<String, Map<String, List<Integer>>> controlFields;
    private List<FieldRule> edges;
    private Map<String, Character> mappings;
    private String include;

    public void addTypes(Set<String> types) {
      ofNullable(this.types).ifPresentOrElse(t -> t.addAll(types), () -> this.setTypes(types));
    }

    public void putSubfields(Map<Character, String> subfields) {
      ofNullable(this.subfields).ifPresentOrElse(s -> s.putAll(subfields), () -> this.setSubfields(subfields));
    }

    public void putConstants(Map<String, String> constants) {
      ofNullable(this.constants).ifPresentOrElse(c -> c.putAll(constants), () -> this.setConstants(constants));
    }

    public void putControlFields(Map<String, Map<String, List<Integer>>> controlFields) {
      ofNullable(this.controlFields)
        .ifPresentOrElse(c -> c.putAll(controlFields), () -> this.setControlFields(controlFields));
    }

    public void addEdges(List<FieldRule> edges) {
      ofNullable(this.edges).ifPresentOrElse(e -> e.addAll(edges), () -> this.setEdges(edges));
    }

    public void putMappings(Map<String, Character> mappings) {
      ofNullable(this.mappings).ifPresentOrElse(m -> m.putAll(mappings), () -> this.setMappings(mappings));
    }
  }

  @Data
  public static class Marc2ldCondition {
    private Map<Character, String> fields;
    private String ind1;
    private String ind2;
    private List<ControlFieldContext> controlFields;
  }

  @Data
  public static class Ld2marcCondition {
    private String edge;
    private boolean skip;
  }

  @Data
  public static class FieldRelation {
    private char code;
    private char text;
  }

  @Data
  public static class ControlFieldContext {
    private String tag;
    private String data;
    private String expression;
    private List<String> args;
  }
}
