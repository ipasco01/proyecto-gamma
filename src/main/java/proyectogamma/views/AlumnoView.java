/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package proyectogamma.views;

import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.ui.FlatComboBoxUI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.DefaultListModel;
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
import proyectogamma.controller.DocenteController;
import proyectogamma.model.Docente;
import proyectogamma.model.Notificacion;
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

    setLocationRelativeTo(null); // Centrar la ventana

    if (alumno != null) {
        lblBienvenida.setText("Bienvenido, " + alumno.getNombre() + " " + alumno.getApellido());
        
    } else {
        lblBienvenida.setText("Bienvenido, Alumno desconocido");
    }
    
    }
    
    private void cargarNotificacionesGrupales() {
    if (alumno != null) {
        System.out.println("Alumno encontrado: " + alumno.getNombre());

        // Obtener el ID del grupo asociado al alumno
        AlumnoController alumnoController = new AlumnoController();
        int idGrupoAlumno = alumnoController.obtenerIdGrupoPorAlumno(alumno.getId());
        System.out.println("ID del grupo del alumno: " + idGrupoAlumno);

        if (idGrupoAlumno != -1) { // Verifica que se haya encontrado un grupo
            NotificacionController notificacionController = new NotificacionController();
            List<Notificacion> notificaciones = notificacionController.obtenerNotificacionesPorGrupo(idGrupoAlumno);
            System.out.println("Notificaciones encontradas: " + notificaciones.size());

            DefaultListModel<String> notificacionesModel = new DefaultListModel<>();
            for (Notificacion notificacion : notificaciones) {
                System.out.println("Agregando notificación: " + notificacion.getTitulo());
                notificacionesModel.addElement(notificacion.getTitulo() + " - " + notificacion.getMensaje());
            }

            listaNotificacionesGrupales.setModel(notificacionesModel);
        } else {
            System.out.println("El alumno no pertenece a ningún grupo.");
        }
    } else {
        System.out.println("Alumno no encontrado.");
    }
}
  private void cargarNotificacionesPersonales() {
    if (alumno != null) {
        // Obtener el ID del alumno
        int idAlumno = alumno.getId();
        System.out.println("Cargando notificaciones personales para el alumno con ID: " + idAlumno);

        NotificacionController notificacionController = new NotificacionController();
        List<Notificacion> notificaciones = notificacionController.obtenerNotificacionesPorAlumno(idAlumno);

        if (notificaciones != null && !notificaciones.isEmpty()) {
            System.out.println("Notificaciones personales encontradas: " + notificaciones.size());

            DefaultListModel<String> notificacionesModel = new DefaultListModel<>();
            DocenteController docenteController = new DocenteController();

            for (Notificacion notificacion : notificaciones) {
                int docenteId = notificacion.getIdDocente();
                String nombreDocente = "Sin docente asignado";

                // Si existe un docente asociado, obtener su información
                if (docenteId != 0) {
                    Docente docente = docenteController.obtenerDocentePorId(docenteId);
                    if (docente != null) {
                        nombreDocente = docente.getNombre() + " " + docente.getApellido();
                    }
                }

                System.out.println("Agregando notificación personal: " + notificacion.getTitulo());
                notificacionesModel.addElement(notificacion.getTitulo() + " - " + notificacion.getMensaje() + " - " + nombreDocente);
            }

            listaNotificacionesPersonal3.setModel(notificacionesModel); // Asignar a la lista de notificaciones personales
        } else {
            System.out.println("No se encontraron notificaciones personales para el alumno.");
        }
    } else {
        System.out.println("El objeto alumno es nulo. No se pueden cargar las notificaciones personales.");
    }
}

