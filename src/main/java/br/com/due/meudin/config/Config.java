package br.com.due.meudin.config;

import br.com.due.meudin.service.user.usecase.RegisterDataValidator;
import br.com.due.meudin.service.user.usecase.UpdateUserValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {
    @Bean
    public RegisterDataValidator getValidator() {
        return new RegisterDataValidator();
    }

    @Bean
    public UpdateUserValidator getUpdateValidator() { return new UpdateUserValidator(); }
}
