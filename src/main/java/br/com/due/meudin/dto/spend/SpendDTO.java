package br.com.due.meudin.dto.spend;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SpendDTO {
    private long id;
    private String nature;
    private String description;
    private BigDecimal cost;
    private String goal;
    private String date;
    private long category;
    private Long cardId;
    private Boolean paid;
    private Long invoiceId;
}
