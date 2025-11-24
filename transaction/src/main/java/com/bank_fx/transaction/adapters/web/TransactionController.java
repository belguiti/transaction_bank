package com.bank_fx.transaction.adapters.web;


import com.bank_fx.transaction.application.dto.TransactionRequest;
import com.bank_fx.transaction.application.service.TransactionService;
import com.bank_fx.transaction.domain.model.Transaction;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/transfer")
    public ResponseEntity<Transaction> transfer(@RequestBody TransactionRequest request) {
        Transaction transaction = transactionService.transfer(request);
        return ResponseEntity.ok(transaction);
    }
}