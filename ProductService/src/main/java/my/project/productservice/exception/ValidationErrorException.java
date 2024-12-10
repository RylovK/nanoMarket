package my.project.productservice.exception;

import jakarta.validation.ValidationException;
import lombok.Getter;
import org.springframework.validation.BindingResult;

@Getter
public class ValidationErrorException extends ValidationException {

    private final BindingResult bindingResult;

    public ValidationErrorException(BindingResult bindingResult) {
        this.bindingResult = bindingResult;
    }
}
