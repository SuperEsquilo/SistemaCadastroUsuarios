package dev.aplication.sistemaCadastro.exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> tratarArgumentoInvalido(IllegalArgumentException ex) {
        Map<String, String> erro = new HashMap<>();
        erro.put("error", "Requisição inválida");
        erro.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> tratarEstadoInvalido(IllegalStateException ex) {
        Map<String, String> erro = new HashMap<>();
        erro.put("error", "Operação não permitida");
        erro.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(erro);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, String>> tratarNaoEncontrado(EntityNotFoundException ex) {
        Map<String, String> erro = new HashMap<>();
        erro.put("error", "Não encontrado");
        erro.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> tratarValidacao(MethodArgumentNotValidException ex) {
        Map<String, Object> resposta = new HashMap<>();
        resposta.put("error", "Erro de validação");

        // Pega a primeira mensagem de erro para toast simplificado
        String primeiraMensagem = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(FieldError::getDefaultMessage)
                .orElse("Dados inválidos");
        resposta.put("message", primeiraMensagem);

        Map<String, String> camposInvalidos = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            camposInvalidos.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        resposta.put("fields", camposInvalidos);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resposta);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> tratarGenerico(Exception ex) {
        Map<String, String> erro = new HashMap<>();
        erro.put("error", "Erro interno do servidor");
        erro.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erro);
    }
}

