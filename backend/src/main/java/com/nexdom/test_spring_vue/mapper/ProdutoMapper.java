package com.nexdom.test_spring_vue.mapper;

import com.nexdom.test_spring_vue.dto.ProdutoRequest;
import com.nexdom.test_spring_vue.dto.ProdutoResponse;
import com.nexdom.test_spring_vue.model.Produto;
import org.springframework.stereotype.Component;

@Component
public class ProdutoMapper {
    public Produto toEntity(ProdutoRequest request) {
        return Produto.builder()
                .codigo(request.codigo())
                .descricao(request.descricao())
                .tipo(request.tipo())
                .valorFornecedor(request.valorFornecedor())
                .build();
    }

    public void updateEntity(Produto produto, ProdutoRequest request) {
        produto.setCodigo(request.codigo());
        produto.setDescricao(request.descricao());
        produto.setTipo(request.tipo());
        produto.setValorFornecedor(request.valorFornecedor());
    }

    public ProdutoResponse toResponse(Produto produto) {
        return new ProdutoResponse(
                produto.getId(),
                produto.getCodigo(),
                produto.getDescricao(),
                produto.getTipo(),
                produto.getValorFornecedor(),
                produto.getQuantidadeEstoque());
    }
}
