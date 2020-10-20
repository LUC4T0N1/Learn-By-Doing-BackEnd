package interfaces.controller;

import infrastructure.dto.ConteudoDto;
import infrastructure.repository.ConteudoRepository;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@RequestScoped
@Path("api/conteudo")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ConteudoController {

    @Inject
    ConteudoRepository conteudoRepository;

    @POST
    @Transactional
    public void cadastrarConteudo(ConteudoDto dto) {
        conteudoRepository.cadastrarConteudo(dto.paraDominio(dto.nome));
    }

    @POST
    @Path("/prova")
    @Transactional
    public void adicionarProvaConteudo(ConteudoDto dto) {
        conteudoRepository.cadastrarProva(dto);
    }

    }
