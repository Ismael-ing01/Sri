package com.backend.sri.util;

import com.backend.sri.model.enums.TipoIdentificacion;
import org.springframework.util.StringUtils;

public class ValidadorIdentificacion {

    /**
     * Valida la identificación según el tipo seleccionado.
     * Retorna null si es válido, o el mensaje de error si es inválido.
     */
    public static String validar(String identificacion, TipoIdentificacion tipo) {
        if (!StringUtils.hasText(identificacion)) {
            return "La identificación no puede estar vacía";
        }

        switch (tipo) {
            case CEDULA:
                return validarCedula(identificacion) ? null : "Cédula inválida";
            case RUC:
                return validarRuc(identificacion) ? null : "RUC inválido";
            case PASAPORTE:
                // Pasaporte: Mínimo 5 caracteres, alfanumérico
                return (identificacion.length() >= 5) ? null : "Pasaporte inválido (muy corto)";
            case CONSUMIDOR_FINAL:
                return "9999999999999".equals(identificacion) ? null : "Consumidor final debe ser 9999999999999";
            default:
                return null; // Otros tipos no tienen validación estricta por ahora
        }
    }

    private static boolean validarCedula(String cedula) {
        if (cedula.length() != 10 || !cedula.matches("\\d+"))
            return false;

        int provincia = Integer.parseInt(cedula.substring(0, 2));
        if (provincia < 1 || provincia > 24)
            return false;

        int tercerDigito = Integer.parseInt(cedula.substring(2, 3));
        if (tercerDigito >= 6)
            return false; // Para cédula debe ser menor a 6

        return validarModulo10(cedula.substring(0, 9), Integer.parseInt(cedula.substring(9)));
    }

    private static boolean validarRuc(String ruc) {
        if (ruc.length() != 13 || !ruc.matches("\\d+"))
            return false;

        String base = ruc.substring(0, 10);
        String establecimiento = ruc.substring(10);
        if ("000".equals(establecimiento))
            return false;

        int tercerDigito = Integer.parseInt(ruc.substring(2, 3));

        if (tercerDigito < 6) {
            // RUC Persona Natural (mismas reglas que cédula + 001/etc)
            return validarModulo10(base.substring(0, 9), Integer.parseInt(base.substring(9)));
        } else if (tercerDigito == 6) {
            // RUC Sociedad Pública (Modulo 11, digito verificador en la posicion 9)
            return validarModulo11(ruc.substring(0, 8), Integer.parseInt(ruc.substring(8, 9)), 321);
        } else if (tercerDigito == 9) {
            // RUC Sociedad Privada (Modulo 11, digito verificador en la posicion 10)
            return validarModulo11(ruc.substring(0, 9), Integer.parseInt(ruc.substring(9, 10)), 432);
        }

        return false;
    }

    private static boolean validarModulo10(String base, int verificador) {
        int total = 0;
        int[] coeficientes = { 2, 1, 2, 1, 2, 1, 2, 1, 2 };

        for (int i = 0; i < base.length(); i++) {
            int valor = Character.getNumericValue(base.charAt(i)) * coeficientes[i];
            total += (valor > 9) ? valor - 9 : valor;
        }

        int digitoValiddaoor = (total % 10 == 0) ? 0 : 10 - (total % 10);
        return digitoValiddaoor == verificador;
    }

    /**
     * Validación Módulo 11
     * tipo 432: Coeficientes 4,3,2,7,6,5,4,3,2 (Sociedad Privada)
     * tipo 321: Coeficientes 3,2,7,6,5,4,3,2 (Sociedad Pública)
     */
    private static boolean validarModulo11(String base, int verificador, int tipo) {
        int total = 0;
        int[] coeficientes;

        if (tipo == 432) {
            coeficientes = new int[] { 4, 3, 2, 7, 6, 5, 4, 3, 2 };
        } else {
            coeficientes = new int[] { 3, 2, 7, 6, 5, 4, 3, 2 };
        }

        for (int i = 0; i < base.length(); i++) {
            total += Character.getNumericValue(base.charAt(i)) * coeficientes[i];
        }

        int residuo = total % 11;
        int resultado = (residuo == 0) ? 0 : 11 - residuo;

        return resultado == verificador;
    }
}
