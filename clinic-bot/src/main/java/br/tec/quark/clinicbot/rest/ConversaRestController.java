package br.tec.quark.clinicbot.rest;

import br.tec.quark.clinicbot.dto.AgendarOnlineRequest;
import br.tec.quark.clinicbot.dto.ConversaDTO;
import br.tec.quark.clinicbot.service.ChatBotService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
public class ConversaRestController {

    private final ChatBotService chatBotService;

    //Detectar intenção
    @PostMapping("/intencao")
    public ConversaDTO detectarIntecao(@RequestParam("texto") String texto) {
        return chatBotService.detectarIntecao(texto);
    }

    @PostMapping("/marcar-consulta")
    public List<ConversaDTO> marcarConsulta(@RequestBody AgendarOnlineRequest agendarOnlineRequest) {
        return chatBotService.marcarConsulta(agendarOnlineRequest);
    }

//    @PostMapping()
//    public ConversaDTO cancelarConsulta(AgendarOnlineRequest agendarOnlineRequest) {
//        return chatBotService.cancelarConsulta(agendarOnlineRequest);
//    }

//    @GetMapping()
//    public ConversaDTO historicoConsulta(AgendarOnlineRequest agendarOnlineRequest) {
//        return chatBotService.historicoConsulta(agendarOnlineRequest);
//    }

}
