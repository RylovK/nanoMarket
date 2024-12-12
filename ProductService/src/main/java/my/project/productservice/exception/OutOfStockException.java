package my.project.productservice.exception;

import lombok.Getter;

@Getter
public class OutOfStockException extends RuntimeException {

    private final String message;

    public OutOfStockException(String message) {
        this.message = message;
    }
}
