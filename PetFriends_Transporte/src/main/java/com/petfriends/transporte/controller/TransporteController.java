package com.petfriends.transporte.controller;

import com.petfriends.transporte.domain.Entrega;
import com.petfriends.transporte.domain.EntregaRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/transporte")
public class TransporteController {

    private final EntregaRepository repository;

    public TransporteController(EntregaRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/entregas")
    public List<Entrega> listarEntregas() {
        return repository.findAll();
    }
}
