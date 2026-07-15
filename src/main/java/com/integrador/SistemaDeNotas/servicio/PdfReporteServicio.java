package com.integrador.SistemaDeNotas.servicio;

import java.awt.Color;
import java.io.IOException;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.integrador.SistemaDeNotas.controlador.AlumnoControlador.FilaCursoAlumnoDTO;
import com.integrador.SistemaDeNotas.controlador.DocenteControlador.FilaReporteAsistenciaDTO;
import com.integrador.SistemaDeNotas.controlador.DocenteControlador.FilaReporteNotaDTO;
import com.integrador.SistemaDeNotas.controlador.VistasControlador.FilaIndicadorCursoDTO;
import com.integrador.SistemaDeNotas.controlador.VistasControlador.IndicadoresDTO;
import com.integrador.SistemaDeNotas.modelo.entidades.Alumno;
import com.integrador.SistemaDeNotas.modelo.entidades.Curso;
import com.integrador.SistemaDeNotas.modelo.entidades.Evaluacion;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;

import jakarta.servlet.http.HttpServletResponse;

@Service
public class PdfReporteServicio {

    private void configurarRespuesta(HttpServletResponse response, String nombreArchivo) {
        response.setContentType("application/pdf");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=" + nombreArchivo + "_" + System.currentTimeMillis() + ".pdf";
        response.setHeader(headerKey, headerValue);
    }

    private void agregarEncabezadoDocumento(Document document, String tituloReporte) {
        Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.BLACK);
        Font fontSubtitulo = FontFactory.getFont(FontFactory.HELVETICA, 12, Color.DARK_GRAY);

        Paragraph titulo = new Paragraph("Sistema Académico JCM", fontTitulo);
        titulo.setAlignment(Element.ALIGN_CENTER);
        document.add(titulo);

