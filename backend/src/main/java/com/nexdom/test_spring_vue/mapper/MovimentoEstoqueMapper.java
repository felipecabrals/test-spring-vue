package com.nexdom.test_spring_vue.mapper;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.nexdom.test_spring_vue.dto.MovimentacaoRequest;
import com.nexdom.test_spring_vue.dto.MovimentoResponse;
import com.nexdom.test_spring_vue.model.MovimentoEstoque;
import com.nexdom.test_spring_vue.model.Produto;
import com.nexdom.test_spring_vue.model.TipoMovimentacao;

@Component
public class MovimentoEstoqueMapper {
    public MovimentoEstoque toEntity(MovimentacaoRequest request, Produto produto) {
        boolean saida = request.tipo() == TipoMovimentacao.SAIDA;
        return MovimentoEstoque.builder()
                .produto(produto)
                .tipo(request.tipo())
                .quantidade(request.quantidade())
                .valorVenda(saida ? request.valorVenda() : null)
                .custoUnitario(produto.getValorFornecedor())
                .dataMovimentacao(LocalDateTime.now())
                .build();
    }

    public MovimentoResponse toResponse(MovimentoEstoque movimento) {
        return new MovimentoResponse(
                movimento.getId(),
                movimento.getProduto().getId(),
                movimento.getProduto().getCodigo(),
                movimento.getTipo(),
                movimento.getQuantidade(),
                movimento.getValorVenda(),
                movimento.getDataMovimentacao());
    }
}
