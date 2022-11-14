package br.tec.quark.clinicbot.dto;

import br.tec.quark.clinicbot.enums.IntencaoEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@Builder
public class ConversaDTO {
    private IntencaoEnum intencao;
    private String text;
    private String type = "text";
}
