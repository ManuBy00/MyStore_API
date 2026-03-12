package com.mby.myStore.Utils;

import org.mindrot.jbcrypt.BCrypt;

public class HashPsw {
    // Hashea una contraseña
    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    // Verifica si una contraseña coincide con su hash
    public static boolean checkPassword(String passwordTextoPlano, String passwordHash) {
        if (passwordTextoPlano == null || passwordHash == null) {
            return false;
        }

        try {
            boolean pass;
            pass = BCrypt.checkpw(passwordTextoPlano, passwordHash);
            if (!pass) {
                System.out.println("error en la contraseña");
            }
            return pass;

        } catch (IllegalArgumentException e) {
            System.err.println("Error al verificar contraseña: El hash no es válido.");
            return false;
        }
    }
}
