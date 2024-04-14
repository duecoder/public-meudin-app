package br.com.due.meudin.domain.card;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "card", schema = "money")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @NotNull
    private long userId;
    @NotNull
    private String cardName;
    @NotNull
    @Column(name = "finalDigits", length = 4)
    private String finalDigits;
    @NotNull
    @Column(name = "closingDay", length = 2)
    private String closingDay;
    @NotNull
    @Column(name = "paymentLimitDay", length = 2)
    private String paymentLimitDay;
}
