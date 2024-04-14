package br.com.due.meudin.repository;

import br.com.due.meudin.domain.spend.Spend;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface SpendRepository extends JpaRepository<Spend, Long> {
    List<Spend> findByUserId(Long userId);

    List<Spend> findByInvoiceId(Long invoiceId);

    @Query(value = "SELECT CASE WHEN COUNT(*) > 0 THEN true ELSE false END FROM money.spend " +
            "WHERE user_id = :user_id AND category = :category", nativeQuery = true)
    Boolean existsByUserIdAndCategoryId(@Param("user_id") Long userId, @Param("category") Long categoryId);

    @Query(value = "SELECT count(1) from money.spend where user_id = :user_id " +
            "AND EXTRACT(MONTH FROM date) = EXTRACT(MONTH FROM CURRENT_DATE)", nativeQuery = true)
    int getUserTotalMonthSpends(@Param("user_id") Long userId);

    @Query(value = "SELECT * FROM money.spend WHERE user_id = :user_id AND nature = 'outcome' " +
            "AND card_id = 0 " +
            "AND EXTRACT(MONTH FROM date) = EXTRACT(MONTH FROM CURRENT_DATE) " +
            "ORDER BY cost DESC LIMIT 1", nativeQuery = true)
    BigDecimal getUserMostExpensiveSpend(@Param("user_id") Long userId);

    @Query(value = "SELECT * FROM money.spend WHERE user_id = :user_id AND nature = 'income' " +
            "AND EXTRACT(MONTH FROM date) = EXTRACT(MONTH FROM CURRENT_DATE) " +
            "ORDER BY cost DESC LIMIT 1", nativeQuery = true)
    BigDecimal getUserHighestIncome(@Param("user_id") Long userId);

    @Query(value = "SELECT " +
            "   TO_CHAR(s.date, 'FMMonth') AS month_name, " +
            "   cat.description, " +
            "   SUM(CAST(cost AS DECIMAL(10, 2))) AS total_amount " +
            "FROM money.spend s " +
            "   INNER JOIN info.spend_category cat ON cat.category_id = s.category " +
            "   LEFT JOIN money.card card ON card.id = s.card_id " +
            "WHERE s.user_id = ?1 " +
            "   AND (CASE WHEN s.card_id > 0 " +
            "           THEN (s.date < TO_DATE(card.closing_day || '/ ' || ?2, 'DD/MM/YYYY') " +
            "                   AND TO_CHAR(s.date, 'MM/YYYY') = ?2 AND s.paid = true)" +
            "           ELSE TO_CHAR(s.date, 'MM/YYYY') = ?2 " +
            "        END) " +
            "   AND s.nature = 'outcome' " +
            "GROUP BY TO_CHAR(s.date, 'FMMonth'), cat.description, s.date", nativeQuery = true)
    List<Object[]> getSpendChartData(Long userId, String monthDate);

    @Query(value = "SELECT SUM(CAST(cost AS DECIMAL(10,2))) AS total_amount " +
            "FROM money.spend WHERE card_id = ?1 AND nature = 'outcome' AND paid = true", nativeQuery = true)
    BigDecimal getCardPaidSpendsSum(Long cardId);

    @Query(value = "SELECT SUM(CAST(cost AS DECIMAL(10,2))) AS total_amount " +
            "FROM money.spend WHERE nature = 'income' AND id IN :ids", nativeQuery = true)
    BigDecimal getIncomeSumToSubtract(@Param("ids") List<Long> spendIds);

    @Query(value = "SELECT SUM(CAST(cost AS DECIMAL(10,2))) AS total_amount " +
            "FROM money.spend WHERE nature = 'outcome' " +
            "AND CASE WHEN card_id > 0 THEN paid = true ELSE true END " +
            "AND id IN :ids", nativeQuery = true)
    BigDecimal getOutcomeSumToReturn(@Param("ids") List<Long> spendIds);

    @Modifying
    @Transactional
    @Query(value = "UPDATE money.spend SET category = :new_category_id " +
            " WHERE user_id = :user_id AND category = :category_id", nativeQuery = true)
    void setUserSpendsToDefaultCategory(
            @Param("user_id") Long userId,
            @Param("new_category_id") Long newCategoryId,
            @Param("category_id") Long categoryId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE money.spend SET paid = true WHERE id IN :ids", nativeQuery = true)
    void updateSpendsPaidStatus(@Param("ids") List<Long> spendIds);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM money.spend WHERE card_id = :card_id", nativeQuery = true)
    void deleteCardSpends(@Param("card_id") Long cardId);
}
