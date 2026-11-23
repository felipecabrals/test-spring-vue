package com.nexdom.test_spring_vue.dto;

public record ResumoMovimentacaoResponse(
        Long id,
        String codigo,
        String descricao,
        Long totalEntrada,
        Long totalSaida,
        Integer saldoAtual) {
    public ResumoMovimentacaoResponse {
        totalEntrada = totalEntrada == null ? 0L : totalEntrada;
        totalSaida = totalSaida == null ? 0L : totalSaida;
    }
}
