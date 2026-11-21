package com.nexdom.test_spring_vue.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.nexdom.test_spring_vue.model.TipoMovimentacao;

public record MovimentoResponse(Long id,
        Long produtoId,
        String codigoProduto,
        TipoMovimentacao tipo,
        Integer quantidade,
        BigDecimal valorVenda,
        LocalDateTime dataMovimentacao) {

}
