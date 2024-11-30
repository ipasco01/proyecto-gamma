/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package proyectogamma.views;

import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.ui.FlatComboBoxUI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.RowFilter;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import proyectogamma.model.Usuario;
import proyectogamma.model.Alumno;
import proyectogamma.controller.UsuarioController;
import proyectogamma.controller.NotificacionController;
import proyectogamma.controller.AlumnoController;
import proyectogamma.controller.AlumnoGrupoController;
import proyectogamma.controller.CalificacionController;
import proyectogamma.controller.DocenteController;
import proyectogamma.controller.GrupoAsignaturaController;
import proyectogamma.controller.GrupoController;
import proyectogamma.controller.HorarioAsignaturaController;
import proyectogamma.model.AlumnoGrupos;
import proyectogamma.model.Calificacion;
import proyectogamma.model.Docente;
import proyectogamma.model.HorarioAsignatura;
import proyectogamma.model.Notificacion;
import proyectogamma.utils.PDFGenerator;

/**
 *
 * @author Isabel
 */
public class AlumnoView extends javax.swing.JFrame {

    private Usuario usuario;
    private Alumno alumno;

    public AlumnoView(Usuario usuario) {

        this.usuario = usuario;
        // Verificar si el usuario tiene un ID de alumno asociado
        if (usuario.getIdAlumno() != null) {
            UsuarioController usuarioController = new UsuarioController();
            this.alumno = usuarioController.obtenerAlumnoPorUsuario(usuario);
            System.out.println("Alumno cargado: " + alumno);
        }
        initComponents();
        cargarNotificacionesGrupales();
        cargarNotificacionesPersonales();

        jTabbedPane4.setUI(new com.formdev.flatlaf.ui.FlatTabbedPaneUI());
        jTabbedPane1.setUI(new com.formdev.flatlaf.ui.FlatTabbedPaneUI());
        jComboBox1.setUI(new com.formdev.flatlaf.ui.FlatComboBoxUI());

        listaNotificacionesGrupales.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                mostrarNotificacionSeleccionada(listaNotificacionesGrupales);
            }
        });

        listaNotificacionesPersonal3.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                mostrarNotificacionSeleccionada(listaNotificacionesPersonal3);
            }
        });
        cargarAsignaturas();
        cargarTodasLasNotas();
        mostrarPromediosDesdeTabla();
        jComboBox1.addActionListener(e -> {
            String asignaturaSeleccionada = (String) jComboBox1.getSelectedItem();
            cargarNotasPorAsignatura(asignaturaSeleccionada);
        });
        jTextPane1.setContentType("text/html");
        jTextPane1.setText("<html><body><i>Selecciona una notificación para ver más detalles.</i></body></html>");
        setLocationRelativeTo(null); // Centrar la ventana

        if (alumno != null) {
            lblBienvenida.setText("Hola, " + alumno.getNombre() + " " + alumno.getApellido());
            AlumnoGrupoController alumnoGrupoController = new AlumnoGrupoController();
            AlumnoGrupos alumnoGrupo = alumnoGrupoController.obtenerAlumnoGrupoPorIdAlumno(alumno.getId());
            if (alumnoGrupo != null) {

                GrupoController grupoController = new GrupoController();
                String nombreGrupo = grupoController.obtenerNombreGrupoPorId(alumnoGrupo.getIdGrupo());
                int idGrupoAlumno = alumnoGrupo.getIdGrupo();

                cargarTablaAlumnosOrdenados(idGrupoAlumno);
                lblBienvenida1.setText("Grupo: " + (nombreGrupo != null ? nombreGrupo : "Sin Grupo"));
                lblBienvenida3.setText("Grupo: " + (nombreGrupo != null ? nombreGrupo : "Sin Grupo"));
                GrupoAsignaturaController grupoAsignaturaController = new GrupoAsignaturaController();
                String asignaturasHTML = grupoAsignaturaController.obtenerAsignaturasHTMLPorGrupo(alumnoGrupo.getIdGrupo());

                // Mostrar las asignaturas en el JLabel
                lblAsignaturas.setText(asignaturasHTML);
                cargarHorario(idGrupoAlumno);
            } else {
                lblBienvenida1.setText("Grupo: Sin Grupo");
            }
        } else {
            lblBienvenida.setText("Bienvenido, Alumno desconocido");
        }

    }

    private void cargarNotificacionesGrupales() {
        if (alumno != null) {
            AlumnoController alumnoController = new AlumnoController();
            int idGrupoAlumno = alumnoController.obtenerIdGrupoPorAlumno(alumno.getId());

            if (idGrupoAlumno != -1) { // Si el alumno pertenece a un grupo
                NotificacionController notificacionController = new NotificacionController();
                List<Notificacion> notificaciones = notificacionController.obtenerNotificacionesPorGrupo(idGrupoAlumno);

                DefaultListModel<String> notificacionesModel = new DefaultListModel<>();
                for (Notificacion notificacion : notificaciones) {
                    String nombreDocente = "Sin docente asignado";

                    if (notificacion.getIdDocente() != 0) {
                        Docente docente = new DocenteController().obtenerDocentePorId(notificacion.getIdDocente());
                        if (docente != null) {
                            nombreDocente = docente.getNombre() + " " + docente.getApellido();
                        }
                    }

                    // Formatear notificación en HTML
                    String formattedNotification = "<html><body>"
                            + "<b>" + notificacion.getTitulo() + "</b><br>"
                            + "<i>" + notificacion.getFechaEnvio() + "</i><br>"
                            + notificacion.getMensaje() + "<br>"
                            + "<small>Por: " + nombreDocente + "</small>"
                            + "</body></html>";

                    notificacionesModel.addElement(formattedNotification);
                }
                listaNotificacionesGrupales.setModel(notificacionesModel);
            } else {
                listaNotificacionesGrupales.setModel(new DefaultListModel<>()); // Vaciar la lista
                System.out.println("El alumno no pertenece a ningún grupo.");
            }
        } else {
            System.out.println("Alumno no encontrado.");
        }
    }

    private void cargarNotificacionesPersonales() {
        if (alumno != null) {
            int idAlumno = alumno.getId();
            NotificacionController notificacionController = new NotificacionController();
            List<Notificacion> notificaciones = notificacionController.obtenerNotificacionesPorAlumno(idAlumno);

            DefaultListModel<String> notificacionesModel = new DefaultListModel<>();
            for (Notificacion notificacion : notificaciones) {
                String nombreDocente = "Sin docente asignado";

                if (notificacion.getIdDocente() != 0) {
                    Docente docente = new DocenteController().obtenerDocentePorId(notificacion.getIdDocente());
                    if (docente != null) {
                        nombreDocente = docente.getNombre() + " " + docente.getApellido();
                    }
                }

                // Formatear notificación en HTML
                String formattedNotification = "<html><body>"
                        + "<b>" + notificacion.getTitulo() + "</b><br>"
                        + "<i>" + notificacion.getFechaEnvio() + "</i><br>"
                        + notificacion.getMensaje() + "<br>"
                        + "<small>Por: " + nombreDocente + "</small>"
                        + "</body></html>";

                notificacionesModel.addElement(formattedNotification);
            }
            listaNotificacionesPersonal3.setModel(notificacionesModel);

        } else {
            System.out.println("Alumno no encontrado.");
        }
    }

    private void mostrarNotificacionSeleccionada(javax.swing.JList<String> lista) {
        int selectedIndex = lista.getSelectedIndex();
        if (selectedIndex != -1) {
            DefaultListModel<String> modelo = (DefaultListModel<String>) lista.getModel();
            String notificacionSeleccionada = modelo.getElementAt(selectedIndex);

            // Mostrar notificación seleccionada en HTML en un JTextPane (mejor para formato HTML)
            jTextPane1.setText(notificacionSeleccionada);
        } else {
            jTextPane1.setText("<html><body><i>Selecciona una notificación para ver más detalles.</i></body></html>");
        }
    }

    private void cargarTablaAlumnosOrdenados(int idGrupo) {
        AlumnoController alumnoController = new AlumnoController();
        List<Alumno> alumnos = alumnoController.obtenerAlumnosOrdenadosPorApellido(idGrupo);

        // Modelo de la tabla con las columnas necesarias
        DefaultTableModel model = new DefaultTableModel(new String[]{"#", "Apellido", "Inicial del Nombre"}, 0);

        int index = 1; // Inicializamos el índice en 1
        for (Alumno alumno : alumnos) {
            // Agregar apellido e inicial del nombre
            String inicialNombre = alumno.getNombre().substring(0, 1).toUpperCase(); // Obtener la inicial
            model.addRow(new Object[]{index++, alumno.getApellido(), inicialNombre + "."}); // Índice, apellido, inicial
        }

        tablaAlumnos.setModel(model);
    }

    private void cargarTodasLasNotas() {
        if (alumno != null) {
            CalificacionController calificacionController = new CalificacionController();
            List<Calificacion> calificaciones = calificacionController.obtenerCalificacionesConTitulos(alumno.getId());

            DefaultTableModel modelo = new DefaultTableModel();
            modelo.addColumn("Asignatura");
            modelo.addColumn("Evaluación");
            modelo.addColumn("Nota");
            modelo.addColumn("Peso (%)");
            modelo.addColumn("Fecha");

            for (Calificacion calificacion : calificaciones) {
                modelo.addRow(new Object[]{
                    calificacion.getTituloAsignatura(),
                    calificacion.getTitulo(),
                    String.format("%.2f", calificacion.getNota()),
                    String.format("%.2f", calificacion.getPeso()),
                    new SimpleDateFormat("dd/MM/yyyy").format(calificacion.getFecha())
                });
            }

            tablaNotas.setModel(modelo);

            // Mostrar los promedios
            mostrarPromediosDesdeTabla();
        }
    }
   private void cargarHorario(int idGrupo) {
    HorarioAsignaturaController horarioController = new HorarioAsignaturaController();
    List<HorarioAsignatura> horarios = horarioController.obtenerHorariosPorGrupo(idGrupo);

    // Crear una matriz para el horario limitado de 7:00 a 14:00
    String[][] timetable = new String[8][7]; // 8 horas (7:00 - 14:00) y 7 días (Lunes a Sábado)
    for (HorarioAsignatura horario : horarios) {
        String dia = horario.getDia();
        int columna = switch (dia.toLowerCase()) {
            case "lunes" -> 1;
            case "martes" -> 2;
            case "miercoles" -> 3;
            case "jueves" -> 4;
            case "viernes" -> 5;
            case "sabado" -> 6;
            default -> 0;
        };

        int filaInicio = horario.getHoraInicio().toLocalTime().getHour() - 7; // Ajustar índice de 7:00
        int filaFin = horario.getHoraFin().toLocalTime().getHour() - 7;

        // Marcar el horario
        for (int i = filaInicio; i < filaFin; i++) {
            if (i >= 0 && i < timetable.length) { // Validar rango de fila
                timetable[i][columna] = horario.getNombreAsignatura();
            }
        }
    }

    // Crear modelo para la tabla
    DefaultTableModel model = new DefaultTableModel(new Object[]{"Hora", "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado"}, 8);
    for (int i = 0; i < 8; i++) {
        model.setValueAt((i + 7) + ":00", i, 0); // Primera columna para las horas
        for (int j = 1; j < 7; j++) {
            model.setValueAt(timetable[i][j] != null ? timetable[i][j] : "", i, j);
        }
    }

    tablaHorario.setModel(model);
}



    private void cargarNotasPorAsignatura(String asignaturaSeleccionada) {
        if (asignaturaSeleccionada != null) {
            DefaultTableModel modelo = (DefaultTableModel) tablaNotas.getModel();
            TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(modelo);
            tablaNotas.setRowSorter(sorter);

            if (!asignaturaSeleccionada.equals("Todas")) {
                sorter.setRowFilter(RowFilter.regexFilter(asignaturaSeleccionada, 0));
            } else {
                sorter.setRowFilter(null);
            }
        }
    }

    private void cargarAsignaturas() {
        if (alumno != null) {
            AlumnoController alumnoController = new AlumnoController();
            int idGrupo = new AlumnoController().obtenerIdGrupoPorAlumno(alumno.getId());
            List<String> asignaturas = alumnoController.obtenerAsignaturasPorGrupo(idGrupo);
            jComboBox1.addItem("Todas");
            if (!asignaturas.isEmpty()) {
                asignaturas.forEach(asignatura -> jComboBox1.addItem(asignatura));
            } else {
                System.out.println("No se encontraron asignaturas para el grupo.");
            }
        }
    }

    private void mostrarPromediosDesdeTabla() {
        DefaultTableModel modelo = (DefaultTableModel) tablaNotas.getModel();

        if (modelo.getRowCount() > 0) {
            // Map para almacenar las notas y pesos agrupados por asignatura
            Map<String, List<double[]>> asignaturaNotas = new HashMap<>();

            // Recorrer las filas de la tabla y agrupar notas y pesos por asignatura
            for (int i = 0; i < modelo.getRowCount(); i++) {
                String asignatura = modelo.getValueAt(i, 0).toString(); // Columna "Asignatura"
                double nota = Double.parseDouble(modelo.getValueAt(i, 2).toString()); // Columna "Nota"
                double peso = Double.parseDouble(modelo.getValueAt(i, 3).toString()); // Columna "Peso (%)"

                asignaturaNotas.putIfAbsent(asignatura, new ArrayList<>());
                asignaturaNotas.get(asignatura).add(new double[]{nota, peso});
            }

            // Construir el texto para los promedios en formato HTML
            StringBuilder textoPromedios = new StringBuilder("<html>");
            textoPromedios.append("<h2>Promedios por asignatura:</h2>");
            textoPromedios.append("<ul>");

            double sumaGeneral = 0;
            double totalPesoGeneral = 0;

            for (Map.Entry<String, List<double[]>> entry : asignaturaNotas.entrySet()) {
                String asignatura = entry.getKey();
                List<double[]> notasPesos = entry.getValue();

                // Calcular promedio ponderado por asignatura
                double sumaPonderada = 0;
                double sumaPesos = 0;

                for (double[] np : notasPesos) {
                    sumaPonderada += np[0] * np[1];
                    sumaPesos += np[1];
                }

                double promedio = sumaPesos > 0 ? sumaPonderada / sumaPesos : 0.0;

                // Agregar al texto en HTML
                textoPromedios.append("<li><strong>").append(asignatura).append("</strong>: ")
                        .append(String.format("%.2f", promedio))
                        .append("</li>");

                // Sumar para el promedio general
                sumaGeneral += sumaPonderada;
                totalPesoGeneral += sumaPesos;
            }

            textoPromedios.append("</ul>");

            // Calcular promedio general ponderado
            double promedioGeneral = totalPesoGeneral > 0 ? sumaGeneral / totalPesoGeneral : 0.0;
            textoPromedios.append("<h3>Promedio general: ")
                    .append(String.format("%.2f", promedioGeneral))
                    .append("</h3>");
            textoPromedios.append("</html>");

            // Mostrar en jTextArea1
            jTextPane2.setContentType("text/html");
            jTextPane2.setText(textoPromedios.toString());
        } else {
            jTextPane2.setContentType("text/plain");
            jTextPane2.setText("No hay notas disponibles para calcular los promedios.");
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel8 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        lblBienvenida = new javax.swing.JLabel();
        closeSession = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        lblBienvenida2 = new javax.swing.JLabel();
        lblAsignaturas = new javax.swing.JLabel();
        lblBienvenida3 = new javax.swing.JLabel();
        mainPanel3 = new javax.swing.JPanel();
        jTabbedPane4 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane8 = new javax.swing.JScrollPane();
        listaNotificacionesGrupales = new javax.swing.JList<>();
        jScrollPane9 = new javax.swing.JScrollPane();
        listaNotificacionesPersonal3 = new javax.swing.JList<>();
        jLabel8 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextPane1 = new javax.swing.JTextPane();
        jPanel4 = new javax.swing.JPanel();
        jscrollpanelhorarios = new javax.swing.JScrollPane();
        tablaHorario = new javax.swing.JTable();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jComboBox1 = new javax.swing.JComboBox<>();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jScrollPane10 = new javax.swing.JScrollPane();
        tablaNotas = new javax.swing.JTable();
        jLabel11 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        DescargarNotas = new javax.swing.JButton();
        jLabel12 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTextPane2 = new javax.swing.JTextPane();
        jPanel3 = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tablaAlumnos = new javax.swing.JTable();
        lblBienvenida1 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        txtContrasena = new javax.swing.JPasswordField();
        CambioContraseña = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtContrasena2 = new javax.swing.JPasswordField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Bienvenido");
        setBackground(java.awt.Color.white);
        setBounds(new java.awt.Rectangle(0, 0, 810, 600));
        setFont(new java.awt.Font("Trebuchet MS", 1, 12)); // NOI18N
        setForeground(new java.awt.Color(204, 204, 204));
        setMaximumSize(new java.awt.Dimension(1000, 600));
        setMinimumSize(new java.awt.Dimension(1000, 600));
        setUndecorated(true);
        setResizable(false);
        setSize(new java.awt.Dimension(800, 500));

        jPanel8.setBackground(new java.awt.Color(255, 255, 255));
        jPanel8.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 204)));
        jPanel8.setPreferredSize(new java.awt.Dimension(800, 500));
        jPanel8.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel9.setBackground(new java.awt.Color(0, 0, 102));
        jPanel9.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblBienvenida.setBackground(new java.awt.Color(255, 255, 255));
        lblBienvenida.setFont(new java.awt.Font("Poppins Medium", 3, 14)); // NOI18N
        lblBienvenida.setForeground(new java.awt.Color(255, 255, 255));
        lblBienvenida.setText("jLabel2");
        jPanel9.add(lblBienvenida, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 260, 130, -1));

        closeSession.setBackground(new java.awt.Color(153, 0, 0));
        closeSession.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        closeSession.setForeground(new java.awt.Color(255, 255, 255));
        closeSession.setText("Cerrar Sesión");
        closeSession.setBorder(null);
        closeSession.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        closeSession.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeSessionActionPerformed(evt);
            }
        });
        jPanel9.add(closeSession, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 510, 150, 30));

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/user_alumno.png"))); // NOI18N
        jPanel9.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 50, -1, -1));

        lblBienvenida2.setBackground(new java.awt.Color(255, 255, 255));
        lblBienvenida2.setFont(new java.awt.Font("Poppins Medium", 3, 16)); // NOI18N
        lblBienvenida2.setForeground(new java.awt.Color(255, 255, 255));
        lblBienvenida2.setText(" Portal del Estudiante");
        jPanel9.add(lblBienvenida2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 10, 190, -1));

        lblAsignaturas.setBackground(new java.awt.Color(255, 255, 255));
        lblAsignaturas.setFont(new java.awt.Font("Poppins Medium", 3, 14)); // NOI18N
        lblAsignaturas.setForeground(new java.awt.Color(255, 255, 255));
        lblAsignaturas.setText("jLabel2");
        jPanel9.add(lblAsignaturas, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 370, 130, -1));

        lblBienvenida3.setBackground(new java.awt.Color(255, 255, 255));
        lblBienvenida3.setFont(new java.awt.Font("Poppins Medium", 3, 14)); // NOI18N
        lblBienvenida3.setForeground(new java.awt.Color(255, 255, 255));
        lblBienvenida3.setText("jLabel2");
        jPanel9.add(lblBienvenida3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 310, 130, -1));

        jPanel8.add(jPanel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 190, 600));

        mainPanel3.setBackground(new java.awt.Color(255, 255, 255));
        mainPanel3.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        mainPanel3.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        mainPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jTabbedPane4.setBackground(new java.awt.Color(255, 255, 255));
        jTabbedPane4.setForeground(new java.awt.Color(0, 0, 153));
        jTabbedPane4.setToolTipText("");
        jTabbedPane4.setFocusable(false);
        jTabbedPane4.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        jTabbedPane4.setRequestFocusEnabled(false);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel4.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        jLabel4.setText("Notificaciones para tu grupo");

        listaNotificacionesGrupales.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        listaNotificacionesGrupales.setModel(listaNotificacionesGrupales.getModel());
        listaNotificacionesGrupales.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        listaNotificacionesGrupales.setFixedCellHeight(70);
        listaNotificacionesGrupales.setLayoutOrientation(javax.swing.JList.VERTICAL_WRAP);
        listaNotificacionesGrupales.setSelectionBackground(new java.awt.Color(0, 0, 204));
        listaNotificacionesGrupales.setSelectionForeground(new java.awt.Color(255, 255, 255));
        listaNotificacionesGrupales.setVisibleRowCount(4);
        jScrollPane8.setViewportView(listaNotificacionesGrupales);

        listaNotificacionesPersonal3.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        listaNotificacionesPersonal3.setModel(listaNotificacionesPersonal3.getModel());
        listaNotificacionesPersonal3.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        listaNotificacionesPersonal3.setFixedCellHeight(60);
        listaNotificacionesPersonal3.setVisibleRowCount(4);
        jScrollPane9.setViewportView(listaNotificacionesPersonal3);

        jLabel8.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        jLabel8.setText("Notificaciones para ti");

        jLabel6.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        jLabel6.setText("Notificación");

        jLabel13.setIcon(new javax.swing.ImageIcon(getClass().getResource("/logoPeque.png"))); // NOI18N

        jLabel14.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(0, 0, 153));
        jLabel14.setText("Mis Notificaciones");

        jLabel15.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jLabel15.setText("<html>Bienvenido a tus notificaciones!</br> Para ver la notificación en</br> la pantalla, haz click y aparecerá abajo</html>");

        jTextPane1.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jScrollPane2.setViewportView(jTextPane1);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel13)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(jLabel14))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(38, 38, 38)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel6)
                            .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 331, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 335, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 39, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, 359, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 359, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addGap(43, 43, 43))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(4, 4, 4)
                        .addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 221, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel13)
                        .addGap(10, 10, 10)
                        .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane2)))
                .addContainerGap(49, Short.MAX_VALUE))
        );

        jTabbedPane4.addTab("Mis Notificaciones", jPanel1);

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));

        tablaHorario.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        tablaHorario.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tablaHorario.setRowHeight(40);
        tablaHorario.setRowMargin(2);
        tablaHorario.setRowSelectionAllowed(false);
        tablaHorario.setShowGrid(true);
        jscrollpanelhorarios.setViewportView(tablaHorario);

        jLabel18.setIcon(new javax.swing.ImageIcon(getClass().getResource("/logoPeque.png"))); // NOI18N

        jLabel19.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(0, 0, 153));
        jLabel19.setText("Mi Horario");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(9, 9, 9)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jscrollpanelhorarios, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 795, Short.MAX_VALUE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel18)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(jLabel19)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(jLabel18)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 13, Short.MAX_VALUE)
                .addComponent(jscrollpanelhorarios, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jTabbedPane4.addTab("Mi Horario", jPanel4);

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));
        jPanel6.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jScrollPane1.setBorder(null);

        jComboBox1.setFont(new java.awt.Font("Poppins", 3, 14)); // NOI18N
        jComboBox1.setForeground(new java.awt.Color(0, 51, 153));
        jComboBox1.setModel(jComboBox1.getModel());
        jComboBox1.setBorder(null);
        jScrollPane1.setViewportView(jComboBox1);

        jPanel6.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 160, 240, 50));

        jLabel9.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        jLabel9.setText("Notas");
        jPanel6.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 40, -1, 20));

        jLabel10.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(0, 0, 153));
        jLabel10.setText("Mis Asignaturas");
        jPanel6.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 90, -1, 20));

        jScrollPane10.setBackground(new java.awt.Color(255, 255, 255));
        jScrollPane10.setBorder(null);
        jScrollPane10.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N

        tablaNotas.setAutoCreateRowSorter(true);
        tablaNotas.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        tablaNotas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tablaNotas.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        tablaNotas.setEditingColumn(0);
        tablaNotas.setEditingRow(0);
        tablaNotas.setEnabled(false);
        tablaNotas.setIntercellSpacing(new java.awt.Dimension(0, 10));
        tablaNotas.setRowHeight(60);
        tablaNotas.setShowGrid(true);
        tablaNotas.setShowHorizontalLines(false);
        jScrollPane10.setViewportView(tablaNotas);

        jPanel6.add(jScrollPane10, new org.netbeans.lib.awtextra.AbsoluteConstraints(298, 64, 456, 420));

        jLabel11.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        jLabel11.setText("Promedios");
        jPanel6.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 230, 110, 20));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/logoPeque.png"))); // NOI18N
        jPanel6.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 410, -1, -1));

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/logoPeque.png"))); // NOI18N
        jPanel6.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, -1, -1));

        DescargarNotas.setBackground(new java.awt.Color(0, 153, 102));
        DescargarNotas.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        DescargarNotas.setForeground(new java.awt.Color(255, 255, 255));
        DescargarNotas.setText("Descargar Notas");
        DescargarNotas.setToolTipText("");
        DescargarNotas.setBorder(null);
        DescargarNotas.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        DescargarNotas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DescargarNotasActionPerformed(evt);
            }
        });
        jPanel6.add(DescargarNotas, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 500, 460, 30));

        jLabel12.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        jLabel12.setText("Asignaturas");
        jPanel6.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 140, -1, 20));

        jTextPane2.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jScrollPane4.setViewportView(jTextPane2);

        jPanel6.add(jScrollPane4, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 260, 240, 210));

        jTabbedPane4.addTab("Mis Asignaturas", jPanel6);

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        jLabel16.setIcon(new javax.swing.ImageIcon(getClass().getResource("/logoPeque.png"))); // NOI18N

        jLabel17.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(0, 0, 153));
        jLabel17.setText("Mis Compañeros");

        jScrollPane3.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N

        tablaAlumnos.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        tablaAlumnos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane3.setViewportView(tablaAlumnos);

        lblBienvenida1.setBackground(new java.awt.Color(255, 255, 255));
        lblBienvenida1.setFont(new java.awt.Font("Poppins Medium", 3, 14)); // NOI18N
        lblBienvenida1.setForeground(new java.awt.Color(255, 255, 255));
        lblBienvenida1.setText("jLabel2");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel16)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel17)
                                .addGap(18, 18, 18)
                                .addComponent(lblBienvenida1, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 734, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(41, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addComponent(jLabel16)
                .addGap(9, 9, 9)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblBienvenida1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 395, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(41, Short.MAX_VALUE))
        );

        jTabbedPane4.addTab("Mis Compañeros", jPanel3);

        jPanel7.setBackground(new java.awt.Color(255, 255, 255));
        jPanel7.setDoubleBuffered(false);
        jPanel7.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jTabbedPane1.setForeground(new java.awt.Color(0, 0, 153));
        jTabbedPane1.setTabPlacement(javax.swing.JTabbedPane.LEFT);
        jTabbedPane1.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        txtContrasena.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        txtContrasena.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Contraseña", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Poppins", 1, 14), new java.awt.Color(0, 0, 204))); // NOI18N
        jPanel2.add(txtContrasena, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 250, 230, 50));

        CambioContraseña.setBackground(new java.awt.Color(0, 0, 204));
        CambioContraseña.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        CambioContraseña.setForeground(new java.awt.Color(255, 255, 255));
        CambioContraseña.setText("Cambiar Contraseña");
        CambioContraseña.setActionCommand("login");
        CambioContraseña.setBorder(null);
        CambioContraseña.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CambioContraseñalogin(evt);
            }
        });
        jPanel2.add(CambioContraseña, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 380, 230, 30));

        jLabel7.setFont(new java.awt.Font("Poppins", 1, 18)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(0, 0, 204));
        jLabel7.setText("CAMBIO DE CONTRASEÑA");
        jPanel2.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 200, -1, -1));

        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/logoPeque.png"))); // NOI18N
        jPanel2.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 120, 80, 70));

        txtContrasena2.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        txtContrasena2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Repetir Contraseña", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Poppins", 1, 14), new java.awt.Color(0, 0, 204))); // NOI18N
        jPanel2.add(txtContrasena2, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 310, 230, 50));

        jTabbedPane1.addTab("Cambio de Contraseña", jPanel2);

        jPanel7.add(jTabbedPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 780, 570));

        jTabbedPane4.addTab("Configuración", jPanel7);

        mainPanel3.add(jTabbedPane4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 3, 810, 600));

        jPanel8.add(mainPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, -4, 810, 600));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, 1000, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, 600, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void closeSessionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeSessionActionPerformed
        int confirm = javax.swing.JOptionPane.showConfirmDialog(
                this,
                "¿Estás seguro de que quieres cerrar sesión?",
                "Confirmar cierre de sesión",
                javax.swing.JOptionPane.YES_NO_OPTION
        );

        if (confirm == javax.swing.JOptionPane.YES_OPTION) {
            System.out.println("Cerrando sesión...");
            // Cerrar la aplicación
            System.exit(0);
        } else {
            System.out.println("El usuario canceló el cierre de sesión.");
        }
    }//GEN-LAST:event_closeSessionActionPerformed

    private void CambioContraseñalogin(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CambioContraseñalogin

        String contrasenaActual = new String(txtContrasena.getPassword());
        String nuevaContrasena = new String(txtContrasena2.getPassword()); // Asegúrate de renombrar el campo para evitar confusión

        if (contrasenaActual.isEmpty() || nuevaContrasena.isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(this, "Por favor, llena ambos campos.", "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validar que la contraseña actual sea correcta
        UsuarioController usuarioController = new UsuarioController();
        Usuario usuarioValidado = usuarioController.login(usuario.getNombreUsuario(), contrasenaActual);

        if (usuarioValidado != null) {
            // Cambiar la contraseña
            boolean cambioExitoso = usuarioController.cambiarContrasena(usuario.getId(), nuevaContrasena);
            if (cambioExitoso) {
                javax.swing.JOptionPane.showMessageDialog(this, "Contraseña actualizada con éxito.", "Éxito", javax.swing.JOptionPane.INFORMATION_MESSAGE);
            } else {
                javax.swing.JOptionPane.showMessageDialog(this, "Hubo un problema al cambiar la contraseña.", "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        } else {
            javax.swing.JOptionPane.showMessageDialog(this, "La contraseña actual no es correcta.", "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_CambioContraseñalogin

    private void DescargarNotasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DescargarNotasActionPerformed
        if (alumno != null) {
            try {
                // Obtener todas las notas desde la tabla
                DefaultTableModel modelo = (DefaultTableModel) tablaNotas.getModel();
                List<String[]> notas = new ArrayList<>();
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

                for (int i = 0; i < modelo.getRowCount(); i++) {
                    String asignatura = (String) modelo.getValueAt(i, 0); // Columna Asignatura
                    String ev = modelo.getValueAt(i, 1).toString();    // Columna 
                    String nota = modelo.getValueAt(i, 2).toString();    // Columna Nota
                    String peso = modelo.getValueAt(i, 3).toString();    // Columna Nota

                    // Obtener y convertir la fecha
                    Object fechaObj = modelo.getValueAt(i, 4); // Columna Fecha
                    String fechaFormateada;
                    if (fechaObj instanceof Date) {
                        fechaFormateada = dateFormat.format((Date) fechaObj);
                    } else if (fechaObj instanceof String) {
                        fechaFormateada = (String) fechaObj; // Suponemos que ya está formateada como String
                    } else {
                        throw new IllegalArgumentException("Formato de fecha desconocido en la tabla.");
                    }

                    notas.add(new String[]{asignatura, ev,nota,peso, fechaFormateada});
                }

                // Generar el PDF
                PDFGenerator.generarPDFNotasAlumno(alumno.getNombre() + " " + alumno.getApellido(), notas);
                javax.swing.JOptionPane.showMessageDialog(this, "El PDF de las notas se ha descargado en la carpeta de Descargas.");
            } catch (Exception e) {
                javax.swing.JOptionPane.showMessageDialog(this, "Error al generar el PDF: " + e.getMessage(), "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        } else {
            javax.swing.JOptionPane.showMessageDialog(this, "No se encontraron notas para descargar.");
        }
    }//GEN-LAST:event_DescargarNotasActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */

 /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                Usuario usuarioPrueba = new Usuario(1, "juanperez", "12345", "Alumno", 1, null, null);
                new AlumnoView(usuarioPrueba).setVisible(true);

            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton CambioContraseña;
    private javax.swing.JButton DescargarNotas;
    private javax.swing.JButton closeSession;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTabbedPane jTabbedPane4;
    private javax.swing.JTextPane jTextPane1;
    private javax.swing.JTextPane jTextPane2;
    private javax.swing.JScrollPane jscrollpanelhorarios;
    private javax.swing.JLabel lblAsignaturas;
    private javax.swing.JLabel lblBienvenida;
    private javax.swing.JLabel lblBienvenida1;
    private javax.swing.JLabel lblBienvenida2;
    private javax.swing.JLabel lblBienvenida3;
    private javax.swing.JList<String> listaNotificacionesGrupales;
    private javax.swing.JList<String> listaNotificacionesPersonal3;
    private javax.swing.JPanel mainPanel3;
    private javax.swing.JTable tablaAlumnos;
    private javax.swing.JTable tablaHorario;
    private javax.swing.JTable tablaNotas;
    private javax.swing.JPasswordField txtContrasena;
    private javax.swing.JPasswordField txtContrasena2;
    // End of variables declaration//GEN-END:variables
}
