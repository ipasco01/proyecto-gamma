/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package proyectogamma.views;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import proyectogamma.controller.AsignaturaController;
import proyectogamma.controller.DocenteController;
import proyectogamma.controller.GrupoAsignaturaController;
import proyectogamma.controller.GrupoController;
import proyectogamma.controller.HorarioAsignaturaController;
import proyectogamma.controller.UsuarioController;
import proyectogamma.model.Asignatura;
import proyectogamma.model.BaseDatos;
import proyectogamma.model.Docente;
import proyectogamma.model.GrupoAsignatura;
import proyectogamma.model.Grupos;
import proyectogamma.model.HorarioAsignatura;
import proyectogamma.model.Usuario;
import proyectogamma.utils.PDFGenerator;

/**
 *
 * @author Isabel
 */
public class AdminView extends javax.swing.JFrame {

    private Usuario usuario;

    /**
     * Creates new form AdminView
     */
    public AdminView(Usuario usuario) {
        // Verificar si el usuario es Admin
        if ("Admin".equals(usuario.getRol())) {
            this.usuario = usuario; // Asignar el usuario a la clase
            initComponents();
            cargarGrados();
            cargarComboBoxGrupos();
            cargarAsignaturas();
            cargarProfesores();
            tablaGrados.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    tablaGradosMouseClicked(evt);
                }
            });
        } else {
            System.out.println("El usuario no tiene permisos de administrador.");
            JOptionPane.showMessageDialog(this, "Acceso denegado: No tienes permisos de administrador.", "Error", JOptionPane.ERROR_MESSAGE);
            dispose(); // Cierra la ventana si no tiene permisos
        }

    }

    private void cargarGrados() {
        GrupoController grupoController = new GrupoController(); // Asume que existe un controlador de Grupos
        List<Grupos> grupos = grupoController.obtenerGrupos(); // Obtener todos los grupos

        // Crear modelo para la tabla
        DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "Nombre", "Descripción", "Fecha de Creación"}, 0);

        // Llenar la tabla con los datos
        for (Grupos grupo : grupos) {
            model.addRow(new Object[]{
                grupo.getId(),
                grupo.getNombre(),
                grupo.getDescripcion(),
                new SimpleDateFormat("dd/MM/yyyy").format(grupo.getFechaCreacion())
            });
        }

        // Asignar el modelo a la tabla
        tablaGrados.setModel(model);
    }

    private void tablaGradosMouseClicked(java.awt.event.MouseEvent evt) {
        int selectedRow = tablaGrados.getSelectedRow();

        if (selectedRow != -1) {
            int idGrupo = Integer.parseInt(tablaGrados.getValueAt(selectedRow, 0).toString()); // Obtener el ID del grupo

            // Obtener las asignaturas asociadas al grupo
            AsignaturaController asignaturaController = new AsignaturaController();
            List<Asignatura> asignaturas = asignaturaController.obtenerAsignaturasPorGrupo(idGrupo);

            // Crear modelo para la tabla
            DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "Asignatura", "Profesor"}, 0);

            // Llenar la tabla con las asignaturas
            for (Asignatura asignatura : asignaturas) {
                model.addRow(new Object[]{
                    asignatura.getId(),
                    asignatura.getNombre(),
                    asignatura.getNombreProfesor() != null ? asignatura.getNombreProfesor() : "Sin profesor asignado"
                });
            }

            // Asignar modelo a tablaClaseGrados
            tablaClaseGrados.setModel(model);
        } else {
            JOptionPane.showMessageDialog(this, "Por favor, selecciona un grupo.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarAsignaturas() {
        GrupoAsignaturaController controller = new GrupoAsignaturaController();
        List<GrupoAsignatura> lista = controller.obtenerAsignaturasConGrupo();

        DefaultTableModel model = new DefaultTableModel(new String[]{"Asignatura", "Profesor", "Grupo"}, 0);

        for (GrupoAsignatura ga : lista) {
            model.addRow(new Object[]{ga.getNombreAsignatura(), ga.getNombreProfesor(), ga.getNombreGrupo()});
        }

        tablaAsignaturas.setModel(model);
    }

    private void cargarComboBoxGrupos() {
        GrupoController grupoController = new GrupoController();
        List<Grupos> grupos = grupoController.obtenerGrupos();
        jComboBoxGrupos.removeAllItems();
        jComboBoxGrupos.addItem("Todos");
        jComboBoxGrupos.addItem("Sin Asignar");
        for (Grupos grupo : grupos) {
            jComboBoxGrupos.addItem(grupo.getNombre());
        }
    }

    private void filtrarAsignaturasPorGrupo() {
        String grupoSeleccionado = jComboBoxGrupos.getSelectedItem().toString();
        GrupoAsignaturaController controller = new GrupoAsignaturaController();
        List<GrupoAsignatura> lista;

        if (grupoSeleccionado.equals("Todos")) {
            lista = controller.obtenerAsignaturasConGrupo();
        } else {
            lista = controller.obtenerAsignaturasPorNombreDeGrupo(grupoSeleccionado);
        }

        DefaultTableModel model = new DefaultTableModel(new String[]{"Asignatura", "Profesor", "Grupo"}, 0);

        for (GrupoAsignatura ga : lista) {
            model.addRow(new Object[]{ga.getNombreAsignatura(), ga.getNombreProfesor(), ga.getNombreGrupo()});
        }

        tablaAsignaturas.setModel(model);
    }

    private void cargarProfesores() {
        DocenteController docenteController = new DocenteController();
        List<Docente> profesores = docenteController.obtenerDocentes();
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
        for (Docente profesor : profesores) {
            model.addElement(profesor.getNombre() + " " + profesor.getApellido());
        }
        comboBoxProfesor.setModel(model);
        comboBoxProfesor1.setModel(model);
    }

    private void cargarHorario(int idGrupo) {
        HorarioAsignaturaController horarioController = new HorarioAsignaturaController();
        List<HorarioAsignatura> horarios = horarioController.obtenerHorariosPorGrupo(idGrupo);

        // Crear una matriz para el horario limitado de 7:00 a 14:00
        String[][] timetable = new String[8][7]; // 8 horas (7:00 - 14:00) y 7 días (Lunes a Sábado)
        for (HorarioAsignatura horario : horarios) {
            String dia = horario.getDia();
            int columna = switch (dia.toLowerCase()) {
                case "lunes" ->
                    1;
                case "martes" ->
                    2;
                case "miercoles" ->
                    3;
                case "jueves" ->
                    4;
                case "viernes" ->
                    5;
                case "sabado" ->
                    6;
                default ->
                    0;
            };

            int filaInicio = horario.getHoraInicio().toLocalTime().getHour() - 7; // Ajustar índice de 7:00
            int filaFin = horario.getHoraFin().toLocalTime().getHour() - 7;

            // Marcar el horario
            for (int i = filaInicio; i < filaFin; i++) {
                if (i >= 0 && i < timetable.length) { // Validar rango de fila
                    timetable[i][columna] = "<html><b>" + horario.getNombreAsignatura() + "</b><br><small>" + horario.getNombreProfesor() + "</small></html>";
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

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        mainPanel3 = new javax.swing.JPanel();
        jTabbedPane4 = new javax.swing.JTabbedPane();
        jPanel6 = new javax.swing.JPanel();
        jTabbedPane3 = new javax.swing.JTabbedPane();
        PanelGrados = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaGrados = new javax.swing.JTable();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel14 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tablaClaseGrados = new javax.swing.JTable();
        modifEval = new javax.swing.JButton();
        AddGrado = new javax.swing.JButton();
        DescargarEv = new javax.swing.JButton();
        jLabel31 = new javax.swing.JLabel();
        ModifGrado = new javax.swing.JButton();
        agregarGrupo = new javax.swing.JPanel();
        Nombre = new javax.swing.JLabel();
        txtAddGrupo1 = new javax.swing.JTextField();
        addGrupo = new javax.swing.JButton();
        jLabel20 = new javax.swing.JLabel();
        Descripcion = new javax.swing.JLabel();
        txtAddGrupo = new javax.swing.JTextField();
        ModifGrados = new javax.swing.JPanel();
        ModificarGrupo = new javax.swing.JButton();
        jLabel24 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        txtGrado3 = new javax.swing.JTextField();
        jLabel28 = new javax.swing.JLabel();
        txtGrado4 = new javax.swing.JFormattedTextField();
        jLabel22 = new javax.swing.JLabel();
        txtGrado = new javax.swing.JTextField();
        txtGrado1 = new javax.swing.JTextField();
        jLabel37 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        tabAlumnos = new javax.swing.JTabbedPane();
        jPanel5 = new javax.swing.JPanel();
        jLabel30 = new javax.swing.JLabel();
        tablaClases = new javax.swing.JScrollPane();
        tablaAsignaturas = new javax.swing.JTable();
        EliminarClase = new javax.swing.JButton();
        DescargarGrupo = new javax.swing.JButton();
        EditarClase = new javax.swing.JButton();
        AgregarClase = new javax.swing.JButton();
        jComboBoxGrupos = new javax.swing.JComboBox<>();
        jLabel36 = new javax.swing.JLabel();
        EditarClase1 = new javax.swing.JButton();
        jPanel9 = new javax.swing.JPanel();
        jscrollpanelhorarios = new javax.swing.JScrollPane();
        tablaHorario = new javax.swing.JTable();
        jLabel33 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        agregarClase = new javax.swing.JPanel();
        jLabel41 = new javax.swing.JLabel();
        btnGuardarClase = new javax.swing.JButton();
        jLabel42 = new javax.swing.JLabel();
        txtNombreClase = new javax.swing.JTextField();
        jLabel52 = new javax.swing.JLabel();
        comboBoxDia = new javax.swing.JComboBox<>();
        comboBoxGrado = new javax.swing.JComboBox<>();
        jLabel74 = new javax.swing.JLabel();
        jLabel75 = new javax.swing.JLabel();
        comboBoxProfesor = new javax.swing.JComboBox<>();
        txtHoraInicio = new javax.swing.JFormattedTextField();
        jLabel76 = new javax.swing.JLabel();
        txtHoraFin = new javax.swing.JFormattedTextField();
        jLabel77 = new javax.swing.JLabel();
        editarProfesor = new javax.swing.JPanel();
        EditNota = new javax.swing.JButton();
        jLabel46 = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        jLabel78 = new javax.swing.JLabel();
        txtNombreClase1 = new javax.swing.JTextField();
        jLabel38 = new javax.swing.JLabel();
        comboBoxProfesor1 = new javax.swing.JComboBox<>();
        jPanel2 = new javax.swing.JPanel();
        tabAlumnos2 = new javax.swing.JTabbedPane();
        jPanel15 = new javax.swing.JPanel();
        jLabel35 = new javax.swing.JLabel();
        jScrollPane8 = new javax.swing.JScrollPane();
        tablaCalificacionAlumno2 = new javax.swing.JTable();
        EliminarNota2 = new javax.swing.JButton();
        DescargarCalificacionesAlumno1 = new javax.swing.JButton();
        ModificarNota2 = new javax.swing.JButton();
        AddNotaAlumno2 = new javax.swing.JButton();
        agregarNota2 = new javax.swing.JPanel();
        jLabel63 = new javax.swing.JLabel();
        guardarNota2 = new javax.swing.JButton();
        jLabel64 = new javax.swing.JLabel();
        jLabel65 = new javax.swing.JLabel();
        txtTituloNota6 = new javax.swing.JTextField();
        txtTituloNota7 = new javax.swing.JTextField();
        jLabel66 = new javax.swing.JLabel();
        jComboTituloNota2 = new javax.swing.JComboBox<>();
        jLabel67 = new javax.swing.JLabel();
        txtTituloNota8 = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        ModifNota2 = new javax.swing.JPanel();
        EditNota2 = new javax.swing.JButton();
        jLabel68 = new javax.swing.JLabel();
        jLabel69 = new javax.swing.JLabel();
        txtModifEval10 = new javax.swing.JTextField();
        jLabel70 = new javax.swing.JLabel();
        txtModifEval11 = new javax.swing.JTextField();
        jLabel71 = new javax.swing.JLabel();
        txtModifEval12 = new javax.swing.JTextField();
        jLabel72 = new javax.swing.JLabel();
        txtModifEval13 = new javax.swing.JFormattedTextField();
        jLabel73 = new javax.swing.JLabel();
        txtModifEval14 = new javax.swing.JTextField();
        tabAlumnos1 = new javax.swing.JTabbedPane();
        jPanel14 = new javax.swing.JPanel();
        jLabel32 = new javax.swing.JLabel();
        jScrollPane7 = new javax.swing.JScrollPane();
        tablaCalificacionAlumno1 = new javax.swing.JTable();
        EliminarNota1 = new javax.swing.JButton();
        DescargarCalificacionesAlumno = new javax.swing.JButton();
        ModificarNota1 = new javax.swing.JButton();
        AddNotaAlumno1 = new javax.swing.JButton();
        agregarNota1 = new javax.swing.JPanel();
        jLabel43 = new javax.swing.JLabel();
        guardarNota1 = new javax.swing.JButton();
        jLabel53 = new javax.swing.JLabel();
        jLabel54 = new javax.swing.JLabel();
        txtTituloNota2 = new javax.swing.JTextField();
        txtTituloNota4 = new javax.swing.JTextField();
        jLabel55 = new javax.swing.JLabel();
        jComboTituloNota1 = new javax.swing.JComboBox<>();
        jLabel56 = new javax.swing.JLabel();
        txtTituloNota5 = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        ModifNota1 = new javax.swing.JPanel();
        EditNota1 = new javax.swing.JButton();
        jLabel57 = new javax.swing.JLabel();
        jLabel58 = new javax.swing.JLabel();
        txtModifEval1 = new javax.swing.JTextField();
        jLabel59 = new javax.swing.JLabel();
        txtModifEval6 = new javax.swing.JTextField();
        jLabel60 = new javax.swing.JLabel();
        txtModifEval7 = new javax.swing.JTextField();
        jLabel61 = new javax.swing.JLabel();
        txtModifEval8 = new javax.swing.JFormattedTextField();
        jLabel62 = new javax.swing.JLabel();
        txtModifEval9 = new javax.swing.JTextField();
        jPanel7 = new javax.swing.JPanel();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        jPanel12 = new javax.swing.JPanel();
        txtContrasena = new javax.swing.JPasswordField();
        CambioContraseña = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtContrasena2 = new javax.swing.JPasswordField();
        jPanel13 = new javax.swing.JPanel();
        closeSession1 = new javax.swing.JButton();
        lblBienvenida2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        lblBienvenida3 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 204), 2));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel8.setBackground(new java.awt.Color(255, 255, 255));
        jPanel8.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 204)));
        jPanel8.setPreferredSize(new java.awt.Dimension(800, 500));
        jPanel8.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

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

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));
        jPanel6.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jTabbedPane3.setBackground(new java.awt.Color(255, 255, 255));
        jTabbedPane3.setForeground(new java.awt.Color(0, 0, 153));
        jTabbedPane3.setTabPlacement(javax.swing.JTabbedPane.LEFT);
        jTabbedPane3.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N

        PanelGrados.setBackground(new java.awt.Color(255, 255, 255));

        jScrollPane1.setColumnHeaderView(null);
        jScrollPane1.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N

        tablaGrados.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        tablaGrados.setModel(new javax.swing.table.DefaultTableModel(
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
        tablaGrados.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        tablaGrados.setCellSelectionEnabled(true);
        tablaGrados.setEditingColumn(0);
        tablaGrados.setEditingRow(0);
        tablaGrados.setRowHeight(30);
        tablaGrados.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tablaGrados.setShowGrid(false);
        tablaGrados.setShowHorizontalLines(true);
        jScrollPane1.setViewportView(tablaGrados);

        jLabel14.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jLabel14.setText("Clases Asignadas");

        jScrollPane4.setBackground(new java.awt.Color(255, 255, 255));
        jScrollPane4.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jScrollPane4.setRowHeaderView(null);

        tablaClaseGrados.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        tablaClaseGrados.setModel(new javax.swing.table.DefaultTableModel(
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
        tablaClaseGrados.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        tablaClaseGrados.setColumnSelectionAllowed(true);
        tablaClaseGrados.setEditingColumn(0);
        tablaClaseGrados.setEditingRow(0);
        tablaClaseGrados.setRowHeight(30);
        tablaClaseGrados.setShowGrid(false);
        tablaClaseGrados.setShowHorizontalLines(true);
        jScrollPane4.setViewportView(tablaClaseGrados);

        modifEval.setBackground(new java.awt.Color(153, 0, 51));
        modifEval.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        modifEval.setForeground(new java.awt.Color(255, 255, 255));
        modifEval.setText("Eliminar Grado");
        modifEval.setBorder(null);
        modifEval.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        modifEval.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                modifEvalActionPerformed(evt);
            }
        });

        AddGrado.setBackground(new java.awt.Color(0, 153, 0));
        AddGrado.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        AddGrado.setForeground(new java.awt.Color(255, 255, 255));
        AddGrado.setText("+  Grado");
        AddGrado.setBorder(null);
        AddGrado.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        AddGrado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AddGradoActionPerformed(evt);
            }
        });

        DescargarEv.setBackground(new java.awt.Color(0, 0, 204));
        DescargarEv.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        DescargarEv.setForeground(new java.awt.Color(255, 255, 255));
        DescargarEv.setText("Descargar Clases");
        DescargarEv.setToolTipText("");
        DescargarEv.setBorder(null);
        DescargarEv.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        DescargarEv.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DescargarEvActionPerformed(evt);
            }
        });

        jLabel31.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jLabel31.setText("Grados");

        ModifGrado.setBackground(new java.awt.Color(0, 0, 204));
        ModifGrado.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        ModifGrado.setForeground(new java.awt.Color(255, 255, 255));
        ModifGrado.setText("Modificar Grado");
        ModifGrado.setBorder(null);
        ModifGrado.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        ModifGrado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ModifGradoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout PanelGradosLayout = new javax.swing.GroupLayout(PanelGrados);
        PanelGrados.setLayout(PanelGradosLayout);
        PanelGradosLayout.setHorizontalGroup(
            PanelGradosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelGradosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PanelGradosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(PanelGradosLayout.createSequentialGroup()
                        .addComponent(AddGrado, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 37, Short.MAX_VALUE)
                        .addComponent(modifEval, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(26, 26, 26)
                        .addComponent(ModifGrado, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, PanelGradosLayout.createSequentialGroup()
                        .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, PanelGradosLayout.createSequentialGroup()
                        .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(DescargarEv, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING))
                .addGap(20, 20, 20))
        );
        PanelGradosLayout.setVerticalGroup(
            PanelGradosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelGradosLayout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jLabel31)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 168, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PanelGradosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(DescargarEv, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 248, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PanelGradosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(modifEval, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ModifGrado, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(AddGrado, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(11, 11, 11))
        );

        jTabbedPane3.addTab("Ver los grados", PanelGrados);

        agregarGrupo.setBackground(new java.awt.Color(255, 255, 255));

        Nombre.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        Nombre.setText("Nombre Grado");

        txtAddGrupo1.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N

        addGrupo.setBackground(new java.awt.Color(0, 0, 204));
        addGrupo.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        addGrupo.setForeground(new java.awt.Color(255, 255, 255));
        addGrupo.setText("Agregar Grado");
        addGrupo.setBorder(null);
        addGrupo.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        addGrupo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addGrupoActionPerformed(evt);
            }
        });

        jLabel20.setFont(new java.awt.Font("Poppins", 1, 18)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(0, 0, 204));
        jLabel20.setText("Agregar Grado");

        Descripcion.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        Descripcion.setText("Descripción");

        txtAddGrupo.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N

        javax.swing.GroupLayout agregarGrupoLayout = new javax.swing.GroupLayout(agregarGrupo);
        agregarGrupo.setLayout(agregarGrupoLayout);
        agregarGrupoLayout.setHorizontalGroup(
            agregarGrupoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, agregarGrupoLayout.createSequentialGroup()
                .addContainerGap(240, Short.MAX_VALUE)
                .addGroup(agregarGrupoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel20, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(addGrupo, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(Nombre, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtAddGrupo, javax.swing.GroupLayout.DEFAULT_SIZE, 216, Short.MAX_VALUE)
                    .addComponent(Descripcion, javax.swing.GroupLayout.DEFAULT_SIZE, 216, Short.MAX_VALUE)
                    .addComponent(txtAddGrupo1))
                .addGap(204, 204, 204))
        );
        agregarGrupoLayout.setVerticalGroup(
            agregarGrupoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(agregarGrupoLayout.createSequentialGroup()
                .addGap(131, 131, 131)
                .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(29, 29, 29)
                .addComponent(Nombre)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtAddGrupo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Descripcion)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtAddGrupo1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addComponent(addGrupo, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(206, Short.MAX_VALUE))
        );

        jTabbedPane3.addTab("Agregar Grado", agregarGrupo);

        ModifGrados.setBackground(new java.awt.Color(255, 255, 255));

        ModificarGrupo.setBackground(new java.awt.Color(0, 0, 204));
        ModificarGrupo.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        ModificarGrupo.setForeground(new java.awt.Color(255, 255, 255));
        ModificarGrupo.setText("Modificar Grado");
        ModificarGrupo.setBorder(null);
        ModificarGrupo.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        ModificarGrupo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ModificarGrupoActionPerformed(evt);
            }
        });

        jLabel24.setFont(new java.awt.Font("Poppins", 1, 18)); // NOI18N
        jLabel24.setForeground(new java.awt.Color(0, 0, 204));
        jLabel24.setText("Editar Grado");

        jLabel27.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jLabel27.setText("Descripción");

        txtGrado3.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N

        jLabel28.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jLabel28.setText("Fecha de Creación");

        txtGrado4.setEditable(false);
        txtGrado4.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.DateFormatter(java.text.DateFormat.getDateInstance(java.text.DateFormat.SHORT))));
        txtGrado4.setToolTipText("d/MM/yy");
        txtGrado4.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N

        jLabel22.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jLabel22.setText("Id");

        txtGrado.setEditable(false);
        txtGrado.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N

        txtGrado1.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N

        jLabel37.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jLabel37.setText("Grado");

        javax.swing.GroupLayout ModifGradosLayout = new javax.swing.GroupLayout(ModifGrados);
        ModifGrados.setLayout(ModifGradosLayout);
        ModifGradosLayout.setHorizontalGroup(
            ModifGradosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ModifGradosLayout.createSequentialGroup()
                .addGap(94, 94, 94)
                .addGroup(ModifGradosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(ModifGradosLayout.createSequentialGroup()
                        .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ModifGradosLayout.createSequentialGroup()
                        .addGroup(ModifGradosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(ModifGradosLayout.createSequentialGroup()
                                .addGroup(ModifGradosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jLabel22, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(txtGrado, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 193, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(ModifGradosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtGrado1)
                                    .addComponent(jLabel37, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addGroup(ModifGradosLayout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addGroup(ModifGradosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(ModifGradosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(txtGrado4)
                                        .addComponent(jLabel28, javax.swing.GroupLayout.DEFAULT_SIZE, 453, Short.MAX_VALUE)
                                        .addComponent(txtGrado3))
                                    .addComponent(ModificarGrupo, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(113, 113, 113))
                    .addGroup(ModifGradosLayout.createSequentialGroup()
                        .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 383, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(183, Short.MAX_VALUE))))
        );
        ModifGradosLayout.setVerticalGroup(
            ModifGradosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ModifGradosLayout.createSequentialGroup()
                .addGap(100, 100, 100)
                .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21)
                .addGroup(ModifGradosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel22)
                    .addComponent(jLabel37))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(ModifGradosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtGrado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtGrado1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel27)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtGrado3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel28)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtGrado4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(47, 47, 47)
                .addComponent(ModificarGrupo, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(168, Short.MAX_VALUE))
        );

        jTabbedPane3.addTab("Editar Grado", ModifGrados);

        jPanel6.add(jTabbedPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 780, 570));

        jTabbedPane4.addTab("Grupos", jPanel6);

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));

        tabAlumnos.setBackground(new java.awt.Color(255, 255, 255));
        tabAlumnos.setForeground(new java.awt.Color(0, 0, 153));
        tabAlumnos.setTabPlacement(javax.swing.JTabbedPane.LEFT);
        tabAlumnos.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));

        jLabel30.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jLabel30.setText("Clases");

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
        tablaClases.setViewportView(tablaAsignaturas);

        EliminarClase.setBackground(new java.awt.Color(204, 0, 0));
        EliminarClase.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        EliminarClase.setForeground(new java.awt.Color(255, 255, 255));
        EliminarClase.setText("Eliminar Clase");
        EliminarClase.setToolTipText("");
        EliminarClase.setBorder(null);
        EliminarClase.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        EliminarClase.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EliminarClaseActionPerformed(evt);
            }
        });

        DescargarGrupo.setBackground(new java.awt.Color(204, 102, 0));
        DescargarGrupo.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        DescargarGrupo.setForeground(new java.awt.Color(255, 255, 255));
        DescargarGrupo.setText("Editar Grado");
        DescargarGrupo.setToolTipText("");
        DescargarGrupo.setBorder(null);
        DescargarGrupo.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        DescargarGrupo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DescargarGrupoActionPerformed(evt);
            }
        });

        EditarClase.setBackground(new java.awt.Color(0, 0, 204));
        EditarClase.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        EditarClase.setForeground(new java.awt.Color(255, 255, 255));
        EditarClase.setText("Editar Profesor");
        EditarClase.setToolTipText("");
        EditarClase.setBorder(null);
        EditarClase.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        EditarClase.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EditarClaseActionPerformed(evt);
            }
        });

        AgregarClase.setBackground(new java.awt.Color(0, 153, 0));
        AgregarClase.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        AgregarClase.setForeground(new java.awt.Color(255, 255, 255));
        AgregarClase.setText("+ Clase");
        AgregarClase.setToolTipText("");
        AgregarClase.setBorder(null);
        AgregarClase.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        AgregarClase.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AgregarClaseActionPerformed(evt);
            }
        });

        jComboBoxGrupos.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jComboBoxGrupos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxGruposActionPerformed(evt);
            }
        });

        jLabel36.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jLabel36.setText("Elegir grupo:");

        EditarClase1.setBackground(new java.awt.Color(0, 0, 204));
        EditarClase1.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        EditarClase1.setForeground(new java.awt.Color(255, 255, 255));
        EditarClase1.setText("Ver Horarios");
        EditarClase1.setToolTipText("");
        EditarClase1.setBorder(null);
        EditarClase1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        EditarClase1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EditarClase1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel36, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jComboBoxGrupos, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(43, 43, 43)
                        .addComponent(AgregarClase, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(tablaClases)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(DescargarGrupo, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(EditarClase1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(EditarClase, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(EliminarClase, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 5, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBoxGrupos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel30)
                    .addComponent(jLabel36)
                    .addComponent(AgregarClase, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(tablaClases, javax.swing.GroupLayout.PREFERRED_SIZE, 454, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(EliminarClase, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(DescargarGrupo, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(EditarClase, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(EditarClase1, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(137, Short.MAX_VALUE))
        );

        tabAlumnos.addTab("Ver Clases", jPanel5);

        jPanel9.setBackground(new java.awt.Color(255, 255, 255));

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

        jLabel33.setIcon(new javax.swing.ImageIcon(getClass().getResource("/logoPeque.png"))); // NOI18N

        jLabel34.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        jLabel34.setForeground(new java.awt.Color(0, 0, 153));
        jLabel34.setText("Horario");

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(9, 9, 9)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jscrollpanelhorarios, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 667, Short.MAX_VALUE)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel33)
                            .addGroup(jPanel9Layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(jLabel34)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(jLabel33)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(31, 31, 31)
                .addComponent(jscrollpanelhorarios, javax.swing.GroupLayout.PREFERRED_SIZE, 427, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(110, Short.MAX_VALUE))
        );

        tabAlumnos.addTab("Horarios", jPanel9);

        agregarClase.setBackground(new java.awt.Color(255, 255, 255));
        agregarClase.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N

        jLabel41.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jLabel41.setText("Grado");

        btnGuardarClase.setBackground(new java.awt.Color(0, 0, 204));
        btnGuardarClase.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        btnGuardarClase.setForeground(new java.awt.Color(255, 255, 255));
        btnGuardarClase.setText("Agregar Clase");
        btnGuardarClase.setBorder(null);
        btnGuardarClase.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnGuardarClase.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarClaseActionPerformed(evt);
            }
        });

        jLabel42.setFont(new java.awt.Font("Poppins", 1, 18)); // NOI18N
        jLabel42.setForeground(new java.awt.Color(0, 0, 204));
        jLabel42.setText("Agregar Clase");

        txtNombreClase.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N

        jLabel52.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jLabel52.setText("Nombre de Clase");

        comboBoxDia.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        comboBoxDia.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Lunes", "Martes", "Miercoles", "Jueves", "Viernes" }));
        comboBoxDia.setToolTipText("");
        comboBoxDia.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        comboBoxGrado.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        comboBoxGrado.setModel(jComboBoxGrupos.getModel());
        comboBoxGrado.setEnabled(false);

        jLabel74.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jLabel74.setText("Profesor");

        jLabel75.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jLabel75.setText("Dia");

        comboBoxProfesor.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N

        txtHoraInicio.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.DateFormatter(java.text.DateFormat.getTimeInstance(java.text.DateFormat.SHORT))));

        jLabel76.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jLabel76.setText("Hora Fin");

        txtHoraFin.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.DateFormatter(java.text.DateFormat.getTimeInstance(java.text.DateFormat.SHORT))));

        jLabel77.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jLabel77.setText("Hora Inicio");

        javax.swing.GroupLayout agregarClaseLayout = new javax.swing.GroupLayout(agregarClase);
        agregarClase.setLayout(agregarClaseLayout);
        agregarClaseLayout.setHorizontalGroup(
            agregarClaseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, agregarClaseLayout.createSequentialGroup()
                .addContainerGap(237, Short.MAX_VALUE)
                .addGroup(agregarClaseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(agregarClaseLayout.createSequentialGroup()
                        .addComponent(jLabel77, javax.swing.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel76, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(agregarClaseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(agregarClaseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel75, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(comboBoxGrado, 0, 216, Short.MAX_VALUE)
                            .addComponent(comboBoxDia, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel52, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtNombreClase, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 216, Short.MAX_VALUE)
                            .addComponent(jLabel42, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnGuardarClase, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel41, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel74, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(comboBoxProfesor, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(agregarClaseLayout.createSequentialGroup()
                            .addComponent(txtHoraInicio, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txtHoraFin, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(225, 225, 225))
        );
        agregarClaseLayout.setVerticalGroup(
            agregarClaseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(agregarClaseLayout.createSequentialGroup()
                .addGap(83, 83, 83)
                .addComponent(jLabel42, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel41)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(comboBoxGrado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel52, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtNombreClase, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel74)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(comboBoxProfesor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8)
                .addComponent(jLabel75)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(comboBoxDia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(agregarClaseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel76)
                    .addComponent(jLabel77))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(agregarClaseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtHoraInicio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtHoraFin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnGuardarClase, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(247, Short.MAX_VALUE))
        );

        tabAlumnos.addTab("Agregar Clase", agregarClase);

        editarProfesor.setBackground(new java.awt.Color(255, 255, 255));

        EditNota.setBackground(new java.awt.Color(0, 0, 204));
        EditNota.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        EditNota.setForeground(new java.awt.Color(255, 255, 255));
        EditNota.setText("Modificar");
        EditNota.setBorder(null);
        EditNota.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        EditNota.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EditNotaActionPerformed(evt);
            }
        });

        jLabel46.setFont(new java.awt.Font("Poppins", 1, 18)); // NOI18N
        jLabel46.setForeground(new java.awt.Color(0, 0, 204));
        jLabel46.setText("Editar Clase");

        jLabel44.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jLabel44.setText("Profesor");

        jLabel78.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jLabel78.setText("Nombre de Clase");

        txtNombreClase1.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N

        jLabel38.setIcon(new javax.swing.ImageIcon(getClass().getResource("/logoPeque.png"))); // NOI18N

        comboBoxProfesor1.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        comboBoxProfesor1.setModel(comboBoxProfesor.getModel());

        javax.swing.GroupLayout editarProfesorLayout = new javax.swing.GroupLayout(editarProfesor);
        editarProfesor.setLayout(editarProfesorLayout);
        editarProfesorLayout.setHorizontalGroup(
            editarProfesorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(editarProfesorLayout.createSequentialGroup()
                .addGap(229, 229, 229)
                .addGroup(editarProfesorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(comboBoxProfesor1, 0, 216, Short.MAX_VALUE)
                    .addGroup(editarProfesorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jLabel38)
                        .addComponent(EditNota, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel78, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtNombreClase1, javax.swing.GroupLayout.DEFAULT_SIZE, 216, Short.MAX_VALUE)
                        .addComponent(jLabel44, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel46, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        editarProfesorLayout.setVerticalGroup(
            editarProfesorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(editarProfesorLayout.createSequentialGroup()
                .addGap(137, 137, 137)
                .addComponent(jLabel38)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel46, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel44)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(comboBoxProfesor1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel78, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtNombreClase1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(EditNota, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(288, 288, 288))
        );

        tabAlumnos.addTab("Editar Profesor", editarProfesor);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabAlumnos, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabAlumnos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jTabbedPane4.addTab("Clases y Horarios", jPanel4);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        tabAlumnos2.setBackground(new java.awt.Color(255, 255, 255));
        tabAlumnos2.setForeground(new java.awt.Color(0, 0, 153));
        tabAlumnos2.setTabPlacement(javax.swing.JTabbedPane.LEFT);
        tabAlumnos2.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N

        jPanel15.setBackground(new java.awt.Color(255, 255, 255));

        jLabel35.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jLabel35.setText("Alumnos");

        tablaCalificacionAlumno2.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        tablaCalificacionAlumno2.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane8.setViewportView(tablaCalificacionAlumno2);

        EliminarNota2.setBackground(new java.awt.Color(204, 0, 0));
        EliminarNota2.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        EliminarNota2.setForeground(new java.awt.Color(255, 255, 255));
        EliminarNota2.setText("Eliminar Notas");
        EliminarNota2.setToolTipText("");
        EliminarNota2.setBorder(null);
        EliminarNota2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        EliminarNota2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EliminarNota2ActionPerformed(evt);
            }
        });

        DescargarCalificacionesAlumno1.setBackground(new java.awt.Color(204, 102, 0));
        DescargarCalificacionesAlumno1.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        DescargarCalificacionesAlumno1.setForeground(new java.awt.Color(255, 255, 255));
        DescargarCalificacionesAlumno1.setText("Descargar Notas");
        DescargarCalificacionesAlumno1.setToolTipText("");
        DescargarCalificacionesAlumno1.setBorder(null);
        DescargarCalificacionesAlumno1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        DescargarCalificacionesAlumno1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DescargarCalificacionesAlumno1ActionPerformed(evt);
            }
        });

        ModificarNota2.setBackground(new java.awt.Color(0, 0, 204));
        ModificarNota2.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        ModificarNota2.setForeground(new java.awt.Color(255, 255, 255));
        ModificarNota2.setText("Modificar Notas");
        ModificarNota2.setToolTipText("");
        ModificarNota2.setBorder(null);
        ModificarNota2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        ModificarNota2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ModificarNota2ActionPerformed(evt);
            }
        });

        AddNotaAlumno2.setBackground(new java.awt.Color(0, 153, 0));
        AddNotaAlumno2.setFont(new java.awt.Font("Poppins", 1, 18)); // NOI18N
        AddNotaAlumno2.setForeground(new java.awt.Color(255, 255, 255));
        AddNotaAlumno2.setText("+");
        AddNotaAlumno2.setToolTipText("");
        AddNotaAlumno2.setBorder(null);
        AddNotaAlumno2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        AddNotaAlumno2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AddNotaAlumno2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane8)
                    .addGroup(jPanel15Layout.createSequentialGroup()
                        .addComponent(jLabel35)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel15Layout.createSequentialGroup()
                        .addComponent(DescargarCalificacionesAlumno1, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(AddNotaAlumno2, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ModificarNota2, javax.swing.GroupLayout.DEFAULT_SIZE, 263, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(EliminarNota2, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel35)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 422, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ModificarNota2, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(EliminarNota2, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(DescargarCalificacionesAlumno1, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(AddNotaAlumno2, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(82, Short.MAX_VALUE))
        );

        tabAlumnos2.addTab("Todos los Profesores", jPanel15);

        agregarNota2.setBackground(new java.awt.Color(255, 255, 255));

        jLabel63.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jLabel63.setText("Evaluación");

        guardarNota2.setBackground(new java.awt.Color(0, 0, 204));
        guardarNota2.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        guardarNota2.setForeground(new java.awt.Color(255, 255, 255));
        guardarNota2.setText("Guardar Nota");
        guardarNota2.setBorder(null);
        guardarNota2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        guardarNota2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                guardarNota2ActionPerformed(evt);
            }
        });

        jLabel64.setFont(new java.awt.Font("Poppins", 1, 18)); // NOI18N
        jLabel64.setForeground(new java.awt.Color(0, 0, 204));
        jLabel64.setText("Agregar Nota");

        jLabel65.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jLabel65.setText("Alumno");

        txtTituloNota6.setEditable(false);
        txtTituloNota6.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N

        txtTituloNota7.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N

        jLabel66.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jLabel66.setText("Nota");

        jComboTituloNota2.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jComboTituloNota2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel67.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jLabel67.setText("Asignatura");

        txtTituloNota8.setEditable(false);
        txtTituloNota8.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N

        jLabel10.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jLabel10.setText("jLabel2");

        javax.swing.GroupLayout agregarNota2Layout = new javax.swing.GroupLayout(agregarNota2);
        agregarNota2.setLayout(agregarNota2Layout);
        agregarNota2Layout.setHorizontalGroup(
            agregarNota2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, agregarNota2Layout.createSequentialGroup()
                .addContainerGap(246, Short.MAX_VALUE)
                .addGroup(agregarNota2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel66, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtTituloNota7, javax.swing.GroupLayout.DEFAULT_SIZE, 216, Short.MAX_VALUE)
                    .addComponent(jLabel65, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtTituloNota6, javax.swing.GroupLayout.DEFAULT_SIZE, 216, Short.MAX_VALUE)
                    .addComponent(jLabel64, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(guardarNota2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jComboTituloNota2, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel67, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtTituloNota8)
                    .addGroup(agregarNota2Layout.createSequentialGroup()
                        .addComponent(jLabel63, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(192, 192, 192))
        );
        agregarNota2Layout.setVerticalGroup(
            agregarNota2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(agregarNota2Layout.createSequentialGroup()
                .addGap(77, 77, 77)
                .addComponent(jLabel64, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel65)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtTituloNota6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel67)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtTituloNota8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(agregarNota2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel63)
                    .addComponent(jLabel10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboTituloNota2, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel66)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtTituloNota7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(16, 16, 16)
                .addComponent(guardarNota2, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(175, Short.MAX_VALUE))
        );

        tabAlumnos2.addTab("Agregar Alumno", agregarNota2);

        ModifNota2.setBackground(new java.awt.Color(255, 255, 255));

        EditNota2.setBackground(new java.awt.Color(0, 0, 204));
        EditNota2.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        EditNota2.setForeground(new java.awt.Color(255, 255, 255));
        EditNota2.setText("Modificar Evaluación");
        EditNota2.setBorder(null);
        EditNota2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        EditNota2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EditNota2ActionPerformed(evt);
            }
        });

        jLabel68.setFont(new java.awt.Font("Poppins", 1, 18)); // NOI18N
        jLabel68.setForeground(new java.awt.Color(0, 0, 204));
        jLabel68.setText("Editar o Eliminar Evaluación");

        jLabel69.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jLabel69.setText("Asignatura");

        txtModifEval10.setEditable(false);
        txtModifEval10.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N

        jLabel70.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jLabel70.setText("Evaluación");

        txtModifEval11.setEditable(false);
        txtModifEval11.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N

        jLabel71.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jLabel71.setText("Nota");

        txtModifEval12.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N

        jLabel72.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jLabel72.setText("Peso");

        txtModifEval13.setEditable(false);
        txtModifEval13.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.DateFormatter(java.text.DateFormat.getDateInstance(java.text.DateFormat.SHORT))));
        txtModifEval13.setToolTipText("d/MM/yy");
        txtModifEval13.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N

        jLabel73.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jLabel73.setText("Id");

        txtModifEval14.setEditable(false);
        txtModifEval14.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N

        javax.swing.GroupLayout ModifNota2Layout = new javax.swing.GroupLayout(ModifNota2);
        ModifNota2.setLayout(ModifNota2Layout);
        ModifNota2Layout.setHorizontalGroup(
            ModifNota2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ModifNota2Layout.createSequentialGroup()
                .addGap(94, 94, 94)
                .addGroup(ModifNota2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(ModifNota2Layout.createSequentialGroup()
                        .addComponent(jLabel68, javax.swing.GroupLayout.PREFERRED_SIZE, 383, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(ModifNota2Layout.createSequentialGroup()
                        .addComponent(jLabel70, javax.swing.GroupLayout.PREFERRED_SIZE, 216, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ModifNota2Layout.createSequentialGroup()
                        .addGroup(ModifNota2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(ModifNota2Layout.createSequentialGroup()
                                .addGroup(ModifNota2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txtModifEval12, javax.swing.GroupLayout.DEFAULT_SIZE, 191, Short.MAX_VALUE)
                                    .addComponent(jLabel71, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(ModifNota2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel72, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(txtModifEval13)))
                            .addComponent(txtModifEval11, javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, ModifNota2Layout.createSequentialGroup()
                                .addGroup(ModifNota2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtModifEval10, javax.swing.GroupLayout.PREFERRED_SIZE, 216, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel69, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(ModifNota2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel73, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(txtModifEval14, javax.swing.GroupLayout.DEFAULT_SIZE, 225, Short.MAX_VALUE)))
                            .addComponent(EditNota2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(113, 113, 113))))
        );
        ModifNota2Layout.setVerticalGroup(
            ModifNota2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ModifNota2Layout.createSequentialGroup()
                .addGap(88, 88, 88)
                .addComponent(jLabel68, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(27, 27, 27)
                .addGroup(ModifNota2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel69)
                    .addComponent(jLabel73))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(ModifNota2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtModifEval10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtModifEval14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel70)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtModifEval11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(ModifNota2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel71)
                    .addComponent(jLabel72))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(ModifNota2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtModifEval12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtModifEval13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 103, Short.MAX_VALUE)
                .addComponent(EditNota2, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(120, 120, 120))
        );

        tabAlumnos2.addTab("Modificar Alumno", ModifNota2);

        jPanel2.add(tabAlumnos2, new org.netbeans.lib.awtextra.AbsoluteConstraints(-5, 0, 810, -1));

        jTabbedPane4.addTab("Profesores", jPanel2);

        tabAlumnos1.setBackground(new java.awt.Color(255, 255, 255));
        tabAlumnos1.setForeground(new java.awt.Color(0, 0, 153));
        tabAlumnos1.setTabPlacement(javax.swing.JTabbedPane.LEFT);
        tabAlumnos1.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N

        jPanel14.setBackground(new java.awt.Color(255, 255, 255));

        jLabel32.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jLabel32.setText("Alumnos");

        tablaCalificacionAlumno1.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        tablaCalificacionAlumno1.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane7.setViewportView(tablaCalificacionAlumno1);

        EliminarNota1.setBackground(new java.awt.Color(204, 0, 0));
        EliminarNota1.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        EliminarNota1.setForeground(new java.awt.Color(255, 255, 255));
        EliminarNota1.setText("Eliminar Notas");
        EliminarNota1.setToolTipText("");
        EliminarNota1.setBorder(null);
        EliminarNota1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        EliminarNota1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EliminarNota1ActionPerformed(evt);
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

        ModificarNota1.setBackground(new java.awt.Color(0, 0, 204));
        ModificarNota1.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        ModificarNota1.setForeground(new java.awt.Color(255, 255, 255));
        ModificarNota1.setText("Modificar Notas");
        ModificarNota1.setToolTipText("");
        ModificarNota1.setBorder(null);
        ModificarNota1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        ModificarNota1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ModificarNota1ActionPerformed(evt);
            }
        });

        AddNotaAlumno1.setBackground(new java.awt.Color(0, 153, 0));
        AddNotaAlumno1.setFont(new java.awt.Font("Poppins", 1, 18)); // NOI18N
        AddNotaAlumno1.setForeground(new java.awt.Color(255, 255, 255));
        AddNotaAlumno1.setText("+");
        AddNotaAlumno1.setToolTipText("");
        AddNotaAlumno1.setBorder(null);
        AddNotaAlumno1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        AddNotaAlumno1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AddNotaAlumno1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane7)
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addComponent(jLabel32)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addComponent(DescargarCalificacionesAlumno, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(AddNotaAlumno1, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ModificarNota1, javax.swing.GroupLayout.DEFAULT_SIZE, 262, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(EliminarNota1, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel32)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 422, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ModificarNota1, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(EliminarNota1, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(DescargarCalificacionesAlumno, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(AddNotaAlumno1, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(82, Short.MAX_VALUE))
        );

        tabAlumnos1.addTab("Todos los Alumnos", jPanel14);

        agregarNota1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel43.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jLabel43.setText("Evaluación");

        guardarNota1.setBackground(new java.awt.Color(0, 0, 204));
        guardarNota1.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        guardarNota1.setForeground(new java.awt.Color(255, 255, 255));
        guardarNota1.setText("Guardar Nota");
        guardarNota1.setBorder(null);
        guardarNota1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        guardarNota1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                guardarNota1ActionPerformed(evt);
            }
        });

        jLabel53.setFont(new java.awt.Font("Poppins", 1, 18)); // NOI18N
        jLabel53.setForeground(new java.awt.Color(0, 0, 204));
        jLabel53.setText("Agregar Nota");

        jLabel54.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jLabel54.setText("Alumno");

        txtTituloNota2.setEditable(false);
        txtTituloNota2.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N

        txtTituloNota4.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N

        jLabel55.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jLabel55.setText("Nota");

        jComboTituloNota1.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jComboTituloNota1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel56.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jLabel56.setText("Asignatura");

        txtTituloNota5.setEditable(false);
        txtTituloNota5.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N

        jLabel9.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jLabel9.setText("jLabel2");

        javax.swing.GroupLayout agregarNota1Layout = new javax.swing.GroupLayout(agregarNota1);
        agregarNota1.setLayout(agregarNota1Layout);
        agregarNota1Layout.setHorizontalGroup(
            agregarNota1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, agregarNota1Layout.createSequentialGroup()
                .addContainerGap(245, Short.MAX_VALUE)
                .addGroup(agregarNota1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel55, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtTituloNota4, javax.swing.GroupLayout.DEFAULT_SIZE, 216, Short.MAX_VALUE)
                    .addComponent(jLabel54, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtTituloNota2, javax.swing.GroupLayout.DEFAULT_SIZE, 216, Short.MAX_VALUE)
                    .addComponent(jLabel53, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(guardarNota1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jComboTituloNota1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel56, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtTituloNota5)
                    .addGroup(agregarNota1Layout.createSequentialGroup()
                        .addComponent(jLabel43, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(192, 192, 192))
        );
        agregarNota1Layout.setVerticalGroup(
            agregarNota1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(agregarNota1Layout.createSequentialGroup()
                .addGap(77, 77, 77)
                .addComponent(jLabel53, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel54)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtTituloNota2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel56)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtTituloNota5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(agregarNota1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel43)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboTituloNota1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel55)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtTituloNota4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(16, 16, 16)
                .addComponent(guardarNota1, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(175, Short.MAX_VALUE))
        );

        tabAlumnos1.addTab("Agregar Alumno", agregarNota1);

        ModifNota1.setBackground(new java.awt.Color(255, 255, 255));

        EditNota1.setBackground(new java.awt.Color(0, 0, 204));
        EditNota1.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        EditNota1.setForeground(new java.awt.Color(255, 255, 255));
        EditNota1.setText("Modificar Evaluación");
        EditNota1.setBorder(null);
        EditNota1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        EditNota1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EditNota1ActionPerformed(evt);
            }
        });

        jLabel57.setFont(new java.awt.Font("Poppins", 1, 18)); // NOI18N
        jLabel57.setForeground(new java.awt.Color(0, 0, 204));
        jLabel57.setText("Editar o Eliminar Evaluación");

        jLabel58.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jLabel58.setText("Asignatura");

        txtModifEval1.setEditable(false);
        txtModifEval1.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N

        jLabel59.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jLabel59.setText("Evaluación");

        txtModifEval6.setEditable(false);
        txtModifEval6.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N

        jLabel60.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jLabel60.setText("Nota");

        txtModifEval7.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N

        jLabel61.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jLabel61.setText("Peso");

        txtModifEval8.setEditable(false);
        txtModifEval8.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.DateFormatter(java.text.DateFormat.getDateInstance(java.text.DateFormat.SHORT))));
        txtModifEval8.setToolTipText("d/MM/yy");
        txtModifEval8.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N

        jLabel62.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jLabel62.setText("Id");

        txtModifEval9.setEditable(false);
        txtModifEval9.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N

        javax.swing.GroupLayout ModifNota1Layout = new javax.swing.GroupLayout(ModifNota1);
        ModifNota1.setLayout(ModifNota1Layout);
        ModifNota1Layout.setHorizontalGroup(
            ModifNota1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ModifNota1Layout.createSequentialGroup()
                .addGap(94, 94, 94)
                .addGroup(ModifNota1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(ModifNota1Layout.createSequentialGroup()
                        .addComponent(jLabel57, javax.swing.GroupLayout.PREFERRED_SIZE, 383, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(ModifNota1Layout.createSequentialGroup()
                        .addComponent(jLabel59, javax.swing.GroupLayout.PREFERRED_SIZE, 216, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ModifNota1Layout.createSequentialGroup()
                        .addGroup(ModifNota1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(ModifNota1Layout.createSequentialGroup()
                                .addGroup(ModifNota1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txtModifEval7, javax.swing.GroupLayout.DEFAULT_SIZE, 191, Short.MAX_VALUE)
                                    .addComponent(jLabel60, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(ModifNota1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel61, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(txtModifEval8)))
                            .addComponent(txtModifEval6, javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, ModifNota1Layout.createSequentialGroup()
                                .addGroup(ModifNota1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtModifEval1, javax.swing.GroupLayout.PREFERRED_SIZE, 216, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel58, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(ModifNota1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel62, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(txtModifEval9, javax.swing.GroupLayout.DEFAULT_SIZE, 224, Short.MAX_VALUE)))
                            .addComponent(EditNota1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(113, 113, 113))))
        );
        ModifNota1Layout.setVerticalGroup(
            ModifNota1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ModifNota1Layout.createSequentialGroup()
                .addGap(88, 88, 88)
                .addComponent(jLabel57, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(27, 27, 27)
                .addGroup(ModifNota1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel58)
                    .addComponent(jLabel62))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(ModifNota1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtModifEval1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtModifEval9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel59)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtModifEval6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(ModifNota1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel60)
                    .addComponent(jLabel61))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(ModifNota1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtModifEval7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtModifEval8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 103, Short.MAX_VALUE)
                .addComponent(EditNota1, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(120, 120, 120))
        );

        tabAlumnos1.addTab("Modificar Alumno", ModifNota1);

        jTabbedPane4.addTab("Alumnos", tabAlumnos1);

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

        mainPanel3.add(jTabbedPane4, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 10, 800, 610));

        jPanel13.setBackground(new java.awt.Color(0, 0, 102));

        closeSession1.setBackground(new java.awt.Color(153, 0, 0));
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

        lblBienvenida2.setBackground(new java.awt.Color(255, 255, 255));
        lblBienvenida2.setFont(new java.awt.Font("Poppins Medium", 3, 18)); // NOI18N
        lblBienvenida2.setForeground(new java.awt.Color(255, 255, 255));
        lblBienvenida2.setText("Portal Admin");

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/user_docente.png"))); // NOI18N

        lblBienvenida3.setBackground(new java.awt.Color(255, 255, 255));
        lblBienvenida3.setFont(new java.awt.Font("Poppins Medium", 3, 14)); // NOI18N
        lblBienvenida3.setForeground(new java.awt.Color(255, 255, 255));
        lblBienvenida3.setText("¡Bienvenid@!");

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblBienvenida2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel13Layout.createSequentialGroup()
                        .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 148, Short.MAX_VALUE)
                            .addComponent(closeSession1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap(21, Short.MAX_VALUE))))
            .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel13Layout.createSequentialGroup()
                    .addContainerGap(29, Short.MAX_VALUE)
                    .addComponent(lblBienvenida3, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(18, 18, 18)))
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(lblBienvenida2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 279, Short.MAX_VALUE)
                .addComponent(closeSession1, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(73, 73, 73))
            .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel13Layout.createSequentialGroup()
                    .addGap(278, 278, 278)
                    .addComponent(lblBienvenida3)
                    .addContainerGap(320, Short.MAX_VALUE)))
        );

        mainPanel3.add(jPanel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 200, 620));

        jPanel8.add(mainPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, -4, -1, 620));

        jPanel1.add(jPanel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1000, 620));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

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

    private void ModificarGrupoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ModificarGrupoActionPerformed
        int selectedRow = tablaGrados.getSelectedRow();

        // Validar si hay una fila seleccionada
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un grupo para modificar.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Obtener los valores actuales del grupo seleccionado
        int idGrupo = Integer.parseInt(tablaGrados.getValueAt(selectedRow, 0).toString());
        String nuevoNombre = txtGrado1.getText().trim();
        String nuevaDescripcion = txtGrado3.getText().trim();

        // Validar campos
        if (nuevoNombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre del grupo no puede estar vacío.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Llamar al método para modificar el grupo
        GrupoController grupoController = new GrupoController();
        if (grupoController.modificarGrupo(idGrupo, nuevoNombre, nuevaDescripcion)) {
            JOptionPane.showMessageDialog(this, "Grupo modificado correctamente.");
            cargarGrados(); // Recargar tabla
        } else {
            JOptionPane.showMessageDialog(this, "Error al modificar el grupo.", "Error", JOptionPane.ERROR_MESSAGE);
        }

    }//GEN-LAST:event_ModificarGrupoActionPerformed

    private void addGrupoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addGrupoActionPerformed
        String nombreGrupo = txtAddGrupo.getText().trim();
        String descripcionGrupo = txtAddGrupo1.getText().trim();

        // Validación de campos
        if (nombreGrupo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre del grupo es obligatorio.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Llamar al método para insertar el grupo en la base de datos
        GrupoController grupoController = new GrupoController();
        if (grupoController.agregarGrupo(nombreGrupo, descripcionGrupo)) {
            JOptionPane.showMessageDialog(this, "Grupo agregado correctamente.");
            cargarGrados(); // Recargar la tabla con los grupos actualizados
            txtAddGrupo.setText(""); // Limpiar los campos
            txtAddGrupo1.setText("");
            jTabbedPane3.setSelectedComponent(PanelGrados);
        } else {
            JOptionPane.showMessageDialog(this, "Error al agregar el grupo.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_addGrupoActionPerformed

    private void DescargarEvActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DescargarEvActionPerformed

    }//GEN-LAST:event_DescargarEvActionPerformed

    private void AddGradoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddGradoActionPerformed
        int selectedRowGrado = tablaGrados.getSelectedRow();

        if (selectedRowGrado == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un grado primero.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {

            jTabbedPane3.setSelectedComponent(agregarGrupo);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Ocurrió un error al cargar los datos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_AddGradoActionPerformed

    private void modifEvalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_modifEvalActionPerformed
// Obtener la fila seleccionada en la tabla de grupos
        int selectedRow = tablaGrados.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, selecciona un grupo para eliminar.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Mostrar confirmación antes de proceder
        int confirm = JOptionPane.showConfirmDialog(this, "¿Estás seguro de que deseas eliminar este grupo? Esto desvinculará a los alumnos asociados, pero no los eliminará.", "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            // Obtener el ID del grupo desde la tabla
            int idGrupo = Integer.parseInt(tablaGrados.getValueAt(selectedRow, 0).toString());

            // Llamar al controlador para eliminar el grupo
            GrupoController grupoController = new GrupoController();
            if (grupoController.eliminarGrupo(idGrupo)) {
                JOptionPane.showMessageDialog(this, "Grupo eliminado correctamente.");

                // Recargar la tabla de grupos para reflejar los cambios
                cargarGrados();
                jTabbedPane3.setSelectedComponent(PanelGrados);
            } else {
                JOptionPane.showMessageDialog(this, "Error al eliminar el grupo.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_modifEvalActionPerformed

    private void btnGuardarClaseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarClaseActionPerformed
        String nombreAsignatura = txtNombreClase.getText();
        String nombreProfesor = comboBoxProfesor.getSelectedItem().toString();
        String nombreGrupo = comboBoxGrado.getSelectedItem().toString();
        String dia = comboBoxDia.getSelectedItem().toString();
        Time horaInicio = Time.valueOf(txtHoraInicio.getText() + ":00");
        Time horaFin = Time.valueOf(txtHoraFin.getText() + ":00");

        DocenteController docenteController = new DocenteController();
        GrupoController grupoController = new GrupoController();

        int idProfesor = docenteController.obtenerIdProfesorPorNombre(nombreProfesor);
        int idGrupo = grupoController.obtenerIdGrupoPorNombre(nombreGrupo);

        if (!nombreAsignatura.isEmpty() && idProfesor > 0 && idGrupo > 0 && !txtHoraInicio.getText().isEmpty() && !txtHoraFin.getText().isEmpty()) {
            AsignaturaController asignaturaController = new AsignaturaController();
            boolean resultado = asignaturaController.crearAsignaturaCompleta(nombreAsignatura, idProfesor, idGrupo, dia, horaInicio, horaFin);
            if (resultado) {
                JOptionPane.showMessageDialog(this, "Clase creada correctamente.");
                cargarAsignaturas(); // Recargar tabla de asignaturas
                tabAlumnos.setSelectedComponent(jPanel5);
            } else {
                JOptionPane.showMessageDialog(this, "Error al crear la clase.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Por favor, complete todos los campos.", "Advertencia", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_btnGuardarClaseActionPerformed

    private void AgregarClaseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AgregarClaseActionPerformed

    }//GEN-LAST:event_AgregarClaseActionPerformed

    private void EditarClaseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EditarClaseActionPerformed

        // Obtener la fila seleccionada de la tabla de asignaturas
        int selectedRow = tablaAsignaturas.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, selecciona una asignatura para editar.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Obtener valores de la fila seleccionada
        String nombreClase = tablaAsignaturas.getValueAt(selectedRow, 0).toString(); // Columna de nombre de la asignatura
        String profesorActual = tablaAsignaturas.getValueAt(selectedRow, 1).toString(); // Columna de profesor actual

        // Cargar los valores en los campos de edición
        txtNombreClase1.setText(nombreClase); // Nombre de la asignatura
        comboBoxProfesor1.setSelectedItem(profesorActual); // Seleccionar el profesor actual en el ComboBox
        tabAlumnos.setSelectedComponent(editarProfesor);
    }//GEN-LAST:event_EditarClaseActionPerformed

    private void DescargarGrupoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DescargarGrupoActionPerformed

    }//GEN-LAST:event_DescargarGrupoActionPerformed

    private void EliminarClaseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EliminarClaseActionPerformed

    }//GEN-LAST:event_EliminarClaseActionPerformed

    private void ModifGradoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ModifGradoActionPerformed
        int selectedRowGrado = tablaGrados.getSelectedRow();

        if (selectedRowGrado == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona una asignatura primero.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            txtGrado.setText(tablaGrados.getValueAt(selectedRowGrado, 0).toString()); // ID de evaluación
            txtGrado1.setText(tablaGrados.getValueAt(selectedRowGrado, 1).toString()); // ID de evaluación
            txtGrado3.setText(tablaGrados.getValueAt(selectedRowGrado, 2).toString()); // ID de evaluación
            txtGrado4.setText(tablaGrados.getValueAt(selectedRowGrado, 3).toString()); // ID de evaluación

            // Cambiar al panel de modificación
            jTabbedPane3.setSelectedComponent(ModifGrados);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Ocurrió un error al cargar los datos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_ModifGradoActionPerformed

    private void EditNota1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EditNota1ActionPerformed

    }//GEN-LAST:event_EditNota1ActionPerformed

    private void guardarNota1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_guardarNota1ActionPerformed

    }//GEN-LAST:event_guardarNota1ActionPerformed

    private void AddNotaAlumno1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddNotaAlumno1ActionPerformed

    }//GEN-LAST:event_AddNotaAlumno1ActionPerformed

    private void ModificarNota1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ModificarNota1ActionPerformed

    }//GEN-LAST:event_ModificarNota1ActionPerformed

    private void DescargarCalificacionesAlumnoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DescargarCalificacionesAlumnoActionPerformed

    }//GEN-LAST:event_DescargarCalificacionesAlumnoActionPerformed

    private void EliminarNota1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EliminarNota1ActionPerformed

    }//GEN-LAST:event_EliminarNota1ActionPerformed

    private void EliminarNota2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EliminarNota2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_EliminarNota2ActionPerformed

    private void DescargarCalificacionesAlumno1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DescargarCalificacionesAlumno1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_DescargarCalificacionesAlumno1ActionPerformed

    private void ModificarNota2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ModificarNota2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ModificarNota2ActionPerformed

    private void AddNotaAlumno2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddNotaAlumno2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_AddNotaAlumno2ActionPerformed

    private void guardarNota2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_guardarNota2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_guardarNota2ActionPerformed

    private void EditNota2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EditNota2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_EditNota2ActionPerformed

    private void jComboBoxGruposActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxGruposActionPerformed
        filtrarAsignaturasPorGrupo();
    }//GEN-LAST:event_jComboBoxGruposActionPerformed

    private void EditNotaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EditNotaActionPerformed
       String nombreProfesor = (String) comboBoxProfesor1.getSelectedItem();
    String nombreClase = txtNombreClase1.getText().trim();
        if (nombreProfesor == null || nombreProfesor.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Por favor, selecciona un profesor válido.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    if (nombreClase.isEmpty()) {
        JOptionPane.showMessageDialog(this, "El nombre de la clase no puede estar vacío.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    // Obtener el ID del profesor por su nombre
    DocenteController docenteController = new DocenteController();
    int idProfesor = docenteController.obtenerIdProfesorPorNombre(nombreProfesor);

    if (idProfesor == -1) {
        JOptionPane.showMessageDialog(this, "El profesor seleccionado no existe.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    }//GEN-LAST:event_EditNotaActionPerformed

    private void EditarClase1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EditarClase1ActionPerformed
        // Obtener el grupo seleccionado del comboBoxGrupos
        String grupoSeleccionado = jComboBoxGrupos.getSelectedItem().toString();

        if (grupoSeleccionado.equals("Todos") || grupoSeleccionado.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, selecciona un grupo válido.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Obtener el ID del grupo seleccionado
        GrupoController grupoController = new GrupoController();
        int idGrupo = grupoController.obtenerIdGrupoPorNombre(grupoSeleccionado);

        if (idGrupo > 0) {
            // Cargar el horario del grupo seleccionado
            cargarHorario(idGrupo);

            // Cambiar a la pestaña del horario
            tabAlumnos.setSelectedComponent(jPanel9);
        } else {
            JOptionPane.showMessageDialog(this, "No se encontró el grupo seleccionado.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_EditarClase1ActionPerformed

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
            java.util.logging.Logger.getLogger(AdminView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(AdminView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(AdminView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AdminView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {

            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton AddGrado;
    private javax.swing.JButton AddNotaAlumno1;
    private javax.swing.JButton AddNotaAlumno2;
    private javax.swing.JButton AgregarClase;
    private javax.swing.JButton CambioContraseña;
    private javax.swing.JButton DescargarCalificacionesAlumno;
    private javax.swing.JButton DescargarCalificacionesAlumno1;
    private javax.swing.JButton DescargarEv;
    private javax.swing.JButton DescargarGrupo;
    private javax.swing.JLabel Descripcion;
    private javax.swing.JButton EditNota;
    private javax.swing.JButton EditNota1;
    private javax.swing.JButton EditNota2;
    private javax.swing.JButton EditarClase;
    private javax.swing.JButton EditarClase1;
    private javax.swing.JButton EliminarClase;
    private javax.swing.JButton EliminarNota1;
    private javax.swing.JButton EliminarNota2;
    private javax.swing.JButton ModifGrado;
    private javax.swing.JPanel ModifGrados;
    private javax.swing.JPanel ModifNota1;
    private javax.swing.JPanel ModifNota2;
    private javax.swing.JButton ModificarGrupo;
    private javax.swing.JButton ModificarNota1;
    private javax.swing.JButton ModificarNota2;
    private javax.swing.JLabel Nombre;
    private javax.swing.JPanel PanelGrados;
    private javax.swing.JButton addGrupo;
    private javax.swing.JPanel agregarClase;
    private javax.swing.JPanel agregarGrupo;
    private javax.swing.JPanel agregarNota1;
    private javax.swing.JPanel agregarNota2;
    private javax.swing.JButton btnGuardarClase;
    private javax.swing.JButton closeSession1;
    private javax.swing.JComboBox<String> comboBoxDia;
    private javax.swing.JComboBox<String> comboBoxGrado;
    private javax.swing.JComboBox<String> comboBoxProfesor;
    private javax.swing.JComboBox<String> comboBoxProfesor1;
    private javax.swing.JPanel editarProfesor;
    private javax.swing.JButton guardarNota1;
    private javax.swing.JButton guardarNota2;
    private javax.swing.JComboBox<String> jComboBoxGrupos;
    private javax.swing.JComboBox<String> jComboTituloNota1;
    private javax.swing.JComboBox<String> jComboTituloNota2;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel57;
    private javax.swing.JLabel jLabel58;
    private javax.swing.JLabel jLabel59;
    private javax.swing.JLabel jLabel60;
    private javax.swing.JLabel jLabel61;
    private javax.swing.JLabel jLabel62;
    private javax.swing.JLabel jLabel63;
    private javax.swing.JLabel jLabel64;
    private javax.swing.JLabel jLabel65;
    private javax.swing.JLabel jLabel66;
    private javax.swing.JLabel jLabel67;
    private javax.swing.JLabel jLabel68;
    private javax.swing.JLabel jLabel69;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel70;
    private javax.swing.JLabel jLabel71;
    private javax.swing.JLabel jLabel72;
    private javax.swing.JLabel jLabel73;
    private javax.swing.JLabel jLabel74;
    private javax.swing.JLabel jLabel75;
    private javax.swing.JLabel jLabel76;
    private javax.swing.JLabel jLabel77;
    private javax.swing.JLabel jLabel78;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JTabbedPane jTabbedPane3;
    private javax.swing.JTabbedPane jTabbedPane4;
    private javax.swing.JScrollPane jscrollpanelhorarios;
    private javax.swing.JLabel lblBienvenida2;
    private javax.swing.JLabel lblBienvenida3;
    private javax.swing.JPanel mainPanel3;
    private javax.swing.JButton modifEval;
    private javax.swing.JTabbedPane tabAlumnos;
    private javax.swing.JTabbedPane tabAlumnos1;
    private javax.swing.JTabbedPane tabAlumnos2;
    private javax.swing.JTable tablaAsignaturas;
    private javax.swing.JTable tablaCalificacionAlumno1;
    private javax.swing.JTable tablaCalificacionAlumno2;
    private javax.swing.JTable tablaClaseGrados;
    private javax.swing.JScrollPane tablaClases;
    private javax.swing.JTable tablaGrados;
    private javax.swing.JTable tablaHorario;
    private javax.swing.JTextField txtAddGrupo;
    private javax.swing.JTextField txtAddGrupo1;
    private javax.swing.JPasswordField txtContrasena;
    private javax.swing.JPasswordField txtContrasena2;
    private javax.swing.JTextField txtGrado;
    private javax.swing.JTextField txtGrado1;
    private javax.swing.JTextField txtGrado3;
    private javax.swing.JFormattedTextField txtGrado4;
    private javax.swing.JFormattedTextField txtHoraFin;
    private javax.swing.JFormattedTextField txtHoraInicio;
    private javax.swing.JTextField txtModifEval1;
    private javax.swing.JTextField txtModifEval10;
    private javax.swing.JTextField txtModifEval11;
    private javax.swing.JTextField txtModifEval12;
    private javax.swing.JFormattedTextField txtModifEval13;
    private javax.swing.JTextField txtModifEval14;
    private javax.swing.JTextField txtModifEval6;
    private javax.swing.JTextField txtModifEval7;
    private javax.swing.JFormattedTextField txtModifEval8;
    private javax.swing.JTextField txtModifEval9;
    private javax.swing.JTextField txtNombreClase;
    private javax.swing.JTextField txtNombreClase1;
    private javax.swing.JTextField txtTituloNota2;
    private javax.swing.JTextField txtTituloNota4;
    private javax.swing.JTextField txtTituloNota5;
    private javax.swing.JTextField txtTituloNota6;
    private javax.swing.JTextField txtTituloNota7;
    private javax.swing.JTextField txtTituloNota8;
    // End of variables declaration//GEN-END:variables
}
