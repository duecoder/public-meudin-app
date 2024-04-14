package br.com.due.meudin.service.user;

import br.com.due.meudin.dto.user.UserAccountDTO;
import br.com.due.meudin.dto.user.UserRegisterDTO;
import br.com.due.meudin.repository.UserRepository;
import br.com.due.meudin.service.user.usecase.RegisterDataValidator;
import br.com.due.meudin.service.user.usecase.UpdateUserValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserService {
    @Autowired
    private UserRepository repository;
    @Autowired
    private RegisterDataValidator validator;
    @Autowired
    private UpdateUserValidator updateValidator;

    public ResponseEntity registerUser(UserRegisterDTO dto) {
        return validator.registerUser(dto);
    };

    public UserAccountDTO getUserData(Long userId) {
        return updateValidator.getUserData(userId);
    }

    public ResponseEntity updateUserData(UserAccountDTO dto) {
        return updateValidator.updateUserData(dto);
    }

    public ResponseEntity verifyExistentCpf(Long cpf) {
        if (repository.existsByCpf(cpf) && !String.valueOf(cpf).equals("11111111111")) {
            return ResponseEntity.ok().body(true);
        } else {
            return ResponseEntity.ok().body(false);
        }
    }
}