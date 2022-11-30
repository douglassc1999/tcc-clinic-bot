package br.tec.quark.clinicbot.service;

import br.tec.quark.clinicbot.dto.AgendarOnlineRequest;
import br.tec.quark.clinicbot.dto.ConversaDTO;
import br.tec.quark.clinicbot.enums.IntencaoEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatBotService {

    private final static List<String> saudacaoList = List.of("ola", "oi", "bom dia", "boa tarde", "boa noite");
    private final static List<String> marcarConsultaList = List.of("marcar", "agendar", "fazer um agendamento");
    private final static List<String> cancelarConsultaList = List.of("cancelar");
    private final static List<String> historicoConsultaList = List.of("historico");

    private final OpenNLPService openNLPService;
    private final MolApiService molApiService;

    public final ConversaDTO detectarIntecao(String texto) {

        // TODO: MUDAR PARA UTILIZAR NPL
        if (texto == null) {
            return null;
        }
        final var textoLowerCase = texto.toLowerCase();
        var text = "";

        String categoria;
        try {
            categoria = openNLPService.contextLoads(textoLowerCase);
        } catch (IOException e) {
            log.error("Erro ao tentar categorizar mensagem de usuário: ", e);
            categoria = "NÃO DETECTADO";
        }

        log.info("Categoria: " + categoria);

//        if(saudacaoList.contains(textoLowerCase)) {
        if("SAUDACAO".equals(categoria)) {

            text = "Olá, tudo bem? Sou o assistente virtual do quarkClinic, em que posso ajudá-lo? " +
                    "Temos as seguintes opções: Marcar consulta, cancelar consulta e histórico de consultas.";
            return ConversaDTO.builder()
                    .intencao(IntencaoEnum.SAUDACAO)
                    .text(text)
                    .build();
//        } else if (marcarConsultaList.contains(textoLowerCase)) {
        } else if ("MARCAR_CONSULTA".equals(categoria)) {

            text = "Entao voce quer marcar uma consulta, Ok! Vamos la!";
            return ConversaDTO.builder()
                    .intencao(IntencaoEnum.MARCAR_CONSULTA)
                    .text(text)
                    .build();

//        } else if (cancelarConsultaList.contains(textoLowerCase)) {
        } else if ("CANCELAR_CONSULTA".equals(categoria)) {

            text = "Então você quer cancelar uma consulta, ok! Vamos lá!";
            return ConversaDTO.builder()
                    .intencao(IntencaoEnum.CANCELAR_CONSULTA)
                    .text(text)
                    .build();

        } else if ("HISTORICO".equals(categoria)) {

            text = "Então você quer ver seu histórico de consultas, ok! Vamos lá!";
            return ConversaDTO.builder()
                    .intencao(IntencaoEnum.HISTORICO)
                    .text(text)
                    .build();

        } else if ("FINALIZAR".equals(categoria)) {

            text = "Até a pŕoxima!";
            return ConversaDTO.builder()
                    .intencao(IntencaoEnum.FINALIZAR)
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

            this.molApiService.marcarConsultaMol(agendamento);

            return List.of(ConversaDTO.builder()
                    .text("Consulta realizada, obrigado")
                    .intencao(IntencaoEnum.FINALIZAR)
                    .build());
        } catch (RestClientException e) {

            log.error("Erro de conexão com mol", e);

            return List.of(ConversaDTO.builder()
                    .text("Deu ruim, tente novamente mais tarde!" + e.getMessage())
                    .intencao(IntencaoEnum.FINALIZAR)
                    .build());
        }
    }


    public List<ConversaDTO> validateAgendamento(AgendarOnlineRequest agendamento) {

        final var conversas = new ArrayList<ConversaDTO>();

        if (agendamento.getConvenioId() == null) {
            final var convenios = this.molApiService.buscarConvenios(agendamento);
            conversas.add(ConversaDTO.builder()
                    .text("Você precisa escolher o convênio: ")
                    .intencao(IntencaoEnum.MARCAR_CONSULTA)
                    .build());
            conversas.add(ConversaDTO.builder()
                    .responseList(convenios)
                    .intencao(IntencaoEnum.MARCAR_CONSULTA)
                    .build());
        } else if (agendamento.getProcedimentoId() == null) {
            final var procedimentos = this.molApiService.buscarProcedimentos(agendamento);

            conversas.add(ConversaDTO.builder()
                    .text("Você precisa escolher o procedimento: ")
                    .intencao(IntencaoEnum.MARCAR_CONSULTA)
                    .build());
            conversas.add(ConversaDTO.builder()
                    .responseList(procedimentos)
                    .intencao(IntencaoEnum.MARCAR_CONSULTA)
                    .build());

        } else if (agendamento.getClinicaId() == null) {
            final var clinicas = this.molApiService.buscarClinicas(agendamento);
            conversas.add(ConversaDTO.builder()
                    .text("Em qual clínica deseja ser atendido? ")
                    .intencao(IntencaoEnum.MARCAR_CONSULTA)
                    .build());

            // TODO: TEM QUE BUSCAR OS DADOS DO MOL
            conversas.add(ConversaDTO.builder()
                    .responseList(clinicas)
                    .intencao(IntencaoEnum.MARCAR_CONSULTA)
                    .build());
        } else if (agendamento.getProfissionalId() == null) {

            final var profissionais = this.molApiService.buscarProfissionais(agendamento);

            conversas.add(ConversaDTO.builder()
                    .text("Você precisa escolher o profissional: ")
                    .intencao(IntencaoEnum.MARCAR_CONSULTA)
                    .build());

            conversas.add(ConversaDTO.builder()
                    .responseList(profissionais)
                    .intencao(IntencaoEnum.MARCAR_CONSULTA)
                    .build());
        } else if (agendamento.getAgendaId() == null) {

            final var horarios = this.molApiService.buscarHorarios(agendamento);

            conversas.add(ConversaDTO.builder()
                    .text("Você precisa escolher a agenda: ")
                    .intencao(IntencaoEnum.MARCAR_CONSULTA)
                    .build());
            conversas.add(ConversaDTO.builder()
                    .responseList(horarios)
                    .intencao(IntencaoEnum.MARCAR_CONSULTA)
                    .build());
        } else if (agendamento.getHoraAgendamento() == null) {

            final var horarios = this.molApiService.buscarHorarios(agendamento);

            conversas.add(ConversaDTO.builder()
                    .text("Você precisa escolher o horário: ")
                    .intencao(IntencaoEnum.MARCAR_CONSULTA)
                    .build());
            conversas.add(ConversaDTO.builder()
                    .responseList(horarios)
                    .intencao(IntencaoEnum.MARCAR_CONSULTA)
                    .build());
        }

        // TODO: CONFIRMAR COM O USUARIO QUE ESTA TUDO CERTO

        return conversas;
    }

    public List<ConversaDTO> cancelarConsulta(AgendarOnlineRequest agendarOnlineRequest) {

        final var conversas = new ArrayList<ConversaDTO>();

        if (agendarOnlineRequest.getMarcacaoCancelarId() == null) {
            conversas.add(ConversaDTO.builder()
                    .text("Você precisa fornecer a identificação da marcação que quer cancelar!")
                    .intencao(IntencaoEnum.CANCELAR_CONSULTA)
                    .build());

            return conversas;
        } else if (agendarOnlineRequest.getMotivo() == null) {
            conversas.add(ConversaDTO.builder()
                    .text("Forneça um motivo para o cancelamento!")
                    .intencao(IntencaoEnum.CANCELAR_CONSULTA)
                    .build());

            return conversas;
        }

        try {

            this.molApiService.cancelarConsulta(agendarOnlineRequest);

            return List.of(ConversaDTO.builder()
                    .text("Consulta cancelada, obrigado")
                    .intencao(IntencaoEnum.FINALIZAR)
                    .build());
        } catch (RestClientException e) {

            log.error("Erro de conexão com mol", e);

            return List.of(ConversaDTO.builder()
                    .text("Deu ruim, tente novamente mais tarde!" + e.getMessage())
                    .intencao(IntencaoEnum.FINALIZAR)
                    .build());
        }
    }

    public List<ConversaDTO> historicoConsulta(AgendarOnlineRequest agendarOnlineRequest) {
        return List.of(ConversaDTO.builder()
                .text(this.molApiService.buscarHistorico(agendarOnlineRequest))
                .intencao(IntencaoEnum.FINALIZAR)
                .build());
    }
}
