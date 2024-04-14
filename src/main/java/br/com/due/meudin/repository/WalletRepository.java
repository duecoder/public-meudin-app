package br.com.due.meudin.repository;

import br.com.due.meudin.domain.card.Card;
import br.com.due.meudin.domain.wallet.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
    Wallet findByUserId(Long userId);

    @Query(value = "SELECT COALESCE(SUM(cost), 0) FROM money.spend s WHERE user_id = :userId " +
            "AND EXTRACT(MONTH FROM s.date) = EXTRACT(MONTH FROM CURRENT_DATE) " +
            "AND EXTRACT(YEAR FROM s.date) = EXTRACT(YEAR FROM CURRENT_DATE) " +
            "AND nature = 'income' " +
            "AND CASE WHEN card_id > 0 THEN paid = true ELSE true END;", nativeQuery = true)
    BigDecimal getUserIncomeMonthBalance(@Param("userId") long userId);

    @Query(value = "SELECT COALESCE(SUM(cost), 0) FROM money.spend s WHERE user_id = :userId " +
            "AND EXTRACT(MONTH FROM s.date) = EXTRACT(MONTH FROM CURRENT_DATE) " +
            "AND EXTRACT(YEAR FROM s.date) = EXTRACT(YEAR FROM CURRENT_DATE) " +
            "AND nature = 'outcome' " +
            "AND CASE WHEN card_id > 0 THEN paid = true ELSE true END;", nativeQuery = true)
    BigDecimal getUserOutcomeMonthBalance(@Param("userId") long userId);

    @Query(value = "SELECT COALESCE(SUM(cost), 0) FROM money.spend s WHERE user_id = :userId " +
            "AND nature = 'income' " +
            "AND CASE WHEN card_id > 0 THEN paid = true ELSE true END;", nativeQuery = true)
    BigDecimal getUserIncomeAllTimeBalance(@Param("userId") long userId);

    @Query(value = "SELECT COALESCE(SUM(cost), 0) FROM money.spend s WHERE user_id = :userId " +
            "AND nature = 'outcome' " +
            "AND CASE WHEN card_id > 0 THEN paid = true ELSE true END;", nativeQuery = true)
    BigDecimal getUserOutcomeAllTimeBalance(@Param("userId") long userId);
}
