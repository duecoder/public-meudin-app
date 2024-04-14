package br.com.due.meudin.service.card;

import br.com.due.meudin.domain.card.Card;
import br.com.due.meudin.domain.card.CardInvoice;
import br.com.due.meudin.exception.APIDefaultError;
import br.com.due.meudin.repository.CardInvoiceRepository;
import br.com.due.meudin.repository.CardRepository;
import br.com.due.meudin.service.card.usecase.CardInvoiceCreator;
import br.com.due.meudin.util.ResponseJson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class CardService {
    @Autowired
    CardRepository cardRepository;
    @Autowired
    CardInvoiceRepository invoiceRepository;
    @Autowired
    CardInvoiceCreator invoiceCreator;

    public List<Card> getUserCards(Long userId) {
        return cardRepository.findAllByUserId(userId);
    }

    public ResponseJson createCard(Card card) {
        ResponseJson response = new ResponseJson(false, "Something went wrong");
        try {
            Card newCard = cardRepository.save(card);
            CardInvoice invoice =
                    invoiceCreator
                    .createNewInvoice(newCard.getId(), newCard.getPaymentLimitDay(), null);
            invoiceRepository.save(invoice);

            response.setSuccess(true);
            response.setMessage("Card created successfully!");
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new APIDefaultError();
        }
        return response;
    }

    public ResponseJson editCard(Card card) {
        ResponseJson response = new ResponseJson(false, "Something went wrong");
        try {
            Card savedCard = cardRepository.save(card);
            if (savedCard != null) {
                response.setSuccess(true);
                response.setMessage("Card updated successfully!");
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new APIDefaultError();
        }
        return response;
    }

    public void deleteCard(Long cardId) {
        try {
            // Excluo as invoices do card
            invoiceRepository.deleteInvoicesByCardId(cardId);
            // Excluo o card
            cardRepository.deleteById(cardId);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new APIDefaultError();
        }
    }
}
