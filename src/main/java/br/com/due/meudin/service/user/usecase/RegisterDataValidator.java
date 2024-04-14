package br.com.due.meudin.service.user.usecase;

import br.com.due.meudin.domain.info.SpendCategory;
import br.com.due.meudin.domain.user.User;
import br.com.due.meudin.domain.user.UserRole;
import br.com.due.meudin.domain.wallet.Wallet;
import br.com.due.meudin.dto.user.UserRegisterDTO;
import br.com.due.meudin.exception.APIDefaultError;
import br.com.due.meudin.repository.SpendCategoryRepository;
import br.com.due.meudin.repository.UserRepository;
import br.com.due.meudin.repository.WalletRepository;
import br.com.due.meudin.util.ResponseJson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class RegisterDataValidator {
    @Autowired
    UserRepository repository;
    @Autowired
    SpendCategoryRepository categoryRepository;
    @Autowired
    WalletRepository walletRepository;

    public ResponseEntity<ResponseJson> validate(UserRegisterDTO dto) {
        if (repository.findByUsername(dto.username()) != null) {
            return ResponseEntity
                    .ok()
                    .body(new ResponseJson(false, "Username already in use"));
        }

        if (!dto.password().equals(dto.confPassword())) {
            return ResponseEntity
                    .ok()
                    .body(new ResponseJson(false, "Password do not match"));
        }
        return null;
    }

    public ResponseEntity registerUser(UserRegisterDTO dto) {
        ResponseEntity<ResponseJson> validation = validate(dto);
        if (validation != null) {
            return validation;
        }
        String encryptedPassword = new BCryptPasswordEncoder().encode(dto.password());
        User newUser = new User(
                dto.name(), dto.surname(), dto.cpf(), dto.email(),
                dto.username(), encryptedPassword, UserRole.ADMIN
        );
        try {
            // Register new user
            Long newUserId = repository.save(newUser).getUserId();
            // Create user default spends categories
            createDefaultCategories(newUserId);
            // Create user wallet
            createUserWallet(newUserId);

            return ResponseEntity
                    .ok()
                    .body(new ResponseJson(true, "User created successfully"));
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new APIDefaultError();
        }
    }

    //  FIXME colocar esse método no serviço de categorias
    private void createDefaultCategories(Long newUserId) {
        List<String> categories = Arrays.asList("Default", "Market", "Rent", "Fun", "Bills", "Gym");
        for (String category : categories) {
            SpendCategory cat = new SpendCategory();
            cat.setUserId(newUserId);
            cat.setDescription(category);
            if (category.equals("Default")) {
                cat.setUserDefault(true);
            }
            categoryRepository.save(cat);
        }
    }

    // FIXME colocar esse método no serviço de wallet
    private void createUserWallet(Long newUserId) {
        Wallet wallet = new Wallet();
        wallet.setUserId(newUserId);
        wallet.setBalance(BigDecimal.valueOf(0));
        walletRepository.save(wallet);
    }
}
