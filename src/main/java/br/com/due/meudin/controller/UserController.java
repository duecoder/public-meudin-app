package br.com.due.meudin.controller;

import br.com.due.meudin.dto.user.UserAccountDTO;
import br.com.due.meudin.dto.user.UserRegisterDTO;
import br.com.due.meudin.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService service;

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody UserRegisterDTO dto) {
        return service.registerUser(dto);
    }

    @PutMapping("/update")
    public ResponseEntity update(@RequestBody UserAccountDTO dto) {
        return service.updateUserData(dto);
    }

    @GetMapping("/{userId}")
    public UserAccountDTO getUserData(@PathVariable Long userId) {
        return service.getUserData(userId);
    }

    @GetMapping("/cpf/{cpf}")
    public ResponseEntity verifyExistentCpf(@PathVariable Long cpf) {
        return service.verifyExistentCpf(cpf);
    }
}