package com.apollographql.federation.graphqljava.directives;

import graphql.Scalars;
import graphql.introspection.Introspection;
import graphql.schema.GraphQLAppliedDirective;
import graphql.schema.GraphQLAppliedDirectiveArgument;
import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLDirective;
import graphql.schema.GraphQLList;
import java.util.List;

public final class LinkDirective {

  private LinkDirective() {
    // hidden constructor
  }

  public static final String LINK_SPEC_URL = "https://specs.apollo.dev/link/v1.0/";
  public static final String FEDERATION_SPEC_URL = "https://specs.apollo.dev/federation/v2.0";

  public static final String LINK_DIRECTIVE_NAME = "link";
  private static final GraphQLArgument LINK_URL_ARGUMENT = GraphQLArgument.newArgument()
    .name("url")
    .type(Scalars.GraphQLString)
    .build();

  private static final GraphQLArgument LINK_IMPORT_ARGUMENT = GraphQLArgument
    .newArgument()
    .name("import")
    .type(GraphQLList.list(Scalars.GraphQLString))
    .build();

  public static final GraphQLDirective LINK_DIRECTIVE = GraphQLDirective.newDirective()
    .name(LINK_DIRECTIVE_NAME)
    .validLocations(Introspection.DirectiveLocation.SCHEMA)
    .argument(LINK_URL_ARGUMENT)
    .argument(LINK_IMPORT_ARGUMENT)
    .repeatable(true)
    .build();

  public static GraphQLAppliedDirective appliedLinkDirective(String url, List<String> imports) {
    return LINK_DIRECTIVE.toAppliedDirective().transform(appliedDirectiveBuilder -> {
      GraphQLAppliedDirectiveArgument urlArgument = LINK_DIRECTIVE.getArgument("url")
        .toAppliedArgument()
        .transform(argumentBuilder -> argumentBuilder.valueProgrammatic(url));
      appliedDirectiveBuilder.argument(urlArgument);

      if (imports != null && imports.size() > 0) {
        GraphQLAppliedDirectiveArgument importArgument = LINK_DIRECTIVE.getArgument("import")
          .toAppliedArgument()
          .transform(argumentBuilder -> argumentBuilder.valueProgrammatic(imports));
        appliedDirectiveBuilder.argument(importArgument);
      }
    });
  }
}
