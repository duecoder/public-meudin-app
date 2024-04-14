package br.com.due.meudin.dto.spend;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class HomeSpendsDTO {
    private BigDecimal mostExpensive;
    private BigDecimal highestIncome;
    private int totalSpends;
}
