package br.tec.quark.clinicbot.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class OpenNLPServiceTest {

    @InjectMocks
    private OpenNLPService openNLPService;

    @Test
    void contextLoads_marcarConsulta() throws IOException {
        final var result = this.openNLPService.contextLoads("quero marcar uma consulta");

        Assertions.assertEquals("MARCAR_CONSULTA", result);
    }

    @Test
    void contextLoads_finalizar() throws IOException {
        final var result = this.openNLPService.contextLoads("tchau, até mais, obrigado");

        Assertions.assertEquals("FINALIZAR", result);
    }
}