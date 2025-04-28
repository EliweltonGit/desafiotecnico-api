package com.desafiotecnico.desafiotecnico.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.desafiotecnico.desafiotecnico.model.Pagamento;

public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {
    
	// BUSCA POR PARAMETROS
	@Query("SELECT p FROM Pagamento p WHERE " +
		       "(:codigoDebito IS NULL OR p.codigoDebito = :codigoDebito) AND " +
		       "(:cpfOuCnpj IS NULL OR LOWER(p.cpfOuCnpj) LIKE LOWER(CONCAT('%', :cpfOuCnpj, '%'))) AND " +
		       "(:statusPagamento IS NULL OR LOWER(p.statusPagamento) LIKE LOWER(CONCAT('%', :statusPagamento, '%')))")
		Page<Pagamento> findByFilters(@Param("codigoDebito") String codigoDebito,
		                              @Param("cpfOuCnpj") String cpfOuCnpj,
		                              @Param("statusPagamento") String statusPagamento,
		                              Pageable pageable);
}
