package br.com.due.meudin.dto.card;

import br.com.due.meudin.domain.card.CardInvoice;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CardDTO {
    private long id;
    private long userId;
    private String cardName;
    private String finalDigits;
    private String closingDay;
    private String paymentLimitDay;
    private List<CardInvoice> invoices;
    private CardInvoice currentInvoice;
    private CardInvoice nextMonthInvoice;
}