private void mostrarNotificacionSeleccionada(javax.swing.JList<String> lista) {
    // Verifica si hay un elemento seleccionado
    int selectedIndex = lista.getSelectedIndex();
    if (selectedIndex != -1) {
        // Obtén el modelo de la lista
        DefaultListModel<String> modelo = (DefaultListModel<String>) lista.getModel();
        // Obtén la notificación seleccionada
        String notificacionSeleccionada = modelo.getElementAt(selectedIndex);
        // Muestra la notificación en el JTextField
        jTextField1.setText(notificacionSeleccionada);
    }
}
private void cargarTodasLasNotas() {
    if (alumno != null) {
        AlumnoController alumnoController = new AlumnoController();
        List<String[]> todasLasNotas = alumnoController.obtenerTodasLasNotasPorAlumno(alumno.getId());

        DefaultTableModel modelo = new DefaultTableModel();
        modelo.addColumn("Asignatura");
        modelo.addColumn("Nota");
        modelo.addColumn("Fecha");

        for (String[] nota : todasLasNotas) {
            modelo.addRow(nota);
        }

        tablaNotas.setModel(modelo);
    }
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
        // Map para almacenar las notas agrupadas por asignatura
        Map<String, List<Double>> asignaturaNotas = new HashMap<>();

        // Recorrer las filas de la tabla y agrupar notas por asignatura
        for (int i = 0; i < modelo.getRowCount(); i++) {
            String asignatura = (String) modelo.getValueAt(i, 0); // Columna "Asignatura"
            double nota = Double.parseDouble(modelo.getValueAt(i, 1).toString()); // Columna "Nota"

            asignaturaNotas.putIfAbsent(asignatura, new ArrayList<>());
            asignaturaNotas.get(asignatura).add(nota);
        }

        // Construir el texto para los promedios
        StringBuilder textoPromedios = new StringBuilder("Promedios por asignatura:\n");
        double sumaGeneral = 0;
        int totalNotas = 0;

        for (Map.Entry<String, List<Double>> entry : asignaturaNotas.entrySet()) {
            String asignatura = entry.getKey();
            List<Double> notas = entry.getValue();

            // Calcular promedio por asignatura
            double promedio = notas.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
            textoPromedios.append("- ").append(asignatura).append(": ").append(String.format("%.2f", promedio)).append("\n");

            // Sumar para el promedio general
            sumaGeneral += notas.stream().mapToDouble(Double::doubleValue).sum();
            totalNotas += notas.size();
        }

        // Calcular promedio general
        double promedioGeneral = totalNotas > 0 ? sumaGeneral / totalNotas : 0.0;
        textoPromedios.append("\nPromedio general: ").append(String.format("%.2f", promedioGeneral));

        // Mostrar en jTextArea1
        jTextArea1.setText(textoPromedios.toString());
    } else {
        jTextArea1.setText("No hay notas disponibles para calcular los promedios.");
    }
}

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel8 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        lblBienvenida = new javax.swing.JLabel();
        closeSession = new javax.swing.JButton();
        mainPanel3 = new javax.swing.JPanel();
        jTabbedPane4 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane8 = new javax.swing.JScrollPane();
        listaNotificacionesGrupales = new javax.swing.JList<>();
        jScrollPane9 = new javax.swing.JScrollPane();
        listaNotificacionesPersonal3 = new javax.swing.JList<>();
        jLabel8 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jComboBox1 = new javax.swing.JComboBox<>();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jScrollPane10 = new javax.swing.JScrollPane();
        tablaNotas = new javax.swing.JTable();
        jLabel11 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane11 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jLabel14 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
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
        setMaximumSize(new java.awt.Dimension(800, 600));
        setMinimumSize(new java.awt.Dimension(800, 600));
        setUndecorated(true);
        setResizable(false);
        setSize(new java.awt.Dimension(800, 500));

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

        closeSession.setBackground(new java.awt.Color(0, 0, 204));
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
        jPanel9.add(closeSession, new org.netbeans.lib.awtextra.AbsoluteConstraints(657, 0, 120, 30));

        jPanel8.add(jPanel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 780, 30));

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
        listaNotificacionesGrupales.setFixedCellHeight(60);
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

        jTextField1.setEditable(false);
        jTextField1.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        jTextField1.setBorder(null);

        jLabel6.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        jLabel6.setText("Notificación");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jTextField1)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 359, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel4))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 19, Short.MAX_VALUE)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, 359, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel8))))
                        .addGap(37, 37, 37))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane9)
                    .addComponent(jScrollPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 377, Short.MAX_VALUE))
                .addContainerGap(44, Short.MAX_VALUE))
        );

        jTabbedPane4.addTab("Ver Notificaciones", jPanel1);

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));
        jPanel6.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jScrollPane1.setBorder(null);

        jComboBox1.setFont(new java.awt.Font("Poppins", 3, 14)); // NOI18N
        jComboBox1.setForeground(new java.awt.Color(0, 51, 153));
        jComboBox1.setModel(jComboBox1.getModel());
        jComboBox1.setBorder(null);
        jScrollPane1.setViewportView(jComboBox1);

        jPanel6.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 70, 255, 50));

        jLabel9.setFont(new java.awt.Font("Poppins", 1, 18)); // NOI18N
        jLabel9.setText("Notas");
        jPanel6.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 40, -1, 20));

        jLabel10.setFont(new java.awt.Font("Poppins", 1, 18)); // NOI18N
        jLabel10.setText("Asignaturas");
        jPanel6.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 40, -1, 20));

        jScrollPane10.setBorder(null);
        jScrollPane10.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N

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
        tablaNotas.setEnabled(false);
        tablaNotas.setIntercellSpacing(new java.awt.Dimension(0, 10));
        tablaNotas.setRowHeight(60);
        tablaNotas.setShowGrid(true);
        tablaNotas.setShowHorizontalLines(false);
        jScrollPane10.setViewportView(tablaNotas);

        jPanel6.add(jScrollPane10, new org.netbeans.lib.awtextra.AbsoluteConstraints(298, 64, 456, 420));

        jLabel11.setFont(new java.awt.Font("Poppins", 1, 18)); // NOI18N
        jLabel11.setText("Promedios");
        jPanel6.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 230, -1, 20));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/logoPeque.png"))); // NOI18N
        jPanel6.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 410, -1, -1));

        jScrollPane11.setBorder(null);

        jTextArea1.setColumns(20);
        jTextArea1.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jTextArea1.setRows(5);
        jTextArea1.setBorder(null);
        jScrollPane11.setViewportView(jTextArea1);

        jPanel6.add(jScrollPane11, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 264, 255, 210));
        jPanel6.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 98, 255, 20));

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/logoPeque.png"))); // NOI18N
        jPanel6.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(-20, 200, -1, -1));

        jTabbedPane4.addTab("Ver Asignaturas", jPanel6);

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

        jPanel7.add(jTabbedPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 780, 520));

        jTabbedPane4.addTab("Configuración", jPanel7);

        mainPanel3.add(jTabbedPane4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 3, 780, 560));

        jPanel8.add(mainPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 36, 780, 540));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
    private javax.swing.JButton closeSession;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane11;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTabbedPane jTabbedPane4;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JLabel lblBienvenida;
    private javax.swing.JList<String> listaNotificacionesGrupales;
    private javax.swing.JList<String> listaNotificacionesPersonal3;
    private javax.swing.JPanel mainPanel3;
    private javax.swing.JTable tablaNotas;
    private javax.swing.JPasswordField txtContrasena;
    private javax.swing.JPasswordField txtContrasena2;
    // End of variables declaration//GEN-END:variables
}
