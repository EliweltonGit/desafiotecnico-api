package com.desafiotecnico.desafiotecnico.model;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;


@Entity
@Data
public class Pagamento {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String codigoDebito;
    private String cpfOuCnpj;
    private String metodoPagamento;
    private String numeroCartao;
    private BigDecimal valorPagamento;
    private String statusPagamento;
    private Boolean status;

}
