package com.nexdom.test_spring_vue.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.nexdom.test_spring_vue.dto.LucroProdutoResponse;
import com.nexdom.test_spring_vue.dto.ProdutoPorTipoResponse;
import com.nexdom.test_spring_vue.model.Produto;
import com.nexdom.test_spring_vue.model.TipoProduto;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    boolean existsByCodigo(String codigo);

    boolean existsByCodigoAndIdNot(String codigo, Long id);

    @Query("""
            SELECT new com.nexdom.test_spring_vue.dto.ProdutoPorTipoResponse(
                p.id, p.codigo, p.descricao, p.tipo,
                COALESCE(SUM(CASE WHEN m.tipo = com.nexdom.test_spring_vue.model.TipoMovimentacao.SAIDA
                                  THEN m.quantidade ELSE 0 END), 0L),
                p.quantidadeEstoque)
            FROM Produto p
            LEFT JOIN MovimentoEstoque m ON m.produto = p
            WHERE p.tipo = :tipo
            GROUP BY p.id, p.codigo, p.descricao, p.tipo, p.quantidadeEstoque
            ORDER BY p.descricao
            """)
    List<ProdutoPorTipoResponse> consultarPorTipo(@Param("tipo") TipoProduto tipo);

    @Query("""
            SELECT new com.nexdom.test_spring_vue.dto.LucroProdutoResponse(
                p.id, p.codigo, p.descricao,
                SUM(m.quantidade),
                SUM((m.valorVenda - m.custoUnitario) * m.quantidade))
            FROM Produto p
            LEFT JOIN MovimentoEstoque m
                   ON m.produto = p
                  AND m.tipo = com.nexdom.test_spring_vue.model.TipoMovimentacao.SAIDA
            GROUP BY p.id, p.codigo, p.descricao
            ORDER BY p.descricao
            """)
    List<LucroProdutoResponse> consultarLucro();
}
