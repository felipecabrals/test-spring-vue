package com.nexdom.test_spring_vue.dto;

import java.math.BigDecimal;

import com.nexdom.test_spring_vue.model.TipoProduto;

public record ProdutoResponse(
        Long id,
        String codigo,
        String descricao,
        TipoProduto tipo,
        BigDecimal valorFornecedor,
        Integer quantidadeEstoque) {

}
