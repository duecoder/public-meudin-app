package br.com.due.meudin.domain.info;

import br.com.due.meudin.domain.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "spend_category", schema = "info")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SpendCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long categoryId;
    @NotNull
    private String description;
    @NotNull
    private long userId;
    @Column(columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean userDefault;
}
