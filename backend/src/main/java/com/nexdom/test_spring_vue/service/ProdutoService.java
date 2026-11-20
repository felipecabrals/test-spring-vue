package com.nexdom.test_spring_vue.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.nexdom.test_spring_vue.dto.ProdutoRequest;
import com.nexdom.test_spring_vue.dto.ProdutoResponse;
import com.nexdom.test_spring_vue.exception.CodigoDuplicadoException;
import com.nexdom.test_spring_vue.exception.ProdutoNaoEncontradoException;
import com.nexdom.test_spring_vue.mapper.ProdutoMapper;
import com.nexdom.test_spring_vue.model.Produto;
import com.nexdom.test_spring_vue.repository.ProdutoRepository;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProdutoService {
    private final ProdutoRepository repository;
    private final ProdutoMapper mapper;

    @Transactional
    public ProdutoResponse criar(ProdutoRequest request) {
        if (repository.existsByCodigo(request.codigo())) {
            throw new CodigoDuplicadoException(request.codigo());
        }
        Produto salvo = repository.save(mapper.toEntity(request));
        return mapper.toResponse(salvo);
    }

    @Transactional(readOnly = true)
    public Page<ProdutoResponse> listar(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public ProdutoResponse buscarPorId(Long id) {
        return mapper.toResponse(buscarEntidade(id));
    }

    @Transactional
    public ProdutoResponse atualizar(Long id, ProdutoRequest request) {
        Produto produto = buscarEntidade(id);
        if (repository.existsByCodigoAndIdNot(request.codigo(), id)) {
            throw new CodigoDuplicadoException(request.codigo());
        }
        mapper.updateEntity(produto, request);
        return mapper.toResponse(produto);
    }

    @Transactional
    public void excluir(Long id) {
        repository.delete(buscarEntidade(id));
    }

    private Produto buscarEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ProdutoNaoEncontradoException(id));
    }
}
