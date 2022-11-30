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
  profissionalId: number | null,
  agendaId: number | null,
  horaAgendamento: number | null,

  marcacaoCancelarId: number | null,
  motivo: string | null,
}

@Component({
  selector: 'app-chat',
  templateUrl: './chat.component.html',
  styleUrls: ['./chat.component.css']
})
export class ChatComponent implements OnInit {

  loading = false;

  validateForm!: UntypedFormGroup;

  mensagens: { text?: string, responseList?: { id: number, nome: string }[], direct: string }[] = [{ text: "Olá", direct: "left"}, {  text: "Tudo bem?", direct: "right" }]

  agendamento: Agendamento = {
    fluxo: null,

    usuarioPortalId: 16199490,
    organizacaoId: 47,
    clinicaId: null,
    convenioId: null,
    procedimentoId: null,
    profissionalId: null,
    agendaId: null,
    horaAgendamento: null,
    marcacaoCancelarId: null,
    motivo: null
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

    if(this.agendamento.fluxo === 'CANCELAR_CONSULTA') {
      if(this.agendamento.marcacaoCancelarId === null) {
        const id = Number.parseInt(message);

        if (!!id) {
          this.agendamento.marcacaoCancelarId = id;
        }
      } else if(this.agendamento.motivo === null) {
        this.agendamento.motivo = message;
      }
    }

    this.enviarMensagem(message);


  }

  private enviarMensagem(message: string) {
    this.loading = true;
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

        if (it.responseList != null) {
          this.mensagens.push({ responseList: it.responseList, direct: 'left' });
        }

        this.mensagens.push({ text: it.text, direct: 'left' });
      });
    },
    () => {},
    () => {
      this.loading = false;
    });
  }

  setCampos(id: number, nome: string) {
    if(this.agendamento.convenioId == null) {
      this.agendamento.convenioId = id;
    } else if(this.agendamento.procedimentoId == null) {
      this.agendamento.procedimentoId = id;
    } else if(this.agendamento.clinicaId == null) {
      this.agendamento.clinicaId = id;
    } else if(this.agendamento.profissionalId == null) {
      this.agendamento.profissionalId = id;
    } else if(this.agendamento.agendaId == null) {
      this.agendamento.agendaId = id;
    } else if(this.agendamento.horaAgendamento == null) {
      this.agendamento.horaAgendamento = id;
    }

    // this.validateForm.get("message")?.patchValue(nome, { emitEvent: false });
    this.mensagens.push({ text: nome, direct: 'right' });
    this.enviarMensagem("");
  }

}
