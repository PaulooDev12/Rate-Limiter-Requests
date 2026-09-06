package rate.limiter.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;

@RestControllerAdvice
public class GlobalExceptHandler {

    @ExceptionHandler(TooManyRequests.class)
    public ResponseEntity<HashMap<String, Object>> exceptionHandler(TooManyRequests e){
        HashMap<String, Object> map = new HashMap<>();
        map.put("error", "Max requests exceeded");
        map.put("message", e.getMessage());
        map.put("status", HttpStatus.TOO_MANY_REQUESTS.value());
        return new ResponseEntity<>(map, HttpStatus.TOO_MANY_REQUESTS);

    }
}
