package com.desafiotecnico.desafiotecnico.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.desafiotecnico.desafiotecnico.model.Pagamento;
import com.desafiotecnico.desafiotecnico.repository.PagamentoRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class PagamentoService {

	@Autowired
	private PagamentoRepository repository;
	
	//LISTAS DE DE INFORMAÇÕES VÁLIDAS, PODENDO SER SUBSTITUÍDO POR OUTRA CLASSE PARA GARANTIR MAIOR ESCALABILIDADE
	private static final List<String> METODOS_VALIDOS = Arrays.asList("boleto", "pix", "cartão de crédito", "cartão de débito");
    private static final List<String> STATUS_VALIDOS = Arrays.asList(
            "Pendente de Processamento", "Processado com Sucesso", "Processado com Falha");

	public Pagamento create(Pagamento obj) {
        // VERIFICA SE O MÉTODO ESTÁ DENTRO DO QUE É PERMITIDO
        if (!METODOS_VALIDOS.contains(obj.getMetodoPagamento().toLowerCase())) {
            throw new IllegalArgumentException("Método de pagamento inválido. Apenas 'boleto', 'pix', 'cartão de crédito' e 'cartão de débito' são aceitos.");
        }

        // VERIFICA SE O CARTÃO FOI INFORMADO CORRETAMENTE POIS SE FOI (CRÉDITO OU DÉBITO) PRECISA INFORMAR O NÚMERO
        if ((obj.getMetodoPagamento().equalsIgnoreCase("cartão de crédito") || obj.getMetodoPagamento().equalsIgnoreCase("cartão de débito")) 
            && (obj.getNumeroCartao() == null || obj.getNumeroCartao().isEmpty())) {
            throw new IllegalArgumentException("O número do cartão é obrigatório para 'cartão de crédito' ou 'cartão de débito'.");
        }
        
     // VERIFICA SE ESTÁ TENTANDO INFORMAR OUTRO MÉTODO DE PAGAMENTO QUE NÃO SEJA DE CARTÃO JUNTO A UM NÚMERO DE CARTAO, LANÇA EXCEÇÃO POR NÃO SER POSSÍVEL
        if (!obj.getMetodoPagamento().equalsIgnoreCase("cartão de crédito") && !obj.getMetodoPagamento().equalsIgnoreCase("cartão de débito")
            && !obj.getNumeroCartao().isEmpty()) {
            throw new IllegalArgumentException("Número do cartão não deve ser informado para o método de pagamento: " + obj.getMetodoPagamento());
        }
        
		//AO CRIAR, SETA AUTOMATICAMENTE O STATUS DE PENDENTE DE PROCESSAMENTO
		obj.setStatusPagamento("Pendente de Processamento");
		return repository.save(obj);
	}
	
	public Pagamento updateStatus(Long id, String statusPagamento) {
	    Optional<Pagamento> objSaved = repository.findById(id);
	    if (objSaved.isEmpty()) {
	        throw new EntityNotFoundException("Pagamento não encontrado com o ID: " + id);
	    }


	    // VERIFICA POSSÍBILIDADE DE MUDANÇA DE STATUS DE PAGAMENTO
	    if (canUpdateStatus(objSaved.get().getStatusPagamento(), statusPagamento)) {
	    	objSaved.get().setStatusPagamento(statusPagamento);
	        return repository.save(objSaved.get());
	    } else {
	        throw new IllegalArgumentException("Não é possível alterar o status do pagamento com o status atual: " + objSaved.get().getStatusPagamento());
	    }
	}
    
	public Page<Pagamento> findAllByFilters(String codigoDebito, String cpfOuCnpj, String status, Pageable pageable) {
        return repository.findByFilters(codigoDebito, cpfOuCnpj, status, pageable);
    }
	
	//DELETE LÓGICO
	public void delete(Long id) {
		Pagamento objSaved = findById(id);
		if (!"Pendente de Processamento".equalsIgnoreCase(objSaved.getStatusPagamento())) {
	        throw new IllegalStateException("Só é possível excluir pagamentos com status 'Pendente de Processamento'.");
	    }
		objSaved.setStatus(false);
		repository.save(objSaved);
		
	}
	
	// MÉTODO PARA VERIFICAR SE É POSSÍVEL A MUDANÇA DE STATUS DE PAGAMENTO
	private boolean canUpdateStatus(String currentStatus, String newStatus) { 
        
        // PRIMEIRO VERIFICA SE AMBOS OS STATUS DE PAGAMENTO SÃO VÁLIDOS
        if (!isStatusValido(currentStatus)) {
            throw new IllegalArgumentException("Status atual inválido: " + currentStatus);
        }
        if (!isStatusValido(newStatus)) {
            throw new IllegalArgumentException("Novo status inválido: " + newStatus);
        }

        if (currentStatus.equals("Pendente de Processamento")) {
            // PODE ALTERAR PARA PROCESSADO COM SUCESSO OU PROCESSADO COM FALHA SOMENTE
            return newStatus.equals("Processado com Sucesso") || newStatus.equals("Processado com Falha");
        } else if (currentStatus.equals("Processado com Sucesso")) {
            //NÃO PODE ALTERAR O STATUS DE PAGAMENTO
            return false;
        } else if (currentStatus.equals("Processado com Falha")) {
            // PODE ALTERAR SOMENTE PARA PENDENTE DE PROCESSAMENTO
            return newStatus.equals("Pendente de Processamento");
        }

        return false; //RETORNA FALSO CASO NENHUMA OPÇÃO SEJA ACEITA
    }
	
    // MÉTODO PARA VERIFICAR SE O STATUS DE PAGAMENTO É VÁLIDO
    private boolean isStatusValido(String status) {
        return STATUS_VALIDOS.contains(status);
    }
	

	public Pagamento findById(Long id) {
		return repository.findById(id).orElseThrow(() -> new EmptyResultDataAccessException(1));
	}
}
