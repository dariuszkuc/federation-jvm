package com.apollographql.federation.graphqljava.directives;

import graphql.Scalars;
import graphql.introspection.Introspection;
import graphql.schema.GraphQLAppliedDirective;
import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLDirective;
import graphql.schema.GraphQLNonNull;

public final class TagDirective {

  private TagDirective() {
    // hidden constructor
  }

  public static final String TAG_DIRECTIVE_NAME = "tag";
  private static final GraphQLArgument TAG_DIRECTIVE_ARGUMENT = GraphQLArgument.newArgument()
    .name("name")
    .type(GraphQLNonNull.nonNull(Scalars.GraphQLString))
    .build();

  public static final GraphQLDirective TAG_DIRECTIVE = GraphQLDirective.newDirective()
    .name(TAG_DIRECTIVE_NAME)
    .validLocations(
      Introspection.DirectiveLocation.FIELD_DEFINITION,
      Introspection.DirectiveLocation.OBJECT,
      Introspection.DirectiveLocation.INTERFACE,
      Introspection.DirectiveLocation.UNION,
      Introspection.DirectiveLocation.ARGUMENT_DEFINITION,
      Introspection.DirectiveLocation.SCALAR,
      Introspection.DirectiveLocation.ENUM,
      Introspection.DirectiveLocation.ENUM_VALUE,
      Introspection.DirectiveLocation.INPUT_OBJECT,
      Introspection.DirectiveLocation.INPUT_FIELD_DEFINITION
    )
    .argument(TAG_DIRECTIVE_ARGUMENT)
    .repeatable(true)
    .build();

  public static GraphQLAppliedDirective appliedTagDirective(String name) {
    return TAG_DIRECTIVE.toAppliedDirective().transform(builder -> builder.argument(
      TAG_DIRECTIVE_ARGUMENT.toAppliedArgument().
        transform(appliedArgument -> appliedArgument.valueProgrammatic(name))
      )
      .build());
  }
}
