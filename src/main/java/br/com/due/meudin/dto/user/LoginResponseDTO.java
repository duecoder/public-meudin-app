package br.com.due.meudin.dto.user;

public record LoginResponseDTO(String token, boolean success, String message) {
}
