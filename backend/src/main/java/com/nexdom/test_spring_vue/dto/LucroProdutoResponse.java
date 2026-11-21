package com.nexdom.test_spring_vue.dto;

import java.math.BigDecimal;

public record LucroProdutoResponse(Long id,
        String codigo,
        String descricao,
        Long quantidadeTotalSaida,
        BigDecimal lucroTotal) {
    public LucroProdutoResponse {
        quantidadeTotalSaida = quantidadeTotalSaida == null ? 0L : quantidadeTotalSaida;
        lucroTotal = lucroTotal == null ? BigDecimal.ZERO : lucroTotal;
    }
}
