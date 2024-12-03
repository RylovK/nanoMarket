package my.project.productservice.exception;

import jakarta.validation.ValidationException;
import org.springframework.validation.BindingResult;

public class ValidationErrorException extends ValidationException {

    private final BindingResult bindingResult;

    public ValidationErrorException(BindingResult bindingResult) {
        this.bindingResult = bindingResult;
    }
}
