package br.com.due.meudin.service.card.usecase;

import br.com.due.meudin.domain.card.CardInvoice;
import br.com.due.meudin.repository.CardInvoiceRepository;
import br.com.due.meudin.util.GlobalMethods;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class CardInvoiceCreator {
    @Autowired
    CardInvoiceRepository invoiceRepository;

    @Transactional
    public CardInvoice createNewInvoice(Long cardId, String paymentLimitDay, LocalDate competence) {
        CardInvoice invoice = new CardInvoice();
        invoice.setCardId(cardId);
        LocalDate cardCompetence = competence != null
                ? competence
                : GlobalMethods.getCardCompetenceByPaymentDay(Integer.valueOf(paymentLimitDay));
        invoice.setCompetence(cardCompetence);
        invoice.setAmount(BigDecimal.ZERO);
        invoice.setClosed(false);
        invoice.setPaid(false);

        return invoiceRepository.save(invoice);
    }
}
