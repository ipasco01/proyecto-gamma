/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package proyectogamma.views;

import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import proyectogamma.controller.AlumnoController;
import proyectogamma.controller.AsignaturaController;
import proyectogamma.controller.EvaluacionController;
import proyectogamma.controller.GrupoController;
import proyectogamma.controller.NotificacionController;
import proyectogamma.controller.UsuarioController;
import proyectogamma.model.Alumno;
import proyectogamma.model.Asignatura;
import proyectogamma.model.Docente;
import proyectogamma.model.Evaluacion;
import proyectogamma.model.Grupos;
import proyectogamma.model.Notificacion;
import proyectogamma.model.Usuario;

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
        }
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
            asignatura.getNombre(),
        });
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
            comboBoxAlumno.setSelectedItem("Sin alumno");}

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

    for (Evaluacion evaluacion : evaluaciones) {
        model.addRow(new Object[]{
            evaluacion.getId(),
            evaluacion.getNombre(),
            evaluacion.getPeso(),
            evaluacion.getFecha()
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
        agregarEvaluacion = new javax.swing.JButton();
        agregarEval = new javax.swing.JPanel();
        jLabel18 = new javax.swing.JLabel();
        txtTituloEval = new javax.swing.JTextField();
        addEval = new javax.swing.JButton();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        txtTituloEval2 = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        txtTituloEval3 = new javax.swing.JTextField();
        jPanel14 = new javax.swing.JPanel();
        jLabel19 = new javax.swing.JLabel();
        txtTituloEval1 = new javax.swing.JTextField();
        addNotif3 = new javax.swing.JButton();
        addNotif4 = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
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
        jScrollPane1.setViewportView(tablaAsignaturas);

        jLabel14.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jLabel14.setText("Evaluaciones");

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
        jScrollPane4.setViewportView(tablaEvaluaciones);

        agregarEvaluacion.setBackground(new java.awt.Color(0, 0, 204));
        agregarEvaluacion.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        agregarEvaluacion.setForeground(new java.awt.Color(255, 255, 255));
        agregarEvaluacion.setText("Agregar o Modificar Evaluación");
        agregarEvaluacion.setActionCommand("Agregar o Modificar Evaluación");
        agregarEvaluacion.setBorder(null);
        agregarEvaluacion.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        agregarEvaluacion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                agregarEvaluacionActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout PanelAsignaturasLayout = new javax.swing.GroupLayout(PanelAsignaturas);
        PanelAsignaturas.setLayout(PanelAsignaturasLayout);
        PanelAsignaturasLayout.setHorizontalGroup(
            PanelAsignaturasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelAsignaturasLayout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(PanelAsignaturasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 539, Short.MAX_VALUE)
                    .addComponent(jSeparator1)
                    .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane4)
                    .addComponent(agregarEvaluacion, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        PanelAsignaturasLayout.setVerticalGroup(
            PanelAsignaturasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelAsignaturasLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel14)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 291, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(agregarEvaluacion, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(21, Short.MAX_VALUE))
        );

        jTabbedPane3.addTab("Ver todas mis asignaturas", PanelAsignaturas);

        agregarEval.setBackground(new java.awt.Color(255, 255, 255));

        jLabel18.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jLabel18.setText("Titulo de la Evaluación");

        txtTituloEval.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N

        addEval.setBackground(new java.awt.Color(0, 0, 204));
        addEval.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        addEval.setForeground(new java.awt.Color(255, 255, 255));
        addEval.setText("Agregar Notificación");
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

        javax.swing.GroupLayout agregarEvalLayout = new javax.swing.GroupLayout(agregarEval);
        agregarEval.setLayout(agregarEvalLayout);
        agregarEvalLayout.setHorizontalGroup(
            agregarEvalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, agregarEvalLayout.createSequentialGroup()
                .addContainerGap(166, Short.MAX_VALUE)
                .addGroup(agregarEvalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(agregarEvalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jLabel22, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtTituloEval3, javax.swing.GroupLayout.PREFERRED_SIZE, 216, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel20, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(agregarEvalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(addEval, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel18, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtTituloEval2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 216, Short.MAX_VALUE)
                        .addComponent(jLabel21, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 216, Short.MAX_VALUE)
                        .addComponent(txtTituloEval, javax.swing.GroupLayout.Alignment.LEADING)))
                .addGap(180, 180, 180))
        );
        agregarEvalLayout.setVerticalGroup(
            agregarEvalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(agregarEvalLayout.createSequentialGroup()
                .addGap(62, 62, 62)
                .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel22)
                .addGap(18, 18, 18)
                .addComponent(txtTituloEval3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel18)
                .addGap(18, 18, 18)
                .addComponent(txtTituloEval2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel21)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtTituloEval, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(60, 60, 60)
                .addComponent(addEval, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(100, Short.MAX_VALUE))
        );

        agregarEvalLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel18, jLabel21, txtTituloEval, txtTituloEval2});

        jTabbedPane3.addTab("Agregar Evaluaciones", agregarEval);

        jPanel14.setBackground(new java.awt.Color(255, 255, 255));

        jLabel19.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jLabel19.setText("Titulo de la Evaluación");

        txtTituloEval1.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N

        addNotif3.setBackground(new java.awt.Color(0, 0, 204));
        addNotif3.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        addNotif3.setForeground(new java.awt.Color(255, 255, 255));
        addNotif3.setText("Agregar Notificación");
        addNotif3.setBorder(null);
        addNotif3.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        addNotif3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addNotif3ActionPerformed(evt);
            }
        });

        addNotif4.setBackground(new java.awt.Color(0, 0, 204));
        addNotif4.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        addNotif4.setForeground(new java.awt.Color(255, 255, 255));
        addNotif4.setText("Agregar Notificación");
        addNotif4.setBorder(null);
        addNotif4.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        addNotif4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addNotif4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel14Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(addNotif4, javax.swing.GroupLayout.PREFERRED_SIZE, 279, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(addNotif3, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12))
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 216, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTituloEval1, javax.swing.GroupLayout.PREFERRED_SIZE, 216, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addGap(79, 79, 79)
                .addComponent(jLabel19)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtTituloEval1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 178, Short.MAX_VALUE)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(addNotif3, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(addNotif4, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(170, 170, 170))
        );

        jTabbedPane3.addTab("Modificar o Eliminar Evaluación", jPanel14);

        jPanel6.add(jTabbedPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 780, 510));

        jTabbedPane4.addTab("Asignaturas", jPanel6);

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 780, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 505, Short.MAX_VALUE)
        );

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

        jPanel1.add(jPanel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, 600));

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
        jTabbedPane1.setSelectedComponent(jPanelNotificaciones); // Volver a la pestaña principal
    } else {
        JOptionPane.showMessageDialog(this, "Error al actualizar la notificación.");
    }
    }//GEN-LAST:event_modifNotificacionActionPerformed

    private void deleteNotificacionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteNotificacionActionPerformed
         int confirm = JOptionPane.showConfirmDialog(this, "¿Estás seguro de que deseas eliminar esta notificación?", "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);
    if (confirm == JOptionPane.YES_OPTION) {
        int idNotificacion = Integer.parseInt(lblIdNotificacion.getText());
        NotificacionController notificacionController = new NotificacionController();
        if (notificacionController.eliminarNotificacion(idNotificacion)) {
            JOptionPane.showMessageDialog(this, "Notificación eliminada correctamente.");
            cargarNotificaciones();
            jTabbedPane1.setSelectedComponent(jPanelNotificaciones); // Volver a la pestaña principal
        } else {
            JOptionPane.showMessageDialog(this, "Error al eliminar la notificación.");
        }
    }
    }//GEN-LAST:event_deleteNotificacionActionPerformed

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
    idGrupo1 = grupoController.obtenerIdGrupoPorNombre(nuevoGrupo1);}

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

    private void agregarEvaluacionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_agregarEvaluacionActionPerformed
       jTabbedPane3.setSelectedComponent(PanelAsignaturas);
    }//GEN-LAST:event_agregarEvaluacionActionPerformed

    private void addNotif3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addNotif3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_addNotif3ActionPerformed

    private void addNotif4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addNotif4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_addNotif4ActionPerformed

    private void addEvalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addEvalActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_addEvalActionPerformed

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
    private javax.swing.JButton CambioContraseña;
    private javax.swing.JPanel PanelAsignaturas;
    private javax.swing.JButton addEval;
    private javax.swing.JButton addNotif;
    private javax.swing.JButton addNotif3;
    private javax.swing.JButton addNotif4;
    private javax.swing.JPanel agregarEval;
    private javax.swing.JButton agregarEvaluacion;
    private javax.swing.JButton closeSession1;
    private javax.swing.JComboBox<String> comboBoxAlumno;
    private javax.swing.JComboBox<String> comboBoxAlumno1;
    private javax.swing.JComboBox<String> comboBoxGrupo;
    private javax.swing.JComboBox<String> comboBoxGrupo1;
    private javax.swing.JButton deleteNotificacion;
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
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
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
    private javax.swing.JButton modifNotificacion;
    private javax.swing.JTable tablaAsignaturas;
    private javax.swing.JTable tablaEvaluaciones;
    private javax.swing.JPasswordField txtContrasena;
    private javax.swing.JPasswordField txtContrasena2;
    private javax.swing.JTextArea txtMensaje;
    private javax.swing.JTextArea txtMensaje1;
    private javax.swing.JTextField txtTitulo;
    private javax.swing.JTextField txtTitulo1;
    private javax.swing.JTextField txtTituloEval;
    private javax.swing.JTextField txtTituloEval1;
    private javax.swing.JTextField txtTituloEval2;
    private javax.swing.JTextField txtTituloEval3;
    // End of variables declaration//GEN-END:variables
}
