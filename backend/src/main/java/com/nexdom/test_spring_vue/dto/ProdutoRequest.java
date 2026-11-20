package com.nexdom.test_spring_vue.dto;

import java.math.BigDecimal;

import com.nexdom.test_spring_vue.model.TipoProduto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ProdutoRequest(
        @NotBlank String codigo,
        @NotBlank String descricao,
        @NotNull TipoProduto tipo,
        @NotNull @Positive BigDecimal valorFornecedor) {
}
