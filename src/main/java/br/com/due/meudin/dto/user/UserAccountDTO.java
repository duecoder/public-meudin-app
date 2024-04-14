package br.com.due.meudin.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserAccountDTO {
    private long id;
    private String name;
    private String surname;
    private String cpf;
    private String email;
    private String username;
    private String password;
    private String confPassword;
}
