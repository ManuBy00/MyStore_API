package com.mby.myStore.Utils;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

public class Utils {

    /**
     * valida el correo con una regex
     * @param correo
     * @return true si el formato es correcto
     */
    public static boolean validarCorreo(String correo){
        String regex = "[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}";
        return correo.matches(regex);
    }

}
