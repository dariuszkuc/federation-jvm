package com.apollographql.federation.graphqljava.directives;

import graphql.introspection.Introspection;
import graphql.schema.GraphQLDirective;

public final class InaccessibleDirective {

  private InaccessibleDirective() {
    // hidden constructor
  }

  public static final String INACCESSIBLE_DIRECTIVE_NAME = "inaccessible";

  public static final GraphQLDirective INACCESSIBLE_DIRECTIVE = GraphQLDirective.newDirective()
    .name(INACCESSIBLE_DIRECTIVE_NAME)
    .validLocations(
      Introspection.DirectiveLocation.FIELD_DEFINITION,
      Introspection.DirectiveLocation.OBJECT,
      Introspection.DirectiveLocation.INTERFACE,
      Introspection.DirectiveLocation.UNION,
      Introspection.DirectiveLocation.ENUM,
      Introspection.DirectiveLocation.ENUM_VALUE,
      Introspection.DirectiveLocation.SCALAR,
      Introspection.DirectiveLocation.INPUT_OBJECT,
      Introspection.DirectiveLocation.INPUT_FIELD_DEFINITION,
      Introspection.DirectiveLocation.ARGUMENT_DEFINITION
    )
    .build();
}
