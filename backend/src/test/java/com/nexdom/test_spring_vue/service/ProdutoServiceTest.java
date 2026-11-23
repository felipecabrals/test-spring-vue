package com.nexdom.test_spring_vue.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.nexdom.test_spring_vue.dto.MovimentacaoRequest;
import com.nexdom.test_spring_vue.dto.ProdutoRequest;
import com.nexdom.test_spring_vue.dto.ProdutoResponse;
import com.nexdom.test_spring_vue.exception.CodigoDuplicadoException;
import com.nexdom.test_spring_vue.exception.ProdutoComMovimentacaoException;
import com.nexdom.test_spring_vue.exception.ProdutoNaoEncontradoException;
import com.nexdom.test_spring_vue.mapper.ProdutoMapper;
import com.nexdom.test_spring_vue.model.Produto;
import com.nexdom.test_spring_vue.model.TipoMovimentacao;
import com.nexdom.test_spring_vue.model.TipoProduto;
import com.nexdom.test_spring_vue.repository.MovimentoEstoqueRepository;
import com.nexdom.test_spring_vue.repository.ProdutoRepository;

@ExtendWith(MockitoExtension.class)
public class ProdutoServiceTest {
    @Mock
    private ProdutoRepository repository;
    @Mock
    private MovimentoEstoqueRepository movimentoRepository;

    private ProdutoService service;

    @Mock
    private MovimentoEstoqueService movimentoService;

    @BeforeEach
    void setUp() {
        service = new ProdutoService(
                repository, movimentoRepository, new ProdutoMapper(), movimentoService);
    }

    private ProdutoRequest request(String codigo) {
        return new ProdutoRequest(codigo, "Notebook", TipoProduto.ELETRONICO, new BigDecimal("3500.00"), null);
    }

    private Produto produtoExistente() {
        return Produto.builder()
                .id(1L)
                .codigo("P001")
                .descricao("Notebook")
                .tipo(TipoProduto.ELETRONICO)
                .valorFornecedor(new BigDecimal("3500.00"))
                .quantidadeEstoque(0)
                .build();
    }

    @Test
    void criarDeveSalvarProdutoComEstoqueZerado() {
        when(repository.existsByCodigo("P001")).thenReturn(false);
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ProdutoResponse response = service.criar(request("P001"));

        assertThat(response.codigo()).isEqualTo("P001");
        assertThat(response.quantidadeEstoque()).isZero();
        verify(repository).save(any(Produto.class));
    }

    @Test
    void criarComCodigoDuplicadoDeveLancarExcecao() {
        when(repository.existsByCodigo("P001")).thenReturn(true);

        assertThatThrownBy(() -> service.criar(request("P001")))
                .isInstanceOf(CodigoDuplicadoException.class);

        verify(repository, never()).save(any());
    }

    @Test
    void buscarPorIdInexistenteDeveLancarNaoEncontrado() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscarPorId(99L))
                .isInstanceOf(ProdutoNaoEncontradoException.class);
    }

    @Test
    void atualizarDeveAlterarOsCamposDoProduto() {
        Produto produto = produtoExistente();
        when(repository.findById(1L)).thenReturn(Optional.of(produto));
        when(repository.existsByCodigoAndIdNot("P010", 1L)).thenReturn(false);

        ProdutoResponse response = service.atualizar(1L, request("P010"));

        assertThat(produto.getCodigo()).isEqualTo("P010");
        assertThat(response.codigo()).isEqualTo("P010");
    }

    @Test
    void atualizarComCodigoDeOutroProdutoDeveLancarExcecao() {
        when(repository.findById(1L)).thenReturn(Optional.of(produtoExistente()));
        when(repository.existsByCodigoAndIdNot("P002", 1L)).thenReturn(true);

        assertThatThrownBy(() -> service.atualizar(1L, request("P002")))
                .isInstanceOf(CodigoDuplicadoException.class);
    }

    @Test
    void excluirProdutoComMovimentacaoDeveLancarExcecaoSemApagar() {
        when(repository.findById(1L)).thenReturn(Optional.of(produtoExistente()));
        when(movimentoRepository.existsByProdutoId(1L)).thenReturn(true);

        assertThatThrownBy(() -> service.excluir(1L))
                .isInstanceOf(ProdutoComMovimentacaoException.class);

        verify(repository, never()).delete(any());
    }

    @Test
    void excluirProdutoSemMovimentacaoDeveApagar() {
        Produto produto = produtoExistente();
        when(repository.findById(1L)).thenReturn(Optional.of(produto));
        when(movimentoRepository.existsByProdutoId(1L)).thenReturn(false);

        service.excluir(1L);

        verify(repository).delete(produto);
    }

    @Test
    void criarComEstoqueInicialDeveRegistrarEntrada() {
        when(repository.existsByCodigo("P001")).thenReturn(false);
        when(repository.save(any())).thenAnswer(inv -> {
            Produto produto = inv.getArgument(0);
            produto.setId(1L);
            return produto;
        });

        service.criar(new ProdutoRequest(
                "P001", "Notebook", TipoProduto.ELETRONICO, new BigDecimal("3500.00"), 10));

        verify(movimentoService).registrar(
                new MovimentacaoRequest(1L, TipoMovimentacao.ENTRADA, 10, null));
    }
}
