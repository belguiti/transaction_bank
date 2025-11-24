package com.bank_fx.transaction.application.service;


import com.bank_fx.transaction.domain.model.BankAccount;
import com.bank_fx.transaction.domain.model.Transaction;
import com.bank_fx.transaction.domain.repository.BankAccountRepository;
import com.bank_fx.transaction.domain.repository.TransactionRepository;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.*;
import org.springframework.stereotype.Service;
import com.lowagie.text.*;

import java.awt.*;
import java.io.ByteArrayOutputStream;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class StatementService {

    private final BankAccountRepository bankAccountRepository;
    private final TransactionRepository transactionRepository;

    public StatementService(BankAccountRepository bankAccountRepository,
                            TransactionRepository transactionRepository) {
        this.bankAccountRepository = bankAccountRepository;
        this.transactionRepository = transactionRepository;
    }
    public byte[] generateAdvancedPdf(String accountNumber, LocalDateTime startDate, LocalDateTime endDate) {
        BankAccount account = bankAccountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        List<Transaction> transactions = transactionRepository.findByAccountAndDateRange(
                accountNumber, startDate, endDate);

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4, 40, 40, 80, 40);
            PdfWriter writer = PdfWriter.getInstance(document, out);

            // ----------------- WATERMARK -----------------
            writer.setPageEvent(new PdfPageEventHelper() {
                @Override
                public void onEndPage(PdfWriter writer, Document document) {
                    PdfContentByte cb = writer.getDirectContentUnder();
                    Font watermarkFont = new Font(Font.HELVETICA, 60, Font.BOLD, new Color(230, 230, 230));
                    Phrase watermark = new Phrase("BANK FX", watermarkFont);

                    ColumnText.showTextAligned(
                            cb, Element.ALIGN_CENTER,
                            watermark,
                            document.getPageSize().getWidth() / 2,
                            document.getPageSize().getHeight() / 2,
                            45
                    );
                }
            });

            document.open();

            // ----------------- LOGO -----------------
            Image logo = Image.getInstance("C:\\Users\\azdin\\Desktop\\arkx\\transaction\\transaction\\src\\main\\resources\\static\\logo.png");
            logo.scaleAbsolute(80, 80);
            logo.setAlignment(Image.ALIGN_LEFT);
            document.add(logo);

            document.add(new Paragraph("\n"));

            // ----------------- TITLE -----------------
            Font titleFont = new Font(Font.HELVETICA, 20, Font.BOLD, new Color(0, 70, 150));
            Paragraph title = new Paragraph("Bank Account Statement", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            document.add(new Paragraph("\n"));

            // ----------------- ACCOUNT INFO -----------------
            Font bold = new Font(Font.HELVETICA, 12, Font.BOLD);
            Font normal = new Font(Font.HELVETICA, 12);

            PdfPTable info = new PdfPTable(2);
            info.setWidthPercentage(100);

            info.addCell(makeCell("Account Number:", bold));
            info.addCell(makeCell(account.getAccountNumber(), normal));

            info.addCell(makeCell("Account Holder:", bold));
            info.addCell(makeCell(account.getUser().getFirstName() + " " + account.getUser().getLastName(), normal));

            info.addCell(makeCell("Currency:", bold));
            info.addCell(makeCell(account.getCurrency().toString(), normal));

            info.addCell(makeCell("Balance:", bold));
            info.addCell(makeCell(account.getBalance() + " " + account.getCurrency(), normal));

            info.addCell(makeCell("Period:", bold));
            info.addCell(makeCell(startDate + " → " + endDate, normal));

            document.add(info);
            document.add(new Paragraph("\n"));

            // ----------------- QR CODE -----------------
            String qrData = "Account: " + account.getAccountNumber() + "\nBalance: " + account.getBalance();
            Image qrCode = generateQRCodeImage(qrData);
            qrCode.scaleAbsolute(120, 120);
            qrCode.setAlignment(Image.ALIGN_RIGHT);
            document.add(qrCode);

            document.newPage();

            // ----------------- TRANSACTION TABLE -----------------
            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            table.setWidths(new int[]{25, 15, 15, 15, 30});

            addTableHeader(table, "Date");
            addTableHeader(table, "Type");
            addTableHeader(table, "Amount");
            addTableHeader(table, "Currency");
            addTableHeader(table, "Description");

            for (Transaction tx : transactions) {
                table.addCell(tx.getTimestamp().toString());
                table.addCell(tx.getType().toString());
                table.addCell(tx.getAmount().toString());
                table.addCell(tx.getFromCurrency().toString());
                table.addCell(tx.getDescription());
            }

            document.add(table);

            document.add(new Paragraph("\n\n"));

            // ----------------- SIGNATURE -----------------
            Image stamp = Image.getInstance("C:\\Users\\azdin\\Desktop\\arkx\\transaction\\transaction\\src\\main\\resources\\static\\stamp.png");
            stamp.scaleAbsolute(120, 120);
            stamp.setAlignment(Image.ALIGN_RIGHT);
            document.add(stamp);

            document.close();

            // ----------------- SAVE PDF TO resources/pdf -----------------
            savePdfToFolder(out.toByteArray(), accountNumber);

            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("PDF generation failed", e);
        }
    }
    private PdfPCell makeCell(String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(5);
        cell.setBorder(Rectangle.NO_BORDER);
        return cell;
    }

    private void addTableHeader(PdfPTable table, String title) {
        PdfPCell header = new PdfPCell();
        header.setBackgroundColor(new Color(0, 70, 150));
        header.setPadding(5);

        Font headerFont = new Font(Font.HELVETICA, 12, Font.BOLD, Color.WHITE);
        header.setPhrase(new Phrase(title, headerFont));
        table.addCell(header);
    }
    private void savePdfToFolder(byte[] data, String accountNumber) throws IOException {
        String folder = "src/main/resources/pdf/";
        Files.createDirectories(Paths.get(folder));

        String filename = folder + "statement_" + accountNumber + "_" +
                System.currentTimeMillis() + ".pdf";

        Files.write(Paths.get(filename), data);
    }


    private Image generateQRCodeImage(String text) throws Exception {
        BitMatrix bitMatrix = new MultiFormatWriter().encode(
                text, BarcodeFormat.QR_CODE, 200, 200);

        ByteArrayOutputStream png = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", png);

        return Image.getInstance(png.toByteArray());
    }

    public byte[] generatePdfStatement(String accountNumber, LocalDateTime startDate, LocalDateTime endDate) {
        BankAccount account = bankAccountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        List<Transaction> transactions = transactionRepository.findByAccountAndDateRange(
                accountNumber, startDate, endDate);

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, out);

            document.open();

            Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD);
            Font normalFont = new Font(Font.HELVETICA, 12);

            document.add(new Paragraph("Bank Account Statement", titleFont));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("Account Number: " + account.getAccountNumber(), normalFont));
            document.add(new Paragraph("Account Holder: " +
                    account.getUser().getFirstName() + " " + account.getUser().getLastName(), normalFont));
            document.add(new Paragraph("Currency: " + account.getCurrency(), normalFont));
            document.add(new Paragraph("Current Balance: " + account.getBalance() + " " + account.getCurrency(), normalFont));
            document.add(new Paragraph("Statement Period: " + startDate + " to " + endDate, normalFont));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("Transactions:", titleFont));
            document.add(new Paragraph(" "));

            if (transactions.isEmpty()) {
                document.add(new Paragraph("No transactions found.", normalFont));
            } else {
                for (Transaction tx : transactions) {
                    document.add(new Paragraph("Date: " + tx.getTimestamp(), normalFont));
                    document.add(new Paragraph("ID: " + tx.getTransactionId(), normalFont));
                    document.add(new Paragraph("Type: " + tx.getType(), normalFont));
                    document.add(new Paragraph("Amount: " + tx.getAmount() + " " + tx.getFromCurrency(), normalFont));
                    document.add(new Paragraph("Description: " + tx.getDescription(), normalFont));
                    document.add(new Paragraph("--------------------------------------------"));
                }
            }

            document.close();
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF", e);
        }
    }

    public String generateTextStatement(String accountNumber, LocalDateTime startDate, LocalDateTime endDate) {
        BankAccount account = bankAccountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        List<Transaction> transactions = transactionRepository.findByAccountAndDateRange(
                accountNumber, startDate, endDate);

        StringBuilder statement = new StringBuilder();
        statement.append("Bank Account Statement\n");
        statement.append("======================\n");
        statement.append("Account Number: ").append(account.getAccountNumber()).append("\n");
        statement.append("Account Holder: ").append(account.getUser().getFirstName())
                .append(" ").append(account.getUser().getLastName()).append("\n");
        statement.append("Currency: ").append(account.getCurrency()).append("\n");
        statement.append("Current Balance: ").append(account.getBalance()).append(" ")
                .append(account.getCurrency()).append("\n");
        statement.append("Statement Period: ").append(startDate).append(" to ").append(endDate).append("\n\n");

        if (transactions.isEmpty()) {
            statement.append("No transactions found for the selected period.\n");
        } else {
            statement.append("Transactions:\n");
            statement.append("-------------\n");
            for (Transaction transaction : transactions) {
                statement.append("Date: ").append(transaction.getTimestamp()).append("\n");
                statement.append("ID: ").append(transaction.getTransactionId()).append("\n");
                statement.append("Type: ").append(transaction.getType()).append("\n");
                statement.append("Amount: ").append(transaction.getAmount()).append(" ")
                        .append(transaction.getFromCurrency()).append("\n");
                statement.append("Description: ").append(transaction.getDescription()).append("\n");
                statement.append("---\n");
            }
        }

        return statement.toString();
    }
}