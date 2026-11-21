package com.nexdom.test_spring_vue.model;

import com.nexdom.test_spring_vue.exception.EstoqueInsuficienteException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ProdutoTest {
    @Test
    void entradaDeveSomarQuantidade() {
        Produto produto = Produto.builder().codigo("P001").quantidadeEstoque(5).build();

        produto.entrada(3);

        assertThat(produto.getQuantidadeEstoque()).isEqualTo(8);
    }

    @Test
    void saidaDeveSubtrairQuantidade() {
        Produto produto = Produto.builder().codigo("P001").quantidadeEstoque(5).build();

        produto.saida(2);

        assertThat(produto.getQuantidadeEstoque()).isEqualTo(3);
    }

    @Test
    void saidaComSaldoInsuficienteDeveLancarExcecaoESemAlterarSaldo() {
        Produto produto = Produto.builder().codigo("P001").quantidadeEstoque(5).build();

        assertThatThrownBy(() -> produto.saida(10))
                .isInstanceOf(EstoqueInsuficienteException.class)
                .hasMessageContaining("disponível 5, solicitado 10");

        assertThat(produto.getQuantidadeEstoque()).isEqualTo(5);
    }
}
