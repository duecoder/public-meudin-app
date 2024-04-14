package br.com.due.meudin.dto.spend;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SpendChartDTO {
    private String monthFilter;
    private String categoryDescription;
    private BigDecimal categorySum;
}