        Paragraph subtitulo = new Paragraph(tituloReporte, fontSubtitulo);
        subtitulo.setAlignment(Element.ALIGN_CENTER);
        subtitulo.setSpacingAfter(10);
        document.add(subtitulo);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        Paragraph fecha = new Paragraph("Fecha de generación: " + dtf.format(LocalDateTime.now()),
                FontFactory.getFont(FontFactory.HELVETICA, 10));
        fecha.setAlignment(Element.ALIGN_RIGHT);
        fecha.setSpacingAfter(20);
        document.add(fecha);
    }

    private void agregarCeldaEncabezado(PdfPTable table, String texto) {
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(new Color(28, 75, 39)); // Verde Institucional
        cell.setPadding(6);
        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE);
        cell.setPhrase(new Phrase(texto != null ? texto : "", font));
        table.addCell(cell);
    }

    private void agregarCelda(PdfPTable table, String texto) {
        PdfPCell cell = new PdfPCell();
        cell.setPadding(5);
        Font font = FontFactory.getFont(FontFactory.HELVETICA, 9, Color.BLACK);
        cell.setPhrase(new Phrase(texto != null ? texto : "", font));
        table.addCell(cell);
    }

    class FooterEvento extends PdfPageEventHelper {
        Font font = FontFactory.getFont(FontFactory.HELVETICA, 8, Color.GRAY);

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            PdfPTable table = new PdfPTable(2);
            table.setTotalWidth(document.right() - document.left());

            PdfPCell cell1 = new PdfPCell(new Paragraph("Sistema Académico JCM", font));
            cell1.setBorder(0);

            PdfPCell cell2 = new PdfPCell(new Paragraph("Página " + writer.getPageNumber(), font));
            cell2.setBorder(0);
            cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell2.setVerticalAlignment(Element.ALIGN_MIDDLE);

            table.addCell(cell1);
            table.addCell(cell2);
            table.writeSelectedRows(0, -1, document.left(), document.bottom() - 10, writer.getDirectContent());
        }
    }

    public void exportarReporteNotas(HttpServletResponse response, Curso curso, List<Evaluacion> evaluacionesActivas,
            List<FilaReporteNotaDTO> filas) throws IOException {
        configurarRespuesta(response, "Reporte_Notas");
        Document document = new Document(PageSize.A4.rotate());
        PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream());
        writer.setPageEvent(new FooterEvento());
        document.open();

        String titulo = "Reporte de Notas"
                + (curso != null ? " - " + curso.getNombreCurso() + " (Sec. " + curso.getSeccion() + ")" : "");
        agregarEncabezadoDocumento(document, titulo);

        int numCols = 4 + (evaluacionesActivas != null ? evaluacionesActivas.size() : 0);
        PdfPTable table = new PdfPTable(numCols);
        table.setWidthPercentage(100f);

        agregarCeldaEncabezado(table, "Nº");
        agregarCeldaEncabezado(table, "Alumno");
        if (evaluacionesActivas != null) {
            for (Evaluacion ev : evaluacionesActivas) {
                agregarCeldaEncabezado(table, ev.getNombre() + " (" + ev.getPesoPorcentual() + "%)");
            }
        }
        agregarCeldaEncabezado(table, "Promedio");
        agregarCeldaEncabezado(table, "Estado");

        int count = 1;
        for (FilaReporteNotaDTO fila : filas) {
            agregarCelda(table, String.valueOf(count++));
            agregarCelda(table, fila.getAlumno().getApellidos() + ", " + fila.getAlumno().getNombres());
            if (fila.getValores() != null) {
                for (java.math.BigDecimal valor : fila.getValores()) {
                    agregarCelda(table, valor != null ? valor.toString() : "--");
                }
            }
            agregarCelda(table, fila.getPromedio() != null ? fila.getPromedio().toString() : "--");
            agregarCelda(table, fila.getEstado());
        }

        document.add(table);
        document.close();
    }

    public void exportarReporteAsistencia(HttpServletResponse response, Curso curso, String fInicio, String fFin,
            List<FilaReporteAsistenciaDTO> filas) throws IOException {
        configurarRespuesta(response, "Reporte_Asistencia");
        Document document = new Document(PageSize.A4.rotate());
        PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream());
        writer.setPageEvent(new FooterEvento());
        document.open();

        String titulo = "Reporte de Asistencia"
                + (curso != null ? " - " + curso.getNombreCurso() + " (Sec. " + curso.getSeccion() + ")" : "");
        if (fInicio != null && !fInicio.isEmpty() && fFin != null && !fFin.isEmpty()) {
            titulo += " | Del " + fInicio + " al " + fFin;
        }
        agregarEncabezadoDocumento(document, titulo);

        PdfPTable table = new PdfPTable(8);
        table.setWidthPercentage(100f);
        table.setWidths(new float[] { 1f, 4f, 1.5f, 1.5f, 1.5f, 2f, 2f, 2f });
        String[] cabeceras = { "Nº", "Alumno", "Asistió", "Tardanzas", "Faltas", "Justificadas", "% Asist.", "Estado" };
        for (String cab : cabeceras)
            agregarCeldaEncabezado(table, cab);

        int count = 1;
        for (FilaReporteAsistenciaDTO fila : filas) {
            agregarCelda(table, String.valueOf(count++));
            agregarCelda(table, fila.getAlumno().getApellidos() + ", " + fila.getAlumno().getNombres());
            agregarCelda(table, String.valueOf(fila.getAsistio()));
            agregarCelda(table, String.valueOf(fila.getTardanza()));
            agregarCelda(table, String.valueOf(fila.getFalta()));
            agregarCelda(table, String.valueOf(fila.getJustificada()));
            agregarCelda(table, fila.getPorcentaje() != null ? fila.getPorcentaje() + "%" : "--");
            agregarCelda(table, fila.getEstado());
        }

        document.add(table);
        document.close();
    }

    public void exportarReporteEliminados(HttpServletResponse response, List<Evaluacion> eliminados)
            throws IOException {
        configurarRespuesta(response, "Auditoria_Eliminados");
        Document document = new Document(PageSize.A4);
        PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream());
        writer.setPageEvent(new FooterEvento());
        document.open();

        agregarEncabezadoDocumento(document, "Auditoría de Eliminación Lógica");

        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100f);
        String[] cabeceras = { "ID", "Curso", "Docente", "Evaluación", "Fecha", "Estado" };
        for (String cab : cabeceras)
            agregarCeldaEncabezado(table, cab);

        for (Evaluacion ev : eliminados) {
            agregarCelda(table, String.valueOf(ev.getIdEvaluacion()));
            agregarCelda(table, ev.getCurso().getNombreCurso() + " (Sec. " + ev.getCurso().getSeccion() + ")");
            agregarCelda(table, ev.getCurso().getDocente().getUsuario().getNombre() + " "
                    + ev.getCurso().getDocente().getUsuario().getApellido());
            agregarCelda(table, ev.getNombre() + " - " + ev.getTipo());
            agregarCelda(table, ev.getFecha().toString());
            agregarCelda(table, "Eliminado");
        }
        document.add(table);
        document.close();
    }

    public void exportarReporteNotasAlumno(HttpServletResponse response, Alumno alumno,
            List<FilaCursoAlumnoDTO> filas, java.math.BigDecimal promedioGeneral,
            java.math.BigDecimal notaMaxGlobal, java.math.BigDecimal notaMinGlobal, int totalCursos)
            throws IOException {

        configurarRespuesta(response,
                "Reporte_Alumno_" + (alumno.getCodigo() != null ? alumno.getCodigo() : "SIN_COD"));
        Document document = new Document(PageSize.A4);
        PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream());
        writer.setPageEvent(new FooterEvento());
        document.open();

        agregarEncabezadoDocumento(document, "Reporte General de Notas");

        Font fontInfo = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.BLACK);
        Font fontBold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.BLACK);

        // Información del Alumno
        Paragraph infoAlumno = new Paragraph();
        infoAlumno.add(new Phrase("Alumno: ", fontBold));
        infoAlumno.add(new Phrase(alumno.getApellidos() + ", " + alumno.getNombres() + "\n", fontInfo));
        infoAlumno.add(new Phrase("Código Institucional: ", fontBold));
        infoAlumno.add(new Phrase(alumno.getCodigo() != null ? alumno.getCodigo() : "N/A", fontInfo));
        infoAlumno.setSpacingAfter(15);
        document.add(infoAlumno);

        // Tabla de Notas
        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100f);
        table.setWidths(new float[] { 3f, 1.5f, 1.5f, 1.5f, 2f });

        agregarCeldaEncabezado(table, "Curso");
        agregarCeldaEncabezado(table, "Promedio");
        agregarCeldaEncabezado(table, "Nota Máxima");
        agregarCeldaEncabezado(table, "Nota Mínima");
        agregarCeldaEncabezado(table, "Estado");

        for (FilaCursoAlumnoDTO fila : filas) {
            agregarCelda(table, fila.getCurso());
            agregarCelda(table, fila.getPromedio() != null ? fila.getPromedio().toString() : "--");
            agregarCelda(table, fila.getNotaMaxima() != null ? fila.getNotaMaxima().toString() : "--");
            agregarCelda(table, fila.getNotaMinima() != null ? fila.getNotaMinima().toString() : "--");
            agregarCelda(table, fila.getEstado());
        }
        document.add(table);
        document.add(new Paragraph("\n"));

        // Tabla de Resumen
        PdfPTable summaryTable = new PdfPTable(2);
        summaryTable.setWidthPercentage(50f);
        summaryTable.setHorizontalAlignment(Element.ALIGN_LEFT);

        agregarCeldaEncabezado(summaryTable, "Resumen General");
        agregarCeldaEncabezado(summaryTable, "Valor");

        agregarCelda(summaryTable, "Promedio General");
        agregarCelda(summaryTable, promedioGeneral != null ? promedioGeneral.toString() : "--");

        agregarCelda(summaryTable, "Nota Más Alta");
        agregarCelda(summaryTable, notaMaxGlobal != null ? notaMaxGlobal.toString() : "--");

        agregarCelda(summaryTable, "Nota Más Baja");
        agregarCelda(summaryTable, notaMinGlobal != null ? notaMinGlobal.toString() : "--");

        agregarCelda(summaryTable, "Cursos Matriculados");
        agregarCelda(summaryTable, String.valueOf(totalCursos));

        document.add(summaryTable);
        document.close();
    }

    // Pequeño método auxiliar para formatear celdas de las métricas.
    private void agregarCeldaDetalle(PdfPTable table, String texto, boolean esNegrita) {
        PdfPCell cell = new PdfPCell();
        cell.setPadding(5);
        Font font = FontFactory.getFont(esNegrita ? FontFactory.HELVETICA_BOLD : FontFactory.HELVETICA, 9, Color.BLACK);
        cell.setPhrase(new Phrase(texto != null ? texto : "", font));
        table.addCell(cell);
    }

    public void exportarReporteIndicadores(HttpServletResponse response, IndicadoresDTO datos, String nombreAdmin)
            throws IOException {
        configurarRespuesta(response, "Reporte_Indicadores");
        Document document = new Document(PageSize.A4);
        PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream());
        writer.setPageEvent(new FooterEvento());
        document.open();

        agregarEncabezadoDocumento(document, "REPORTE DE INDICADORES ACADÉMICOS");

        Font fontNormal = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.BLACK);
        Font fontSubTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, new Color(28, 75, 39));

        Paragraph adminInfo = new Paragraph("Generado por: " + nombreAdmin, fontNormal);
        adminInfo.setSpacingAfter(15);
        document.add(adminInfo);

        // INDICADOR 1
        document.add(new Paragraph("1. Tiempo promedio de registro de notas", fontSubTitulo));
        PdfPTable t1 = new PdfPTable(2);
        t1.setWidthPercentage(100f);
        t1.setWidths(new float[] { 3f, 7f });
        t1.setSpacingBefore(5f);
        t1.setSpacingAfter(15f);
        agregarCeldaDetalle(t1, "Descripción", true);
        agregarCeldaDetalle(t1,
                "Mide el tiempo promedio que tardan los docentes en registrar las notas desde la fecha programada de evaluación.",
                false);
        agregarCeldaDetalle(t1, "Fórmula", true);
        agregarCeldaDetalle(t1, "Sumatoria de días de diferencia / Total de notas registradas", false);
        agregarCeldaDetalle(t1, "Resultado", true);
        agregarCeldaDetalle(t1, datos.promedioTiempo + " días", false);
        agregarCeldaDetalle(t1, "Objetivo", true);
        agregarCeldaDetalle(t1, "< 1 día", false);
        agregarCeldaDetalle(t1, "Estado Actual", true);
        agregarCeldaDetalle(t1, datos.estadoTextoTiempo, false);
        document.add(t1);

        // INDICADOR 2
        document.add(new Paragraph("2. Porcentaje de errores en consolidación", fontSubTitulo));
        PdfPTable t2 = new PdfPTable(2);
        t2.setWidthPercentage(100f);
        t2.setWidths(new float[] { 3f, 7f });
        t2.setSpacingBefore(5f);
        t2.setSpacingAfter(15f);
        agregarCeldaDetalle(t2, "Total Notas en Sistema", true);
        agregarCeldaDetalle(t2, String.valueOf(datos.totalNotas), false);
        agregarCeldaDetalle(t2, "Notas Corregidas", true);
        agregarCeldaDetalle(t2, String.valueOf(datos.notasCorregidas), false);
        agregarCeldaDetalle(t2, "Fórmula", true);
        agregarCeldaDetalle(t2, "(Notas Corregidas / Total Notas) * 100", false);
        agregarCeldaDetalle(t2, "Porcentaje Obtenido", true);
        agregarCeldaDetalle(t2, datos.porcentajeErrores + "%", false);
        agregarCeldaDetalle(t2, "Estado Actual", true);
        agregarCeldaDetalle(t2, datos.estadoTextoErrores, false);
        document.add(t2);

        // INDICADOR 3
        document.add(new Paragraph("3. Disponibilidad de reportes académicos", fontSubTitulo));
        PdfPTable t3 = new PdfPTable(2);
        t3.setWidthPercentage(100f);
        t3.setWidths(new float[] { 3f, 7f });
        t3.setSpacingBefore(5f);
        t3.setSpacingAfter(15f);
        agregarCeldaDetalle(t3, "Reportes Esperados", true);
        agregarCeldaDetalle(t3, String.valueOf(datos.reportesEsperados), false);
        agregarCeldaDetalle(t3, "Reportes Implementados", true);
        agregarCeldaDetalle(t3, String.valueOf(datos.reportesImplementados), false);
        agregarCeldaDetalle(t3, "Fórmula", true);
        agregarCeldaDetalle(t3, "(Implementados / Esperados) * 100", false);
        agregarCeldaDetalle(t3, "Porcentaje", true);
        agregarCeldaDetalle(t3, datos.disponibilidadReportes + "%", false);
        agregarCeldaDetalle(t3, "Estado Actual", true);
        agregarCeldaDetalle(t3, datos.estadoTextoReportes, false);
        document.add(t3);

        // INDICADOR 4
        document.add(new Paragraph("4. Porcentaje de alumnos aprobados por curso", fontSubTitulo));
        PdfPTable t4 = new PdfPTable(5);
        t4.setWidthPercentage(100f);
        t4.setWidths(new float[] { 4f, 1.5f, 1.5f, 1.5f, 1.5f });
        t4.setSpacingBefore(10f);

        agregarCeldaEncabezado(t4, "Curso Asignado");
        agregarCeldaEncabezado(t4, "Total Alumnos");
        agregarCeldaEncabezado(t4, "Aprobados");
        agregarCeldaEncabezado(t4, "Desaprobados");
        agregarCeldaEncabezado(t4, "% Aprobados");

        java.math.BigDecimal sumaAprobacion = java.math.BigDecimal.ZERO;
        for (FilaIndicadorCursoDTO f : datos.tablaCursos) {
            agregarCelda(t4, f.getNombreCurso() + " - " + f.getSeccion());
            agregarCelda(t4, String.valueOf(f.getTotalAlumnos()));
            agregarCelda(t4, String.valueOf(f.getAprobados()));
            agregarCelda(t4, String.valueOf(f.getDesaprobados()));
            agregarCelda(t4, f.getPorcentajeAprobados() + "%");
            sumaAprobacion = sumaAprobacion.add(f.getPorcentajeAprobados());
        }
        document.add(t4);

        int totalCursos = datos.tablaCursos.size();
        java.math.BigDecimal promGeneralAprob = totalCursos > 0
                ? sumaAprobacion.divide(new java.math.BigDecimal(totalCursos), 2, RoundingMode.HALF_UP)
                : java.math.BigDecimal.ZERO;

        Paragraph p4Foot = new Paragraph(
                "Cursos Evaluados: " + totalCursos + " | Promedio General de Aprobación: " + promGeneralAprob + "%",
                fontNormal);
        p4Foot.setSpacingAfter(15f);
        document.add(p4Foot);

        // INDICADOR 5
        document.add(new Paragraph("5. Notas registradas dentro del plazo", fontSubTitulo));
        PdfPTable t5 = new PdfPTable(2);
        t5.setWidthPercentage(100f);
        t5.setWidths(new float[] { 3f, 7f });
        t5.setSpacingBefore(5f);
        t5.setSpacingAfter(15f);
        agregarCeldaDetalle(t5, "Total de Notas", true);
        agregarCeldaDetalle(t5, String.valueOf(datos.totalNotas), false);
        agregarCeldaDetalle(t5, "Registradas a Tiempo", true);
        agregarCeldaDetalle(t5, String.valueOf(datos.notasATiempo), false);
        agregarCeldaDetalle(t5, "Fórmula", true);
        agregarCeldaDetalle(t5, "(A tiempo / Total) * 100", false);
        agregarCeldaDetalle(t5, "Porcentaje Obtenido", true);
        agregarCeldaDetalle(t5, datos.porcentajeAlDia + "%", false);
        agregarCeldaDetalle(t5, "Estado Actual", true);
        agregarCeldaDetalle(t5, datos.estadoTextoAlDia, false);
        document.add(t5);

        // INDICADOR 6
        document.add(new Paragraph("6. Nivel de Satisfacción Docente", fontSubTitulo));
        PdfPTable t6 = new PdfPTable(2);
        t6.setWidthPercentage(100f);
        t6.setWidths(new float[] { 3f, 7f });
        t6.setSpacingBefore(5f);
        t6.setSpacingAfter(15f);
        agregarCeldaDetalle(t6, "Total Encuestados", true);
        agregarCeldaDetalle(t6, String.valueOf(datos.totalEncuestas), false);
        agregarCeldaDetalle(t6, "Promedio Puntos", true);
        agregarCeldaDetalle(t6, datos.promedioSatisfaccion + " / 5.0", false);
        agregarCeldaDetalle(t6, "Porcentaje", true);
        agregarCeldaDetalle(t6, datos.porcentajeSatisfaccion + "%", false);
        agregarCeldaDetalle(t6, "Estado Actual", true);
        agregarCeldaDetalle(t6, datos.estadoTextoSatisfaccion, false);
        document.add(t6);

        // RESUMEN GENERAL (Nueva Página opcional para que se vea limpio)
        document.newPage();
        document.add(new Paragraph("RESUMEN GENERAL", fontSubTitulo));
        PdfPTable tRes = new PdfPTable(3);
        tRes.setWidthPercentage(100f);
        tRes.setWidths(new float[] { 5f, 2.5f, 2.5f });
        tRes.setSpacingBefore(10f);
        tRes.setSpacingAfter(15f);

        agregarCeldaEncabezado(tRes, "Indicador Estratégico");
        agregarCeldaEncabezado(tRes, "Resultado");
        agregarCeldaEncabezado(tRes, "Estado");

        agregarCelda(tRes, "Tiempo promedio de registro");
        agregarCelda(tRes, datos.promedioTiempo + " días");
        agregarCelda(tRes, datos.estadoTextoTiempo);
        agregarCelda(tRes, "Errores en consolidación");
        agregarCelda(tRes, datos.porcentajeErrores + "%");
        agregarCelda(tRes, datos.estadoTextoErrores);
        agregarCelda(tRes, "Disponibilidad de reportes");
        agregarCelda(tRes, datos.disponibilidadReportes + "%");
        agregarCelda(tRes, datos.estadoTextoReportes);
        agregarCelda(tRes, "Aprobación por curso");
        agregarCelda(tRes, promGeneralAprob + "%");
        agregarCelda(tRes,
                promGeneralAprob.compareTo(new java.math.BigDecimal("60")) >= 0 ? "Óptimo/Aceptable" : "Crítico");
        agregarCelda(tRes, "Notas registradas a tiempo");
        agregarCelda(tRes, datos.porcentajeAlDia + "%");
        agregarCelda(tRes, datos.estadoTextoAlDia);
        agregarCelda(tRes, "Satisfacción docente");
        agregarCelda(tRes, datos.porcentajeSatisfaccion + "%");
        agregarCelda(tRes, datos.estadoTextoSatisfaccion);

        document.add(tRes);

        // CONCLUSIONES DINAMICAS
        document.add(new Paragraph("CONCLUSIONES", fontSubTitulo));
        List<String> optimos = new ArrayList<>();
        List<String> criticos = new ArrayList<>();

        if ("Óptimo".equals(datos.estadoTextoTiempo))
            optimos.add("Tiempo de registro de notas");
        else if ("Crítico".equals(datos.estadoTextoTiempo))
            criticos.add("Tiempo de registro de notas");

        if ("Óptimo".equals(datos.estadoTextoErrores))
            optimos.add("Consolidación sin errores");
        else if ("Crítico".equals(datos.estadoTextoErrores))
            criticos.add("Errores en consolidación");

        if ("Óptimo".equals(datos.estadoTextoReportes))
            optimos.add("Disponibilidad de plataforma");
        else if ("Crítico".equals(datos.estadoTextoReportes))
            criticos.add("Disponibilidad de plataforma");

        if ("Óptimo".equals(datos.estadoTextoAlDia))
            optimos.add("Puntualidad docente");
        else if ("Crítico".equals(datos.estadoTextoAlDia))
            criticos.add("Puntualidad docente");

        if ("Óptimo".equals(datos.estadoTextoSatisfaccion))
            optimos.add("Alta satisfacción del profesorado");
        else if ("Crítico".equals(datos.estadoTextoSatisfaccion))
            criticos.add("Satisfacción del profesorado baja");

        String txtOptimos = optimos.isEmpty() ? "Ninguno superó las métricas esperadas este periodo."
                : String.join(", ", optimos) + ".";
        String txtCriticos = criticos.isEmpty() ? "Excelente trabajo, ningún indicador está en estado crítico."
                : String.join(", ", criticos)
                        + ". Se recomienda atención inmediata por parte de coordinación académica.";

        Paragraph pConclusion1 = new Paragraph("• Indicadores con cumplimiento Óptimo/Destacado: \n  " + txtOptimos,
                fontNormal);
        pConclusion1.setSpacingBefore(10f);
        Paragraph pConclusion2 = new Paragraph("• Indicadores en estado Crítico (Requieren mejora): \n  " + txtCriticos,
                fontNormal);
        pConclusion2.setSpacingBefore(5f);
        Paragraph pConclusion3 = new Paragraph(
                "• Observación General: \n  El promedio global de aprobación institucional se sitúa en un "
                        + promGeneralAprob
                        + "%. Se sugiere acompañamiento pedagógico a las secciones con mayor tasa de desaprobación identificadas en el cuadro superior.",
                fontNormal);
        pConclusion3.setSpacingBefore(5f);

        document.add(pConclusion1);
        document.add(pConclusion2);
        document.add(pConclusion3);

        document.close();
    }
}