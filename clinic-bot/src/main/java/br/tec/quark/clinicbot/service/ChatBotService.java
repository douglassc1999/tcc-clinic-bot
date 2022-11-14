package br.tec.quark.clinicbot.service;

import br.tec.quark.clinicbot.dto.AgendarOnlineRequest;
import br.tec.quark.clinicbot.dto.ConversaDTO;
import br.tec.quark.clinicbot.enums.IntencaoEnum;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatBotService {

    private final static List<String> saudacaoList = List.of("ola", "oi", "bom dia", "boa tarde", "boa noite");
    private final static List<String> marcarConsultaList = List.of("marcar", "agendar", "fazer um agendamento");
    private final static List<String> cancelarConsultaList = List.of("cancelar");
    private final static List<String> historicoConsultaList = List.of("historico");

    public final ConversaDTO detectarIntecao(String texto) {

        // TODO: MUDAR PARA UTILIZAR NPL
        if (texto == null) {
            return null;
        }
        final var textoLowerCase = texto.toLowerCase();
        var text = "";

        if(saudacaoList.contains(textoLowerCase)) {
            text = "Ola, tudo bem? Em que posso ajuda-lo? Temos as seguintes opçoes: Marcar consulta, cancelar consulta e histórico de consultas.";
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
            text = "Entao voce quer cancelar uma consulta, Ok! Vamos la!";
            return ConversaDTO.builder()
                    .intencao(IntencaoEnum.CANCELAR_CONSULTA)
                    .text(text)
                    .build();

        } else if (historicoConsultaList.contains(textoLowerCase)) {
            text = "Entao voce quer ver seu histórico de consultas, Ok! Vamos la!";
            return ConversaDTO.builder()
                    .intencao(IntencaoEnum.HISTORICO)
                    .text(text)
                    .build();
        } else {
            text = "Desculpe, nao entendi, pode repetir?";
            return ConversaDTO.builder()
                    .intencao(IntencaoEnum.NAO_IDENTIFICADO)
                    .text(text)
                    .build();
        }
    }

    public ConversaDTO marcarConsulta(AgendarOnlineRequest agendamento) {

        final var validate = validateAgendamento(agendamento);
        if (validate != null) {
            return ConversaDTO.builder()
                    .text(validate)
                    .build();
        }

        return ConversaDTO.builder()
                .text("Consulta realizada, obrigado")
                .intencao(IntencaoEnum.FINALIZAR)
                .build();
    }


    public String validateAgendamento(AgendarOnlineRequest agendamento) {

        if (agendamento.getConvenioId() == null) {
            return "Voce precisa escolher o convenio";
        }

        if (agendamento.getProcedimentoId() == null) {
            return "Voce precisa escolher o procedimento";
        }

        if (agendamento.getAgendaId() == null) {
            return "Voce precisa escolher o profissional";
        }

        if (agendamento.getHoraAgendamento() == null) {
            return "Voce precisa escolher o horario do agendamento";
        }

        return null;
    }
}
