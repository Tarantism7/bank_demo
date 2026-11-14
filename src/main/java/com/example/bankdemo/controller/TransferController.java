package com.example.bankdemo.controller;

import com.example.bankdemo.dto.TransferRequest;
import jakarta.validation.Valid;
import com.example.bankdemo.model.Transfer;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.bankdemo.repository.TransferRepository;
import com.example.bankdemo.service.TransferService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/transfers")
public class TransferController {
    private final TransferService transferService;
    private final TransferRepository transferRepository;

    /*
        USER:
        Transfer money between own cards
    */
    @PostMapping
    public ResponseEntity<Transfer> transfer(@Valid @RequestBody TransferRequest request) {
        Transfer transfer =
                transferService.transfer(
                        request.getFromCard(),
                        request.getToCard(),
                        request.getAmount()
                );
        return ResponseEntity.ok(transfer);
    }

    /*
        USER:
        View transfers
    */
    @GetMapping
    public ResponseEntity<List<Transfer>> history() {
        return ResponseEntity.ok(
                transferRepository.findAll()
        );
    }
}
