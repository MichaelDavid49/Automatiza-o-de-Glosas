package br.com.cooperativa.glosa.exception;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.LocalDateTime;
import java.util.Map;
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ArquivoInvalidoException.class)
    public ResponseEntity<Map<String, Object>> handleArquivo(ArquivoInvalidoException ex) {
        return ResponseEntity.badRequest().body(Map.of(
            "timestamp", LocalDateTime.now().toString(),
            "erro", ex.getMessage()
        ));
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenerico(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
            "timestamp", LocalDateTime.now().toString(),
            "erro", "Erro interno: " + ex.getMessage()
        ));
    }
}
