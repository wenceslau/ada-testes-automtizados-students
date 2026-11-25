package com.ada.aulas.aula02;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    boolean existsBySku(String sku);

    Optional<Produto> findBySku(String sku);
}
