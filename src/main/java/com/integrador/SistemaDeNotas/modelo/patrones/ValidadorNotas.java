package com.integrador.SistemaDeNotas.modelo.patrones;

import java.math.BigDecimal;

public abstract class ValidadorNotas {
    protected ValidadorNotas siguiente;

    public ValidadorNotas(ValidadorNotas siguiente) {
        this.siguiente = siguiente;
    }

    public String procesar(BigDecimal nota) {
        if (!validar(nota)) {
            return mensajeError();
        }
        return (siguiente != null) ? siguiente.procesar(nota) : "Aprobado";
    }

    protected abstract boolean validar(BigDecimal nota);
    protected abstract String mensajeError();

    public static ValidadorNotas obtenerCadenaDeValidacion() {

        return new ValidadorMaximo(
                new ValidadorMinimo(
                        new ValidadorExcelencia(null)
                )
        );
    }
}

class ValidadorMinimo extends ValidadorNotas {
    public ValidadorMinimo(ValidadorNotas siguiente) { super(siguiente); }
    @Override protected boolean validar(BigDecimal nota) {
        return nota.compareTo(new BigDecimal("10.00")) >= 0; 
    }
    @Override protected String mensajeError() { return "Reprobado - Nota mínima no alcanzada"; }
}

class ValidadorMaximo extends ValidadorNotas {
    public ValidadorMaximo(ValidadorNotas siguiente) { super(siguiente); }
    @Override protected boolean validar(BigDecimal nota) {
        return nota.compareTo(new BigDecimal("20.00")) <= 0;
    }
    @Override protected String mensajeError() { return "Error - La nota supera el máximo"; }
}

class ValidadorExcelencia extends ValidadorNotas {
    public ValidadorExcelencia(ValidadorNotas siguiente) { super(siguiente); }
    @Override protected boolean validar(BigDecimal nota) {
        return nota.compareTo(new BigDecimal("16.00")) >= 0;
    }
    @Override protected String mensajeError() { return "Aprobado pero sin excelencia"; }
}