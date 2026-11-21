package com.nexdom.test_spring_vue.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.nexdom.test_spring_vue.dto.ProdutoPorTipoResponse;
import com.nexdom.test_spring_vue.model.MovimentoEstoque;
import com.nexdom.test_spring_vue.model.Produto;
import com.nexdom.test_spring_vue.model.TipoMovimentacao;
import com.nexdom.test_spring_vue.model.TipoProduto;

@SpringBootTest
@Transactional
public class ProdutoRepositoryTest {
    @Autowired
    private ProdutoRepository produtoRepository;
    @Autowired
    private MovimentoEstoqueRepository movimentoRepository;

    @Test
    void deveSomarApenasSaidasEMostrarSaldoDisponivel() {
        Produto notebook = salvarProduto("P001", "Notebook", TipoProduto.ELETRONICO, 7);
        salvarProduto("P002", "Geladeira", TipoProduto.ELETRODOMESTICO, 4);
        movimentoRepository.save(movimento(notebook, TipoMovimentacao.ENTRADA, 10));
        movimentoRepository.save(movimento(notebook, TipoMovimentacao.SAIDA, 3));
        movimentoRepository.save(movimento(notebook, TipoMovimentacao.SAIDA, 2));

        List<ProdutoPorTipoResponse> resultado = produtoRepository.consultarPorTipo(TipoProduto.ELETRONICO);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).codigo()).isEqualTo("P001");
        assertThat(resultado.get(0).quantidadeSaida()).isEqualTo(5L);
        assertThat(resultado.get(0).quantidadeDisponivel()).isEqualTo(7);
    }

    @Test
    void produtoSemMovimentacaoDeveAparecerComSaidaZero() {
        salvarProduto("P003", "Sofa", TipoProduto.MOVEL, 2);

        List<ProdutoPorTipoResponse> resultado = produtoRepository.consultarPorTipo(TipoProduto.MOVEL);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).quantidadeSaida()).isZero();
    }

    private Produto salvarProduto(String codigo, String descricao, TipoProduto tipo, int saldo) {
        return produtoRepository.save(Produto.builder()
                .codigo(codigo)
                .descricao(descricao)
                .tipo(tipo)
                .valorFornecedor(new BigDecimal("1000.00"))
                .quantidadeEstoque(saldo)
                .build());
    }

    private MovimentoEstoque movimento(Produto produto, TipoMovimentacao tipo, int quantidade) {
        return MovimentoEstoque.builder()
                .produto(produto)
                .tipo(tipo)
                .quantidade(quantidade)
                .valorVenda(tipo == TipoMovimentacao.SAIDA ? new BigDecimal("1500.00") : null)
                .custoUnitario(produto.getValorFornecedor())
                .dataMovimentacao(LocalDateTime.now())
                .build();
    }
}
