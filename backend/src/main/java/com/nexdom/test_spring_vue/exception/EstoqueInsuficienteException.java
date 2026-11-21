package com.nexdom.test_spring_vue.exception;

public class EstoqueInsuficienteException extends RuntimeException {
    public EstoqueInsuficienteException(String codigo, int disponivel, int solicitado) {
        super("Estoque insuficiente para o produto %s: disponível %d, solicitado %d"
                .formatted(codigo, disponivel, solicitado));
    }
}
