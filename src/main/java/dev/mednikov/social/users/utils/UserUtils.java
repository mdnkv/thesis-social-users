package dev.mednikov.social.users.utils;

import org.apache.commons.codec.digest.DigestUtils;

public final class UserUtils {

    public static String getGravatarUrl(String email) {
        String normalizedEmail = email.trim().toLowerCase();
        String hash = DigestUtils.sha256Hex(normalizedEmail);
        return "https://www.gravatar.com/avatar/" + hash;
    }

}
