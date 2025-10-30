package com.udea.innosistemas.security.directive;

import graphql.schema.DataFetcher;
import graphql.schema.FieldCoordinates;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.idl.SchemaDirectiveWiring;
import graphql.schema.idl.SchemaDirectiveWiringEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Implementación de la directiva @auth para GraphQL.
 * Valida que el usuario esté autenticado antes de ejecutar el campo.
 *
 * Autor: Fábrica-Escuela de Software UdeA
 * Versión: 1.0.0
 */
@Component
public class AuthDirective implements SchemaDirectiveWiring {

    private static final Logger logger = LoggerFactory.getLogger(AuthDirective.class);

    @Override
    public GraphQLFieldDefinition onField(SchemaDirectiveWiringEnvironment<GraphQLFieldDefinition> environment) {
        GraphQLFieldDefinition field = environment.getElement();
        FieldCoordinates coordinates = FieldCoordinates.coordinates(
                environment.getFieldsContainer().getName(),
                field.getName()
        );
        DataFetcher<?> originalDataFetcher = environment.getCodeRegistry().getDataFetcher(
                coordinates,
                field
        );

        DataFetcher<?> authDataFetcher = dataFetchingEnvironment -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated() ||
                "anonymousUser".equals(authentication.getName())) {
                logger.warn("@auth directive blocked unauthenticated access to field: {}", field.getName());
                throw new AccessDeniedException("Debe estar autenticado para acceder a este campo");
            }

            logger.debug("@auth directive validated user {} for field: {}",
                    authentication.getName(), field.getName());

            return originalDataFetcher.get(dataFetchingEnvironment);
        };

        environment.getCodeRegistry().dataFetcher(
                coordinates,
                authDataFetcher
        );

        return field;
    }
}
