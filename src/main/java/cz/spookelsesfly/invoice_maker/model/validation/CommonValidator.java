package cz.spookelsesfly.invoice_maker.model.validation;

import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Component
public final class CommonValidator {

    public <T, E extends RuntimeException> T requireNotNull(T value, Supplier<E> exceptionSupplier) {
        if (value == null) {
            throw exceptionSupplier.get();
        }
        return value;
    }

    public <E extends RuntimeException> void requireRequired(String value, Supplier<E> exceptionSupplier) {
        if (value == null || value.trim().isEmpty()) {
            throw exceptionSupplier.get();
        }
    }

    public <E extends RuntimeException> void requirePositive(int value, Supplier<E> exceptionSupplier) {
        if (value < 1) {
            throw exceptionSupplier.get();
        }
    }

    public <E extends RuntimeException> void requireEmailLike(String email, Supplier<E> exceptionSupplier) {
        if (email == null || !email.contains("@")) {
            throw exceptionSupplier.get();
        }
    }

    public <E extends RuntimeException> void requireCompleteAddress(
            String firstLine,
            String secondLine,
            String state,
            Supplier<E> exceptionSupplier
    ) {
        if (isBlank(firstLine) || isBlank(secondLine) || isBlank(state)) {
            throw exceptionSupplier.get();
        }
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}