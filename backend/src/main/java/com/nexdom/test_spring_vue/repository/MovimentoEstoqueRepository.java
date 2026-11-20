package com.nexdom.test_spring_vue.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nexdom.test_spring_vue.model.MovimentoEstoque;

public interface MovimentoEstoqueRepository extends JpaRepository<MovimentoEstoque, Long> {

}
