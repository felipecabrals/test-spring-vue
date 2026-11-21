package com.nexdom.test_spring_vue.dto;

import com.nexdom.test_spring_vue.model.TipoProduto;

public record ProdutoPorTipoResponse(Long id,
        String codigo,
        String descricao,
        TipoProduto tipo,
        Long quantidadeSaida,
        Integer quantidadeDisponivel) {

}
