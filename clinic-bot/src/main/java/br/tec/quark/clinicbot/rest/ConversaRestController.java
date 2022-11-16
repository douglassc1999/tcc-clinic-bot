package br.tec.quark.clinicbot.rest;

import br.tec.quark.clinicbot.dto.AgendarOnlineRequest;
import br.tec.quark.clinicbot.dto.ConversaDTO;
import br.tec.quark.clinicbot.enums.IntencaoEnum;
import br.tec.quark.clinicbot.service.ChatBotService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
public class ConversaRestController {

    private final ChatBotService chatBotService;

    private final Map<String, Map<String, String>> bd = new LinkedHashMap<>();

    //Detectar intenção
//    @PostMapping("/intencao")
//    public ConversaDTO detectarIntecao(@RequestParam("texto") String texto) {
//        return chatBotService.detectarIntecao(texto);
//    }

//    @PostMapping("/marcar-consulta")
    @PostMapping()
    public List<ConversaDTO> conversa(
            @RequestParam(value = "texto", required = false) String texto,
            // TODO: LEVANDO EM CONTA A VERSÃO WEB, EM QUE O DTO É MANTIDO EM MEMÓRIA NO FRONT
            @RequestBody AgendarOnlineRequest agendarOnlineRequest) {

        // TODO: REGISTRO DE SESSÃO
        Map<String, String> mapSessaoInicial = initMap(agendarOnlineRequest);

        final var sessaoKey = "sessao" + agendarOnlineRequest.getUsuarioPortalId();
        final var sessaoValue = this.bd.putIfAbsent(sessaoKey, mapSessaoInicial);

        // TODO: REGISTRO DE CONVERSA
        final var textoIntencao = chatBotService.detectarIntecao(texto);

        if (textoIntencao != null && agendarOnlineRequest.getFluxo() == null) {
            final var result = List.of(textoIntencao);

            final var ehFinalizado = result.stream()
                    .anyMatch(it -> IntencaoEnum.FINALIZAR.equals(it.getIntencao()));

            if (ehFinalizado && sessaoValue != null) {
                sessaoValue.put("status", "FINALIZADO");
                this.bd.put(sessaoKey, sessaoValue);
            }

            return result;
        }

        if (IntencaoEnum.MARCAR_CONSULTA.equals(agendarOnlineRequest.getFluxo())) {
            if (sessaoValue != null) {
                sessaoValue.put("lastUpdate", String.valueOf(new Date()));
                mapSessaoInicial.put("status", "EM_ANDAMENTO");
                sessaoValue.put("metaDados", String.valueOf(agendarOnlineRequest));
                this.bd.put(sessaoKey, sessaoValue);
            }

            return chatBotService.marcarConsulta(agendarOnlineRequest);
        }

//        if (IntencaoEnum.CANCELAR_CONSULTA.equals(agendarOnlineRequest.getFluxo())) {
//            return chatBotService.cancelarConsulta(agendarOnlineRequest);
//        }
//
//        if (IntencaoEnum.HISTORICO.equals(agendarOnlineRequest.getFluxo())) {
//            return chatBotService.historicoConsulta(agendarOnlineRequest);
//        }

        return null;
    }

    private Map<String, String> initMap(AgendarOnlineRequest agendarOnlineRequest) {
        Map<String, String > mapSessaoInicial = new LinkedHashMap<>();
        mapSessaoInicial.put("id_usuario", String.valueOf(agendarOnlineRequest.getUsuarioPortalId()));
        mapSessaoInicial.put("inicio", String.valueOf(new Date()));
        mapSessaoInicial.put("lastUpdate", String.valueOf(new Date()));
        mapSessaoInicial.put("status", "INICIADO");
        mapSessaoInicial.put("metaDados", String.valueOf(agendarOnlineRequest));
        return mapSessaoInicial;
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
