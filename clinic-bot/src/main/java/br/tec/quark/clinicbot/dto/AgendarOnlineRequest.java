package br.tec.quark.clinicbot.dto;

import br.tec.quark.clinicbot.enums.IntencaoEnum;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class AgendarOnlineRequest {

    private IntencaoEnum fluxo;

    // MARCAR CONSULTA
    private Long organizacaoId;
    private Long clinicaId;
    private Long usuarioPortalId;
    private Long convenioId;
    private Long procedimentoId;
    private Long agendaId;
    private Date horaAgendamento;
    private Long dependenteId;

    // CANCELAR CONSULTA

    // HISTORICO
}