package br.com.due.meudin.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/*
Neste exemplo, handleAPIDefaultError é anotado com @ExceptionHandler(APIDefaultError.class),
o que significa que esse método será chamado sempre que uma exceção
do tipo MinhaExcecaoPersonalizada for lançada no aplicativo.
A resposta apropriada será enviada com base na lógica dentro desse método.
Neste caso, uma resposta HTTP 400 (Bad Request) é enviada, e a mensagem da exceção é incluída no corpo da resposta.
Isso permite que você trate exceções de forma consistente no aplicativo, fornecendo respostas adequadas
para os clientes. Essa abordagem também ajuda a centralizar a lógica de tratamento de exceções,
facilitando a manutenção do código.
*/
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(APIDefaultError.class)
    public ResponseEntity<String> handleAPIDefaultError(APIDefaultError ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
}