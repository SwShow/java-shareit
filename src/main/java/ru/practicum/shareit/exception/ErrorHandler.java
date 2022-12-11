    package ru.practicum.shareit.exception;

    import org.apache.catalina.connector.Response;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.ExceptionHandler;
    import lombok.extern.slf4j.Slf4j;
    import org.springframework.web.bind.annotation.ResponseStatus;
    import org.springframework.web.bind.annotation.RestControllerAdvice;

    import java.sql.SQLException;
    import java.util.NoSuchElementException;

    @Slf4j
    @RestControllerAdvice
    public class ErrorHandler {

        @ExceptionHandler(ValidationException.class)
        public ResponseEntity<Response> handleException (ValidationException e){
            log.info("Пользователь не прошел валидацию! {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        @ExceptionHandler(ConflictException.class)
        public ResponseEntity<Response> handleException (ConflictException e){
            log.info("Конфликт создания! {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        @ExceptionHandler(NullPointerException.class)
        public ResponseEntity<Response> handleException (NullPointerException e){
            log.info("NullPointerException! {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        @ExceptionHandler(RuntimeException.class)
        public ResponseEntity<Response> handleException (RuntimeException e){
            log.info("RuntimeException! {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        @ExceptionHandler(NoSuchElementException.class)
        public ResponseEntity<Response> handleException (NoSuchElementException e){
            log.info("NoSuchElementException! {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        @ExceptionHandler(SQLException.class)
        public ResponseEntity<Response> handleException (SQLException e){
            log.info("SQLException! {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        @ExceptionHandler
        @ResponseStatus(HttpStatus.BAD_REQUEST)
        public ErrorResponse handleException(final BadRequestException e) {
            log.error("BAD_REQUEST", e);
            return new ErrorResponse(e.getMessage());
        }
    }
