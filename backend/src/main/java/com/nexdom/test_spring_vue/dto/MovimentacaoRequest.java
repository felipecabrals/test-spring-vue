package com.nexdom.test_spring_vue.dto;

import java.math.BigDecimal;

import com.nexdom.test_spring_vue.model.TipoMovimentacao;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record MovimentacaoRequest(@NotNull Long produtoId,
        @NotNull TipoMovimentacao tipo,
        @NotNull @Positive Integer quantidade,
        @Positive BigDecimal valorVenda) {

}
