package com.desafiotecnico.desafiotecnico.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.desafiotecnico.desafiotecnico.model.Pagamento;
import com.desafiotecnico.desafiotecnico.model.dto.StatusPagamentoDTO;
import com.desafiotecnico.desafiotecnico.service.PagamentoService;

@RestController
@RequestMapping("/api/pagamentos")
public class PagamentoController {

    @Autowired
    private PagamentoService service;

    @GetMapping
    public ResponseEntity<Page<Pagamento>> listarPagamentos(@RequestParam(required = false) String codigoDebito,
												            @RequestParam(required = false) String cpfOuCnpj,
												            @RequestParam(required = false) String statusPagamento,
												            Pageable pageable) {
    	Page<Pagamento> objSaved = service.findAllByFilters(codigoDebito, cpfOuCnpj, statusPagamento, pageable);
        return ResponseEntity.ok(objSaved);
    }
    
    
    @PostMapping
    public ResponseEntity<Pagamento> create(@RequestBody Pagamento pagamento) {
        Pagamento savedPagamento = service.create(pagamento);
        return ResponseEntity.ok(savedPagamento);
    }
    
    @PutMapping("/{id}/status")
    public ResponseEntity<Pagamento> updateStatus(@PathVariable Long id, @RequestBody StatusPagamentoDTO dto) {
        Pagamento objSaved = service.updateStatus(id, dto.getStatusPagamento());
        return ResponseEntity.ok(objSaved);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable Long id) {
        service.delete(id);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Pagamento inativado com sucesso.");

        return ResponseEntity.ok(response);
    }
}