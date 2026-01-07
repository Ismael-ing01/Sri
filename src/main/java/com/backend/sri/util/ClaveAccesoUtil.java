package com.backend.sri.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class ClaveAccesoUtil {

    /**
     * Genera la Clave de Acceso de 49 dígitos.
     * Estructura:
     * Fecha Emisión (8) + Tipo Comprobante (2) + RUC (13) + Ambiente (1) +
     * Serie (6) + Secuencial (9) + Código Numérico (8) + Tipo Emisión (1) + Dígito
     * Verificador (1)
     */
    public static String generarClaveAcceso(Date fechaEmision, String tipoComprobante, String ruc,
            String ambiente, String serie, String secuencial) {

        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
        String fecha = sdf.format(fechaEmision);

        // Código Numérico (8 dígitos): Para simplificar usaremos el secuencial
        // formateado al revés o un random.
        // El SRI dice que puede ser cualquier número. Usaremos un random seguro.
        String codigoNumerico = generarCodigoNumerico();

        // Tipo de Emisión (1): Normal
        String tipoEmision = "1";

        StringBuilder clave = new StringBuilder();
        clave.append(fecha);
        clave.append(tipoComprobante);
        clave.append(ruc);
        clave.append(ambiente);
        clave.append(serie); // Establecimiento + Punto Emisión
        clave.append(secuencial);
        clave.append(codigoNumerico);
        clave.append(tipoEmision);

        // Calcular Dígito Verificador (Módulo 11)
        int digitoVerificador = generarDigitoVerificador(clave.toString());

        clave.append(digitoVerificador);

        return clave.toString();
    }

    private static String generarCodigoNumerico() {
        // Genera 8 dígitos random
        Random r = new Random();
        int n = 10000000 + r.nextInt(90000000);
        return String.valueOf(n);
    }

    /**
     * Algoritmo Módulo 11 para obtener el dígito verificador.
     */
    private static int generarDigitoVerificador(String cadena) {
        int factor = 2;
        int suma = 0;

        // Recorrer la cadena de derecha a izquierda
        for (int i = cadena.length() - 1; i >= 0; i--) {
            int digito = Integer.parseInt(String.valueOf(cadena.charAt(i)));
            suma += (digito * factor);
            factor++;
            if (factor > 7) {
                factor = 2;
            }
        }

        int residuo = suma % 11;
        int digitoVerificador = 11 - residuo;

        if (digitoVerificador == 11) {
            return 0;
        } else if (digitoVerificador == 10) {
            return 1;
        }

        return digitoVerificador;
    }
}
