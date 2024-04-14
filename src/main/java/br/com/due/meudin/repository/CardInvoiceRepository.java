package br.com.due.meudin.repository;

import br.com.due.meudin.domain.card.CardInvoice;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface CardInvoiceRepository extends JpaRepository<CardInvoice, Long> {

    List<CardInvoice> findAllByCardId(Long cardId);

    @Query(value = "SELECT * FROM money.card_invoice WHERE competence = :competence " +
            "AND card_id = :cardId", nativeQuery = true)
    CardInvoice findByCardIdAndCompetence(@Param("cardId") Long cardId, @Param("competence") LocalDate competence);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM money.card_invoice WHERE card_id = :card_id", nativeQuery = true)
    void deleteInvoicesByCardId(@Param("card_id") Long cardId);
}
