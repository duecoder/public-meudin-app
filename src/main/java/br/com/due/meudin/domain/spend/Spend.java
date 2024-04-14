package br.com.due.meudin.domain.spend;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;

@Entity
@Table(name = "spend", schema = "money")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Spend {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private Long userId;
    @NotNull
    private String nature;
    @NotNull
    private String description;
    @NotNull
    private Long category;
    @NotNull
    @Positive
    private BigDecimal cost;
    @Column(columnDefinition = "DATE")
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate date;
    private Long cardId;
    private Boolean paid;
    private Long invoiceId;
}
