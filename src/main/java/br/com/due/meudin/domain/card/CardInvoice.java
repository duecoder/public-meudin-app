package br.com.due.meudin.domain.card;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(
    name = "card_invoice",
    schema = "money",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"card_id", "competence"})}
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CardInvoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @Column(name = "card_id")
    private Long cardId;
    @NotNull
    @Column(name = "competence", columnDefinition = "DATE")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate competence;
    private BigDecimal amount;
    private Boolean closed;
    private Boolean paid;
}
