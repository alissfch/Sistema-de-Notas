package com.integrador.SistemaDeNotas.modelo.patrones;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import com.integrador.SistemaDeNotas.modelo.entidades.Alumno;
import com.integrador.SistemaDeNotas.modelo.entidades.Nota;

// 1. Le quitamos el 'public' a la interfaz. Ahora es visible en este paquete.
interface Comando {
    String ejecutar();
}

// 2. Le ponemos el 'public' a esta clase porque coincide con el nombre del archivo.
public class ComandoCalcularPromedio implements Comando {
    
    // El IDE te sugiere ponerle 'final' (es una buena práctica en Java para 
    // variables que no van a cambiar después de que se crea el objeto).
    private final Alumno estudiante;

    public ComandoCalcularPromedio(Alumno estudiante) {
        this.estudiante = estudiante;
    }

    @Override
    public String ejecutar() {
        List<Nota> notas = estudiante.getNotas();
        
        if (notas == null || notas.isEmpty()) {
            return "El estudiante " + estudiante.getNombres() + " no tiene notas.";
        }

        BigDecimal suma = BigDecimal.ZERO;
        for (Nota nota : notas) {
            suma = suma.add(nota.getValor());
        }

        BigDecimal promedio = suma.divide(new BigDecimal(notas.size()), 2, RoundingMode.HALF_UP);
        return String.format("Promedio de %s: %s", estudiante.getNombres(), promedio.toString());
    }
}

// 3. El Invoker también se queda aquí sin 'public'
class Invoker {
    private List<String> historial = new ArrayList<>();

    public String ejecutarComando(Comando comando) {
        String resultado = comando.ejecutar();
        historial.add(resultado);
        return resultado;
    }

    public List<String> getHistorial() {
        return historial;
    }
}