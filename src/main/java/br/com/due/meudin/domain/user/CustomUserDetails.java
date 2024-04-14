package br.com.due.meudin.domain.user;

import org.springframework.security.core.userdetails.UserDetails;

public interface CustomUserDetails extends UserDetails {
    /* Nessa interface, eu extendo a classe UserDetails e adiciono o método getUserId,
    e então, na minha entidade User, implemento essa interface e consigo utilizar o método getUserId
    no momento em que qualquer requisição chega nos controllers. */
    Long getUserId();
}