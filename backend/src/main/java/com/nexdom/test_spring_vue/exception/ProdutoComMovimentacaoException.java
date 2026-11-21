package com.nexdom.test_spring_vue.exception;

public class ProdutoComMovimentacaoException extends RuntimeException {
    public ProdutoComMovimentacaoException(Long id) {
        super("O produto " + id + " possui movimentações e não pode ser excluído");
    }
}
