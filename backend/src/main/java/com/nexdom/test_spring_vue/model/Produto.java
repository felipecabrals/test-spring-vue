package com.nexdom.test_spring_vue.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

import com.nexdom.test_spring_vue.exception.EstoqueInsuficienteException;

@Entity
@Table(name = "produto")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Produto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String codigo;

    @Column(nullable = false)
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoProduto tipo;

    @Column(name = "valor_fornecedor", nullable = false, precision = 12, scale = 2)
    private BigDecimal valorFornecedor;

    @Builder.Default
    @Column(name = "quantidade_estoque", nullable = false)
    private Integer quantidadeEstoque = 0;

    @Version
    private Long versao;

    public void entrada(int quantidade) {
        this.quantidadeEstoque += quantidade;
    }

    public void saida(int quantidade) {
        if (this.quantidadeEstoque < quantidade) {
            throw new EstoqueInsuficienteException(codigo, quantidadeEstoque, quantidade);
        }
        this.quantidadeEstoque -= quantidade;
    }
}