package synamyk.util;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import synamyk.repo.UserRepository;

/**
 * Resolves a user's preferred interface language from their profile.
 * Returns "RU" as default when user is not found or has no preference set.
 */
@Component
@RequiredArgsConstructor
public class LangResolver {

    private final UserRepository userRepository;

    public String resolve(UserDetails userDetails) {
        if (userDetails == null) return "RU";
        return userRepository.findByPhone(userDetails.getUsername())
                .map(u -> u.getLanguage() != null ? u.getLanguage() : "RU")
                .orElse("RU");
    }
}