package br.com.due.meudin.controller;

import br.com.due.meudin.domain.user.CustomUserDetails;
import br.com.due.meudin.dto.wallet.WalletIncomeOutcomeDTO;
import br.com.due.meudin.service.wallet.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/wallet")
public class WalletController {
    @Autowired
    WalletService service;

    @GetMapping("/balance")
    public ResponseEntity<BigDecimal> getUserBalance(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUserId();

        return ResponseEntity.ok().body(service.getUserBalance(userId));
    }

    @GetMapping("/month")
    public ResponseEntity<WalletIncomeOutcomeDTO> getMonthBalance(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUserId();
        WalletIncomeOutcomeDTO dto = service.getMonthBalance(userId);

        return ResponseEntity.ok().body(dto);
    }

    @GetMapping("/alltime")
    public ResponseEntity<WalletIncomeOutcomeDTO> getAllTimeBalance(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUserId();
        WalletIncomeOutcomeDTO dto = service.getAllTimeBalance(userId);

        return ResponseEntity.ok().body(dto);
    }
}
