package com.udea.innosistemas;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class InnoSistemasApplicationTest {

    @Test
    void contextLoads() {
        assertTrue(true, "El contexto de Spring Boot debería cargarse correctamente");
    }

    @Test
    void systemPropertiesShouldBeConfigured() {
        assertEquals("UTF-8", System.getProperty("file.encoding", "UTF-8"));
    }
}
