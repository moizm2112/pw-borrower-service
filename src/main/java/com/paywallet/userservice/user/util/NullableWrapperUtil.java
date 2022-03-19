package com.paywallet.userservice.user.util;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.Supplier;

@Data
@Component
public class NullableWrapperUtil {

    public static <T> Optional<T> resolve(Supplier<T> resolver) {
        try {
            T result = resolver.get();
            return Optional.ofNullable(result);
        }
        catch (NullPointerException e) {
            return Optional.empty();
        }
    }
}
