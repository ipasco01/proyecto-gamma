/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyectogamma.utils;

/**
 *
 * @author Isabel
 */
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.layout.*;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.UnitValue;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;
import proyectogamma.controller.AlumnoController;
import proyectogamma.controller.CalificacionController;
import proyectogamma.model.Evaluacion;
import proyectogamma.controller.EvaluacionController;
import proyectogamma.controller.DocenteController;
import proyectogamma.model.Alumno;
import proyectogamma.model.Calificacion;
import proyectogamma.model.Docente;

public class PDFGenerator {

    public static void generarPDFExamenesPorAsignatura(int idAsignatura, String nombreAsignatura, int idProfesor) {
        String userDownloads = System.getProperty("user.home") + "/Downloads/";
        String filePath = userDownloads + "Examenes_" + nombreAsignatura + ".pdf"; // Guardar en Descargas

        try {
            // Obtener el nombre del profesor
            DocenteController docenteController = new DocenteController();
            Docente docente = docenteController.obtenerDocentePorId(idProfesor);
            String nombreProfesor = docente != null ? docente.getNombre() + " " + docente.getApellido() : "Desconocido";

            // Crear el documento PDF
            PdfWriter writer = new PdfWriter(new FileOutputStream(filePath));
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Título del documento
            Paragraph titulo = new Paragraph("Exámenes de la Asignatura: " + nombreAsignatura)
                    .setBold()
                    .setFontSize(14);
            document.add(titulo);

            // Subtítulo con el nombre del profesor
            Paragraph subtitulo = new Paragraph("Profesor: " + nombreProfesor)
                    .setFontSize(12);
            document.add(subtitulo);

            // Espaciado
            document.add(new Paragraph("\n"));

            // Crear una tabla para los datos
            Table table = new Table(new float[]{3, 2, 2}); // Columnas: Nombre, Peso, Fecha

            // Encabezados
            table.addHeaderCell(new Cell().add(new Paragraph("Nombre")).setBold());
            table.addHeaderCell(new Cell().add(new Paragraph("Peso (%)")).setBold());
            table.addHeaderCell(new Cell().add(new Paragraph("Fecha")).setBold());

            // Obtener los exámenes asociados a la asignatura
            EvaluacionController evaluacionController = new EvaluacionController();
            List<Evaluacion> evaluaciones = evaluacionController.obtenerEvaluacionesPorAsignatura(idAsignatura);

            // Agregar filas con los datos
            for (Evaluacion evaluacion : evaluaciones) {
                table.addCell(evaluacion.getNombre()); // Nombre del examen
                table.addCell(String.format("%.2f", evaluacion.getPeso())); // Peso con dos decimales
                table.addCell(new java.text.SimpleDateFormat("dd/MM/yyyy").format(evaluacion.getFecha())); // Fecha formateada
            }

            // Agregar la tabla al documento
            document.add(table);

            // Cerrar el documento
            document.close();

            System.out.println("PDF generado correctamente en: " + filePath);
            javax.swing.JOptionPane.showMessageDialog(null, "PDF generado correctamente en: " + filePath, "Éxito", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            System.out.println("Error al generar el PDF: " + e.getMessage());
            javax.swing.JOptionPane.showMessageDialog(null, "Error al generar el PDF: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void generarPDFNotasAlumno(String nombreAlumno, List<String[]> notas) {
        String filePath = System.getProperty("user.home") + "/Downloads/Notas_" + nombreAlumno + ".pdf";

        Document document = null;
        try {
            PdfWriter writer = new PdfWriter(new FileOutputStream(filePath));
            PdfDocument pdf = new PdfDocument(writer);
            document = new Document(pdf);

            // Título del documento
            Paragraph titulo = new Paragraph("Notas del Alumno: " + nombreAlumno)
                    .setBold()
                    .setFontSize(16);

            document.add(titulo);

            document.add(new Paragraph("\n")); // Espaciado

            // Crear una tabla para las notas
            Table table = new Table(UnitValue.createPercentArray(new float[]{4, 8, 4})); // Proporciones: 4-8-4
            table.setWidth(UnitValue.createPercentValue(100)); // Ancho total del 100%
            // Encabezados de la tabla
            table.addHeaderCell(new Cell().add(new Paragraph("Asignatura").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Nota").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Fecha").setBold()));

            // Agregar las notas como filas
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            for (String[] nota : notas) {
                table.addCell(new Cell().add(new Paragraph(nota[0]))); // Asignatura
                table.addCell(new Cell().add(new Paragraph(nota[1]))); // Nota
                table.addCell(new Cell().add(new Paragraph(nota[2]))); // Fecha
            }

            document.add(table); // Agregar la tabla al documento
        } catch (Exception e) {
            System.out.println("Error al generar el PDF: " + e.getMessage());
        } finally {
            if (document != null) {
                document.close();
            }
        }

    }

    public static void generarListaAsistenciaDiaria(int idAsignatura, String nombreAsignatura, int idDocente) {
        String filePath = System.getProperty("user.home") + "/Downloads/Lista_Asistencia_" + nombreAsignatura + ".pdf"; // Guardar en Descargas
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String fechaHoy = dateFormat.format(new Date());

        try {
            // Crear el documento PDF
            PdfWriter writer = new PdfWriter(new FileOutputStream(filePath));
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Título del documento
            Paragraph titulo = new Paragraph("Lista de Asistencia Diaria")
                    .setBold()
                    .setFontSize(16);
            document.add(titulo);

            // Espaciado
            document.add(new Paragraph("\n"));

            // Información adicional (fecha, asignatura y docente)
            DocenteController docenteController = new DocenteController();
            Docente docente = docenteController.obtenerDocentePorId(idDocente);
            String nombreDocente = docente != null ? docente.getNombre() + " " + docente.getApellido() : "Sin docente asignado";

            document.add(new Paragraph("Fecha: " + fechaHoy));
            document.add(new Paragraph("Asignatura: " + nombreAsignatura));
            document.add(new Paragraph("Docente: " + nombreDocente));

            // Espaciado
            document.add(new Paragraph("\n"));

            // Crear una tabla para los alumnos
            Table table = new Table(UnitValue.createPercentArray(new float[]{4, 8, 4})); // Proporciones: 4-8-4
            table.setWidth(UnitValue.createPercentValue(100)); // Ancho total del 100%

            // Encabezados
            table.addHeaderCell(new Cell().add(new Paragraph("ID")).setBold());
            table.addHeaderCell(new Cell().add(new Paragraph("Nombre Completo")).setBold());
            table.addHeaderCell(new Cell().add(new Paragraph("Firma")).setBold());

            // Obtener los alumnos asociados a la asignatura
            AlumnoController alumnoController = new AlumnoController();
            List<Alumno> alumnos = alumnoController.obtenerAlumnosPorAsignatura(idAsignatura);

            // Agregar filas con los datos de los alumnos
            for (Alumno alumno : alumnos) {
                table.addCell(String.valueOf(alumno.getId())); // ID del alumno
                table.addCell(alumno.getNombre() + " " + alumno.getApellido()); // Nombre completo
                table.addCell(""); // Celda en blanco para la firma
            }

            // Agregar la tabla al documento
            document.add(table);

            // Cerrar el documento
            document.close();

            System.out.println("PDF generado correctamente en: " + filePath);

        } catch (Exception e) {
            System.out.println("Error al generar el PDF: " + e.getMessage());
        }
    }

    public static void generarPDFCalificacionesAlumno(int idAlumno, String nombreAlumno, int idAsignatura, String nombreAsignatura) {
        String filePath = System.getProperty("user.home") + "/Downloads/Calificaciones_"
                + nombreAlumno.replace(" ", "_") + "_"
                + nombreAsignatura.replace(" ", "_") + ".pdf";

        try {
            // Crear el documento PDF
            PdfWriter writer = new PdfWriter(filePath);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Agregar título del documento
            Paragraph titulo = new Paragraph("Calificaciones de " + nombreAlumno + " en " + nombreAsignatura)
                    .setBold()
                    .setFontSize(14);
            document.add(titulo);

            // Espaciado
            document.add(new Paragraph("\n"));

            // Crear tabla para las calificaciones
            Table table = new Table(new float[]{4, 1, 2 ,3}); // Proporciones: Evaluación, Nota, Fecha
            table.setWidth(UnitValue.createPercentValue(100)); // Ancho total del 100%

            // Encabezados
            table.addHeaderCell(new Cell().add(new Paragraph("Evaluación").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Nota").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Peso").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Fecha").setBold()));

            // Obtener calificaciones del alumno para la asignatura
            CalificacionController calificacionController = new CalificacionController();
            List<Calificacion> calificaciones = calificacionController.obtenerCalificacionesPorAlumnoYAsignatura(idAlumno, idAsignatura);

            // Validar si hay calificaciones disponibles
            if (calificaciones.isEmpty()) {
                document.add(new Paragraph("No hay calificaciones disponibles para esta asignatura.")
                        .setItalic()
                        .setFontSize(12));
            } else {
                // Agregar filas con los datos
                for (Calificacion calificacion : calificaciones) {
                    table.addCell(calificacion.getTitulo() != null ? calificacion.getTitulo() : "N/A"); // Título de la evaluación
                    table.addCell(String.format("%.2f", calificacion.getNota())); // Nota
                    table.addCell(String.format("%.2f%%", calificacion.getPeso())); // Peso
                    table.addCell(new java.text.SimpleDateFormat("dd/MM/yyyy").format(calificacion.getFecha())); // Fecha
                }

                // Agregar la tabla al documento
                document.add(table);
            }

            // Cerrar el documento
            document.close();

            System.out.println("PDF generado correctamente en: " + filePath);

        } catch (Exception e) {
            System.out.println("Error al generar el PDF: " + e.getMessage());
        }
    }

    public static void generarPDFCalificacionesPorEvaluacion(int idAsignatura, String nombreAsignatura, int idEvaluacion, String nombreEvaluacion) {
        String filePath = System.getProperty("user.home") + "/Downloads/Calificaciones_" + nombreEvaluacion + ".pdf";

        try {
            // Crear el documento PDF
            PdfWriter writer = new PdfWriter(new FileOutputStream(filePath));
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Título del documento
            Paragraph titulo = new Paragraph("Calificaciones de la Evaluación: " + nombreEvaluacion)
                    .setBold()
                    .setFontSize(14);
            document.add(titulo);

            // Información adicional
            Paragraph subtitulo = new Paragraph("Asignatura: " + nombreAsignatura)
                    .setFontSize(12);
            document.add(subtitulo);

            document.add(new Paragraph("Fecha: " + new java.text.SimpleDateFormat("dd/MM/yyyy").format(new java.util.Date()))
                    .setFontSize(12));
            document.add(new Paragraph("\n")); // Espacio

            // Crear una tabla
            Table table = new Table(UnitValue.createPercentArray(new float[]{4, 6, 4})); // Columnas: Alumno, Nota, Fecha
            table.setWidth(UnitValue.createPercentValue(100)); // Ancho total del 100%

            // Encabezados
            table.addHeaderCell(new Cell().add(new Paragraph("Alumno")).setBold());
            table.addHeaderCell(new Cell().add(new Paragraph("Nota")).setBold());
            table.addHeaderCell(new Cell().add(new Paragraph("Fecha")).setBold());

            // Obtener las calificaciones
            CalificacionController calificacionController = new CalificacionController();
            AlumnoController alumnoController = new AlumnoController();
            List<Alumno> alumnos = alumnoController.obtenerAlumnosPorAsignatura(idAsignatura);
            List<Calificacion> calificaciones = calificacionController.obtenerCalificacionesPorEvaluacion(idEvaluacion);

            // Agregar filas con datos
            for (Alumno alumno : alumnos) {
                // Buscar si el alumno tiene calificación
                Calificacion calificacion = calificaciones.stream()
                        .filter(c -> c.getIdAlumno() == alumno.getId())
                        .findFirst()
                        .orElse(null);

                table.addCell(alumno.getNombre() + " " + alumno.getApellido()); // Nombre completo del alumno

                if (calificacion != null) {
                    table.addCell(String.format("%.2f", calificacion.getNota())); // Nota con 2 decimales
                    table.addCell(new java.text.SimpleDateFormat("dd/MM/yyyy").format(calificacion.getFecha())); // Fecha formateada
                } else {
                    table.addCell("No calificado"); // Nota no disponible
                    table.addCell("-"); // Fecha no disponible
                }
            }

            // Agregar la tabla al documento
            document.add(table);

            // Cerrar el documento
            document.close();

            JOptionPane.showMessageDialog(null, "PDF generado correctamente en: " + filePath, "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al generar el PDF: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

}
