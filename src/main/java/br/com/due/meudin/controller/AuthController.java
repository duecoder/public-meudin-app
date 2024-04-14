package br.com.due.meudin.controller;

import br.com.due.meudin.config.security.TokenService;
import br.com.due.meudin.domain.user.*;
import br.com.due.meudin.dto.user.AuthDTO;
import br.com.due.meudin.dto.user.LoginResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    TokenService tokenService;
    @Autowired
    AuthenticationManager authManager;

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody AuthDTO dto) {
        try {
            var usernamePassword = new UsernamePasswordAuthenticationToken(dto.username(), dto.password());
            var auth = this.authManager.authenticate(usernamePassword);
            var user = (User) auth.getPrincipal();
            var token = tokenService.generateToken(user);

            return ResponseEntity.ok(new LoginResponseDTO(token, true, ""));

        } catch (AuthenticationException e) {
            // Se as credenciais forem inválidas, retorno uma resposta de erro
            // com status "401 Unauthorized" e um booleano informando que houve um erro.
            log.error(e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginResponseDTO("", false, "Invalid credentials"));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.BAD_GATEWAY)
                    .body(new LoginResponseDTO("", false, "Something went wrong"));
        }
    }
}
