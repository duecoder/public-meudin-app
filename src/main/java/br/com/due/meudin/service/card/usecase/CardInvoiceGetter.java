package br.com.due.meudin.service.card.usecase;

import br.com.due.meudin.domain.card.Card;
import br.com.due.meudin.domain.card.CardInvoice;
import br.com.due.meudin.repository.CardInvoiceRepository;
import br.com.due.meudin.repository.CardRepository;
import br.com.due.meudin.util.GlobalMethods;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class CardInvoiceGetter {
    @Autowired
    CardRepository cardRepository;
    @Autowired
    CardInvoiceRepository invoiceRepository;
    @Autowired
    CardInvoiceCreator invoiceCreator;

    public List<CardInvoice> getCardInvoices(Long cardId) {
        return invoiceRepository.findAllByCardId(cardId);
    }
    public LocalDate getCompetenceByCardClosingDay(Long cardId, LocalDate spendDate) {
        Optional<Card> cardOp = cardRepository.findById(cardId);
        if (cardOp.isPresent()) {
            Card card = cardOp.get();
            int cardClosingDay = Integer.valueOf(card.getClosingDay());
            int paymentLimitDay = Integer.valueOf(card.getPaymentLimitDay());
            LocalDate closingDate = GlobalMethods.getClosingDate(spendDate, cardClosingDay, paymentLimitDay);
            // Caso a data da spend seja maior ou igual a data de fechamento, significa que a spend será adicionada
            // à próxima invoice (próximo mês)
            if (spendDate.isAfter(closingDate)) {
                LocalDate newDate =
                        spendDate
                        .plusMonths(1)
                        .withDayOfMonth(Integer.valueOf(card.getPaymentLimitDay()));
                return newDate;
            }
            LocalDate newDate =
                    spendDate.withDayOfMonth(Integer.valueOf(card.getPaymentLimitDay()));
            return newDate;
        } else {
            return null;
        }
    }

    public CardInvoice getCardCurrentInvoice(Card card, boolean nextMonth) {
        LocalDate currentDate = LocalDate.now();
        int currentDay = currentDate.getDayOfMonth();
        int closingDayInt = Integer.valueOf(card.getClosingDay());
        int paymentDayInt = Integer.valueOf(card.getPaymentLimitDay());
        LocalDate closingDate = GlobalMethods.getClosingDate(currentDate, closingDayInt, paymentDayInt);
        // Data abaixo é usada pra definir o mês (baseado no boolean nextMonth)
        // e ser usada nos próximos métodos
        LocalDate auxDate = nextMonth
                            ? currentDate.plusMonths(1)
                            : currentDate;
        LocalDate competence = GlobalMethods.getCompetence(auxDate, currentDay, paymentDayInt);
        boolean isInvoiceInCloseInterval =
                (auxDate.isAfter(closingDate))
                && currentDate.isBefore(competence);

        CardInvoice invoice = invoiceRepository.findByCardIdAndCompetence(card.getId(), competence);
        if (invoice == null) {
            invoice = invoiceCreator.createNewInvoice(card.getId(), card.getPaymentLimitDay(), competence);
        } else if (!nextMonth && isInvoiceInCloseInterval && !invoice.getClosed()) {
            invoice.setClosed(true);
            invoiceRepository.save(invoice);
        }

        return invoice;
    }
}
