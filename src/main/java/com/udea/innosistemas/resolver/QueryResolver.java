package com.udea.innosistemas.resolver;

import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

@Controller
public class QueryResolver {

    @QueryMapping
    @PreAuthorize("hasRole('STUDENT')")
    public String hello() {
        return "Hello from InnoSistemas GraphQL API!";
    }
}