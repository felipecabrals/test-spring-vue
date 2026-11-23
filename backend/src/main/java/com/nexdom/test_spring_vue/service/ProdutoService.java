package com.nexdom.test_spring_vue.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.nexdom.test_spring_vue.dto.LucroProdutoResponse;
import com.nexdom.test_spring_vue.dto.MovimentacaoRequest;
import com.nexdom.test_spring_vue.dto.ProdutoPorTipoResponse;
import com.nexdom.test_spring_vue.dto.ProdutoRequest;
import com.nexdom.test_spring_vue.dto.ProdutoResponse;
import com.nexdom.test_spring_vue.dto.ResumoMovimentacaoResponse;
import com.nexdom.test_spring_vue.exception.CodigoDuplicadoException;
import com.nexdom.test_spring_vue.exception.ProdutoComMovimentacaoException;
import com.nexdom.test_spring_vue.exception.ProdutoNaoEncontradoException;
import com.nexdom.test_spring_vue.exception.RegraNegocioException;
import com.nexdom.test_spring_vue.mapper.ProdutoMapper;
import com.nexdom.test_spring_vue.model.Produto;
import com.nexdom.test_spring_vue.model.TipoMovimentacao;
import com.nexdom.test_spring_vue.model.TipoProduto;
import com.nexdom.test_spring_vue.repository.MovimentoEstoqueRepository;
import com.nexdom.test_spring_vue.repository.ProdutoRepository;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProdutoService {
    private final ProdutoRepository repository;
    private final MovimentoEstoqueRepository movimentoRepository;
    private final ProdutoMapper mapper;
    private final MovimentoEstoqueService movimentoService;

    @Transactional
    public ProdutoResponse criar(ProdutoRequest request) {
        if (repository.existsByCodigo(request.codigo())) {
            throw new CodigoDuplicadoException(request.codigo());
        }
        Produto salvo = repository.save(mapper.toEntity(request));
        if (request.quantidadeInicial() != null && request.quantidadeInicial() > 0) {
            movimentoService.registrar(new MovimentacaoRequest(
                    salvo.getId(), TipoMovimentacao.ENTRADA, request.quantidadeInicial(), null));
        }
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
        Produto produto = buscarEntidade(id);
        if (movimentoRepository.existsByProdutoId(id)) { // novo
            throw new ProdutoComMovimentacaoException(id); // novo
        }
        repository.delete(produto);
    }

    private Produto buscarEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ProdutoNaoEncontradoException(id));
    }

    @Transactional(readOnly = true)
    public List<ProdutoPorTipoResponse> consultarPorTipo(TipoProduto tipo) {
        return repository.consultarPorTipo(tipo);
    }

    @Transactional(readOnly = true)
    public List<LucroProdutoResponse> consultarLucro() {
        return repository.consultarLucro();
    }

    @Transactional(readOnly = true)
    public List<ResumoMovimentacaoResponse> resumirMovimentacao(LocalDate inicio, LocalDate fim) {
        if (inicio != null && fim != null && fim.isBefore(inicio)) {
            throw new RegraNegocioException("A data final não pode ser anterior à data inicial");
        }
        LocalDateTime de = inicio != null ? inicio.atStartOfDay() : null;
        LocalDateTime ate = fim != null ? fim.plusDays(1).atStartOfDay() : null;
        return repository.resumirMovimentacao(de, ate);
    }
}
