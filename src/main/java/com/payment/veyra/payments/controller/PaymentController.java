package com.payment.veyra.payments.controller;

import com.payment.veyra.merchant.entity.Merchant;
import com.payment.veyra.payments.dto.request.PaymentInitRequest;
import com.payment.veyra.payments.dto.response.PaymentResponse;
import com.payment.veyra.payments.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/v1/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;
    UUID merchantId = UUID.fromString("a208ab14-2e5d-41ce-aadf-76847e36cbea"); //TODO: replace it with merchantContext


    @PostMapping("/initiate")
    public ResponseEntity<PaymentResponse> initiatePayment(UUID merchantId, @Valid @RequestBody PaymentInitRequest request){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(paymentService.initiatePayment(merchantId, request));
    }
}
