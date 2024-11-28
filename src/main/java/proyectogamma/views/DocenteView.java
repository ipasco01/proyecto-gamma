/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package proyectogamma.views;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import proyectogamma.controller.AlumnoController;
import proyectogamma.controller.AsignaturaController;
import proyectogamma.controller.CalificacionController;
import proyectogamma.controller.EvaluacionController;
import proyectogamma.controller.GrupoController;
import proyectogamma.controller.NotificacionController;
import proyectogamma.controller.UsuarioController;
import proyectogamma.model.Alumno;
import proyectogamma.model.Calificacion;
import proyectogamma.model.Asignatura;
import proyectogamma.model.Docente;
import proyectogamma.model.Evaluacion;
import proyectogamma.model.Grupos;
import proyectogamma.model.Notificacion;
import proyectogamma.model.Usuario;
import proyectogamma.utils.PDFGenerator;

/**
 *
 * @author Isbael
 */
public class DocenteView extends javax.swing.JFrame {

    private Docente docente;
    private Usuario usuario;

    /**
     * Creates new form DocenteView
     */
    public DocenteView(Usuario usuario) {
        this.usuario = usuario;

        // Cargar el docente asociado al usuario
        if (usuario.getIdDocente() != null) {
            UsuarioController usuarioController = new UsuarioController();
            this.docente = usuarioController.obtenerDocentePorUsuario(usuario);

            if (docente != null) {
                System.out.println("Docente cargado: " + docente);
            } else {
                System.out.println("No se encontró un docente asociado.");
            }
        } else {
            System.out.println("El usuario no tiene un ID de docente asociado.");
        }

        initComponents();
        setLocationRelativeTo(null); // Centrar la ventana
        // Estilo de FlatLaf
        jTabbedPane1.setUI(new com.formdev.flatlaf.ui.FlatTabbedPaneUI());
        cargarGruposYAlumnos();
        cargarNotificaciones();
        cargarAsignaturasYGrupos();
        jTabbedPane3.setUI(new com.formdev.flatlaf.ui.FlatTabbedPaneUI());

        // Mostrar información del docente
        if (docente != null) {
            lblBienvenida1.setText("Bienvenido, Prof." + docente.getNombre() + " " + docente.getApellido());

        } else {
            lblBienvenida1.setText("Bienvenido, Docente");
        }
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int selectedRow = jTable1.getSelectedRow();
                if (selectedRow != -1) {
                    String titulo = (String) jTable1.getValueAt(selectedRow, 1); // Columna de título
                    String mensaje = (String) jTable1.getValueAt(selectedRow, 2); // Columna de mensaje

                    // Mostrar la notificación en jTextArea2
                    jTextArea2.setText("Título: " + titulo + "\n\n" + "Mensaje: " + mensaje);
                }
            }
        });
        tablaAsignaturas.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int selectedRow = tablaAsignaturas.getSelectedRow();
                if (selectedRow != -1) {
                    int idAsignatura = Integer.parseInt(tablaAsignaturas.getValueAt(selectedRow, 0).toString());
                    cargarEvaluacionesPorAsignatura(idAsignatura);
                    txtTituloEval3.setText(tablaAsignaturas.getValueAt(selectedRow, 1).toString());
                }
            }
        });
        // Agrega un ItemListener al comboBox
        jComboTituloNota.addItemListener(evt -> {
            if (evt.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
                // Obtener la evaluación seleccionada
                String evaluacionSeleccionada = jComboTituloNota.getSelectedItem().toString();

                // Asegurarte de que haya una asignatura seleccionada
                int selectedRowAsignatura = tablaAsignaturas.getSelectedRow();
                if (selectedRowAsignatura == -1) {
                    JOptionPane.showMessageDialog(this, "Selecciona una asignatura primero.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Obtener el ID de la asignatura seleccionada
                int idAsignatura = Integer.parseInt(tablaAsignaturas.getValueAt(selectedRowAsignatura, 0).toString());

                // Obtener el peso de la evaluación seleccionada
                EvaluacionController evaluacionController = new EvaluacionController();
                double peso = evaluacionController.obtenerPesoEvaluacionPorNombre(evaluacionSeleccionada, idAsignatura);

                // Actualizar el jLabel con el peso
                jLabel2.setText(" (Peso: " + peso + "%)");
            }
        });

    }

    private void cargarAsignaturasYGrupos() {
        AsignaturaController asignaturaController = new AsignaturaController();
        List<Asignatura> asignaturas = asignaturaController.obtenerAsignaturasPorDocente(docente.getId());

        DefaultTableModel model = new DefaultTableModel(new String[]{"ID Asignatura", "Asignatura"}, 0);

        for (Asignatura asignatura : asignaturas) {
            model.addRow(new Object[]{
                asignatura.getId(),
                asignatura.getNombre(),});
        }

        tablaAsignaturas.setModel(model);
    }

    private void cargarGruposYAlumnos() {
        // Limpiar los comboBox antes de cargarlos para evitar duplicados
        comboBoxGrupo.removeAllItems();
        comboBoxAlumno.removeAllItems();
        comboBoxGrupo1.removeAllItems();
        comboBoxAlumno1.removeAllItems();

        // Agregar la opción "Sin grupo" al comboBox de grupos
        comboBoxGrupo.addItem("Sin grupo");
        comboBoxGrupo1.addItem("Sin grupo");

        GrupoController grupoController = new GrupoController();
        List<Grupos> grupos = grupoController.obtenerGrupos();
        for (Grupos grupo : grupos) {
            comboBoxGrupo.addItem(grupo.getNombre());
            comboBoxGrupo1.addItem(grupo.getNombre());
        }

        // Agregar la opción "Sin alumno" al comboBox de alumnos
        comboBoxAlumno.addItem("Sin alumno");
        comboBoxAlumno1.addItem("Sin alumno");

        AlumnoController alumnoController = new AlumnoController();
        List<Alumno> alumnos = alumnoController.obtenerAlumnos();
        for (Alumno alumno : alumnos) {
            comboBoxAlumno.addItem(alumno.getNombre() + " " + alumno.getApellido());
            comboBoxAlumno1.addItem(alumno.getNombre() + " " + alumno.getApellido());
        }
    }

    private void abrirTabModificarEliminar(int idNotificacion) {
        NotificacionController notificacionController = new NotificacionController();
        Notificacion notificacion = notificacionController.obtenerNotificacionPorId(idNotificacion);

        if (notificacion != null) {
            // Cambia a la pestaña de modificar/eliminar
            jTabbedPane1.setSelectedComponent(jPanel10);

            // Carga los datos de la notificación en los campos de edición
            txtTitulo.setText(notificacion.getTitulo());
            txtMensaje.setText(notificacion.getMensaje());
            lblIdNotificacion.setText(String.valueOf(notificacion.getId()));

            // Seleccionar el grupo en el comboBox
            if (notificacion.getIdGrupo() != null) {
                GrupoController grupoController = new GrupoController();
                String nombreGrupo = grupoController.obtenerNombreGrupoPorId(notificacion.getIdGrupo());
                comboBoxGrupo.setSelectedItem(nombreGrupo);
            } else {
                comboBoxGrupo.setSelectedItem("Sin grupo");
            }

            if (notificacion.getIdAlumno() != null) {
                AlumnoController alumnoController = new AlumnoController();
                String nombreAlumno = alumnoController.obtenerNombreAlumnoPorId(notificacion.getIdAlumno());
                comboBoxAlumno.setSelectedItem(nombreAlumno);
            } else {
                comboBoxAlumno.setSelectedItem("Sin alumno");
            }

        } else {
            System.out.println("No se encontró la notificación con ID: " + idNotificacion);
        }
    }

    private void cargarNotificaciones() {
        NotificacionController notificacionController = new NotificacionController();
        List<Notificacion> notificaciones = notificacionController.obtenerNotificacionesConDetallesPorDocente(docente.getId());

        DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "Título", "Mensaje", "Fecha", "Grupo", "Alumno"}, 0);

        for (Notificacion notificacion : notificaciones) {
            model.addRow(new Object[]{
                notificacion.getId(),
                notificacion.getTitulo(),
                notificacion.getMensaje(),
                notificacion.getFechaEnvio(),
                notificacion.getNombreGrupo(),
                notificacion.getNombreAlumno()
            });
        }

        jTable1.setModel(model);
    }

    private void cargarEvaluacionesPorAsignatura(int idAsignatura) {
        EvaluacionController evaluacionController = new EvaluacionController();
        List<Evaluacion> evaluaciones = evaluacionController.obtenerEvaluacionesPorAsignatura(idAsignatura);

        DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "Nombre", "Peso", "Fecha"}, 0);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
        for (Evaluacion evaluacion : evaluaciones) {
            String fechaFormateada = sdf.format(evaluacion.getFecha());
            model.addRow(new Object[]{
                evaluacion.getId(),
                evaluacion.getNombre(),
                evaluacion.getPeso(),
                fechaFormateada
            });
        }

        tablaEvaluaciones.setModel(model);
    }

    private void limpiarCamposNotificacion() {
        txtTitulo1.setText("");
        txtMensaje1.setText("");
        comboBoxGrupo1.setSelectedIndex(0); // Seleccionar "Sin grupo" o la primera opción
        comboBoxAlumno1.setSelectedIndex(0); // Seleccionar vacío o la primera opción
    }

    private void limpiarCamposEvaluacion() {
        txtTituloEval3.setText("");
        txtTituloEval.setText("");
        txtTituloEval4.setText(""); // Seleccionar "Sin grupo" o la primera opción
        txtTituloEval2.setText(""); // Seleccionar "Sin grupo" o la primera opción
    }

    private void limpiarCamposModifEvaluacion() {
        txtTituloEval5.setText("");
        txtTituloEval6.setText("");
        txtTituloEval7.setText(""); // Seleccionar "Sin grupo" o la primera opción
        txtTituloEval8.setText(""); // Seleccionar "Sin grupo" o la primera opción
    }

    private void cargarAlumnosPorAsignatura(int idAsignatura) {
        AlumnoController alumnoController = new AlumnoController();
        List<Alumno> alumnos = alumnoController.obtenerAlumnosPorAsignatura(idAsignatura);

        // Configurar modelo de tabla
        DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "Nombre", "Apellido", "Email"}, 0);
        for (Alumno alumno : alumnos) {
            model.addRow(new Object[]{
                alumno.getId(),
                alumno.getNombre(),
                alumno.getApellido(),
                alumno.getEmail()
            });
        }

        tablaAlumnos.setModel(model);
    }

    private void cargarTablaCalificacionesAlumno(int idAlumno, int idAsignatura) {
        // Controlador para obtener las calificaciones
        CalificacionController calificacionController = new CalificacionController();
        List<Calificacion> calificaciones = calificacionController.obtenerCalificacionesPorAlumnoYAsignatura(idAlumno, idAsignatura);

        // Crear modelo de tabla con las columnas adecuadas
        DefaultTableModel model = new DefaultTableModel(new String[]{"ID Evaluación", "Título", "Nota", "Peso (%)", "Fecha"}, 0);

        // Llenar la tabla con las calificaciones
        for (Calificacion calificacion : calificaciones) {
            model.addRow(new Object[]{
                calificacion.getId(),
                calificacion.getTitulo(),
                String.format("%.2f", calificacion.getNota()),
                String.format("%.2f", calificacion.getPeso()),
                new SimpleDateFormat("dd/MM/yyyy").format(calificacion.getFecha())
            });
        }

        // Asignar modelo a la tabla
        tablaCalificacionAlumno.setModel(model);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jDesktopPane1 = new javax.swing.JDesktopPane();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        lblBienvenida1 = new javax.swing.JLabel();
        closeSession1 = new javax.swing.JButton();
        jPanel8 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        lblBienvenida = new javax.swing.JLabel();
        mainPanel3 = new javax.swing.JPanel();
        jTabbedPane4 = new javax.swing.JTabbedPane();
        jPanel3 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanelNotificaciones = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();
        modNotif = new javax.swing.JButton();
        jPanel11 = new javax.swing.JPanel();
        txtTitulo1 = new javax.swing.JTextField();
        comboBoxAlumno1 = new javax.swing.JComboBox<>();
        comboBoxGrupo1 = new javax.swing.JComboBox<>();
        jScrollPane6 = new javax.swing.JScrollPane();
        txtMensaje1 = new javax.swing.JTextArea();
        addNotif = new javax.swing.JButton();
        jLabel13 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtTitulo = new javax.swing.JTextField();
        jScrollPane5 = new javax.swing.JScrollPane();
        txtMensaje = new javax.swing.JTextArea();
        lblIdNotificacion = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        modifNotificacion = new javax.swing.JButton();
        deleteNotificacion = new javax.swing.JButton();
        comboBoxGrupo = new javax.swing.JComboBox<>();
        comboBoxAlumno = new javax.swing.JComboBox<>();
        jLabel8 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jTabbedPane3 = new javax.swing.JTabbedPane();
        PanelAsignaturas = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaAsignaturas = new javax.swing.JTable();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel14 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tablaEvaluaciones = new javax.swing.JTable();
        modifEval = new javax.swing.JButton();
        VerAlumnos = new javax.swing.JButton();
        VerCalificaciones = new javax.swing.JButton();
        AddEval = new javax.swing.JButton();
        DescargarEv = new javax.swing.JButton();
        jLabel31 = new javax.swing.JLabel();
        agregarEval = new javax.swing.JPanel();
        jLabel18 = new javax.swing.JLabel();
        txtTituloEval = new javax.swing.JTextField();
        addEval = new javax.swing.JButton();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        txtTituloEval2 = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        txtTituloEval3 = new javax.swing.JTextField();
        txtTituloEval4 = new javax.swing.JFormattedTextField();
        jLabel23 = new javax.swing.JLabel();
        ModifEval = new javax.swing.JPanel();
        EliminarEvaluacion = new javax.swing.JButton();
        ModificarEvaluacion = new javax.swing.JButton();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        txtTituloEval5 = new javax.swing.JTextField();
        jLabel26 = new javax.swing.JLabel();
        txtTituloEval6 = new javax.swing.JTextField();
        jLabel27 = new javax.swing.JLabel();
        txtTituloEval7 = new javax.swing.JTextField();
        jLabel28 = new javax.swing.JLabel();
        txtTituloEval8 = new javax.swing.JFormattedTextField();
        jLabel29 = new javax.swing.JLabel();
        txtTituloEval9 = new javax.swing.JTextField();
        VerCalif = new javax.swing.JPanel();
        jLabel32 = new javax.swing.JLabel();
        jScrollPane9 = new javax.swing.JScrollPane();
        CalificacionesPorEvaluacion = new javax.swing.JTable();
        DescargarCalificaciones = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        tabAlumnos = new javax.swing.JTabbedPane();
        VerAlumnosPorA = new javax.swing.JPanel();
        jScrollPane8 = new javax.swing.JScrollPane();
        tablaAlumnos = new javax.swing.JTable();
        VerCalificacionesPorAlumno = new javax.swing.JButton();
        DescargarAlumnos = new javax.swing.JButton();
        jLabel19 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel30 = new javax.swing.JLabel();
        jScrollPane7 = new javax.swing.JScrollPane();
        tablaCalificacionAlumno = new javax.swing.JTable();
        EliminarNota = new javax.swing.JButton();
        DescargarCalificacionesAlumno = new javax.swing.JButton();
        ModificarNota = new javax.swing.JButton();
        AddNotaAlumno = new javax.swing.JButton();
        agregarNota = new javax.swing.JPanel();
        jLabel41 = new javax.swing.JLabel();
        guardarNota = new javax.swing.JButton();
        jLabel42 = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        txtTituloNota = new javax.swing.JTextField();
        txtTituloNota3 = new javax.swing.JTextField();
        jLabel52 = new javax.swing.JLabel();
        jComboTituloNota = new javax.swing.JComboBox<>();
        jLabel45 = new javax.swing.JLabel();
        txtTituloNota1 = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        ModifNota = new javax.swing.JPanel();
        EditNota = new javax.swing.JButton();
        jLabel46 = new javax.swing.JLabel();
        jLabel47 = new javax.swing.JLabel();
        txtModifEval = new javax.swing.JTextField();
        jLabel48 = new javax.swing.JLabel();
        txtModifEval3 = new javax.swing.JTextField();
        jLabel49 = new javax.swing.JLabel();
        txtModifEval4 = new javax.swing.JTextField();
        jLabel50 = new javax.swing.JLabel();
        txtModifEval5 = new javax.swing.JFormattedTextField();
        jLabel51 = new javax.swing.JLabel();
        txtModifEval2 = new javax.swing.JTextField();
        jPanel7 = new javax.swing.JPanel();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        jPanel12 = new javax.swing.JPanel();
        txtContrasena = new javax.swing.JPasswordField();
        CambioContraseña = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtContrasena2 = new javax.swing.JPasswordField();

        javax.swing.GroupLayout jDesktopPane1Layout = new javax.swing.GroupLayout(jDesktopPane1);
        jDesktopPane1.setLayout(jDesktopPane1Layout);
        jDesktopPane1Layout.setHorizontalGroup(
            jDesktopPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jDesktopPane1Layout.setVerticalGroup(
            jDesktopPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 204), 2));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setPreferredSize(new java.awt.Dimension(790, 50));

        lblBienvenida1.setBackground(new java.awt.Color(255, 255, 255));
        lblBienvenida1.setFont(new java.awt.Font("Poppins Medium", 3, 14)); // NOI18N
        lblBienvenida1.setForeground(new java.awt.Color(0, 51, 204));
        lblBienvenida1.setText("jLabel2");

        closeSession1.setBackground(new java.awt.Color(0, 0, 204));
        closeSession1.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        closeSession1.setForeground(new java.awt.Color(255, 255, 255));
        closeSession1.setText("Cerrar Sesión");
        closeSession1.setBorder(null);
        closeSession1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        closeSession1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeSession1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblBienvenida1, javax.swing.GroupLayout.PREFERRED_SIZE, 310, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(454, Short.MAX_VALUE))
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                    .addContainerGap(634, Short.MAX_VALUE)
                    .addComponent(closeSession1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(16, 16, 16)))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(13, 13, 13)
                .addComponent(lblBienvenida1)
                .addContainerGap(10, Short.MAX_VALUE))
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(closeSession1, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap()))
        );

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 770, 40));

        jPanel8.setBackground(new java.awt.Color(255, 255, 255));
        jPanel8.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 204)));
        jPanel8.setPreferredSize(new java.awt.Dimension(800, 500));
        jPanel8.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel9.setBackground(new java.awt.Color(255, 255, 255));
        jPanel9.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblBienvenida.setBackground(new java.awt.Color(255, 255, 255));
        lblBienvenida.setFont(new java.awt.Font("Poppins Medium", 3, 14)); // NOI18N
        lblBienvenida.setForeground(new java.awt.Color(0, 51, 204));
        lblBienvenida.setText("jLabel2");
        jPanel9.add(lblBienvenida, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 310, -1));

        jPanel8.add(jPanel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 780, 30));

        mainPanel3.setBackground(new java.awt.Color(255, 255, 255));
        mainPanel3.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        mainPanel3.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        mainPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jTabbedPane4.setBackground(new java.awt.Color(255, 255, 255));
        jTabbedPane4.setForeground(new java.awt.Color(0, 0, 153));
        jTabbedPane4.setToolTipText("");
        jTabbedPane4.setFocusable(false);
        jTabbedPane4.setFont(new java.awt.Font("Poppins SemiBold", 0, 14)); // NOI18N
        jTabbedPane4.setRequestFocusEnabled(false);

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        jTabbedPane1.setBackground(new java.awt.Color(255, 255, 255));
        jTabbedPane1.setForeground(new java.awt.Color(0, 0, 153));
        jTabbedPane1.setTabPlacement(javax.swing.JTabbedPane.LEFT);
        jTabbedPane1.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N

        jPanelNotificaciones.setBackground(new java.awt.Color(255, 255, 255));

        jTable1.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane2.setViewportView(jTable1);

        jTextArea2.setColumns(5);
        jTextArea2.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jTextArea2.setRows(5);
        jScrollPane3.setViewportView(jTextArea2);

        modNotif.setBackground(new java.awt.Color(0, 0, 204));
        modNotif.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        modNotif.setForeground(new java.awt.Color(255, 255, 255));
        modNotif.setText("Modificar o eliminar");
        modNotif.setBorder(null);
        modNotif.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        modNotif.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                modNotifActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelNotificacionesLayout = new javax.swing.GroupLayout(jPanelNotificaciones);
        jPanelNotificaciones.setLayout(jPanelNotificacionesLayout);
        jPanelNotificacionesLayout.setHorizontalGroup(
            jPanelNotificacionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelNotificacionesLayout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(jPanelNotificacionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelNotificacionesLayout.createSequentialGroup()
                        .addComponent(jScrollPane2)
                        .addContainerGap())
                    .addGroup(jPanelNotificacionesLayout.createSequentialGroup()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 422, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(modNotif)
                        .addGap(0, 3, Short.MAX_VALUE))))
        );
        jPanelNotificacionesLayout.setVerticalGroup(
            jPanelNotificacionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelNotificacionesLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanelNotificacionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 51, Short.MAX_VALUE)
                    .addComponent(modNotif, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 425, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Ver todas la Notificaciones", jPanelNotificaciones);

        jPanel11.setBackground(new java.awt.Color(255, 255, 255));

        txtTitulo1.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N

        comboBoxAlumno1.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        comboBoxAlumno1.setModel(comboBoxAlumno1.getModel());

        comboBoxGrupo1.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        comboBoxGrupo1.setModel(comboBoxGrupo1.getModel());

        txtMensaje1.setColumns(20);
        txtMensaje1.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        txtMensaje1.setRows(5);
        jScrollPane6.setViewportView(txtMensaje1);

        addNotif.setBackground(new java.awt.Color(0, 0, 204));
        addNotif.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        addNotif.setForeground(new java.awt.Color(255, 255, 255));
        addNotif.setText("Agregar Notificación");
        addNotif.setBorder(null);
        addNotif.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        addNotif.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addNotifActionPerformed(evt);
            }
        });

        jLabel13.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jLabel13.setText("Titulo");

        jLabel15.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jLabel15.setText("Asignado al Alumno");

        jLabel16.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jLabel16.setText("Asignado al Grupo");

        jLabel17.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jLabel17.setText("Descripción");

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGap(60, 60, 60)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel11Layout.createSequentialGroup()
                        .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(418, 418, 418))
                    .addComponent(jLabel17, javax.swing.GroupLayout.DEFAULT_SIZE, 467, Short.MAX_VALUE)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(addNotif, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(60, Short.MAX_VALUE))
            .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel11Layout.createSequentialGroup()
                    .addGap(60, 60, 60)
                    .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 467, Short.MAX_VALUE)
                        .addGroup(jPanel11Layout.createSequentialGroup()
                            .addComponent(comboBoxAlumno1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(comboBoxGrupo1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addComponent(txtTitulo1))
                    .addGap(60, 60, 60)))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGap(47, 47, 47)
                .addComponent(jLabel13)
                .addGap(43, 43, 43)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(jLabel16))
                .addGap(47, 47, 47)
                .addComponent(jLabel17)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 207, Short.MAX_VALUE)
                .addComponent(addNotif, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(72, 72, 72))
            .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel11Layout.createSequentialGroup()
                    .addGap(73, 73, 73)
                    .addComponent(txtTitulo1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(33, 33, 33)
                    .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(comboBoxGrupo1, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(comboBoxAlumno1, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(31, 31, 31)
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(140, Short.MAX_VALUE)))
        );

        jTabbedPane1.addTab("Agregar Notificación", jPanel11);

        jPanel10.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jLabel1.setText("Titulo");

        txtTitulo.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N

        txtMensaje.setColumns(20);
        txtMensaje.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        txtMensaje.setRows(5);
        jScrollPane5.setViewportView(txtMensaje);

        lblIdNotificacion.setEditable(false);
        lblIdNotificacion.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N

        jLabel4.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jLabel4.setText("Id");

        jLabel6.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jLabel6.setText("Descripción");

        modifNotificacion.setBackground(new java.awt.Color(0, 0, 204));
        modifNotificacion.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        modifNotificacion.setForeground(new java.awt.Color(255, 255, 255));
        modifNotificacion.setText("Modificar");
        modifNotificacion.setBorder(null);
        modifNotificacion.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        modifNotificacion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                modifNotificacionActionPerformed(evt);
            }
        });

        deleteNotificacion.setBackground(new java.awt.Color(204, 0, 0));
        deleteNotificacion.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        deleteNotificacion.setForeground(new java.awt.Color(255, 255, 255));
        deleteNotificacion.setText("Eliminar");
        deleteNotificacion.setBorder(null);
        deleteNotificacion.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        deleteNotificacion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteNotificacionActionPerformed(evt);
            }
        });

        comboBoxGrupo.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        comboBoxGrupo.setModel(comboBoxGrupo.getModel());

        comboBoxAlumno.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        comboBoxAlumno.setModel(comboBoxAlumno.getModel());

        jLabel8.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jLabel8.setText("Asignado al Alumno");

        jLabel12.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jLabel12.setText("Asignado al Grupo");

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGap(50, 52, Short.MAX_VALUE)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(modifNotificacion, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(deleteNotificacion, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 467, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel10Layout.createSequentialGroup()
                                        .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGap(354, 354, 354))
                                    .addGroup(jPanel10Layout.createSequentialGroup()
                                        .addComponent(txtTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, 393, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel4)
                                    .addComponent(lblIdNotificacion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 467, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel10Layout.createSequentialGroup()
                                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(comboBoxAlumno, javax.swing.GroupLayout.Alignment.LEADING, 0, 240, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(comboBoxGrupo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                        .addGap(19, 19, 19)))
                .addContainerGap(49, Short.MAX_VALUE))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGap(68, 68, 68)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblIdNotificacion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(jLabel12))
                .addGap(2, 2, 2)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(comboBoxGrupo, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(comboBoxAlumno, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(33, 33, 33)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(modifNotificacion, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(deleteNotificacion, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(54, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Modificar o Eliminar", jPanel10);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jTabbedPane1)
                .addContainerGap())
        );

        jTabbedPane4.addTab("Notificaciones", jPanel3);

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));
        jPanel6.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jTabbedPane3.setBackground(new java.awt.Color(255, 255, 255));
        jTabbedPane3.setForeground(new java.awt.Color(0, 0, 153));
        jTabbedPane3.setTabPlacement(javax.swing.JTabbedPane.LEFT);
        jTabbedPane3.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N

        PanelAsignaturas.setBackground(new java.awt.Color(255, 255, 255));

        jScrollPane1.setColumnHeaderView(null);
        jScrollPane1.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N

        tablaAsignaturas.setBackground(new java.awt.Color(255, 255, 255));
        tablaAsignaturas.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        tablaAsignaturas.setModel(new javax.swing.table.DefaultTableModel(
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
        tablaAsignaturas.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        tablaAsignaturas.setCellSelectionEnabled(true);
        tablaAsignaturas.setEditingColumn(0);
        tablaAsignaturas.setEditingRow(0);
        tablaAsignaturas.setRowHeight(30);
        tablaAsignaturas.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tablaAsignaturas.setShowGrid(false);
        tablaAsignaturas.setShowHorizontalLines(true);
        jScrollPane1.setViewportView(tablaAsignaturas);

        jLabel14.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jLabel14.setText("Evaluaciones");

        jScrollPane4.setBackground(new java.awt.Color(255, 255, 255));
        jScrollPane4.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jScrollPane4.setRowHeaderView(null);

        tablaEvaluaciones.setBackground(new java.awt.Color(255, 255, 255));
        tablaEvaluaciones.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        tablaEvaluaciones.setModel(new javax.swing.table.DefaultTableModel(
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
        tablaEvaluaciones.setColumnSelectionAllowed(true);
        tablaEvaluaciones.setEditingColumn(0);
        tablaEvaluaciones.setEditingRow(0);
        tablaEvaluaciones.setRowHeight(30);
        tablaEvaluaciones.setShowGrid(false);
        tablaEvaluaciones.setShowHorizontalLines(true);
        jScrollPane4.setViewportView(tablaEvaluaciones);

        modifEval.setBackground(new java.awt.Color(0, 0, 204));
        modifEval.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        modifEval.setForeground(new java.awt.Color(255, 255, 255));
        modifEval.setText("Modificar o Eliminar Evaluación");
        modifEval.setBorder(null);
        modifEval.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        modifEval.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                modifEvalActionPerformed(evt);
            }
        });

        VerAlumnos.setBackground(new java.awt.Color(255, 153, 0));
        VerAlumnos.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        VerAlumnos.setForeground(new java.awt.Color(255, 255, 255));
        VerAlumnos.setText("Ver Alumnos");
        VerAlumnos.setBorder(null);
        VerAlumnos.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        VerAlumnos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                VerAlumnosActionPerformed(evt);
            }
        });

        VerCalificaciones.setBackground(new java.awt.Color(204, 102, 0));
        VerCalificaciones.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        VerCalificaciones.setForeground(new java.awt.Color(255, 255, 255));
        VerCalificaciones.setText("Ver Calificaciones");
        VerCalificaciones.setBorder(null);
        VerCalificaciones.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        VerCalificaciones.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                VerCalificacionesActionPerformed(evt);
            }
        });

        AddEval.setBackground(new java.awt.Color(0, 0, 204));
        AddEval.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        AddEval.setForeground(new java.awt.Color(255, 255, 255));
        AddEval.setText("Agregar  Evaluación");
        AddEval.setBorder(null);
        AddEval.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        AddEval.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AddEvalActionPerformed(evt);
            }
        });

        DescargarEv.setBackground(new java.awt.Color(0, 153, 102));
        DescargarEv.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        DescargarEv.setForeground(new java.awt.Color(255, 255, 255));
        DescargarEv.setText("Descargar Ev.");
        DescargarEv.setToolTipText("");
        DescargarEv.setBorder(null);
        DescargarEv.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        DescargarEv.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DescargarEvActionPerformed(evt);
            }
        });

        jLabel31.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jLabel31.setText("Asignaturas");

        javax.swing.GroupLayout PanelAsignaturasLayout = new javax.swing.GroupLayout(PanelAsignaturas);
        PanelAsignaturas.setLayout(PanelAsignaturasLayout);
        PanelAsignaturasLayout.setHorizontalGroup(
            PanelAsignaturasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelAsignaturasLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PanelAsignaturasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PanelAsignaturasLayout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 540, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelAsignaturasLayout.createSequentialGroup()
                        .addGroup(PanelAsignaturasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(PanelAsignaturasLayout.createSequentialGroup()
                                .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(AddEval, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(DescargarEv, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(VerAlumnos, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, PanelAsignaturasLayout.createSequentialGroup()
                                .addComponent(modifEval, javax.swing.GroupLayout.PREFERRED_SIZE, 276, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(VerCalificaciones, javax.swing.GroupLayout.PREFERRED_SIZE, 262, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jScrollPane4, javax.swing.GroupLayout.Alignment.LEADING))
                        .addGap(20, 20, 20))
                    .addGroup(PanelAsignaturasLayout.createSequentialGroup()
                        .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        PanelAsignaturasLayout.setVerticalGroup(
            PanelAsignaturasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelAsignaturasLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel31)
                .addGap(5, 5, 5)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PanelAsignaturasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(VerAlumnos, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14)
                    .addComponent(AddEval, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(DescargarEv, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 248, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PanelAsignaturasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(modifEval, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(VerCalificaciones, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane3.addTab("Ver todas mis asignaturas", PanelAsignaturas);

        agregarEval.setBackground(new java.awt.Color(255, 255, 255));

        jLabel18.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jLabel18.setText("Titulo de la Evaluación");

        txtTituloEval.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N

        addEval.setBackground(new java.awt.Color(0, 0, 204));
        addEval.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        addEval.setForeground(new java.awt.Color(255, 255, 255));
        addEval.setText("Agregar Evaluación");
        addEval.setBorder(null);
        addEval.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        addEval.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addEvalActionPerformed(evt);
            }
        });

        jLabel20.setFont(new java.awt.Font("Poppins", 1, 18)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(0, 0, 204));
        jLabel20.setText("Agregar Evaluación");

        jLabel21.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jLabel21.setText("Peso");

        txtTituloEval2.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N

        jLabel22.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jLabel22.setText("Asignatura");

        txtTituloEval3.setEditable(false);
        txtTituloEval3.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N

        txtTituloEval4.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.DateFormatter(java.text.DateFormat.getDateInstance(java.text.DateFormat.SHORT))));
        txtTituloEval4.setToolTipText("d/MM/yy");
        txtTituloEval4.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N

        jLabel23.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jLabel23.setText("Fecha de Evaluación");

        javax.swing.GroupLayout agregarEvalLayout = new javax.swing.GroupLayout(agregarEval);
        agregarEval.setLayout(agregarEvalLayout);
        agregarEvalLayout.setHorizontalGroup(
            agregarEvalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, agregarEvalLayout.createSequentialGroup()
                .addContainerGap(166, Short.MAX_VALUE)
                .addGroup(agregarEvalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel22, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtTituloEval3, javax.swing.GroupLayout.DEFAULT_SIZE, 216, Short.MAX_VALUE)
                    .addComponent(jLabel20, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(addEval, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtTituloEval2, javax.swing.GroupLayout.DEFAULT_SIZE, 216, Short.MAX_VALUE)
                    .addComponent(jLabel21, javax.swing.GroupLayout.DEFAULT_SIZE, 216, Short.MAX_VALUE)
                    .addComponent(txtTituloEval)
                    .addComponent(txtTituloEval4)
                    .addComponent(jLabel23, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 216, Short.MAX_VALUE))
                .addGap(180, 180, 180))
        );
        agregarEvalLayout.setVerticalGroup(
            agregarEvalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(agregarEvalLayout.createSequentialGroup()
                .addGap(59, 59, 59)
                .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel22)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtTituloEval3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel18)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtTituloEval2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel21)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtTituloEval, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(jLabel23)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtTituloEval4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(addEval, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        agregarEvalLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel18, jLabel21, txtTituloEval, txtTituloEval2});

        jTabbedPane3.addTab("Agregar Evaluaciones", agregarEval);

        ModifEval.setBackground(new java.awt.Color(255, 255, 255));

        EliminarEvaluacion.setBackground(new java.awt.Color(153, 0, 0));
        EliminarEvaluacion.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        EliminarEvaluacion.setForeground(new java.awt.Color(255, 255, 255));
        EliminarEvaluacion.setText("Eliminar Evaluación");
        EliminarEvaluacion.setBorder(null);
        EliminarEvaluacion.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        EliminarEvaluacion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EliminarEvaluacionActionPerformed(evt);
            }
        });

        ModificarEvaluacion.setBackground(new java.awt.Color(0, 0, 204));
        ModificarEvaluacion.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        ModificarEvaluacion.setForeground(new java.awt.Color(255, 255, 255));
        ModificarEvaluacion.setText("Modificar Evaluación");
        ModificarEvaluacion.setBorder(null);
        ModificarEvaluacion.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        ModificarEvaluacion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ModificarEvaluacionActionPerformed(evt);
            }
        });

        jLabel24.setFont(new java.awt.Font("Poppins", 1, 18)); // NOI18N
        jLabel24.setForeground(new java.awt.Color(0, 0, 204));
        jLabel24.setText("Editar o Eliminar Evaluación");

        jLabel25.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jLabel25.setText("Asignatura");

        txtTituloEval5.setEditable(false);
        txtTituloEval5.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N

        jLabel26.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jLabel26.setText("Titulo de la Evaluación");

        txtTituloEval6.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N

        jLabel27.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jLabel27.setText("Peso");

        txtTituloEval7.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N

        jLabel28.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jLabel28.setText("Fecha de Evaluación");

        txtTituloEval8.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.DateFormatter(java.text.DateFormat.getDateInstance(java.text.DateFormat.SHORT))));
        txtTituloEval8.setToolTipText("d/MM/yy");
        txtTituloEval8.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N

        jLabel29.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jLabel29.setText("Id");

        txtTituloEval9.setEditable(false);
        txtTituloEval9.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N

        javax.swing.GroupLayout ModifEvalLayout = new javax.swing.GroupLayout(ModifEval);
        ModifEval.setLayout(ModifEvalLayout);
        ModifEvalLayout.setHorizontalGroup(
            ModifEvalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ModifEvalLayout.createSequentialGroup()
                .addGap(94, 94, 94)
                .addGroup(ModifEvalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(ModifEvalLayout.createSequentialGroup()
                        .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 383, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(ModifEvalLayout.createSequentialGroup()
                        .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 216, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ModifEvalLayout.createSequentialGroup()
                        .addGroup(ModifEvalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(ModifEvalLayout.createSequentialGroup()
                                .addGroup(ModifEvalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txtTituloEval7, javax.swing.GroupLayout.DEFAULT_SIZE, 191, Short.MAX_VALUE)
                                    .addComponent(jLabel27, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(ModifEvalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel28, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(txtTituloEval8)))
                            .addComponent(txtTituloEval6, javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(ModifEvalLayout.createSequentialGroup()
                                .addComponent(ModificarEvaluacion, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(EliminarEvaluacion, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, ModifEvalLayout.createSequentialGroup()
                                .addGroup(ModifEvalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtTituloEval5, javax.swing.GroupLayout.PREFERRED_SIZE, 216, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(ModifEvalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel29, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(txtTituloEval9))))
                        .addGap(113, 113, 113))))
        );
        ModifEvalLayout.setVerticalGroup(
            ModifEvalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ModifEvalLayout.createSequentialGroup()
                .addGap(88, 88, 88)
                .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(27, 27, 27)
                .addGroup(ModifEvalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel25)
                    .addComponent(jLabel29))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(ModifEvalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtTituloEval5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTituloEval9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel26)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtTituloEval6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(ModifEvalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel27)
                    .addComponent(jLabel28))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(ModifEvalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtTituloEval7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTituloEval8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 41, Short.MAX_VALUE)
                .addGroup(ModifEvalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(EliminarEvaluacion, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ModificarEvaluacion, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(120, 120, 120))
        );

        jTabbedPane3.addTab("Modificar o Eliminar Evaluación", ModifEval);

        VerCalif.setBackground(new java.awt.Color(255, 255, 255));
        VerCalif.setForeground(new java.awt.Color(255, 255, 255));

        jLabel32.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jLabel32.setText("Asignaturas");

        CalificacionesPorEvaluacion.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        CalificacionesPorEvaluacion.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane9.setViewportView(CalificacionesPorEvaluacion);

        DescargarCalificaciones.setBackground(new java.awt.Color(0, 153, 102));
        DescargarCalificaciones.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        DescargarCalificaciones.setForeground(new java.awt.Color(255, 255, 255));
        DescargarCalificaciones.setText("Descargar Calificaciones");
        DescargarCalificaciones.setToolTipText("");
        DescargarCalificaciones.setBorder(null);
        DescargarCalificaciones.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        DescargarCalificaciones.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DescargarCalificacionesActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout VerCalifLayout = new javax.swing.GroupLayout(VerCalif);
        VerCalif.setLayout(VerCalifLayout);
        VerCalifLayout.setHorizontalGroup(
            VerCalifLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(VerCalifLayout.createSequentialGroup()
                .addGroup(VerCalifLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(VerCalifLayout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addGroup(VerCalifLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane9, javax.swing.GroupLayout.DEFAULT_SIZE, 544, Short.MAX_VALUE)
                            .addComponent(jLabel32, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, VerCalifLayout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(DescargarCalificaciones, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        VerCalifLayout.setVerticalGroup(
            VerCalifLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(VerCalifLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel32)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, 410, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(DescargarCalificaciones, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane3.addTab("Ver Notas/ Evaluacion", VerCalif);

        jPanel6.add(jTabbedPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 780, 510));

        jTabbedPane4.addTab("Asignaturas", jPanel6);

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        tabAlumnos.setBackground(new java.awt.Color(255, 255, 255));
        tabAlumnos.setForeground(new java.awt.Color(0, 0, 153));
        tabAlumnos.setTabPlacement(javax.swing.JTabbedPane.LEFT);
        tabAlumnos.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N

        VerAlumnosPorA.setBackground(new java.awt.Color(255, 255, 255));

        jScrollPane8.setColumnHeaderView(null);
        jScrollPane8.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N

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
        tablaAlumnos.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        tablaAlumnos.setCellSelectionEnabled(true);
        tablaAlumnos.setEditingColumn(0);
        tablaAlumnos.setEditingRow(0);
        tablaAlumnos.setRowHeight(30);
        tablaAlumnos.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tablaAlumnos.setShowGrid(false);
        tablaAlumnos.setShowHorizontalLines(true);
        jScrollPane8.setViewportView(tablaAlumnos);

        VerCalificacionesPorAlumno.setBackground(new java.awt.Color(204, 102, 0));
        VerCalificacionesPorAlumno.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        VerCalificacionesPorAlumno.setForeground(new java.awt.Color(255, 255, 255));
        VerCalificacionesPorAlumno.setText("Ver Calificaciones");
        VerCalificacionesPorAlumno.setBorder(null);
        VerCalificacionesPorAlumno.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        VerCalificacionesPorAlumno.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                VerCalificacionesPorAlumnoActionPerformed(evt);
            }
        });

        DescargarAlumnos.setBackground(new java.awt.Color(0, 153, 102));
        DescargarAlumnos.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        DescargarAlumnos.setForeground(new java.awt.Color(255, 255, 255));
        DescargarAlumnos.setText("Descargar Alumnos");
        DescargarAlumnos.setToolTipText("");
        DescargarAlumnos.setBorder(null);
        DescargarAlumnos.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        DescargarAlumnos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DescargarAlumnosActionPerformed(evt);
            }
        });

        jLabel19.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jLabel19.setText("Alumnos");

        javax.swing.GroupLayout VerAlumnosPorALayout = new javax.swing.GroupLayout(VerAlumnosPorA);
        VerAlumnosPorA.setLayout(VerAlumnosPorALayout);
        VerAlumnosPorALayout.setHorizontalGroup(
            VerAlumnosPorALayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(VerAlumnosPorALayout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(VerAlumnosPorALayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(VerAlumnosPorALayout.createSequentialGroup()
                        .addComponent(jLabel19)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, VerAlumnosPorALayout.createSequentialGroup()
                        .addGroup(VerAlumnosPorALayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jScrollPane8, javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(VerAlumnosPorALayout.createSequentialGroup()
                                .addComponent(DescargarAlumnos, javax.swing.GroupLayout.DEFAULT_SIZE, 303, Short.MAX_VALUE)
                                .addGap(18, 18, 18)
                                .addComponent(VerCalificacionesPorAlumno, javax.swing.GroupLayout.PREFERRED_SIZE, 262, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(12, 12, 12))))
        );
        VerAlumnosPorALayout.setVerticalGroup(
            VerAlumnosPorALayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(VerAlumnosPorALayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel19)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 414, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(VerAlumnosPorALayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(VerCalificacionesPorAlumno, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(DescargarAlumnos, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tabAlumnos.addTab("Ver todas los alumnos", VerAlumnosPorA);

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));

        jLabel30.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jLabel30.setText("Alumnos");

        tablaCalificacionAlumno.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        tablaCalificacionAlumno.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane7.setViewportView(tablaCalificacionAlumno);

        EliminarNota.setBackground(new java.awt.Color(204, 0, 0));
        EliminarNota.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        EliminarNota.setForeground(new java.awt.Color(255, 255, 255));
        EliminarNota.setText("Eliminar Notas");
        EliminarNota.setToolTipText("");
        EliminarNota.setBorder(null);
        EliminarNota.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        EliminarNota.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EliminarNotaActionPerformed(evt);
            }
        });

        DescargarCalificacionesAlumno.setBackground(new java.awt.Color(204, 102, 0));
        DescargarCalificacionesAlumno.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        DescargarCalificacionesAlumno.setForeground(new java.awt.Color(255, 255, 255));
        DescargarCalificacionesAlumno.setText("Descargar Notas");
        DescargarCalificacionesAlumno.setToolTipText("");
        DescargarCalificacionesAlumno.setBorder(null);
        DescargarCalificacionesAlumno.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        DescargarCalificacionesAlumno.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DescargarCalificacionesAlumnoActionPerformed(evt);
            }
        });

        ModificarNota.setBackground(new java.awt.Color(0, 0, 204));
        ModificarNota.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        ModificarNota.setForeground(new java.awt.Color(255, 255, 255));
        ModificarNota.setText("Modificar Notas");
        ModificarNota.setToolTipText("");
        ModificarNota.setBorder(null);
        ModificarNota.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        ModificarNota.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ModificarNotaActionPerformed(evt);
            }
        });

        AddNotaAlumno.setBackground(new java.awt.Color(0, 153, 0));
        AddNotaAlumno.setFont(new java.awt.Font("Poppins", 1, 18)); // NOI18N
        AddNotaAlumno.setForeground(new java.awt.Color(255, 255, 255));
        AddNotaAlumno.setText("+");
        AddNotaAlumno.setToolTipText("");
        AddNotaAlumno.setBorder(null);
        AddNotaAlumno.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        AddNotaAlumno.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AddNotaAlumnoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane7)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel30)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(DescargarCalificacionesAlumno, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(AddNotaAlumno, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ModificarNota, javax.swing.GroupLayout.DEFAULT_SIZE, 221, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(EliminarNota, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel30)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 422, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(EliminarNota, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(DescargarCalificacionesAlumno, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ModificarNota, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(AddNotaAlumno, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(17, Short.MAX_VALUE))
        );

        tabAlumnos.addTab("Ver Calificaciones", jPanel5);

        agregarNota.setBackground(new java.awt.Color(255, 255, 255));

        jLabel41.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jLabel41.setText("Evaluación");

        guardarNota.setBackground(new java.awt.Color(0, 0, 204));
        guardarNota.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        guardarNota.setForeground(new java.awt.Color(255, 255, 255));
        guardarNota.setText("Guardar Nota");
        guardarNota.setBorder(null);
        guardarNota.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        guardarNota.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                guardarNotaActionPerformed(evt);
            }
        });

        jLabel42.setFont(new java.awt.Font("Poppins", 1, 18)); // NOI18N
        jLabel42.setForeground(new java.awt.Color(0, 0, 204));
        jLabel42.setText("Agregar Nota");

        jLabel44.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jLabel44.setText("Alumno");

        txtTituloNota.setEditable(false);
        txtTituloNota.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N

        txtTituloNota3.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N

        jLabel52.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jLabel52.setText("Nota");

        jComboTituloNota.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jComboTituloNota.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel45.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jLabel45.setText("Asignatura");

        txtTituloNota1.setEditable(false);
        txtTituloNota1.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N

        jLabel2.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jLabel2.setText("jLabel2");

        javax.swing.GroupLayout agregarNotaLayout = new javax.swing.GroupLayout(agregarNota);
        agregarNota.setLayout(agregarNotaLayout);
        agregarNotaLayout.setHorizontalGroup(
            agregarNotaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, agregarNotaLayout.createSequentialGroup()
                .addContainerGap(204, Short.MAX_VALUE)
                .addGroup(agregarNotaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel52, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtTituloNota3, javax.swing.GroupLayout.DEFAULT_SIZE, 216, Short.MAX_VALUE)
                    .addComponent(jLabel44, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtTituloNota, javax.swing.GroupLayout.DEFAULT_SIZE, 216, Short.MAX_VALUE)
                    .addComponent(jLabel42, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(guardarNota, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jComboTituloNota, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel45, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtTituloNota1)
                    .addGroup(agregarNotaLayout.createSequentialGroup()
                        .addComponent(jLabel41, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(192, 192, 192))
        );
        agregarNotaLayout.setVerticalGroup(
            agregarNotaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(agregarNotaLayout.createSequentialGroup()
                .addGap(77, 77, 77)
                .addComponent(jLabel42, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel44)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtTituloNota, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel45)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtTituloNota1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(agregarNotaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel41)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboTituloNota, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel52)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtTituloNota3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(16, 16, 16)
                .addComponent(guardarNota, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(110, Short.MAX_VALUE))
        );

        tabAlumnos.addTab("Agregar Notas", agregarNota);

        ModifNota.setBackground(new java.awt.Color(255, 255, 255));

        EditNota.setBackground(new java.awt.Color(0, 0, 204));
        EditNota.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        EditNota.setForeground(new java.awt.Color(255, 255, 255));
        EditNota.setText("Modificar Evaluación");
        EditNota.setBorder(null);
        EditNota.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        EditNota.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EditNotaActionPerformed(evt);
            }
        });

        jLabel46.setFont(new java.awt.Font("Poppins", 1, 18)); // NOI18N
        jLabel46.setForeground(new java.awt.Color(0, 0, 204));
        jLabel46.setText("Editar o Eliminar Evaluación");

        jLabel47.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jLabel47.setText("Asignatura");

        txtModifEval.setEditable(false);
        txtModifEval.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N

        jLabel48.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jLabel48.setText("Evaluación");

        txtModifEval3.setEditable(false);
        txtModifEval3.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N

        jLabel49.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jLabel49.setText("Nota");

        txtModifEval4.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N

        jLabel50.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jLabel50.setText("Peso");

        txtModifEval5.setEditable(false);
        txtModifEval5.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.DateFormatter(java.text.DateFormat.getDateInstance(java.text.DateFormat.SHORT))));
        txtModifEval5.setToolTipText("d/MM/yy");
        txtModifEval5.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N

        jLabel51.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jLabel51.setText("Id");

        txtModifEval2.setEditable(false);
        txtModifEval2.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N

        javax.swing.GroupLayout ModifNotaLayout = new javax.swing.GroupLayout(ModifNota);
        ModifNota.setLayout(ModifNotaLayout);
        ModifNotaLayout.setHorizontalGroup(
            ModifNotaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ModifNotaLayout.createSequentialGroup()
                .addGap(94, 94, 94)
                .addGroup(ModifNotaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(ModifNotaLayout.createSequentialGroup()
                        .addComponent(jLabel46, javax.swing.GroupLayout.PREFERRED_SIZE, 383, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(ModifNotaLayout.createSequentialGroup()
                        .addComponent(jLabel48, javax.swing.GroupLayout.PREFERRED_SIZE, 216, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ModifNotaLayout.createSequentialGroup()
                        .addGroup(ModifNotaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(ModifNotaLayout.createSequentialGroup()
                                .addGroup(ModifNotaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txtModifEval4, javax.swing.GroupLayout.DEFAULT_SIZE, 191, Short.MAX_VALUE)
                                    .addComponent(jLabel49, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(ModifNotaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel50, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(txtModifEval5)))
                            .addComponent(txtModifEval3, javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, ModifNotaLayout.createSequentialGroup()
                                .addGroup(ModifNotaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtModifEval, javax.swing.GroupLayout.PREFERRED_SIZE, 216, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel47, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(ModifNotaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel51, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(txtModifEval2, javax.swing.GroupLayout.DEFAULT_SIZE, 183, Short.MAX_VALUE)))
                            .addComponent(EditNota, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(113, 113, 113))))
        );
        ModifNotaLayout.setVerticalGroup(
            ModifNotaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ModifNotaLayout.createSequentialGroup()
                .addGap(88, 88, 88)
                .addComponent(jLabel46, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(27, 27, 27)
                .addGroup(ModifNotaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel47)
                    .addComponent(jLabel51))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(ModifNotaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtModifEval, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtModifEval2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel48)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtModifEval3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(ModifNotaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel49)
                    .addComponent(jLabel50))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(ModifNotaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtModifEval4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtModifEval5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 38, Short.MAX_VALUE)
                .addComponent(EditNota, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(120, 120, 120))
        );

        tabAlumnos.addTab("Modificar Notas", ModifNota);

        jPanel4.add(tabAlumnos, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 780, 510));

        jTabbedPane4.addTab("Alumnos", jPanel4);

        jPanel7.setBackground(new java.awt.Color(255, 255, 255));
        jPanel7.setDoubleBuffered(false);
        jPanel7.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jTabbedPane2.setForeground(new java.awt.Color(0, 0, 153));
        jTabbedPane2.setTabPlacement(javax.swing.JTabbedPane.LEFT);
        jTabbedPane2.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N

        jPanel12.setBackground(new java.awt.Color(255, 255, 255));
        jPanel12.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        txtContrasena.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        txtContrasena.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Contraseña", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Poppins", 1, 14), new java.awt.Color(0, 0, 204))); // NOI18N
        jPanel12.add(txtContrasena, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 250, 230, 50));

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
        jPanel12.add(CambioContraseña, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 380, 230, 30));

        jLabel7.setFont(new java.awt.Font("Poppins", 1, 18)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(0, 0, 204));
        jLabel7.setText("CAMBIO DE CONTRASEÑA");
        jPanel12.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 200, -1, -1));

        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/logoPeque.png"))); // NOI18N
        jPanel12.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 120, 80, 70));

        txtContrasena2.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        txtContrasena2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Repetir Contraseña", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Poppins", 1, 14), new java.awt.Color(0, 0, 204))); // NOI18N
        jPanel12.add(txtContrasena2, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 310, 230, 50));

        jTabbedPane2.addTab("Cambio de Contraseña", jPanel12);

        jPanel7.add(jTabbedPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 780, 510));

        jTabbedPane4.addTab("Configuración", jPanel7);

        mainPanel3.add(jTabbedPane4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 23, 780, 540));

        jPanel8.add(mainPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 36, 780, 560));

        jPanel1.add(jPanel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 800, 600));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void closeSession1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeSession1ActionPerformed
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
    }//GEN-LAST:event_closeSession1ActionPerformed

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
            //Cambiar la contraseña
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

    private void ModificarEvaluacionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ModificarEvaluacionActionPerformed
        String tituloModif = txtTituloEval6.getText(); // Título de la evaluación
        String pesoStrModif = txtTituloEval7.getText().trim().replace(",", "."); // Peso, con coma reemplazada por punto
        String fechaStrModif = txtTituloEval8.getText(); // Fecha

        // Validar campos vacíos
        if (tituloModif.isEmpty() || pesoStrModif.isEmpty() || fechaStrModif.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, completa todos los campos.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Validar peso como número
            double peso = Double.parseDouble(pesoStrModif);

            // Validar fecha
            Date fecha = new java.text.SimpleDateFormat("dd/MM/yy").parse(fechaStrModif);

            // Obtener la fila seleccionada en la tabla de asignaturas
            int selectedRow = tablaAsignaturas.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Selecciona una asignatura primero.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Obtener ID de la asignatura y evaluación
            int idAsignatura = Integer.parseInt(tablaAsignaturas.getValueAt(selectedRow, 0).toString());
            int idEvaluacion = Integer.parseInt(tablaEvaluaciones.getValueAt(tablaEvaluaciones.getSelectedRow(), 0).toString());

            // Crear objeto Evaluacion con los nuevos datos
            Evaluacion evaluacionModificada = new Evaluacion(idEvaluacion, tituloModif, peso, idAsignatura, fecha);

            // Actualizar en la base de datos
            EvaluacionController evaluacionController = new EvaluacionController();
            if (evaluacionController.actualizarEvaluacion(evaluacionModificada)) {
                JOptionPane.showMessageDialog(this, "Evaluación modificada correctamente.");
                cargarEvaluacionesPorAsignatura(idAsignatura); // Recargar evaluaciones en la tabla
                jTabbedPane3.setSelectedComponent(PanelAsignaturas);
            } else {
                JOptionPane.showMessageDialog(this, "Error al modificar la evaluación.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El peso debe ser un número válido.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (java.text.ParseException e) {
            JOptionPane.showMessageDialog(this, "La fecha debe tener el formato 'dd/MM/yy'.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_ModificarEvaluacionActionPerformed

    private void EliminarEvaluacionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EliminarEvaluacionActionPerformed
        int selectedRow = tablaEvaluaciones.getSelectedRow(); // Obtener la fila seleccionada en la tabla

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, selecciona una evaluación para eliminar.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "¿Estás seguro de que deseas eliminar esta evaluación?", "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            // Obtener el ID de la evaluación de la columna correspondiente
            int idEvaluacion = Integer.parseInt(tablaEvaluaciones.getValueAt(selectedRow, 0).toString());

            // Llamar al controlador para eliminar la evaluación
            EvaluacionController evaluacionController = new EvaluacionController();
            if (evaluacionController.eliminarEvaluacion(idEvaluacion)) {
                JOptionPane.showMessageDialog(this, "Evaluación eliminada correctamente.");

                // Recargar las evaluaciones para reflejar los cambios en la tabla
                int idAsignatura = Integer.parseInt(tablaAsignaturas.getValueAt(tablaAsignaturas.getSelectedRow(), 0).toString());
                cargarEvaluacionesPorAsignatura(idAsignatura);
                jTabbedPane3.setSelectedComponent(PanelAsignaturas);
            } else {
                JOptionPane.showMessageDialog(this, "Error al eliminar la evaluación.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_EliminarEvaluacionActionPerformed

    private void addEvalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addEvalActionPerformed
        String titulo = txtTituloEval2.getText();
        String pesoStr = txtTituloEval.getText().trim().replace(",", ".");
        System.out.println("Valor de pesoStr: '" + pesoStr + "'");
        String asignatura = txtTituloEval3.getText(); // Ya seleccionado previamente
        String fechaStr = txtTituloEval4.getText();

        // Intentar parsear el texto a un número
        // Aquí puedes continuar con el proceso, como agregar la evaluación
        if (titulo.isEmpty() || pesoStr.isEmpty() || fechaStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, completa todos los campos.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            double peso = Double.parseDouble(pesoStr); // Convertir el peso a double
            System.out.println("Peso válido: " + peso);
            Date fecha = new java.text.SimpleDateFormat("d/MM/yy").parse(fechaStr); // Convertir la fecha

            // Obtener ID de la asignatura seleccionada
            int selectedRow = tablaAsignaturas.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Selecciona una asignatura primero.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int idAsignatura = Integer.parseInt(tablaAsignaturas.getValueAt(selectedRow, 0).toString());

            // Crear objeto Evaluacion
            Evaluacion nuevaEvaluacion = new Evaluacion(0, titulo, peso, idAsignatura, fecha);

            // Guardar en la base de datos
            EvaluacionController evaluacionController = new EvaluacionController();
            if (evaluacionController.agregarEvaluacion(nuevaEvaluacion)) {
                JOptionPane.showMessageDialog(this, "Evaluación agregada correctamente.");
                cargarEvaluacionesPorAsignatura(idAsignatura); // Recargar tabla
                limpiarCamposEvaluacion();
            } else {
                JOptionPane.showMessageDialog(this, "Error al agregar la evaluación.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El peso debe ser un número válido.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (java.text.ParseException e) {
            JOptionPane.showMessageDialog(this, "La fecha debe tener el formato 'yyyy-MM-dd'.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_addEvalActionPerformed

    private void DescargarEvActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DescargarEvActionPerformed
        int selectedRow = tablaAsignaturas.getSelectedRow();
        if (selectedRow != -1) {
            String nombreAsignatura = tablaAsignaturas.getValueAt(selectedRow, 1).toString();
            int idAsignatura = Integer.parseInt(tablaAsignaturas.getValueAt(selectedRow, 0).toString());

            // Asegúrate de tener el ID del profesor en tu modelo o variable
            int idProfesor = docente.getId();

            PDFGenerator.generarPDFExamenesPorAsignatura(idAsignatura, nombreAsignatura, idProfesor);
        } else {
            JOptionPane.showMessageDialog(this, "Por favor, selecciona una asignatura primero.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_DescargarEvActionPerformed

    private void AddEvalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddEvalActionPerformed
        int selectedRow = tablaAsignaturas.getSelectedRow();
        if (selectedRow != -1) {
            int idAsignatura = Integer.parseInt(tablaAsignaturas.getValueAt(selectedRow, 0).toString());
            cargarEvaluacionesPorAsignatura(idAsignatura);
            txtTituloEval3.setText(tablaAsignaturas.getValueAt(selectedRow, 1).toString());
            jTabbedPane3.setSelectedComponent(agregarEval);
        }
    }//GEN-LAST:event_AddEvalActionPerformed

    private void VerCalificacionesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_VerCalificacionesActionPerformed
        int selectedRowAsignatura = tablaAsignaturas.getSelectedRow();
        int selectedRowEvaluacion = tablaEvaluaciones.getSelectedRow();

        if (selectedRowAsignatura == -1 || selectedRowEvaluacion == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, selecciona una asignatura y una evaluación.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Obtener datos de la asignatura y la evaluación seleccionada
        int idAsignatura = Integer.parseInt(tablaAsignaturas.getValueAt(selectedRowAsignatura, 0).toString());
        String nombreAsignatura = tablaAsignaturas.getValueAt(selectedRowAsignatura, 1).toString();

        int idEvaluacion = Integer.parseInt(tablaEvaluaciones.getValueAt(selectedRowEvaluacion, 0).toString());
        String nombreEvaluacion = tablaEvaluaciones.getValueAt(selectedRowEvaluacion, 1).toString();

        // Cambiar el texto del jLabel32
        jLabel32.setText("Notas de " + nombreEvaluacion);

        // Obtener alumnos de la asignatura
        AlumnoController alumnoController = new AlumnoController();
        List<Alumno> alumnos = alumnoController.obtenerAlumnosPorAsignatura(idAsignatura);

        // Obtener las calificaciones de la evaluación
        CalificacionController calificacionController = new CalificacionController();
        List<Calificacion> calificaciones = calificacionController.obtenerCalificacionesPorEvaluacion(idEvaluacion);

        // Crear un modelo para la tabla
        DefaultTableModel model = new DefaultTableModel(new String[]{"Alumno", "Nota", "Fecha"}, 0);

        // Agregar datos a la tabla
        for (Alumno alumno : alumnos) {
            // Buscar si el alumno ya tiene una calificación para esta evaluación
            Calificacion calificacion = calificaciones.stream()
                    .filter(c -> c.getIdAlumno() == alumno.getId())
                    .findFirst()
                    .orElse(null);

            if (calificacion != null) {
                // Alumno con calificación
                model.addRow(new Object[]{
                    alumno.getNombre() + " " + alumno.getApellido(), // Nombre completo del alumno
                    calificacion.getNota(), // Nota
                    new java.text.SimpleDateFormat("dd/MM/yyyy").format(calificacion.getFecha()) // Fecha
                });
            } else {
                // Alumno sin calificación
                model.addRow(new Object[]{
                    alumno.getNombre() + " " + alumno.getApellido(), // Nombre completo del alumno
                    "No calificado", // Sin nota
                    "-" // Sin fecha
                });
            }
        }

        // Asignar modelo a la tabla
        CalificacionesPorEvaluacion.setModel(model);

        // Cambiar a la pestaña de calificaciones
        jTabbedPane3.setSelectedComponent(VerCalif);
    }//GEN-LAST:event_VerCalificacionesActionPerformed

    private void VerAlumnosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_VerAlumnosActionPerformed

        int selectedRow = tablaAsignaturas.getSelectedRow();
        if (selectedRow != -1) {
            int idAsignatura = Integer.parseInt(tablaAsignaturas.getValueAt(selectedRow, 0).toString());
            cargarAlumnosPorAsignatura(idAsignatura);

        } else {
            JOptionPane.showMessageDialog(this, "Por favor, selecciona una asignatura primero.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_VerAlumnosActionPerformed

    private void modifEvalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_modifEvalActionPerformed
        int selectedRowAsignatura = tablaAsignaturas.getSelectedRow();
        int selectedRowEvaluacion = tablaEvaluaciones.getSelectedRow();

        if (selectedRowAsignatura == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona una asignatura primero.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (selectedRowEvaluacion == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona una evaluación primero.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Asignar datos de la asignatura y evaluación seleccionada
            txtTituloEval5.setText(tablaAsignaturas.getValueAt(selectedRowAsignatura, 1).toString()); // Nombre de la asignatura
            txtTituloEval9.setText(tablaEvaluaciones.getValueAt(selectedRowEvaluacion, 0).toString()); // ID de evaluación
            txtTituloEval6.setText(tablaEvaluaciones.getValueAt(selectedRowEvaluacion, 1).toString()); // Título
            txtTituloEval7.setText(tablaEvaluaciones.getValueAt(selectedRowEvaluacion, 2).toString()); // Peso
            txtTituloEval8.setText(tablaEvaluaciones.getValueAt(selectedRowEvaluacion, 3).toString()); // Fecha

            // Cambiar al panel de modificación
            jTabbedPane3.setSelectedComponent(ModifEval);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Ocurrió un error al cargar los datos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_modifEvalActionPerformed

    private void deleteNotificacionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteNotificacionActionPerformed
        int confirm = JOptionPane.showConfirmDialog(this, "¿Estás seguro de que deseas eliminar esta notificación?", "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            int idNotificacion = Integer.parseInt(lblIdNotificacion.getText());
            NotificacionController notificacionController = new NotificacionController();
            if (notificacionController.eliminarNotificacion(idNotificacion)) {
                JOptionPane.showMessageDialog(this, "Notificación eliminada correctamente.");
                cargarNotificaciones();
                limpiarCamposModifEvaluacion();
                jTabbedPane1.setSelectedComponent(jPanelNotificaciones); // Volver a la pestaña principal
            } else {
                JOptionPane.showMessageDialog(this, "Error al eliminar la notificación.");
            }
        }
    }//GEN-LAST:event_deleteNotificacionActionPerformed

    private void modifNotificacionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_modifNotificacionActionPerformed
        int idNotificacion = Integer.parseInt(lblIdNotificacion.getText());
        String nuevoTitulo = txtTitulo.getText();
        String nuevoMensaje = txtMensaje.getText();
        String nuevoGrupo = (String) comboBoxGrupo.getSelectedItem();
        String nuevoAlumno = (String) comboBoxAlumno.getSelectedItem();
        Integer idGrupo = null;
        Integer idAlumno = null;

        // Obtener IDs correspondientes
        GrupoController grupoController = new GrupoController();
        AlumnoController alumnoController = new AlumnoController();
        if (!"Sin grupo".equals(nuevoGrupo)) {
            idGrupo = grupoController.obtenerIdGrupoPorNombre(nuevoGrupo);
        }
        if (!"Sin alumno".equals(nuevoAlumno)) {
            idAlumno = alumnoController.obtenerIdAlumnoPorNombre(nuevoAlumno);
        }

        // Crear la notificación actualizada
        Notificacion notificacion = new Notificacion(idNotificacion, nuevoTitulo, nuevoMensaje, new Date(), idAlumno, idGrupo, docente.getId());

        // Actualizar en la base de datos
        NotificacionController notificacionController = new NotificacionController();
        if (notificacionController.actualizarNotificacion(notificacion)) {
            JOptionPane.showMessageDialog(this, "Notificación actualizada correctamente.");
            cargarNotificaciones();
            limpiarCamposModifEvaluacion();
            jTabbedPane1.setSelectedComponent(jPanelNotificaciones); // Volver a la pestaña principal
        } else {
            JOptionPane.showMessageDialog(this, "Error al actualizar la notificación.");
        }
    }//GEN-LAST:event_modifNotificacionActionPerformed

    private void addNotifActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addNotifActionPerformed

        String nuevoTitulo1 = txtTitulo1.getText();
        String nuevoMensaje1 = txtMensaje1.getText();
        String nuevoGrupo1 = (String) comboBoxGrupo1.getSelectedItem();
        String nuevoAlumno1 = (String) comboBoxAlumno1.getSelectedItem();
        Integer idGrupo1 = null;
        Integer idAlumno1 = null;
        if (nuevoTitulo1.isEmpty() || nuevoMensaje1.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El título y el mensaje no pueden estar vacíos.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // Obtener IDs correspondientes
        GrupoController grupoController = new GrupoController();
        AlumnoController alumnoController = new AlumnoController();
        if (!"Sin grupo".equals(nuevoGrupo1)) {
            idGrupo1 = grupoController.obtenerIdGrupoPorNombre(nuevoGrupo1);
        }

        if (!"Sin alumno".equals(nuevoAlumno1)) {
            idAlumno1 = alumnoController.obtenerIdAlumnoPorNombre(nuevoAlumno1);
        }

        // Crear la nueva notificación
        Notificacion nuevaNotificacion = new Notificacion(
                0, // Se genera automáticamente en la base de datos
                nuevoTitulo1,
                nuevoMensaje1,
                new Date(), // Fecha actual
                idAlumno1,
                idGrupo1,
                docente.getId() // El docente actual
        );

        // Guardar en la base de datos
        NotificacionController notificacionController = new NotificacionController();
        if (notificacionController.agregarNotificacion(nuevaNotificacion)) {
            JOptionPane.showMessageDialog(this, "Notificación agregada correctamente.");
            cargarNotificaciones(); // Recargar la tabla con las notificaciones actualizadas
            limpiarCamposNotificacion(); // Limpiar los campos de entrada
            jTabbedPane1.setSelectedComponent(jPanelNotificaciones); // Volver a la pestaña principal
        } else {
            JOptionPane.showMessageDialog(this, "Error al agregar la notificación.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_addNotifActionPerformed

    private void modNotifActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_modNotifActionPerformed
        int selectedRow = jTable1.getSelectedRow();
        if (selectedRow != -1) {
            // Obtén el ID de la notificación seleccionada
            int idNotificacion = (int) jTable1.getValueAt(selectedRow, 0);

            // Cambiar a la pestaña de modificar/eliminar
            abrirTabModificarEliminar(idNotificacion);
        } else {
            JOptionPane.showMessageDialog(this, "Por favor, selecciona una notificación primero.");
        }
    }//GEN-LAST:event_modNotifActionPerformed

    private void DescargarAlumnosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DescargarAlumnosActionPerformed
        int selectedRow = tablaAsignaturas.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona una asignatura primero.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int idAsignatura = Integer.parseInt(tablaAsignaturas.getValueAt(selectedRow, 0).toString());
        String nombreAsignatura = tablaAsignaturas.getValueAt(selectedRow, 1).toString(); // Ajusta según tu tabla
        int idDocente = docente.getId(); // Asume que tienes la información del docente actual

        // Llamar al generador de PDF
        PDFGenerator.generarListaAsistenciaDiaria(idAsignatura, nombreAsignatura, idDocente);
    }//GEN-LAST:event_DescargarAlumnosActionPerformed

    private void VerCalificacionesPorAlumnoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_VerCalificacionesPorAlumnoActionPerformed
        int selectedRowAlumno = tablaAlumnos.getSelectedRow();
        int selectedRowAsignatura = tablaAsignaturas.getSelectedRow();

        // Validar selección de alumno
        if (selectedRowAlumno == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un alumno primero.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validar selección de asignatura
        if (selectedRowAsignatura == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona una asignatura primero.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Obtener información del alumno y asignatura
        int idAlumno = Integer.parseInt(tablaAlumnos.getValueAt(selectedRowAlumno, 0).toString());
        String nombreAlumno = tablaAlumnos.getValueAt(selectedRowAlumno, 1).toString();
        int idAsignatura = Integer.parseInt(tablaAsignaturas.getValueAt(selectedRowAsignatura, 0).toString());
        String nombreAsignatura = tablaAsignaturas.getValueAt(selectedRowAsignatura, 1).toString();

        // Cambiar el texto del JLabel
        jLabel30.setText("Calificaciones - " + nombreAlumno + " - " + nombreAsignatura);

        // Cargar las calificaciones en la tabla
        cargarTablaCalificacionesAlumno(idAlumno, idAsignatura);
        tabAlumnos.setSelectedComponent(jPanel5);
    }//GEN-LAST:event_VerCalificacionesPorAlumnoActionPerformed
    private void llenarEvaluacionesFaltantes(int idAlumno, int idAsignatura) {
        // Limpiar comboBox antes de llenarlo
        jComboTituloNota.removeAllItems();

        // Obtener evaluaciones faltantes
        EvaluacionController evaluacionController = new EvaluacionController();
        List<Evaluacion> evaluacionesFaltantes = evaluacionController.obtenerEvaluacionesFaltantesPorAlumno(idAlumno, idAsignatura);

        // Agregar evaluaciones al comboBox
        for (Evaluacion evaluacion : evaluacionesFaltantes) {
            jComboTituloNota.addItem(evaluacion.getNombre());
        }
        //jLabel2.setText(" (Peso: " + evaluacion.getPeso() + "%)");

        // Validar si no hay evaluaciones faltantes
        if (evaluacionesFaltantes.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay evaluaciones pendientes para este alumno.", "Información", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void guardarNotaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_guardarNotaActionPerformed
        int selectedRowAlumno = tablaAlumnos.getSelectedRow();
        int selectedRowAsignatura = tablaAsignaturas.getSelectedRow();
        String notaStr = txtTituloNota3.getText();

        // Validar campos
        if (selectedRowAlumno == -1 || selectedRowAsignatura == -1 || notaStr.isEmpty() || jComboTituloNota.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Por favor, completa todos los campos.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Obtener valores
            double nota = Double.parseDouble(notaStr);
            int idAlumno = Integer.parseInt(tablaAlumnos.getValueAt(selectedRowAlumno, 0).toString());
            int idAsignatura = Integer.parseInt(tablaAsignaturas.getValueAt(selectedRowAsignatura, 0).toString());
            String evaluacionSeleccionada = jComboTituloNota.getSelectedItem().toString();
            EvaluacionController evaluacionController = new EvaluacionController();
            int idEvaluacion = evaluacionController.obtenerIdEvaluacionPorNombre(evaluacionSeleccionada, idAsignatura);

            // Crear objeto Calificacion
            Calificacion nuevaCalificacion = new Calificacion(0, idAlumno, idAsignatura, nota, idEvaluacion, new Date());
            System.out.println(idEvaluacion);
            // Guardar en la base de datos
            CalificacionController calificacionController = new CalificacionController();
            if (calificacionController.agregarCalificacion(nuevaCalificacion)) {
                JOptionPane.showMessageDialog(this, "Nota guardada correctamente.");
                cargarTablaCalificacionesAlumno(idAlumno, idAsignatura);
                tabAlumnos.setSelectedComponent(jPanel5);
            } else {
                JOptionPane.showMessageDialog(this, "Error al guardar la nota.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "La nota debe ser un número válido.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_guardarNotaActionPerformed

    private void EditNotaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EditNotaActionPerformed
        // Validar campos
        if (txtModifEval2.getText().isEmpty() || txtModifEval4.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, completa todos los campos.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Obtener valores de los campos
            int idCalificacion = Integer.parseInt(txtModifEval2.getText());
            double nuevaNota = Double.parseDouble(txtModifEval4.getText());

            // Crear objeto Calificacion actualizado
            CalificacionController calificacionController = new CalificacionController();
            Calificacion calificacion = calificacionController.obtenerCalificacionPorId(idCalificacion);

            if (calificacion == null) {
                JOptionPane.showMessageDialog(this, "No se encontró la calificación.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Actualizar nota
            calificacion.setNota(nuevaNota);
            if (calificacionController.actualizarCalificacion(calificacion)) {
                JOptionPane.showMessageDialog(this, "Nota actualizada correctamente.");
                cargarTablaCalificacionesAlumno(calificacion.getIdAlumno(), calificacion.getIdAsignatura()); // Recargar tabla

            } else {
                JOptionPane.showMessageDialog(this, "Error al actualizar la nota.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "La nota debe ser un número válido.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_EditNotaActionPerformed

    private void EliminarNotaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EliminarNotaActionPerformed
        // Obtener la fila seleccionada de la tabla
        int selectedRow = tablaCalificacionAlumno.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, selecciona una nota para eliminar.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Confirmar eliminación
            int confirm = JOptionPane.showConfirmDialog(this, "¿Estás seguro de que deseas eliminar esta nota?", "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                // Obtener el ID de la calificación desde la tabla
                int idCalificacion = Integer.parseInt(tablaCalificacionAlumno.getValueAt(selectedRow, 0).toString());

                // Eliminar la calificación
                CalificacionController calificacionController = new CalificacionController();
                if (calificacionController.eliminarCalificacion(idCalificacion)) {
                    JOptionPane.showMessageDialog(this, "Nota eliminada correctamente.");

                    // Recargar la tabla después de eliminar
                    int idAlumno = Integer.parseInt(tablaAlumnos.getValueAt(tablaAlumnos.getSelectedRow(), 0).toString());
                    int idAsignatura = Integer.parseInt(tablaAsignaturas.getValueAt(tablaAsignaturas.getSelectedRow(), 0).toString());
                    cargarTablaCalificacionesAlumno(idAlumno, idAsignatura);
                    tabAlumnos.setSelectedComponent(jPanel5);
                } else {
                    JOptionPane.showMessageDialog(this, "Error al eliminar la nota.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Error al procesar el ID de la calificación.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_EliminarNotaActionPerformed

    private void DescargarCalificacionesAlumnoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DescargarCalificacionesAlumnoActionPerformed
        int selectedRowAlumno = tablaAlumnos.getSelectedRow();
        int selectedRowAsignatura = tablaAsignaturas.getSelectedRow();

        // Validar si se seleccionó un alumno y una asignatura
        if (selectedRowAlumno == -1 || selectedRowAsignatura == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, selecciona un alumno y una asignatura.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Obtener datos del alumno y asignatura
            int idAlumno = Integer.parseInt(tablaAlumnos.getValueAt(selectedRowAlumno, 0).toString());
            String nombreAlumno = tablaAlumnos.getValueAt(selectedRowAlumno, 1).toString();
            int idAsignatura = Integer.parseInt(tablaAsignaturas.getValueAt(selectedRowAsignatura, 0).toString());
            String nombreAsignatura = tablaAsignaturas.getValueAt(selectedRowAsignatura, 1).toString();

            // Generar PDF con las calificaciones
            PDFGenerator.generarPDFCalificacionesAlumno(idAlumno, nombreAlumno, idAsignatura, nombreAsignatura);

            JOptionPane.showMessageDialog(this, "PDF generado correctamente. Revisa tu carpeta de Descargas.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al generar el PDF: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_DescargarCalificacionesAlumnoActionPerformed

    private void ModificarNotaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ModificarNotaActionPerformed
        // Verificar que haya una fila seleccionada en la tabla de calificaciones
        int selectedRow = tablaCalificacionAlumno.getSelectedRow();
        int selectedAsignatura = tablaAsignaturas.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, selecciona una calificación para modificar.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (selectedAsignatura == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, selecciona una asignatura para modificar.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Obtener datos de la tabla
            String asignatura = tablaAsignaturas.getValueAt(selectedAsignatura, 1).toString();; // Asignatura
            String evaluacion = tablaCalificacionAlumno.getValueAt(selectedRow, 1).toString(); // Evaluación
            String nota = tablaCalificacionAlumno.getValueAt(selectedRow, 2).toString(); // Nota
            String fecha = tablaCalificacionAlumno.getValueAt(selectedRow, 3).toString(); // Fecha

            // Establecer valores en los campos de texto del panel de modificación
            txtModifEval.setText(asignatura);  // Asignatura
            txtModifEval2.setText(tablaCalificacionAlumno.getValueAt(selectedRow, 0).toString()); // ID de la evaluación
            txtModifEval3.setText(evaluacion); // Evaluación
            txtModifEval4.setText(nota);       // Nota
            txtModifEval5.setText(fecha);      // Fecha de la evaluación

            // Cambiar al panel de modificación de notas
            tabAlumnos.setSelectedComponent(ModifNota);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar los datos para la modificación: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

    }//GEN-LAST:event_ModificarNotaActionPerformed

    private void AddNotaAlumnoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddNotaAlumnoActionPerformed
        int selectedRowAlumno = tablaAlumnos.getSelectedRow();
        int selectedRowAsignatura = tablaAsignaturas.getSelectedRow();

        // Validar selección de alumno y asignatura
        if (selectedRowAlumno == -1 || selectedRowAsignatura == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un alumno y una asignatura primero.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Obtener información del alumno y asignatura
        int idAlumno = Integer.parseInt(tablaAlumnos.getValueAt(selectedRowAlumno, 0).toString());
        String nombreAlumno = tablaAlumnos.getValueAt(selectedRowAlumno, 1).toString();
        int idAsignatura = Integer.parseInt(tablaAsignaturas.getValueAt(selectedRowAsignatura, 0).toString());
        String nombreAsignatura = tablaAsignaturas.getValueAt(selectedRowAsignatura, 1).toString();

        // Establecer los valores en los campos de texto
        txtTituloNota.setText(nombreAlumno);
        txtTituloNota1.setText(nombreAsignatura);
        //txtTituloNota2.setText(String.valueOf(idAsignatura)); // Peso o información adicional si aplica

        // Llenar el comboBox con evaluaciones faltantes para este alumno
        llenarEvaluacionesFaltantes(idAlumno, idAsignatura);

        // Cambiar a la pestaña de agregar nota
        tabAlumnos.setSelectedComponent(agregarNota);

    }//GEN-LAST:event_AddNotaAlumnoActionPerformed

    private void DescargarCalificacionesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DescargarCalificacionesActionPerformed

        int selectedRowAsignatura = tablaAsignaturas.getSelectedRow();
        int selectedRowEvaluacion = tablaEvaluaciones.getSelectedRow();

        if (selectedRowAsignatura == -1 || selectedRowEvaluacion == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, selecciona una asignatura y una evaluación.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Obtener datos de la asignatura y la evaluación seleccionada
        int idAsignatura = Integer.parseInt(tablaAsignaturas.getValueAt(selectedRowAsignatura, 0).toString());
        String nombreAsignatura = tablaAsignaturas.getValueAt(selectedRowAsignatura, 1).toString();

        int idEvaluacion = Integer.parseInt(tablaEvaluaciones.getValueAt(selectedRowEvaluacion, 0).toString());
        String nombreEvaluacion = tablaEvaluaciones.getValueAt(selectedRowEvaluacion, 1).toString();

        // Llamar al generador de PDF
        PDFGenerator.generarPDFCalificacionesPorEvaluacion(idAsignatura, nombreAsignatura, idEvaluacion, nombreEvaluacion);

    }//GEN-LAST:event_DescargarCalificacionesActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(DocenteView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(DocenteView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(DocenteView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(DocenteView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {

            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton AddEval;
    private javax.swing.JButton AddNotaAlumno;
    private javax.swing.JTable CalificacionesPorEvaluacion;
    private javax.swing.JButton CambioContraseña;
    private javax.swing.JButton DescargarAlumnos;
    private javax.swing.JButton DescargarCalificaciones;
    private javax.swing.JButton DescargarCalificacionesAlumno;
    private javax.swing.JButton DescargarEv;
    private javax.swing.JButton EditNota;
    private javax.swing.JButton EliminarEvaluacion;
    private javax.swing.JButton EliminarNota;
    private javax.swing.JPanel ModifEval;
    private javax.swing.JPanel ModifNota;
    private javax.swing.JButton ModificarEvaluacion;
    private javax.swing.JButton ModificarNota;
    private javax.swing.JPanel PanelAsignaturas;
    private javax.swing.JButton VerAlumnos;
    private javax.swing.JPanel VerAlumnosPorA;
    private javax.swing.JPanel VerCalif;
    private javax.swing.JButton VerCalificaciones;
    private javax.swing.JButton VerCalificacionesPorAlumno;
    private javax.swing.JButton addEval;
    private javax.swing.JButton addNotif;
    private javax.swing.JPanel agregarEval;
    private javax.swing.JPanel agregarNota;
    private javax.swing.JButton closeSession1;
    private javax.swing.JComboBox<String> comboBoxAlumno;
    private javax.swing.JComboBox<String> comboBoxAlumno1;
    private javax.swing.JComboBox<String> comboBoxGrupo;
    private javax.swing.JComboBox<String> comboBoxGrupo1;
    private javax.swing.JButton deleteNotificacion;
    private javax.swing.JButton guardarNota;
    private javax.swing.JComboBox<String> jComboTituloNota;
    private javax.swing.JDesktopPane jDesktopPane1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JPanel jPanelNotificaciones;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JTabbedPane jTabbedPane3;
    private javax.swing.JTabbedPane jTabbedPane4;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextArea jTextArea2;
    private javax.swing.JLabel lblBienvenida;
    private javax.swing.JLabel lblBienvenida1;
    private javax.swing.JTextField lblIdNotificacion;
    private javax.swing.JPanel mainPanel3;
    private javax.swing.JButton modNotif;
    private javax.swing.JButton modifEval;
    private javax.swing.JButton modifNotificacion;
    private javax.swing.JTabbedPane tabAlumnos;
    private javax.swing.JTable tablaAlumnos;
    private javax.swing.JTable tablaAsignaturas;
    private javax.swing.JTable tablaCalificacionAlumno;
    private javax.swing.JTable tablaEvaluaciones;
    private javax.swing.JPasswordField txtContrasena;
    private javax.swing.JPasswordField txtContrasena2;
    private javax.swing.JTextArea txtMensaje;
    private javax.swing.JTextArea txtMensaje1;
    private javax.swing.JTextField txtModifEval;
    private javax.swing.JTextField txtModifEval2;
    private javax.swing.JTextField txtModifEval3;
    private javax.swing.JTextField txtModifEval4;
    private javax.swing.JFormattedTextField txtModifEval5;
    private javax.swing.JTextField txtTitulo;
    private javax.swing.JTextField txtTitulo1;
    private javax.swing.JTextField txtTituloEval;
    private javax.swing.JTextField txtTituloEval2;
    private javax.swing.JTextField txtTituloEval3;
    private javax.swing.JFormattedTextField txtTituloEval4;
    private javax.swing.JTextField txtTituloEval5;
    private javax.swing.JTextField txtTituloEval6;
    private javax.swing.JTextField txtTituloEval7;
    private javax.swing.JFormattedTextField txtTituloEval8;
    private javax.swing.JTextField txtTituloEval9;
    private javax.swing.JTextField txtTituloNota;
    private javax.swing.JTextField txtTituloNota1;
    private javax.swing.JTextField txtTituloNota3;
    // End of variables declaration//GEN-END:variables
}
