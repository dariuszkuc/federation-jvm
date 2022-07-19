package com.apollographql.federation.graphqljava;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.apollographql.federation.graphqljava.data.Product;
import graphql.ExecutionResult;
import graphql.Scalars;
import graphql.com.google.common.collect.ImmutableMap;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLNamedType;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLSchema;
import graphql.schema.GraphQLUnionType;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.TypeRuntimeWiring;
import graphql.schema.idl.errors.SchemaProblem;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

class FederationTest {
  private final String emptySDL = FileUtils.readResource("schemas/empty.graphql");
  private final String emptyFederatedSDL = FileUtils.readResource("schemas/emptyFederated.graphql");
  private final String emptySchemaFederatedSDL =
      FileUtils.readResource("schemas/emptySchemaFederated.graphql");
  private final String emptyWithExtendQuerySDL =
      FileUtils.readResource("schemas/emptyWithExtendQuery.graphql");
  private final String emptyWithExtendQueryFederatedSDL =
      FileUtils.readResource("schemas/emptyWithExtendQueryFederated.graphql");
  private final String emptyWithExtendQueryServiceSDL =
      FileUtils.readResource("schemas/emptyWithExtendQueryService.graphql");
  private final String interfacesSDL = FileUtils.readResource("schemas/interfaces.graphql");
  private final String isolatedSDL = FileUtils.readResource("schemas/isolated.graphql");
  private final String productSDL = FileUtils.readResource("schemas/product.graphql");
  private final String fed2SDL = FileUtils.readResource("schemas/fed2.graphql");
  private final String fed2FederatedSDL = FileUtils.readResource("schemas/fed2Federated.graphql");
  private final String fed2ServiceSDL = FileUtils.readResource("schemas/fed2Service.graphql");
  private final String unionsSDL = FileUtils.readResource("schemas/unions.graphql");
  private final String unionsFederatedSDL =
      FileUtils.readResource("schemas/unionsFederated.graphql");

  @Test
  void testEmptySDL() {
    final GraphQLSchema federatedSchema = Federation.transform(emptySDL).build();
    SchemaUtils.assertSDL(federatedSchema, emptyFederatedSDL, emptySDL);
  }

  @Test
  void testEmptyWithExtendQuerySDL() {
    final GraphQLSchema federatedSchema = Federation.transform(emptyWithExtendQuerySDL).build();
    SchemaUtils.assertSDL(
        federatedSchema, emptyWithExtendQueryFederatedSDL, emptyWithExtendQueryServiceSDL);
  }

  @Test
  void testEmptySchema() {
    final GraphQLSchema federatedSchema =
        Federation.transform(
                GraphQLSchema.newSchema()
                    .query(
                        GraphQLObjectType.newObject()
                            .name("Query")
                            .field(
                                GraphQLFieldDefinition.newFieldDefinition()
                                    .name("dummy")
                                    .type(Scalars.GraphQLString)
                                    .build())
                            .build())
                    .build(),
                true)
            .build();
    SchemaUtils.assertSDL(federatedSchema, emptySchemaFederatedSDL, emptySDL);
  }

  @Test
  void testRequirements() {
    assertThrows(SchemaProblem.class, () -> Federation.transform(productSDL).build());
    assertThrows(
        SchemaProblem.class,
        () -> Federation.transform(productSDL).resolveEntityType(env -> null).build());
    assertThrows(
        SchemaProblem.class,
        () -> Federation.transform(productSDL).fetchEntities(env -> null).build());
  }

  @Test
  void testSimpleService() {
    final GraphQLSchema federated =
        Federation.transform(productSDL)
            .fetchEntities(
                env ->
                    env.<List<Map<String, Object>>>getArgument(_Entity.argumentName).stream()
                        .map(
                            map -> {
                              if ("Product".equals(map.get("__typename"))) {
                                return Product.PLANCK;
                              }
                              return null;
                            })
                        .collect(Collectors.toList()))
            .resolveEntityType(env -> env.getSchema().getObjectType("Product"))
            .build();

    SchemaUtils.assertSDL(federated, null, productSDL);

    final ExecutionResult result =
        SchemaUtils.execute(
            federated,
            "{\n"
                + "  _entities(representations: [{__typename:\"Product\"}]) {\n"
                + "    ... on Product { price }\n"
                + "  }"
                + "}");
    assertEquals(0, result.getErrors().size(), "No errors");

    final Map<String, Object> data = result.getData();
    @SuppressWarnings("unchecked")
    final List<Map<String, Object>> _entities = (List<Map<String, Object>>) data.get("_entities");

    assertEquals(1, _entities.size());
    assertEquals(180, _entities.get(0).get("price"));
  }

  // From https://github.com/apollographql/federation-jvm/issues/7
  @Test
  void testSchemaTransformationIsolated() {
    Federation.transform(isolatedSDL)
        .resolveEntityType(env -> null)
        .fetchEntities(environment -> null)
        .build();
    Federation.transform(isolatedSDL)
        .resolveEntityType(env -> null)
        .fetchEntities(environment -> null)
        .build();
  }

  @Test
  void testInterfacesAreCovered() {
    final RuntimeWiring wiring =
        RuntimeWiring.newRuntimeWiring()
            .type(TypeRuntimeWiring.newTypeWiring("Product").typeResolver(env -> null).build())
            .build();

    final GraphQLSchema transformed =
        Federation.transform(interfacesSDL, wiring)
            .resolveEntityType(env -> null)
            .fetchEntities(environment -> null)
            .build();

    final GraphQLUnionType entityType = (GraphQLUnionType) transformed.getType(_Entity.typeName);

    final Iterable<String> unionTypes =
        entityType.getTypes().stream()
            .map(GraphQLNamedType::getName)
            .sorted()
            .collect(Collectors.toList());

    assertIterableEquals(Arrays.asList("Book", "Movie", "Page"), unionTypes);
  }

  @Test
  void testFed2() {
    final GraphQLSchema federatedSchema =
        Federation.transform(fed2SDL)
            .resolveEntityType(env -> env.getSchema().getObjectType("Point"))
            .fetchEntities(
                entityFetcher ->
                    ImmutableMap.builder().put("id", "1000").put("x", 0).put("y", 0).build())
            .build();
    SchemaUtils.assertSDL(federatedSchema, fed2FederatedSDL, fed2ServiceSDL);
  }

  @Test
  void testUnions() {
    final RuntimeWiring runtimeWiring = RuntimeWiring.newRuntimeWiring().build();
    runtimeWiring.getTypeResolvers().put("ProductResult", env -> null);
    final GraphQLSchema federatedSchema = Federation.transform(unionsSDL, runtimeWiring).build();
    SchemaUtils.assertSDL(federatedSchema, unionsFederatedSDL, unionsSDL);
  }
}
