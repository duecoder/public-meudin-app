package br.com.due.meudin.service.user.usecase;

import br.com.due.meudin.config.security.TokenService;
import br.com.due.meudin.domain.user.User;
import br.com.due.meudin.dto.user.UserAccountDTO;
import br.com.due.meudin.exception.APIDefaultError;
import br.com.due.meudin.repository.UserRepository;
import br.com.due.meudin.util.ResponseJson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

//@Service
@Slf4j
public class UpdateUserValidator {
    @Autowired
    private UserRepository repository;
    @Autowired
    private TokenService tokenService;

    public UserAccountDTO getUserData(Long userId) {
        Optional<User> optionalUser = repository.findById(userId);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            UserAccountDTO userAccountDTO =
                    new UserAccountDTO(
                            user.getId(), user.getName(),
                            user.getSurname(), String.valueOf(user.getCpf()),
                            user.getEmail(), user.getUsername(), "", "");
            return userAccountDTO;
        } else {
            log.warn("UserService.getUserData: User not found");
            throw new APIDefaultError();
        }
    }

    public ResponseEntity<ResponseJson> updateUserData(UserAccountDTO dto) {
        try {
            ResponseJson validation = validateUserData(dto);
            if (validation != null) {
                return ResponseEntity.ok(validation);
            }
            // Dados validados, atualiza user
            String pass = !dto.getPassword().equals("")
                    ? new BCryptPasswordEncoder().encode(dto.getPassword())
                    : null;
            repository
                    .updateUserFromDTO(
                            dto.getId(), dto.getName(), dto.getSurname(),
                            Long.valueOf(dto.getCpf()), dto.getEmail(),
                            dto.getUsername(), pass);
            return ResponseEntity.ok(updateSecurityAndGetResponse(dto));
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new APIDefaultError();
        }
    }

    public ResponseJson validateUserData(UserAccountDTO dto) {
        User user = repository.findByUsername(dto.getUsername());
        if (user != null && user.getId() != dto.getId()) {
            return new ResponseJson(false, "Username already in use");
        }

        if (!dto.getPassword().equals("") && !dto.getPassword().equals(dto.getConfPassword())) {
            return new ResponseJson(false, "Password do not match");
        }

        return null;
    }

    private ResponseJson updateSecurityAndGetResponse(UserAccountDTO dto) {
        // Renova autenticação/authorities
        User updatedUser = repository.findById(dto.getId()).orElse(null);
        if (updatedUser != null) {
            var usernamePassword =
                    new UsernamePasswordAuthenticationToken(
                            updatedUser, null, updatedUser.getAuthorities()
                    );
            SecurityContextHolder.getContext().setAuthentication(usernamePassword);
            var token = tokenService.generateToken(updatedUser);

            return new ResponseJson(true, token);
        } else {
            log.warn("UserService.updateUserData: Updated user not found");
            return new ResponseJson(false, "Updated user not found");
        }
    }
}
