package br.com.due.meudin.service.wallet;

import br.com.due.meudin.domain.wallet.Wallet;
import br.com.due.meudin.dto.wallet.WalletIncomeOutcomeDTO;
import br.com.due.meudin.exception.APIDefaultError;
import br.com.due.meudin.repository.WalletRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
public class WalletService {
    @Autowired
    WalletRepository repository;

    public void updateUserBalance(Long userId, String nature, BigDecimal amount) {
        if (amount == null) return;
        Wallet userWallet = repository.findByUserId(userId);
        switch (nature) {
            case "income":
                userWallet.setBalance(userWallet.getBalance().add(amount));
                repository.save(userWallet);
                break;
            case "outcome":
                userWallet.setBalance(userWallet.getBalance().subtract(amount));
                repository.save(userWallet);
                break;
            default:
                break;
        }
    }

    public BigDecimal getUserBalance(Long userId) {
        BigDecimal balance;
        try {
            Wallet userWallet = repository.findByUserId(userId);
            balance = userWallet.getBalance();

            return balance;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new APIDefaultError();
        }
    }

    public WalletIncomeOutcomeDTO getMonthBalance(Long userId) {
        WalletIncomeOutcomeDTO dto = new WalletIncomeOutcomeDTO();
        try {
            dto.setIncome(repository.getUserIncomeMonthBalance(userId));
            dto.setOutcome(repository.getUserOutcomeMonthBalance(userId));
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new APIDefaultError();
        }

        return dto;
    }

    public WalletIncomeOutcomeDTO getAllTimeBalance(Long userId) {
        WalletIncomeOutcomeDTO dto = new WalletIncomeOutcomeDTO();
        try {
            dto.setIncome(repository.getUserIncomeAllTimeBalance(userId));
            dto.setOutcome(repository.getUserOutcomeAllTimeBalance(userId));
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new APIDefaultError();
        }

        return dto;
    }
}
