package com.bank_fx.transaction.adapters.web;

import com.bank_fx.transaction.application.service.StatementService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/statements")
public class StatementController {

    private final StatementService statementService;

    public StatementController(StatementService statementService) {
        this.statementService = statementService;
    }

    @GetMapping("/{accountNumber}/text")
    public ResponseEntity<String> generateTextStatement(
            @PathVariable String accountNumber,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        String statement = statementService.generateTextStatement(accountNumber, startDate, endDate);
        return ResponseEntity.ok(statement);
    }

    @GetMapping("/{accountNumber}/pdf")
    public ResponseEntity<byte[]> generatePdf(
            @PathVariable String accountNumber,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        byte[] pdf = statementService.generatePdfStatement(accountNumber, startDate, endDate);

        return ResponseEntity.ok()
                .header("Content-Type", "application/pdf")
                .header("Content-Disposition", "attachment; filename=statement_" + accountNumber + ".pdf")
                .body(pdf);
    }
    @GetMapping("/{accountNumber}/pdf-advanced")
    public ResponseEntity<byte[]> getPdf(
            @PathVariable String accountNumber,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        byte[] pdf = statementService.generateAdvancedPdf(accountNumber, startDate, endDate);

        return ResponseEntity.ok()
                .header("Content-Type", "application/pdf")
                .header("Content-Disposition", "attachment; filename=bank_statement.pdf")
                .body(pdf);
    }

}
