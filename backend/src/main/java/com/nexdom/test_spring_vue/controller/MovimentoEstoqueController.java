package com.nexdom.test_spring_vue.controller;

import java.net.URI;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nexdom.test_spring_vue.dto.MovimentacaoRequest;
import com.nexdom.test_spring_vue.dto.MovimentoResponse;
import com.nexdom.test_spring_vue.service.MovimentoEstoqueService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/movimentos")
@RequiredArgsConstructor
public class MovimentoEstoqueController {
    private final MovimentoEstoqueService service;

    @PostMapping
    public ResponseEntity<MovimentoResponse> registrar(@RequestBody @Valid MovimentacaoRequest request) {
        MovimentoResponse response = service.registrar(request);
        return ResponseEntity.created(URI.create("/api/movimentos/" + response.id())).body(response);
    }

    @GetMapping
    public Page<MovimentoResponse> listar(Pageable pageable) {
        return service.listar(pageable);
    }
}
