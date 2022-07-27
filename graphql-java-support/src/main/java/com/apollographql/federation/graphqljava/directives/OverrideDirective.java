package com.apollographql.federation.graphqljava.directives;

import graphql.Scalars;
import graphql.introspection.Introspection;
import graphql.schema.GraphQLAppliedDirective;
import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLDirective;
import graphql.schema.GraphQLNonNull;

public final class OverrideDirective {

  private OverrideDirective() {
    // hidden constructor
  }

  public static final String OVERRIDE_DIRECTIVE_NAME = "override";

  private static final GraphQLArgument OVERRIDE_ARGUMENT = GraphQLArgument.newArgument()
    .name("from")
    .type(GraphQLNonNull.nonNull(Scalars.GraphQLString))
    .build();

  public static final GraphQLDirective OVERRIDE_DIRECTIVE = GraphQLDirective.newDirective()
    .name(OVERRIDE_DIRECTIVE_NAME)
    .validLocations(Introspection.DirectiveLocation.FIELD_DEFINITION)
    .argument(OVERRIDE_ARGUMENT)
    .repeatable(true)
    .build();

  public static GraphQLAppliedDirective appliedOverrideDirective(String from) {
    return OVERRIDE_DIRECTIVE.toAppliedDirective().transform(builder -> builder.argument(
      OVERRIDE_ARGUMENT.toAppliedArgument()
          .transform(appliedArgument -> appliedArgument.valueProgrammatic(from))
      )
      .build());
  }
}
