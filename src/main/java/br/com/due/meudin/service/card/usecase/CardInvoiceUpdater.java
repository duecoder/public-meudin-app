package br.com.due.meudin.service.card.usecase;

import br.com.due.meudin.domain.card.Card;
import br.com.due.meudin.domain.card.CardInvoice;
import br.com.due.meudin.domain.spend.Spend;
import br.com.due.meudin.domain.wallet.Wallet;
import br.com.due.meudin.exception.APIDefaultError;
import br.com.due.meudin.repository.CardInvoiceRepository;
import br.com.due.meudin.repository.CardRepository;
import br.com.due.meudin.repository.SpendRepository;
import br.com.due.meudin.repository.WalletRepository;
import br.com.due.meudin.service.spend.SpendService;
import br.com.due.meudin.service.wallet.WalletService;
import br.com.due.meudin.util.ResponseJson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class CardInvoiceUpdater {
    @Autowired
    CardInvoiceGetter invoiceGetter;
    @Autowired
    CardInvoiceRepository invoiceRepository;
    @Autowired
    CardRepository cardRepository;
    @Autowired
    CardInvoiceCreator invoiceCreator;
    @Autowired
    WalletService walletService;

    public Long addSpendToCardInvoice(Spend savedSpend) {
        try {
            LocalDate invoiceCompetence =
                    invoiceGetter.getCompetenceByCardClosingDay(savedSpend.getCardId(), savedSpend.getDate());
            CardInvoice invoice =
                    invoiceRepository
                            .findByCardIdAndCompetence(savedSpend.getCardId(), invoiceCompetence);
            if (!invoiceCompetence.equals("") && invoice == null) {
                // Caso ele não encontre a invoice, significa que ela ainda não foi criada, então criamos
                Optional<Card> cardOp = cardRepository.findById(savedSpend.getCardId());
                if (cardOp.isPresent()) {
                    Card card = cardOp.get();
                    invoice = invoiceCreator
                              .createNewInvoice(savedSpend.getCardId(), card.getPaymentLimitDay(), invoiceCompetence);
                }
            }
            invoice.setAmount(invoice.getAmount().add(savedSpend.getCost()));

            return invoiceRepository.save(invoice).getId();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new APIDefaultError();
        }
    }

    public void removeSpendFromCard(Spend spend) {
        try {
            Optional<CardInvoice> invoiceOp = invoiceRepository.findById(spend.getInvoiceId());
            if (invoiceOp.isPresent()) {
                CardInvoice invoice = invoiceOp.get();
                invoice.setAmount(invoice.getAmount().subtract(spend.getCost()));
                invoiceRepository.save(invoice);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new APIDefaultError();
        }
    }

    public ResponseJson payInvoice(Long userId, Long invoiceId) {
        ResponseJson json = new ResponseJson(false, "Something went wrong");
        try {
            Optional<CardInvoice> invoiceOp = invoiceRepository.findById(invoiceId);
            if (invoiceOp.isPresent()) {
                CardInvoice invoice = invoiceOp.get();
                // Subtraio o valor da fatura do saldo/balance do usuário
                walletService.updateUserBalance(userId, "outcome", invoice.getAmount());
                // Atualizo fatura para paga
                invoice.setPaid(true);
                invoiceRepository.save(invoice);

                json.setSuccess(true);
                json.setMessage("Invoice paid successfully!");
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new APIDefaultError();
        }
        return json;
    }
}
