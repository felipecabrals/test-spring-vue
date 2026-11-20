package com.nexdom.test_spring_vue.exception;

public class CodigoDuplicadoException extends RuntimeException {
    public CodigoDuplicadoException(String codigo) {
        super("Código duplicado: " + codigo);
    }

}
