package br.com.due.meudin.repository;

import br.com.due.meudin.domain.card.Card;
import br.com.due.meudin.domain.card.CardInvoice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CardRepository extends JpaRepository<Card, Long> {

    List<Card> findAllByUserId(Long userId);
}
