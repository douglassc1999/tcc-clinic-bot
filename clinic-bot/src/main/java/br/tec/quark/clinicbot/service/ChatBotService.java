package br.tec.quark.clinicbot.service;

import br.tec.quark.clinicbot.dto.AgendarOnlineRequest;
import br.tec.quark.clinicbot.dto.ConversaDTO;
import br.tec.quark.clinicbot.enums.IntencaoEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ChatBotService {

    private final static List<String> saudacaoList = List.of("ola", "oi", "bom dia", "boa tarde", "boa noite");
    private final static List<String> marcarConsultaList = List.of("marcar", "agendar", "fazer um agendamento");
    private final static List<String> cancelarConsultaList = List.of("cancelar");
    private final static List<String> historicoConsultaList = List.of("historico");

    @Value("${quark.mol.url}")
    private String urlMol;

    public final ConversaDTO detectarIntecao(String texto) {

        // TODO: MUDAR PARA UTILIZAR NPL
        if (texto == null) {
            return null;
        }
        final var textoLowerCase = texto.toLowerCase();
        var text = "";

        if(saudacaoList.contains(textoLowerCase)) {

            text = "Olá, tudo bem? Sou o assistente virtual do quarkClinic, em que posso ajudá-lo? " +
                    "Temos as seguintes opções: Marcar consulta, cancelar consulta e histórico de consultas.";
            return ConversaDTO.builder()
                    .intencao(IntencaoEnum.SAUDACAO)
                    .text(text)
                    .build();
        } else if (marcarConsultaList.contains(textoLowerCase)) {

            text = "Entao voce quer marcar uma consulta, Ok! Vamos la!";
            return ConversaDTO.builder()
                    .intencao(IntencaoEnum.MARCAR_CONSULTA)
                    .text(text)
                    .build();

        } else if (cancelarConsultaList.contains(textoLowerCase)) {

            text = "Então você quer cancelar uma consulta, ok! Vamos lá!";
            return ConversaDTO.builder()
                    .intencao(IntencaoEnum.CANCELAR_CONSULTA)
                    .text(text)
                    .build();

        } else if (historicoConsultaList.contains(textoLowerCase)) {

            text = "Então você quer ver seu histórico de consultas, ok! Vamos lá!";
            return ConversaDTO.builder()
                    .intencao(IntencaoEnum.HISTORICO)
                    .text(text)
                    .build();
        } else {

            text = "Desculpe, não entendi, pode repetir?";
            return ConversaDTO.builder()
                    .intencao(IntencaoEnum.NAO_IDENTIFICADO)
                    .text(text)
                    .build();
        }
    }

    public List<ConversaDTO> marcarConsulta(AgendarOnlineRequest agendamento) {

        // TODO: TIMEOUT DEVE FICAR NO FRONT?
        final var validate = validateAgendamento(agendamento);
        if (!validate.isEmpty()) {
            return validate;
        }

        try {

            this.marcarConsultaMol(agendamento);

            return List.of(ConversaDTO.builder()
                    .text("Consulta realizada, obrigado")
                    .intencao(IntencaoEnum.FINALIZAR)
                    .build());
        } catch (RestClientException e) {

            log.error("Erro de conexão com mol", e);

            return List.of(ConversaDTO.builder()
                    .text("Deu ruim, tente novamente mais tarde!")
                    .intencao(IntencaoEnum.FINALIZAR)
                    .build());
        }
    }


    public List<ConversaDTO> validateAgendamento(AgendarOnlineRequest agendamento) {

        final var conversas = new ArrayList<ConversaDTO>();

        if (agendamento.getClinicaId() == null) {
            conversas.add(ConversaDTO.builder()
                    .text("Em qual clínica deseja ser atendido? ")
                    .build());

            // TODO: TEM QUE BUSCAR OS DADOS DO MOL
            conversas.add(ConversaDTO.builder()
                    .text("Treinamento Quarkclinic, tabajara")
                    .build());
        } else if (agendamento.getConvenioId() == null) {
            conversas.add(ConversaDTO.builder()
                    .text("Você precisa escolher o convênio: ")
                    .build());
            conversas.add(ConversaDTO.builder()
                    .text("PARTICULAR, AMIL, UNIMED")
                    .build());
        } else if (agendamento.getProcedimentoId() == null) {

            conversas.add(ConversaDTO.builder()
                    .text("Você precisa escolher o procedimento: ")
                    .build());
            conversas.add(ConversaDTO.builder()
                    .text("PRIMEIRA CONSULTA, AJUSTE OCLUSAL, CHECK-UP")
                    .build());
        } else if (agendamento.getAgendaId() == null) {

            conversas.add(ConversaDTO.builder()
                    .text("Você precisa escolher o profissional: ")
                    .build());
            conversas.add(ConversaDTO.builder()
                    .text("Floriwaldo, Douglas Uzumaki")
                    .build());
        } else if (agendamento.getHoraAgendamento() == null) {
            conversas.add(ConversaDTO.builder()
                    .text("Você precisa escolher o agendamento: ")
                    .build());
            conversas.add(ConversaDTO.builder()
                    .text("12:00, 14:00, 16:00")
                    .build());
        }

        // TODO: CONFIRMAR COM O USUARIO QUE ESTA TUDO CERTO

        return conversas;
    }

    public void marcarConsultaMol(AgendarOnlineRequest agendamento) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders header = new HttpHeaders();
        header.set("X-Organizacao-ID", String.valueOf(agendamento.getOrganizacaoId()));
        header.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<AgendarOnlineRequest> requestEntity = new HttpEntity<>(agendamento, header);
        restTemplate.postForObject(
                urlMol + "/api/chatbot/agendamento",
                requestEntity,
                AgendarOnlineRequest.class
        );
    }
}
