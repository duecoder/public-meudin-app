package br.com.due.meudin.dto.user;

public record UserRegisterDTO(
        String name, String surname, long cpf, String email,
        String username, String password, String confPassword
    ) {
}
