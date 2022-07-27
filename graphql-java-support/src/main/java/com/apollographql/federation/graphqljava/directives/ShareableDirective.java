package com.apollographql.federation.graphqljava.directives;

import graphql.introspection.Introspection;
import graphql.schema.GraphQLDirective;

public final class ShareableDirective {
  private ShareableDirective() {
    // hidden constructor
  }

  public static final String SHAREABLE_DIRECTIVE_NAME = "shareable";

  public static final GraphQLDirective SHAREABLE_DIRECTIVE = GraphQLDirective.newDirective()
    .name(SHAREABLE_DIRECTIVE_NAME)
    .validLocations(Introspection.DirectiveLocation.FIELD_DEFINITION, Introspection.DirectiveLocation.OBJECT)
    .build();
}
