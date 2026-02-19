package com.teachsync.utils;


import org.mindrot.jbcrypt.BCrypt;

public abstract class PasswordUtils {

    public static String hash(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(5));
    }

    public static boolean verify(String password, String hash) {
        return BCrypt.checkpw(password, hash);
    }
}

