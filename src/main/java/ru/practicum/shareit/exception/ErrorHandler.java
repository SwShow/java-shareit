    package ru.practicum.shareit.exception;

    import org.apache.catalina.connector.Response;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.ControllerAdvice;
    import org.springframework.web.bind.annotation.ExceptionHandler;
    import lombok.extern.slf4j.Slf4j;

    import java.sql.SQLException;
    import java.util.NoSuchElementException;

    @Slf4j
    @ControllerAdvice
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
    }
