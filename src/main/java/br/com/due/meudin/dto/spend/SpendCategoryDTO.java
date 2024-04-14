package br.com.due.meudin.dto.spend;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SpendCategoryDTO {
    private long categoryId;
    private String description;
    private long userId;
    private boolean userDefault;
}
