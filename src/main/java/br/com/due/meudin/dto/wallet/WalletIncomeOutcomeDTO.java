package br.com.due.meudin.dto.wallet;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class WalletIncomeOutcomeDTO {
    private BigDecimal income;
    private BigDecimal outcome;
}
