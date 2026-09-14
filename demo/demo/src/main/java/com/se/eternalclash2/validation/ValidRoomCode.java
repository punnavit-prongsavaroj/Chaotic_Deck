package com.se.eternalclash2.validation;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;
@Documented
@Constraint(validatedBy = {})
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidRoomCode {
    String message() default "Invalid room code format";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
