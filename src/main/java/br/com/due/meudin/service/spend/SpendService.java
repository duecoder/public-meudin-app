package br.com.due.meudin.service.spend;

import br.com.due.meudin.domain.info.SpendCategory;
import br.com.due.meudin.domain.spend.Spend;
import br.com.due.meudin.dto.spend.HomeSpendsDTO;
import br.com.due.meudin.dto.spend.SpendChartDTO;
import br.com.due.meudin.exception.APIDefaultError;
import br.com.due.meudin.repository.SpendCategoryRepository;
import br.com.due.meudin.repository.SpendRepository;
import br.com.due.meudin.service.card.usecase.CardInvoiceUpdater;
import br.com.due.meudin.service.wallet.WalletService;
import br.com.due.meudin.util.ResponseJson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class SpendService {
    @Autowired
    SpendRepository spendRepository;
    @Autowired
    CardInvoiceUpdater invoiceUpdater;
    @Autowired
    WalletService walletService;
    @Autowired
    SpendCategoryRepository categoryRepository;

    public HomeSpendsDTO getHomeSpendsData(Long userId) {
        HomeSpendsDTO dto = new HomeSpendsDTO();
        BigDecimal mostExpensive = spendRepository.getUserMostExpensiveSpend(userId);
        dto.setMostExpensive(mostExpensive != null ? mostExpensive : BigDecimal.ZERO);
        BigDecimal highestIncome = spendRepository.getUserHighestIncome(userId);
        dto.setHighestIncome(highestIncome != null ? highestIncome : BigDecimal.ZERO);
        dto.setTotalSpends(spendRepository.getUserTotalMonthSpends(userId));

        return dto;
    }

    public List<SpendChartDTO> getChartData(Long userId, String monthDate) {
        String newDate = monthDate.replace("-", "/");
        List<Object[]> results = spendRepository.getSpendChartData(userId, newDate);
        List<SpendChartDTO> dtos = new ArrayList<>();

        for (Object[] result : results) {
            String monthFilter = (String) result[0];
            String categoryDescription = (String) result[1];
            BigDecimal categorySum = ((BigDecimal) result[2]);

            SpendChartDTO dto = new SpendChartDTO(monthFilter, categoryDescription, categorySum);
            dtos.add(dto);
        }
        return dtos;
    }

    public ResponseJson save(Spend spend) {
        ResponseJson json = new ResponseJson(false, "Something went wrong");
        try {
            if (spend.getCardId() > 0) {
                // Caso seja gasto de cartão, adiciono na devida invoice
                Long invoiceId = invoiceUpdater.addSpendToCardInvoice(spend);
                spend.setInvoiceId(invoiceId);
            } else {
                // Caso não, atualizo o saldo total da carteira
                walletService.updateUserBalance(spend.getUserId(), spend.getNature(), spend.getCost());
            }
            spendRepository.save(spend);
            json.setMessage("Spend added successfully");
            json.setSuccess(true);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new APIDefaultError();
        }
        return json;
    }

    public void deleteSpend(Long spendId) {
        Optional<Spend> spendOp = spendRepository.findById(spendId);
        if (spendOp.isPresent()) {
            Spend spend = spendOp.get();
            if (spend.getCardId() > 0 && spend.getInvoiceId() > 0) {
                invoiceUpdater.removeSpendFromCard(spend);
            }
            // Caso invoice da spend já paga (ou n sendo de cartao), devolver o valor da spend do saldo total
            if (spend.getCardId() > 0 && spend.getPaid() || !spend.getPaid()) {
                String action = spend.getNature().equals("income") ? "outcome" : "income";
                walletService.updateUserBalance(spend.getUserId(), action, spend.getCost());
            }
            spendRepository.deleteById(spendId);
        }
    }

    // FIXME validar método abaixo
    public void deleteManySpends(Long userId, List<Long> ids) {
        // Pego valor que deve ser devolvido a balance (entrou como outcome)
        BigDecimal amountToReturn = spendRepository.getOutcomeSumToReturn(ids);
        // E o valor que deve ser removido (entrou como income)
        BigDecimal amountToSubtract = spendRepository.getIncomeSumToSubtract(ids);
        // Atualizo a user balance
        walletService.updateUserBalance(userId, "income", amountToReturn);
        walletService.updateUserBalance(userId, "outcome", amountToSubtract);
        // Deleto as spends
        spendRepository.deleteAllById(ids);
    }

    public void deleteCardSpends(Long userId, Long cardId) {
        // Antes de deletar o card, eu preciso somar o valor total das spends desse card
        // (só as que foram pagas: paid = true)
        BigDecimal amountToReturn = spendRepository.getCardPaidSpendsSum(cardId);
        BigDecimal amountToSubtract = null;
        if (amountToReturn != null) {
            // E então devolver esse valor na carteira/saldo
            walletService.updateUserBalance(userId, "income", amountToReturn);
            // Crio uma spend com esse valor para que o histórico em wallet seja condizente
            addSpendFromDeletedInvoiceAmount(userId, amountToReturn);
        }
        // E deletar as spends desse card do histórico
        spendRepository.deleteCardSpends(cardId);
    }

    public void addSpendFromDeletedInvoiceAmount(Long userId, BigDecimal spendsAmount) {
        Spend spend = new Spend();
        spend.setUserId(userId);
        spend.setDescription("Balance from deleted card");
        spend.setCost(spendsAmount);
        spend.setNature("income");
        spend.setCardId(0L);
        spend.setDate(LocalDate.now());
        SpendCategory category = categoryRepository.findUserDefaultByUserId(userId);
        spend.setCategory(category.getCategoryId());

        spendRepository.save(spend);
    }

    public void setCardSpendsAsPaid(Long invoiceId) {
        List<Spend> spendsFromInvoice = spendRepository.findByInvoiceId(invoiceId);
        List<Long> spendIds = new ArrayList<>();
        for (Spend spend : spendsFromInvoice) {
            spendIds.add(spend.getId());
        }
        try {
            spendRepository.updateSpendsPaidStatus(spendIds);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new APIDefaultError();
        }
    }
}
