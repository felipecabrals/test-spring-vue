package com.nexdom.test_spring_vue.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nexdom.test_spring_vue.dto.MovimentacaoRequest;
import com.nexdom.test_spring_vue.dto.MovimentoResponse;
import com.nexdom.test_spring_vue.exception.ProdutoNaoEncontradoException;
import com.nexdom.test_spring_vue.exception.RegraNegocioException;
import com.nexdom.test_spring_vue.mapper.MovimentoEstoqueMapper;
import com.nexdom.test_spring_vue.model.MovimentoEstoque;
import com.nexdom.test_spring_vue.model.Produto;
import com.nexdom.test_spring_vue.model.TipoMovimentacao;
import com.nexdom.test_spring_vue.repository.MovimentoEstoqueRepository;
import com.nexdom.test_spring_vue.repository.ProdutoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MovimentoEstoqueService {
    private final MovimentoEstoqueRepository movimentoRepository;
    private final ProdutoRepository produtoRepository;
    private final MovimentoEstoqueMapper mapper;

    @Transactional
    public MovimentoResponse registrar(MovimentacaoRequest request) {
        Produto produto = produtoRepository.findById(request.produtoId())
                .orElseThrow(() -> new ProdutoNaoEncontradoException(request.produtoId()));

        if (request.tipo() == TipoMovimentacao.SAIDA) {
            if (request.valorVenda() == null) {
                throw new RegraNegocioException("O valor de venda é obrigatório para saída de estoque");
            }
            produto.saida(request.quantidade());
        } else {
            produto.entrada(request.quantidade());
        }

        MovimentoEstoque salvo = movimentoRepository.save(mapper.toEntity(request, produto));
        return mapper.toResponse(salvo);
    }

    @Transactional(readOnly = true)
    public Page<MovimentoResponse> listar(Pageable pageable) {
        return movimentoRepository.findAll(pageable).map(mapper::toResponse);
    }
}
