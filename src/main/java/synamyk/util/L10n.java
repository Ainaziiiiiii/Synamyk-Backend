package synamyk.util;

/**
 * Localization helper: picks the right translation field.
 * Falls back to Russian (ru) when KY value is null or blank.
 */
public final class L10n {

    private L10n() {}

    /**
     * Returns {@code ky} if lang is "KY" and ky is not blank, otherwise returns {@code ru}.
     */
    public static String pick(String ru, String ky, String lang) {
        if ("KY".equalsIgnoreCase(lang) && ky != null && !ky.isBlank()) {
            return ky;
        }
        return ru;
    }
}