package com.nexdom.test_spring_vue.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nexdom.test_spring_vue.model.Produto;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    boolean existsByCodigo(String codigo);

    boolean existsByCodigoAndIdNot(String codigo, Long id);
}
