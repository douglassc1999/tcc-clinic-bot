import { ChatBotServiceService } from './chatBotService.service';
import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';

export interface Agendamento {
  fluxo: string | null,

  usuarioPortalId: number,
  organizacaoId: number,
  clinicaId: number | null,
  convenioId: number | null,
  procedimentoId: number | null,
  agendaId: number | null,
  horaAgendamento : Date | null
}

@Component({
  selector: 'app-chat',
  templateUrl: './chat.component.html',
  styleUrls: ['./chat.component.css']
})
export class ChatComponent implements OnInit {

  validateForm!: UntypedFormGroup;

  mensagens: { text: string, direct: string }[] = [{ text: "Olá", direct: "left"}, {  text: "Tudo bem?", direct: "right" }]

  agendamento: Agendamento = {
    fluxo: null,

    usuarioPortalId: 1,
    organizacaoId: 2,
    clinicaId: null,
    convenioId: null,
    procedimentoId: null,
    agendaId: null,
    horaAgendamento : null
  }

  constructor(
    private fb: UntypedFormBuilder,
    private chatBotServiceService: ChatBotServiceService) { }

  ngOnInit() {
    this.validateForm = this.fb.group({
      message: null
    });
  }

  enviar() {
    const message = this.validateForm.get("message")?.value;

    if(message === null) {
      return;
    }

    const objDiv = document.getElementById("scroll");
    console.log(objDiv?.scrollHeight)
    if (objDiv != null) {
      objDiv.scrollTop = objDiv?.scrollHeight;
    }

    this.mensagens.push({ text: message, direct: 'right' });
    this.validateForm.get("message")?.patchValue(null);

    this.chatBotServiceService.sendMessage(message, this.agendamento).subscribe(conversas => {
      // console.log(conversas);

      if (conversas == null || conversas.length == 0) {
        this.agendamento.fluxo = null;
      }

      conversas?.forEach(it => {
        if (this.agendamento.fluxo == null && ["MARCAR_CONSULTA", "CANCELAR_CONSULTA", "HISTORICO"].includes(it.intencao)) {
          this.agendamento.fluxo = it.intencao;
        }

        if (["FINALIZAR"].includes(it.intencao)) {
          this.agendamento.fluxo = null;
        }
        this.mensagens.push({ text: it.text, direct: 'left' });
      });
    });


  }

}
