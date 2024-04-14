package br.com.due.meudin.repository;

import br.com.due.meudin.domain.user.User;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.userdetails.UserDetails;

public interface UserRepository extends JpaRepository<User, Long> {

    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.name = :name, u.surname = :surname, " +
            "u.cpf = :cpf, u.email = :email, u.username = :username, " +
            "u.password = CASE WHEN :password IS NOT NULL THEN :password ELSE u.password END " +
            "WHERE u.id = :userId")
    void updateUserFromDTO(
            @Param("userId") Long userId,
            @Param("name") String name,
            @Param("surname") String surname,
            @Param("cpf") Long cpf,
            @Param("email") String email,
            @Param("username") String username,
            @Param("password") String password
    );

    /* Método abaixo é pra trazer o novo User após a edição dos dados
    e atualizar no SecurityContext
    @Query("SELECT u FROM User u WHERE u.id = :userId")
    User findUserById(@Param("userId") Long userId); */
    User findByUsername(String username);
    /*
    Verificar método abaixo - acredito que não está sendo usado
    UserDetails findByUsername(String username);
    */
    UserDetails findByEmail(String email);
    boolean existsByCpf(Long cpf);
    boolean existsByUsername(String username);
}

