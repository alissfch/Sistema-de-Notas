package com.integrador.SistemaDeNotas.servicio;

import java.awt.Color;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Service;

import com.integrador.SistemaDeNotas.controlador.DocenteControlador.FilaReporteAsistenciaDTO;
import com.integrador.SistemaDeNotas.controlador.DocenteControlador.FilaReporteNotaDTO;
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

}