package br.tec.quark.clinicbot.rest;

import br.tec.quark.clinicbot.service.OpenNLPService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/nlp")
@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
public class NlpRestController {

    private final OpenNLPService openNLPService;

    @GetMapping
    public String categorizador(@RequestParam("text") String text) throws IOException {
        return openNLPService.contextLoads(text);
    }
}
