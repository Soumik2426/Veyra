package com.payment.veyra.vault.controller;

import com.payment.veyra.vault.dto.request.TokenizeRequest;
import com.payment.veyra.vault.dto.response.TokenizeResponse;
import com.payment.veyra.vault.service.CardTokenService;
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
@RequiredArgsConstructor
@RequestMapping("/v1/vault")
public class VaultController {
    private final CardTokenService cardTokenService;
    UUID merchantId = UUID.fromString("a208ab14-2e5d-41ce-aadf-76847e36cbea"); //TODO: replace it with merchantContext

    @PostMapping("/tokenize")
    public ResponseEntity<TokenizeResponse> tokenize(@Valid @RequestBody TokenizeRequest request){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(cardTokenService.tokenize(request, merchantId));
    }
}
