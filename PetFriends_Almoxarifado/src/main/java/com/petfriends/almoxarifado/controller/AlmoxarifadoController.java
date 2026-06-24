package com.petfriends.almoxarifado.controller;

import com.petfriends.almoxarifado.domain.OrdemSeparacao;
import com.petfriends.almoxarifado.domain.OrdemSeparacaoRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/almoxarifado")
public class AlmoxarifadoController {

    private final OrdemSeparacaoRepository repository;

    public AlmoxarifadoController(OrdemSeparacaoRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/ordens-separacao")
    public List<OrdemSeparacao> listarOrdens() {
        return repository.findAll();
    }
}
