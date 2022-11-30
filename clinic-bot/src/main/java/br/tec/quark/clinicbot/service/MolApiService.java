package br.tec.quark.clinicbot.service;

import br.tec.quark.clinicbot.dto.AgendarOnlineRequest;
import br.tec.quark.clinicbot.dto.TipoDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MolApiService {

    @Value("${quark.mol.url}")
    private String urlMol;

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

    public List<TipoDTO> buscarConvenios(AgendarOnlineRequest agendarOnlineRequest) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders header = new HttpHeaders();
        header.set("X-Organizacao-ID", String.valueOf(agendarOnlineRequest.getOrganizacaoId()));
        header.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<TipoDTO[]> requestEntity = new HttpEntity<>(header);
        final var result = restTemplate.exchange(
                urlMol + "/api/agendamentos/convenios",
                HttpMethod.GET,
                requestEntity,
                TipoDTO[].class
        );

        return Arrays.asList(result.getBody());
    }

    public List<TipoDTO> buscarProcedimentos(AgendarOnlineRequest agendarOnlineRequest) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders header = new HttpHeaders();
        header.set("X-Organizacao-ID", String.valueOf(agendarOnlineRequest.getOrganizacaoId()));
        header.setContentType(MediaType.APPLICATION_JSON);


        String urlTemplate = UriComponentsBuilder.fromHttpUrl(urlMol + "/api/agendamentos/procedimentos")
                .queryParam("convenio_id", "{convenio_id}")
                .queryParam("telemedicina", "{telemedicina}")
                .encode()
                .toUriString();

        Map<String, String> params = new HashMap<>();
        params.put("convenio_id", String.valueOf(agendarOnlineRequest.getConvenioId()));
        params.put("telemedicina", "true");

        HttpEntity<TipoDTO[]> requestEntity = new HttpEntity<>(header);
        final var result = restTemplate.exchange(
                urlTemplate,
                HttpMethod.GET,
                requestEntity,
                TipoDTO[].class,
                params
        );

        return Arrays.asList(result.getBody());
    }

    public List<TipoDTO> buscarClinicas(AgendarOnlineRequest agendarOnlineRequest) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders header = new HttpHeaders();
        header.set("X-Organizacao-ID", String.valueOf(agendarOnlineRequest.getOrganizacaoId()));
        header.setContentType(MediaType.APPLICATION_JSON);


        String urlTemplate = UriComponentsBuilder.fromHttpUrl(urlMol + "/api/agendamentos/clinicas")
                .queryParam("convenio_id", "{convenio_id}")
                .queryParam("procedimento_id", "{procedimento_id}")
                .queryParam("telemedicina", "{telemedicina}")
                .encode()
                .toUriString();

        Map<String, String> params = new HashMap<>();
        params.put("convenio_id", String.valueOf(agendarOnlineRequest.getConvenioId()));
        params.put("procedimento_id", String.valueOf(agendarOnlineRequest.getProcedimentoId()));
        params.put("telemedicina", "true");

        HttpEntity<TipoDTO[]> requestEntity = new HttpEntity<>(header);
        final var result = restTemplate.exchange(
                urlTemplate,
                HttpMethod.GET,
                requestEntity,
                TipoDTO[].class,
                params
        );

        return Arrays.asList(result.getBody());
    }

    public List<TipoDTO> buscarProfissionais(AgendarOnlineRequest agendarOnlineRequest) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders header = new HttpHeaders();
        header.set("X-Organizacao-ID", String.valueOf(agendarOnlineRequest.getOrganizacaoId()));
        header.setContentType(MediaType.APPLICATION_JSON);


        String urlTemplate = UriComponentsBuilder.fromHttpUrl(urlMol + "/api/agendamentos/profissionais")
                .queryParam("convenio_id", "{convenio_id}")
                .queryParam("procedimento_id", "{procedimento_id}")
                .queryParam("clinica_id", "{clinica_id}")
                .queryParam("telemedicina", "{telemedicina}")
                .encode()
                .toUriString();

        Map<String, String> params = new HashMap<>();
        params.put("convenio_id", String.valueOf(agendarOnlineRequest.getConvenioId()));
        params.put("procedimento_id", String.valueOf(agendarOnlineRequest.getProcedimentoId()));
        params.put("clinica_id", String.valueOf(agendarOnlineRequest.getClinicaId()));
        params.put("telemedicina", "true");

        HttpEntity<TipoDTO[]> requestEntity = new HttpEntity<>(header);
        final var result = restTemplate.exchange(
                urlTemplate,
                HttpMethod.GET,
                requestEntity,
                TipoDTO[].class,
                params
        );

        return Arrays.asList(result.getBody());
    }

    public List<TipoDTO> buscarHorarios(AgendarOnlineRequest agendarOnlineRequest) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders header = new HttpHeaders();
        header.set("X-Organizacao-ID", String.valueOf(agendarOnlineRequest.getOrganizacaoId()));
        header.setContentType(MediaType.APPLICATION_JSON);


        var urlTemplate = UriComponentsBuilder.fromHttpUrl(urlMol + "/api/chatbot/horarios")
                .queryParam("convenio_id", "{convenio_id}")
                .queryParam("procedimento_id", "{procedimento_id}")
                .queryParam("clinica_id", "{clinica_id}")
                .queryParam("profissional_id", "{profissional_id}")
                .queryParam("limite", "{limite}")
                .queryParam("telemedicina", "{telemedicina}");

        Map<String, String> params = new HashMap<>();
        params.put("convenio_id", String.valueOf(agendarOnlineRequest.getConvenioId()));
        params.put("procedimento_id", String.valueOf(agendarOnlineRequest.getProcedimentoId()));
        params.put("clinica_id", String.valueOf(agendarOnlineRequest.getClinicaId()));
        params.put("profissional_id", String.valueOf(agendarOnlineRequest.getProfissionalId()));
        params.put("limite", "1");
        params.put("telemedicina", "true");

        if (agendarOnlineRequest.getAgendaId() != null) {
            urlTemplate.queryParam("agenda_id", "{agenda_id}");
            params.put("agenda_id", String.valueOf(agendarOnlineRequest.getAgendaId()));
        }

        final var urlTemplateFinal = urlTemplate
                .encode()
                .toUriString();

        HttpEntity<TipoDTO[]> requestEntity = new HttpEntity<>(header);
        final var result = restTemplate.exchange(
                urlTemplateFinal,
                HttpMethod.GET,
                requestEntity,
                TipoDTO[].class,
                params
        );

        return Arrays.asList(result.getBody());
    }

    public String buscarHistorico(AgendarOnlineRequest agendarOnlineRequest) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders header = new HttpHeaders();
        header.set("X-Organizacao-ID", String.valueOf(agendarOnlineRequest.getOrganizacaoId()));
        header.setContentType(MediaType.APPLICATION_JSON);


        var urlTemplate = UriComponentsBuilder.fromHttpUrl(urlMol + "/api/chatbot/historico")
                .queryParam("usuario_id", "{usuario_id}");

        Map<String, String> params = new HashMap<>();
        params.put("usuario_id", String.valueOf(agendarOnlineRequest.getUsuarioPortalId()));

        final var urlTemplateFinal = urlTemplate
                .encode()
                .toUriString();

        HttpEntity<TipoDTO[]> requestEntity = new HttpEntity<>(header);
        final var result = restTemplate.exchange(
                urlTemplateFinal,
                HttpMethod.GET,
                requestEntity,
                String.class,
                params
        );

        return result.getBody();
    }

    public void cancelarConsulta(AgendarOnlineRequest agendarOnlineRequest) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders header = new HttpHeaders();
        header.set("X-Organizacao-ID", String.valueOf(agendarOnlineRequest.getOrganizacaoId()));
        header.setContentType(MediaType.APPLICATION_JSON);


        var urlTemplate = UriComponentsBuilder.fromHttpUrl(urlMol + "/api/chatbot/cancelar")
                .queryParam("usuario_id", "{usuario_id}")
                .queryParam("marcacao_id", "{marcacao_id}")
                .queryParam("motivo", "{motivo}");

        Map<String, String> params = new HashMap<>();
        params.put("usuario_id", String.valueOf(agendarOnlineRequest.getUsuarioPortalId()));
        params.put("marcacao_id", String.valueOf(agendarOnlineRequest.getMarcacaoCancelarId()));
        params.put("motivo", String.valueOf(agendarOnlineRequest.getMotivo()));

        final var urlTemplateFinal = urlTemplate
                .encode()
                .toUriString();

        HttpEntity<TipoDTO[]> requestEntity = new HttpEntity<>(header);
        restTemplate.exchange(
                urlTemplateFinal,
                HttpMethod.POST,
                requestEntity,
                String.class,
                params
        );
    }
}
