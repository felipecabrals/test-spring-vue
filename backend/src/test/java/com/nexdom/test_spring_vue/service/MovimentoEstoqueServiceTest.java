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

import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.nexdom.test_spring_vue.dto.MovimentacaoRequest;
import com.nexdom.test_spring_vue.dto.MovimentoResponse;
import com.nexdom.test_spring_vue.exception.EstoqueInsuficienteException;
import com.nexdom.test_spring_vue.exception.ProdutoNaoEncontradoException;
import com.nexdom.test_spring_vue.exception.RegraNegocioException;
import com.nexdom.test_spring_vue.mapper.MovimentoEstoqueMapper;
import com.nexdom.test_spring_vue.model.MovimentoEstoque;
import com.nexdom.test_spring_vue.model.Produto;
import com.nexdom.test_spring_vue.model.TipoMovimentacao;
import com.nexdom.test_spring_vue.repository.MovimentoEstoqueRepository;
import com.nexdom.test_spring_vue.repository.ProdutoRepository;

@ExtendWith(MockitoExtension.class)
public class MovimentoEstoqueServiceTest {
    @Mock
    private MovimentoEstoqueRepository movimentoRepository;
    @Mock
    private ProdutoRepository produtoRepository;

    private MovimentoEstoqueService service;
    private Produto produto;

    @BeforeEach
    void setUp() {
        service = new MovimentoEstoqueService(
                movimentoRepository, produtoRepository, new MovimentoEstoqueMapper());
        produto = Produto.builder()
                .id(1L)
                .codigo("P001")
                .valorFornecedor(new BigDecimal("3500.00"))
                .quantidadeEstoque(5)
                .build();
    }

    @Test
    void entradaDeveAumentarSaldoERegistrarMovimento() {
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(movimentoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        MovimentoResponse response = service.registrar(
                new MovimentacaoRequest(1L, TipoMovimentacao.ENTRADA, 10, null));

        assertThat(produto.getQuantidadeEstoque()).isEqualTo(15);
        assertThat(response.tipo()).isEqualTo(TipoMovimentacao.ENTRADA);
        verify(movimentoRepository).save(any(MovimentoEstoque.class));
    }

    @Test
    void saidaDeveDiminuirSaldoEGuardarCustoUnitario() {
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(movimentoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.registrar(new MovimentacaoRequest(
                1L, TipoMovimentacao.SAIDA, 3, new BigDecimal("4200.00")));

        assertThat(produto.getQuantidadeEstoque()).isEqualTo(2);
        ArgumentCaptor<MovimentoEstoque> captor = ArgumentCaptor.forClass(MovimentoEstoque.class);
        verify(movimentoRepository).save(captor.capture());
        assertThat(captor.getValue().getCustoUnitario()).isEqualByComparingTo("3500.00");
    }

    @Test
    void saidaComSaldoInsuficienteNaoDeveRegistrarMovimento() {
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));

        assertThatThrownBy(() -> service.registrar(new MovimentacaoRequest(
                1L, TipoMovimentacao.SAIDA, 50, new BigDecimal("4200.00"))))
                .isInstanceOf(EstoqueInsuficienteException.class);

        assertThat(produto.getQuantidadeEstoque()).isEqualTo(5);
        verify(movimentoRepository, never()).save(any());
    }

    @Test
    void saidaSemValorVendaDeveLancarRegraNegocio() {
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));

        assertThatThrownBy(() -> service.registrar(
                new MovimentacaoRequest(1L, TipoMovimentacao.SAIDA, 1, null)))
                .isInstanceOf(RegraNegocioException.class);

        verify(movimentoRepository, never()).save(any());
    }

    @Test
    void produtoInexistenteDeveLancarNaoEncontrado() {
        when(produtoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.registrar(
                new MovimentacaoRequest(99L, TipoMovimentacao.ENTRADA, 1, null)))
                .isInstanceOf(ProdutoNaoEncontradoException.class);
    }
}
