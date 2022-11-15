package br.tec.quark.clinicbot.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class AgendarOnlineRequest {
    private Long organizacaoId;
    private Long clinicaId;
    private Long usuarioPortalId;
    private Long convenioId;
    private Long procedimentoId;
    private Long agendaId;
    private Date horaAgendamento;
    private Long dependenteId;
}