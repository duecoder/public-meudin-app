package br.com.due.meudin.controller;

import br.com.due.meudin.domain.card.Card;
import br.com.due.meudin.domain.card.CardInvoice;
import br.com.due.meudin.domain.user.CustomUserDetails;
import br.com.due.meudin.dto.card.CardDTO;
import br.com.due.meudin.dto.card.PayRequestDTO;
import br.com.due.meudin.service.card.CardService;
import br.com.due.meudin.service.card.usecase.CardInvoiceGetter;
import br.com.due.meudin.service.card.usecase.CardInvoiceUpdater;
import br.com.due.meudin.service.spend.SpendService;
import br.com.due.meudin.util.ResponseJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/card")
public class CardController {
    @Autowired
    CardService cardService;
    @Autowired
    CardInvoiceGetter invoiceGetter;
    @Autowired
    CardInvoiceUpdater invoiceUpdater;
    @Autowired
    SpendService spendService;

    @GetMapping("/cards")
    public List<CardDTO> getUserCards(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUserId();
        List<Card> cards = cardService.getUserCards(userId);

        return cards.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @PostMapping("/save")
    public ResponseEntity<ResponseJson> createCard(@RequestBody Card card) {
        return ResponseEntity.ok().body(cardService.createCard(card));
    }

    @PutMapping("/edit")
    public ResponseEntity<ResponseJson> editCard(@RequestBody Card card) {
        return ResponseEntity.ok().body(cardService.editCard(card));
    }

    @PutMapping("/pay")
    public ResponseEntity<ResponseJson> payInvoice(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody PayRequestDTO dto) {
        Long userId = userDetails.getUserId();
        // Atualizo as spends desse card para paid = true
        spendService.setCardSpendsAsPaid(dto.invoiceId());
        // Pago a invoice
        ResponseJson response = invoiceUpdater.payInvoice(userId, dto.invoiceId());

        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("/delete/{cardId}")
    public void deleteCard(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long cardId) {
        // Lido com as spends/wallet balance
        spendService.deleteCardSpends(userDetails.getUserId(), cardId);
        // Deleto o card
        cardService.deleteCard(cardId);
    }

    public CardDTO mapToDTO(Card card) {
        CardDTO dto = new CardDTO();
        dto.setId(card.getId());
        dto.setUserId(card.getUserId());
        dto.setCardName(card.getCardName());
        dto.setFinalDigits(card.getFinalDigits());
        dto.setClosingDay(card.getClosingDay());
        dto.setPaymentLimitDay(card.getPaymentLimitDay());
        // All invoices
        List<CardInvoice> invoices = invoiceGetter.getCardInvoices(card.getId());
        dto.setInvoices(invoices);
        // Current invoice
        CardInvoice currentInvoice = invoiceGetter.getCardCurrentInvoice(card, false);
        dto.setCurrentInvoice(currentInvoice);
        // Next month invoice
        CardInvoice nextMonthInvoice = invoiceGetter.getCardCurrentInvoice(card, true);
        dto.setNextMonthInvoice(nextMonthInvoice);

        return dto;
    }
}
