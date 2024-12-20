/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package proyectogamma.views;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import static java.sql.Types.NULL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import proyectogamma.controller.AlumnoController;
import proyectogamma.controller.AsignaturaController;
import proyectogamma.controller.DocenteController;
import proyectogamma.controller.GrupoAsignaturaController;
import proyectogamma.controller.GrupoController;
import proyectogamma.controller.HorarioAsignaturaController;
import proyectogamma.controller.UsuarioController;
import proyectogamma.model.Alumno;
import proyectogamma.model.Asignatura;
import proyectogamma.model.BaseDatos;
import proyectogamma.model.Docente;
import proyectogamma.model.GrupoAsignatura;
import proyectogamma.model.Grupos;
import proyectogamma.model.HorarioAsignatura;
import proyectogamma.model.Usuario;
import proyectogamma.utils.PDFGenerator;
import java.sql.ResultSet;
import proyectogamma.controller.AlumnoGrupoController;

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
            cargarProfesoresEnTabla();
            cargarAlumnosEnTabla();
            tablaGrados.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    tablaGradosMouseClicked(evt);
                }
            });
            tablaHorarios.getSelectionModel().addListSelectionListener(event -> {
                if (!event.getValueIsAdjusting() && tablaHorarios.getSelectedRow() != -1) {
                    int selectedRow = tablaHorarios.getSelectedRow();

                    // Obtener datos de la fila seleccionada
                    String dia = tablaHorarios.getValueAt(selectedRow, 1).toString();
                    String horaInicio = tablaHorarios.getValueAt(selectedRow, 2).toString();
                    String horaFin = tablaHorarios.getValueAt(selectedRow, 3).toString();

                    // Prellenar los campos de edición
                    comboDia.setSelectedItem(dia);
                    txtHoraInicio1.setText(horaInicio);
                    txtHoraFin1.setText(horaFin);

                }
            });
            DefaultTableModel modeloTablaAlumnos = new DefaultTableModel(new String[]{"Nombre", "Apellido", "Email"}, 0);
            tablaAlumnosCargaMasiva.setModel(modeloTablaAlumnos);

        } else {
            System.out.println("El usuario no tiene permisos de administrador.");
            JOptionPane.showMessageDialog(this, "Acceso denegado: No tienes permisos de administrador.", "Error", JOptionPane.ERROR_MESSAGE);
            dispose(); // Cierra la ventana si no tiene permisos
        }

    }

    private void cargarAlumnosEnTabla() {
        // Crear el modelo de tabla con una nueva columna "Grupo"
        DefaultTableModel model = new DefaultTableModel(
                new String[]{"ID", "Nombre", "Apellido", "Email", "Fecha Nacimiento", "Fecha Registro", "Grupo"}, 0);

        String sql = """
            SELECT 
                a.id, 
                a.nombre, 
                a.apellido, 
                a.email, 
                a.fecha_nacimiento, 
                a.fecha_registro, 
                g.nombre AS grupo 
            FROM alumno a
            LEFT JOIN alumno_grupos ag ON a.id = ag.id_alumno
            LEFT JOIN grupos g ON ag.id_grupo = g.id
            """;

        try (Connection conn = BaseDatos.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            // Llenar el modelo con los datos obtenidos
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getString("apellido"),
                    rs.getString("email"),
                    rs.getDate("fecha_nacimiento") != null
                    ? new SimpleDateFormat("dd/MM/yyyy").format(rs.getDate("fecha_nacimiento"))
                    : "N/A",
                    rs.getTimestamp("fecha_registro") != null
                    ? new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(rs.getTimestamp("fecha_registro"))
                    : "N/A",
                    rs.getString("grupo") != null ? rs.getString("grupo") : "Sin grupo"
                });
            }

        } catch (SQLException e) {
            System.out.println("Error al cargar alumnos: " + e.getMessage());
        }

        // Asignar el modelo a la tabla
        TablaAlumnosTotales.setModel(model);
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

        DefaultTableModel model = new DefaultTableModel(new String[]{"Id", "Asignatura", "Profesor", "Grupo"}, 0);

        for (GrupoAsignatura ga : lista) {
            model.addRow(new Object[]{ga.getIdAsignatura(), ga.getNombreAsignatura(), ga.getNombreProfesor(), ga.getNombreGrupo()});
        }

        tablaAsignaturas.setModel(model);
    }

    private void cargarComboBoxGrupos() {
        GrupoController grupoController = new GrupoController();
        List<Grupos> grupos = grupoController.obtenerGrupos();
        System.out.println("Grupos obtenidos: " + grupos.size());

        // Limpiar los ComboBox
        jComboBoxGrupos.removeAllItems();
        jComboBoxGrupos2.removeAllItems();
        comboBoxGrupos.removeAllItems();

        // Agregar elementos por defecto
        jComboBoxGrupos.addItem("Todos");
        jComboBoxGrupos.addItem("Sin Asignar");

        for (Grupos grupo : grupos) {
            System.out.println("Agregando grupo: " + grupo.getNombre());
            jComboBoxGrupos.addItem(grupo.getNombre());
            jComboBoxGrupos2.addItem(grupo.getNombre());
            comboBoxGrupos.addItem(grupo.getNombre());
        }
    }

    private void cargarProfesoresEnTabla() {
        DocenteController docenteController = new DocenteController();
        List<Docente> docentes = docenteController.obtenerTodosLosDocentes();

        // Crear el modelo para la tabla
        DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "Nombre", "Apellido", "Email", "Especialidad", "Fecha Contratación"}, 0);

        for (Docente docente : docentes) {
            model.addRow(new Object[]{
                docente.getId(),
                docente.getNombre(),
                docente.getApellido(),
                docente.getEmail(),
                docente.getEspecialidad(),
                new SimpleDateFormat("yyyy-MM-dd").format(docente.getFechaContratacion())
            });
        }

        // Asignar el modelo a la tabla
        tablaProfesores.setModel(model);
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

        DefaultTableModel model = new DefaultTableModel(new String[]{"Id", "Asignatura", "Profesor", "Grupo"}, 0);

        for (GrupoAsignatura ga : lista) {
            model.addRow(new Object[]{ga.getIdAsignatura(), ga.getNombreAsignatura(), ga.getNombreProfesor(), ga.getNombreGrupo()});
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

            int filaInicio = horario.getHoraInicio().toLocalTime().getHour() - 7;
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

    private void cargarHorariosDeAsignatura(int idClase) {
        HorarioAsignaturaController horarioController = new HorarioAsignaturaController();

        // Obtener ID de la asignatura
        int idAsignatura = idClase;

        if (idAsignatura == -1) {
            JOptionPane.showMessageDialog(this, "Error: no se pudo encontrar el ID de la asignatura seleccionada.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Obtener horarios
        List<HorarioAsignatura> horarios = horarioController.obtenerHorariosPorIdAsignatura(idAsignatura);

        // Crear modelo de tabla
        DefaultTableModel model = new DefaultTableModel(new String[]{"Id", "Día", "Hora Inicio", "Hora Fin"}, 0);
        for (HorarioAsignatura horario : horarios) {
            model.addRow(new Object[]{
                horario.getId(),
                horario.getDia(),
                horario.getHoraInicio().toString(),
                horario.getHoraFin().toString()
            });
        }

        tablaHorarios.setModel(model); // Asignar el modelo a la tabla
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
        EditarGrado = new javax.swing.JButton();
        EditarClase = new javax.swing.JButton();
        AgregarClase = new javax.swing.JButton();
        jComboBoxGrupos = new javax.swing.JComboBox<>();
        jLabel36 = new javax.swing.JLabel();
        VerHorarios = new javax.swing.JButton();
        AddHorario = new javax.swing.JButton();
        jPanel9 = new javax.swing.JPanel();
        jscrollpanelhorarios = new javax.swing.JScrollPane();
        tablaHorario = new javax.swing.JTable();
        jLabel33 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
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
        jPanel3 = new javax.swing.JPanel();
        jLabel39 = new javax.swing.JLabel();
        jLabel47 = new javax.swing.JLabel();
        jLabel45 = new javax.swing.JLabel();
        jComboBoxGrupos2 = new javax.swing.JComboBox<>();
        jLabel79 = new javax.swing.JLabel();
        txtNombreClase2 = new javax.swing.JTextField();
        EditNota3 = new javax.swing.JButton();
        jPanel10 = new javax.swing.JPanel();
        comboDia = new javax.swing.JComboBox<>();
        txtHoraInicio1 = new javax.swing.JFormattedTextField();
        txtHoraFin1 = new javax.swing.JFormattedTextField();
        jLabel80 = new javax.swing.JLabel();
        jLabel81 = new javax.swing.JLabel();
        jLabel82 = new javax.swing.JLabel();
        txtNombreClase3 = new javax.swing.JTextField();
        jLabel83 = new javax.swing.JLabel();
        btnAgregarHorario = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tablaHorarios = new javax.swing.JTable();
        jLabel40 = new javax.swing.JLabel();
        jLabel48 = new javax.swing.JLabel();
        btnModificarHorario = new javax.swing.JButton();
        btnEliminarHorario = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        tabProfes = new javax.swing.JTabbedPane();
        jPanel15 = new javax.swing.JPanel();
        jLabel35 = new javax.swing.JLabel();
        jScrollPane8 = new javax.swing.JScrollPane();
        tablaProfesores = new javax.swing.JTable();
        EliminarProfe = new javax.swing.JButton();
        ModificarProfesor = new javax.swing.JButton();
        AddNotaAlumno2 = new javax.swing.JButton();
        AddModifProfe = new javax.swing.JPanel();
        guardarNota2 = new javax.swing.JButton();
        jLabel65 = new javax.swing.JLabel();
        txtNombreProfesor = new javax.swing.JTextField();
        jLabel66 = new javax.swing.JLabel();
        txtApellidoProfesor = new javax.swing.JTextField();
        jLabel67 = new javax.swing.JLabel();
        txtEmailProfesor = new javax.swing.JTextField();
        txtFechaContratacionProfesor = new javax.swing.JFormattedTextField();
        jLabel68 = new javax.swing.JLabel();
        jLabel69 = new javax.swing.JLabel();
        txtEspecialidadProfesor = new javax.swing.JTextField();
        txtNombreUsuario = new javax.swing.JTextField();
        txtContrasenaP = new javax.swing.JTextField();
        jLabel70 = new javax.swing.JLabel();
        jLabel71 = new javax.swing.JLabel();
        jLabel72 = new javax.swing.JLabel();
        txtIdProfesor = new javax.swing.JTextField();
        modifyProf = new javax.swing.JButton();
        tabAlumnos1 = new javax.swing.JTabbedPane();
        jPanel11 = new javax.swing.JPanel();
        ModificarNota1 = new javax.swing.JButton();
        EliminarNota1 = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        TablaAlumnosTotales = new javax.swing.JTable();
        jPanel14 = new javax.swing.JPanel();
        jLabel32 = new javax.swing.JLabel();
        jScrollPane7 = new javax.swing.JScrollPane();
        tablaAlumnosCargaMasiva = new javax.swing.JTable();
        AddNotaAlumno1 = new javax.swing.JButton();
        AddNotaAlumno3 = new javax.swing.JButton();
        comboBoxGrupos = new javax.swing.JComboBox<>();
        jPanelModificarAlumno = new javax.swing.JPanel();
        EditNota1 = new javax.swing.JButton();
        jLabel57 = new javax.swing.JLabel();
        jLabel58 = new javax.swing.JLabel();
        txtNombreAlumno = new javax.swing.JTextField();
        jLabel59 = new javax.swing.JLabel();
        txtEmailAlumno = new javax.swing.JTextField();
        jLabel60 = new javax.swing.JLabel();
        jLabel61 = new javax.swing.JLabel();
        jLabel62 = new javax.swing.JLabel();
        txtApellidoAlumno = new javax.swing.JTextField();
        comboBoxGrados1 = new javax.swing.JComboBox<>();
        txtIdAlumno = new javax.swing.JTextField();
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
        tablaAsignaturas.setEditingColumn(0);
        tablaAsignaturas.setEditingRow(0);
        tablaAsignaturas.setFillsViewportHeight(true);
        tablaAsignaturas.setRowHeight(40);
        tablaAsignaturas.setShowGrid(true);
        tablaAsignaturas.setShowHorizontalLines(true);
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

        EditarGrado.setBackground(new java.awt.Color(204, 102, 0));
        EditarGrado.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        EditarGrado.setForeground(new java.awt.Color(255, 255, 255));
        EditarGrado.setText("Editar Grado");
        EditarGrado.setToolTipText("");
        EditarGrado.setBorder(null);
        EditarGrado.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        EditarGrado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EditarGradoActionPerformed(evt);
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
        jComboBoxGrupos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jComboBoxGruposMousePressed(evt);
            }
        });

        jLabel36.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jLabel36.setText("Elegir grupo:");

        VerHorarios.setBackground(new java.awt.Color(0, 0, 204));
        VerHorarios.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        VerHorarios.setForeground(new java.awt.Color(255, 255, 255));
        VerHorarios.setText("Ver Horario de Grado");
        VerHorarios.setToolTipText("");
        VerHorarios.setBorder(null);
        VerHorarios.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        VerHorarios.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                VerHorariosActionPerformed(evt);
            }
        });

        AddHorario.setBackground(new java.awt.Color(0, 153, 0));
        AddHorario.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        AddHorario.setForeground(new java.awt.Color(255, 255, 255));
        AddHorario.setText("Horario de Clase");
        AddHorario.setToolTipText("");
        AddHorario.setBorder(null);
        AddHorario.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        AddHorario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AddHorarioActionPerformed(evt);
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
                        .addGap(42, 42, 42)
                        .addComponent(jLabel36, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBoxGrupos, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(VerHorarios, javax.swing.GroupLayout.DEFAULT_SIZE, 162, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(AgregarClase, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(tablaClases)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(AddHorario, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(EditarGrado, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(EditarClase, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(EliminarClase, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
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
                    .addComponent(AgregarClase, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(VerHorarios, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tablaClases, javax.swing.GroupLayout.PREFERRED_SIZE, 481, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(EliminarClase, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(EditarGrado, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(EditarClase, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(AddHorario, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(164, Short.MAX_VALUE))
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
        tablaHorario.setEditingColumn(0);
        tablaHorario.setEditingRow(0);
        tablaHorario.setRowHeight(40);
        tablaHorario.setRowMargin(2);
        tablaHorario.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tablaHorario.setShowGrid(true);
        jscrollpanelhorarios.setViewportView(tablaHorario);

        jLabel33.setIcon(new javax.swing.ImageIcon(getClass().getResource("/logoPeque.png"))); // NOI18N

        jLabel34.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        jLabel34.setForeground(new java.awt.Color(0, 0, 153));
        jLabel34.setText("Horario");

        jLabel1.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel1.setText("jLabel1");

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(9, 9, 9)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jscrollpanelhorarios, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 657, Short.MAX_VALUE)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel33)
                            .addGroup(jPanel9Layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(jLabel34)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(jLabel33)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 23, Short.MAX_VALUE))
                .addGap(31, 31, 31)
                .addComponent(jscrollpanelhorarios, javax.swing.GroupLayout.PREFERRED_SIZE, 409, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(167, 167, 167))
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
                .addContainerGap(230, Short.MAX_VALUE)
                .addGroup(agregarClaseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(agregarClaseLayout.createSequentialGroup()
                        .addComponent(jLabel77, javax.swing.GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE)
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
                .addContainerGap(289, Short.MAX_VALUE))
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
        jLabel46.setText("Editar Profesor");

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
                    .addComponent(jLabel38)
                    .addComponent(EditNota, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel78, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtNombreClase1, javax.swing.GroupLayout.DEFAULT_SIZE, 216, Short.MAX_VALUE)
                    .addComponent(jLabel44, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel46, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        jLabel39.setIcon(new javax.swing.ImageIcon(getClass().getResource("/logoPeque.png"))); // NOI18N

        jLabel47.setFont(new java.awt.Font("Poppins", 1, 18)); // NOI18N
        jLabel47.setForeground(new java.awt.Color(0, 0, 204));
        jLabel47.setText("Editar Grado");

        jLabel45.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jLabel45.setText("Grado");

        jComboBoxGrupos2.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jComboBoxGrupos2.setModel(comboBoxProfesor.getModel());

        jLabel79.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jLabel79.setText("Nombre de Clase");

        txtNombreClase2.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N

        EditNota3.setBackground(new java.awt.Color(0, 0, 204));
        EditNota3.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        EditNota3.setForeground(new java.awt.Color(255, 255, 255));
        EditNota3.setText("Modificar");
        EditNota3.setBorder(null);
        EditNota3.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        EditNota3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EditNota3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(229, 229, 229)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jComboBoxGrupos2, 0, 216, Short.MAX_VALUE)
                    .addComponent(jLabel39)
                    .addComponent(EditNota3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel79, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtNombreClase2, javax.swing.GroupLayout.DEFAULT_SIZE, 216, Short.MAX_VALUE)
                    .addComponent(jLabel45, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel47, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(137, 137, 137)
                .addComponent(jLabel39)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel47, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel45)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBoxGrupos2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel79, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtNombreClase2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(EditNota3, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(288, 288, 288))
        );

        tabAlumnos.addTab("Edtar Grado", jPanel3);

        jPanel10.setBackground(new java.awt.Color(255, 255, 255));

        comboDia.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        comboDia.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Lunes", "Martes", "Miercoles", "Jueves", "Viernes" }));
        comboDia.setToolTipText("");
        comboDia.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        txtHoraInicio1.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.DateFormatter(java.text.DateFormat.getTimeInstance(java.text.DateFormat.SHORT))));

        txtHoraFin1.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.DateFormatter(java.text.DateFormat.getTimeInstance(java.text.DateFormat.SHORT))));

        jLabel80.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jLabel80.setText("Hora Fin");

        jLabel81.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jLabel81.setText("Hora Inicio");

        jLabel82.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jLabel82.setText("Dia");

        txtNombreClase3.setEditable(false);
        txtNombreClase3.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N

        jLabel83.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jLabel83.setText("Nombre de Clase");

        btnAgregarHorario.setBackground(new java.awt.Color(0, 153, 0));
        btnAgregarHorario.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        btnAgregarHorario.setForeground(new java.awt.Color(255, 255, 255));
        btnAgregarHorario.setText("Agregar Horario");
        btnAgregarHorario.setBorder(null);
        btnAgregarHorario.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAgregarHorario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregarHorarioActionPerformed(evt);
            }
        });

        tablaHorarios.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane2.setViewportView(tablaHorarios);

        jLabel40.setIcon(new javax.swing.ImageIcon(getClass().getResource("/logoPeque.png"))); // NOI18N

        jLabel48.setFont(new java.awt.Font("Poppins", 1, 18)); // NOI18N
        jLabel48.setForeground(new java.awt.Color(0, 0, 204));
        jLabel48.setText("Agregar Horario");

        btnModificarHorario.setBackground(new java.awt.Color(204, 102, 0));
        btnModificarHorario.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        btnModificarHorario.setForeground(new java.awt.Color(255, 255, 255));
        btnModificarHorario.setText("Modificar Horario");
        btnModificarHorario.setBorder(null);
        btnModificarHorario.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnModificarHorario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnModificarHorarioActionPerformed(evt);
            }
        });

        btnEliminarHorario.setBackground(new java.awt.Color(153, 0, 0));
        btnEliminarHorario.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        btnEliminarHorario.setForeground(new java.awt.Color(255, 255, 255));
        btnEliminarHorario.setText("Eliminar Horario");
        btnEliminarHorario.setBorder(null);
        btnEliminarHorario.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnEliminarHorario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarHorarioActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 294, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(62, 62, 62)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnAgregarHorario, javax.swing.GroupLayout.DEFAULT_SIZE, 216, Short.MAX_VALUE)
                    .addComponent(txtNombreClase3, javax.swing.GroupLayout.DEFAULT_SIZE, 216, Short.MAX_VALUE)
                    .addComponent(comboDia, 0, 216, Short.MAX_VALUE)
                    .addComponent(jLabel82, javax.swing.GroupLayout.DEFAULT_SIZE, 216, Short.MAX_VALUE)
                    .addComponent(jLabel83, javax.swing.GroupLayout.DEFAULT_SIZE, 216, Short.MAX_VALUE)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel81, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtHoraInicio1, javax.swing.GroupLayout.DEFAULT_SIZE, 105, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel80, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtHoraFin1, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jLabel40)
                    .addComponent(jLabel48, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnEliminarHorario, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 216, Short.MAX_VALUE)
                    .addComponent(btnModificarHorario, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(70, Short.MAX_VALUE))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGap(73, 73, 73)
                        .addComponent(jLabel40)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel48, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel83, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtNombreClase3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(jLabel82)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(comboDia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel80)
                            .addComponent(jLabel81))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtHoraInicio1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtHoraFin1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(btnAgregarHorario, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnModificarHorario, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnEliminarHorario, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGap(46, 46, 46)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 483, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(200, Short.MAX_VALUE))
        );

        tabAlumnos.addTab("Agregar Horario", jPanel10);

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

        tabProfes.setBackground(new java.awt.Color(255, 255, 255));
        tabProfes.setForeground(new java.awt.Color(0, 0, 153));
        tabProfes.setTabPlacement(javax.swing.JTabbedPane.LEFT);
        tabProfes.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N

        jPanel15.setBackground(new java.awt.Color(255, 255, 255));

        jLabel35.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jLabel35.setText("Profesores");

        tablaProfesores.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        tablaProfesores.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane8.setViewportView(tablaProfesores);

        EliminarProfe.setBackground(new java.awt.Color(204, 0, 0));
        EliminarProfe.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        EliminarProfe.setForeground(new java.awt.Color(255, 255, 255));
        EliminarProfe.setText("Eliminar Profesor");
        EliminarProfe.setToolTipText("");
        EliminarProfe.setBorder(null);
        EliminarProfe.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        EliminarProfe.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EliminarProfeActionPerformed(evt);
            }
        });

        ModificarProfesor.setBackground(new java.awt.Color(0, 0, 204));
        ModificarProfesor.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        ModificarProfesor.setForeground(new java.awt.Color(255, 255, 255));
        ModificarProfesor.setText("Modificar Profesor");
        ModificarProfesor.setToolTipText("");
        ModificarProfesor.setBorder(null);
        ModificarProfesor.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        ModificarProfesor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ModificarProfesorActionPerformed(evt);
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
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel15Layout.createSequentialGroup()
                        .addComponent(AddNotaAlumno2, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ModificarProfesor, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(EliminarProfe, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 607, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(25, Short.MAX_VALUE))
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel35)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 486, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(AddNotaAlumno2, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ModificarProfesor, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(EliminarProfe, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(90, Short.MAX_VALUE))
        );

        tabProfes.addTab("Todos los Profesores", jPanel15);

        AddModifProfe.setBackground(new java.awt.Color(255, 255, 255));

        guardarNota2.setBackground(new java.awt.Color(0, 0, 204));
        guardarNota2.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        guardarNota2.setForeground(new java.awt.Color(255, 255, 255));
        guardarNota2.setText("Guardar Profesor");
        guardarNota2.setBorder(null);
        guardarNota2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        guardarNota2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                guardarNota2ActionPerformed(evt);
            }
        });

        jLabel65.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jLabel65.setText("Nombre");

        txtNombreProfesor.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N

        jLabel66.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jLabel66.setText("Apellido");

        txtApellidoProfesor.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N

        jLabel67.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jLabel67.setText("Fecha de Contratación");

        txtEmailProfesor.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N

        txtFechaContratacionProfesor.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.DateFormatter(java.text.DateFormat.getDateInstance(java.text.DateFormat.SHORT))));
        txtFechaContratacionProfesor.setToolTipText("Dia/Mes/Año");

        jLabel68.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jLabel68.setText("Email");

        jLabel69.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jLabel69.setText("Especialidad ");

        txtEspecialidadProfesor.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N

        txtNombreUsuario.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N

        txtContrasenaP.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N

        jLabel70.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jLabel70.setText("Usuario");

        jLabel71.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jLabel71.setText("Contraseña");

        jLabel72.setBackground(new java.awt.Color(255, 255, 255));
        jLabel72.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jLabel72.setText("Id");

        txtIdProfesor.setEditable(false);
        txtIdProfesor.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        txtIdProfesor.setCaretColor(new java.awt.Color(255, 255, 255));
        txtIdProfesor.setDisabledTextColor(new java.awt.Color(204, 204, 204));
        txtIdProfesor.setFocusable(false);

        modifyProf.setBackground(new java.awt.Color(0, 0, 204));
        modifyProf.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        modifyProf.setForeground(new java.awt.Color(255, 255, 255));
        modifyProf.setText("Modificar Profesor");
        modifyProf.setBorder(null);
        modifyProf.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        modifyProf.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                modifyProfActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout AddModifProfeLayout = new javax.swing.GroupLayout(AddModifProfe);
        AddModifProfe.setLayout(AddModifProfeLayout);
        AddModifProfeLayout.setHorizontalGroup(
            AddModifProfeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, AddModifProfeLayout.createSequentialGroup()
                .addContainerGap(198, Short.MAX_VALUE)
                .addGroup(AddModifProfeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel67, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtEmailProfesor, javax.swing.GroupLayout.DEFAULT_SIZE, 310, Short.MAX_VALUE)
                    .addComponent(jLabel66, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtApellidoProfesor, javax.swing.GroupLayout.DEFAULT_SIZE, 310, Short.MAX_VALUE)
                    .addComponent(jLabel65, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtNombreProfesor, javax.swing.GroupLayout.DEFAULT_SIZE, 310, Short.MAX_VALUE)
                    .addComponent(guardarNota2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtFechaContratacionProfesor)
                    .addComponent(jLabel68, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel69, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtEspecialidadProfesor, javax.swing.GroupLayout.DEFAULT_SIZE, 310, Short.MAX_VALUE)
                    .addComponent(txtNombreUsuario, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 310, Short.MAX_VALUE)
                    .addComponent(txtContrasenaP, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 310, Short.MAX_VALUE)
                    .addComponent(jLabel70, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel71, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(AddModifProfeLayout.createSequentialGroup()
                        .addComponent(jLabel72, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtIdProfesor))
                    .addComponent(modifyProf, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(136, 136, 136))
        );
        AddModifProfeLayout.setVerticalGroup(
            AddModifProfeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(AddModifProfeLayout.createSequentialGroup()
                .addGap(46, 46, 46)
                .addGroup(AddModifProfeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel72)
                    .addComponent(txtIdProfesor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel65)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtNombreProfesor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel66)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtApellidoProfesor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel68)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtEmailProfesor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel69)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtEspecialidadProfesor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel67)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtFechaContratacionProfesor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel70)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtNombreUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel71)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtContrasenaP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(16, 16, 16)
                .addComponent(modifyProf, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(guardarNota2, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(105, Short.MAX_VALUE))
        );

        tabProfes.addTab("Editar o Agregar", AddModifProfe);

        jPanel2.add(tabProfes, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 0, 800, -1));

        jTabbedPane4.addTab("Profesores", jPanel2);

        tabAlumnos1.setBackground(new java.awt.Color(255, 255, 255));
        tabAlumnos1.setForeground(new java.awt.Color(0, 0, 153));
        tabAlumnos1.setTabPlacement(javax.swing.JTabbedPane.LEFT);
        tabAlumnos1.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N

        jPanel11.setBackground(new java.awt.Color(255, 255, 255));

        ModificarNota1.setBackground(new java.awt.Color(0, 0, 204));
        ModificarNota1.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        ModificarNota1.setForeground(new java.awt.Color(255, 255, 255));
        ModificarNota1.setText("Modificar Alumno");
        ModificarNota1.setToolTipText("");
        ModificarNota1.setBorder(null);
        ModificarNota1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        ModificarNota1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ModificarNota1ActionPerformed(evt);
            }
        });

        EliminarNota1.setBackground(new java.awt.Color(204, 0, 0));
        EliminarNota1.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        EliminarNota1.setForeground(new java.awt.Color(255, 255, 255));
        EliminarNota1.setText("Eliminar Alumno");
        EliminarNota1.setToolTipText("");
        EliminarNota1.setBorder(null);
        EliminarNota1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        EliminarNota1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EliminarNota1ActionPerformed(evt);
            }
        });

        jScrollPane3.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N

        TablaAlumnosTotales.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        TablaAlumnosTotales.setModel(new javax.swing.table.DefaultTableModel(
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
        TablaAlumnosTotales.setRowHeight(30);
        jScrollPane3.setViewportView(TablaAlumnosTotales);

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 650, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel11Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(ModificarNota1, javax.swing.GroupLayout.PREFERRED_SIZE, 248, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(EliminarNota1, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel11Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 488, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ModificarNota1, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(EliminarNota1, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15))
        );

        tabAlumnos1.addTab("Alumnos", jPanel11);

        jPanel14.setBackground(new java.awt.Color(255, 255, 255));

        jLabel32.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jLabel32.setText("Agregar Alumnos");

        tablaAlumnosCargaMasiva.setAutoCreateRowSorter(true);
        tablaAlumnosCargaMasiva.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jScrollPane7.setViewportView(tablaAlumnosCargaMasiva);
        tablaAlumnosCargaMasiva.setColumnModel(tablaAlumnosCargaMasiva.getColumnModel());

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

        AddNotaAlumno3.setBackground(new java.awt.Color(153, 0, 0));
        AddNotaAlumno3.setFont(new java.awt.Font("Poppins", 1, 18)); // NOI18N
        AddNotaAlumno3.setForeground(new java.awt.Color(255, 255, 255));
        AddNotaAlumno3.setText("Guardar Alumnos");
        AddNotaAlumno3.setToolTipText("");
        AddNotaAlumno3.setBorder(null);
        AddNotaAlumno3.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        AddNotaAlumno3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AddNotaAlumno3ActionPerformed(evt);
            }
        });

        comboBoxGrupos.setFont(new java.awt.Font("Poppins Medium", 0, 12)); // NOI18N
        comboBoxGrupos.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(AddNotaAlumno3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 644, Short.MAX_VALUE)
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addComponent(jLabel32)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(comboBoxGrupos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(AddNotaAlumno1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel32)
                    .addComponent(comboBoxGrupos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(AddNotaAlumno1, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(AddNotaAlumno3, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(31, Short.MAX_VALUE))
        );

        tabAlumnos1.addTab("Agregar Alumnos", jPanel14);

        jPanelModificarAlumno.setBackground(new java.awt.Color(255, 255, 255));

        EditNota1.setBackground(new java.awt.Color(0, 0, 204));
        EditNota1.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        EditNota1.setForeground(new java.awt.Color(255, 255, 255));
        EditNota1.setText("Modificar");
        EditNota1.setBorder(null);
        EditNota1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        EditNota1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EditNota1ActionPerformed(evt);
            }
        });

        jLabel57.setFont(new java.awt.Font("Poppins", 1, 18)); // NOI18N
        jLabel57.setForeground(new java.awt.Color(0, 0, 204));
        jLabel57.setText("Editar Alumno");

        jLabel58.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jLabel58.setText("Nombre");

        txtNombreAlumno.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N

        jLabel59.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jLabel59.setText("Correo");

        txtEmailAlumno.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N

        jLabel60.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jLabel60.setText("Grupo");

        jLabel61.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jLabel61.setText("Id");

        jLabel62.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jLabel62.setText("Apellidos");

        txtApellidoAlumno.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N

        comboBoxGrados1.setFont(new java.awt.Font("Poppins Medium", 0, 12)); // NOI18N
        comboBoxGrados1.setModel(comboBoxGrupos.getModel());

        txtIdAlumno.setEditable(false);
        txtIdAlumno.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        txtIdAlumno.setText("jTextField1");

        javax.swing.GroupLayout jPanelModificarAlumnoLayout = new javax.swing.GroupLayout(jPanelModificarAlumno);
        jPanelModificarAlumno.setLayout(jPanelModificarAlumnoLayout);
        jPanelModificarAlumnoLayout.setHorizontalGroup(
            jPanelModificarAlumnoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelModificarAlumnoLayout.createSequentialGroup()
                .addGap(94, 94, 94)
                .addGroup(jPanelModificarAlumnoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelModificarAlumnoLayout.createSequentialGroup()
                        .addComponent(comboBoxGrados1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanelModificarAlumnoLayout.createSequentialGroup()
                        .addComponent(jLabel59, javax.swing.GroupLayout.PREFERRED_SIZE, 216, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelModificarAlumnoLayout.createSequentialGroup()
                        .addGroup(jPanelModificarAlumnoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanelModificarAlumnoLayout.createSequentialGroup()
                                .addComponent(jLabel57, javax.swing.GroupLayout.PREFERRED_SIZE, 303, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel61, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtIdAlumno, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(1, 1, 1))
                            .addGroup(jPanelModificarAlumnoLayout.createSequentialGroup()
                                .addComponent(jLabel60, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(txtEmailAlumno, javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanelModificarAlumnoLayout.createSequentialGroup()
                                .addGroup(jPanelModificarAlumnoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtNombreAlumno, javax.swing.GroupLayout.PREFERRED_SIZE, 216, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel58, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanelModificarAlumnoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel62, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(txtApellidoAlumno, javax.swing.GroupLayout.DEFAULT_SIZE, 233, Short.MAX_VALUE)))
                            .addComponent(EditNota1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(113, 113, 113))))
        );
        jPanelModificarAlumnoLayout.setVerticalGroup(
            jPanelModificarAlumnoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelModificarAlumnoLayout.createSequentialGroup()
                .addGap(166, 166, 166)
                .addGroup(jPanelModificarAlumnoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel57, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel61)
                    .addComponent(txtIdAlumno, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(27, 27, 27)
                .addGroup(jPanelModificarAlumnoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel58)
                    .addComponent(jLabel62))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelModificarAlumnoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtNombreAlumno, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtApellidoAlumno, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel59)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtEmailAlumno, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel60)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(comboBoxGrados1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(EditNota1, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(216, 216, 216))
        );

        tabAlumnos1.addTab("Modificar Alumno", jPanelModificarAlumno);

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
            cargarComboBoxGrupos();
            jComboBoxGrupos.repaint(); // Refresca visualmente el ComboBox
            jComboBoxGrupos2.repaint();
            cargarGrados();
            jTabbedPane3.setSelectedComponent(PanelGrados);
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
            cargarComboBoxGrupos();
            jComboBoxGrupos.repaint(); // Refresca visualmente el ComboBox
            jComboBoxGrupos2.repaint();
            txtAddGrupo.setText(""); // Limpiar los campos
            txtAddGrupo1.setText("");
            cargarGrados();
            jTabbedPane3.setSelectedComponent(PanelGrados);

        } else {
            JOptionPane.showMessageDialog(this, "Error al agregar el grupo.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_addGrupoActionPerformed

    private void DescargarEvActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DescargarEvActionPerformed

    }//GEN-LAST:event_DescargarEvActionPerformed

    private void AddGradoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddGradoActionPerformed

        jTabbedPane3.setSelectedComponent(agregarGrupo);


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
                cargarComboBoxGrupos();
                jComboBoxGrupos.repaint(); // Refresca visualmente el ComboBox
                jComboBoxGrupos2.repaint();
                txtAddGrupo.setText(""); // Limpiar los campos
                txtAddGrupo1.setText("");
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

        tabAlumnos.setSelectedComponent(agregarClase);

    }//GEN-LAST:event_AgregarClaseActionPerformed

    private void EditarClaseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EditarClaseActionPerformed

        // Obtener la fila seleccionada de la tabla de asignaturas
        int selectedRow = tablaAsignaturas.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, selecciona una asignatura para editar.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Obtener valores de la fila seleccionada
        String nombreClase = tablaAsignaturas.getValueAt(selectedRow, 1).toString(); // Columna de nombre de la asignatura
        String profesorActual = tablaAsignaturas.getValueAt(selectedRow, 2).toString(); // Columna de profesor actual

        // Cargar los valores en los campos de edición
        txtNombreClase1.setText(nombreClase); // Nombre de la asignatura
        comboBoxProfesor1.setSelectedItem(profesorActual); // Seleccionar el profesor actual en el ComboBox
        tabAlumnos.setSelectedComponent(editarProfesor);
    }//GEN-LAST:event_EditarClaseActionPerformed

    private void EditarGradoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EditarGradoActionPerformed

        // Obtener la fila seleccionada de la tabla de asignaturas
        int selectedRow = tablaAsignaturas.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, selecciona una asignatura para editar.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Obtener valores de la fila seleccionada
        String nombreClase = tablaAsignaturas.getValueAt(selectedRow, 1).toString();
        String grupoSeleccionado = tablaAsignaturas.getValueAt(selectedRow, 3).toString();
        // Obtener el ID del grupo seleccionado
        GrupoController grupoController = new GrupoController();
        int idGrupo = grupoController.obtenerIdGrupoPorNombre(grupoSeleccionado);

        if (idGrupo > 0) {
            // Cargar el horario del grupo seleccionado
            jComboBoxGrupos2.setSelectedItem(grupoSeleccionado);
            txtNombreClase2.setText(nombreClase);
            tabAlumnos.setSelectedComponent(jPanel3);
        } else {
            JOptionPane.showMessageDialog(this, "No se encontró el grupo seleccionado.", "Error", JOptionPane.ERROR_MESSAGE);
        }

    }//GEN-LAST:event_EditarGradoActionPerformed

    private void EliminarClaseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EliminarClaseActionPerformed
        int selectedRow = tablaAsignaturas.getSelectedRow(); // Obtener la fila seleccionada en la tabla
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, selecciona una clase para eliminar.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "¿Estás seguro de que deseas eliminar esta clase?", "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        // Obtener el nombre o ID de la clase seleccionada
        int idAsignatura = (int) tablaAsignaturas.getValueAt(selectedRow, 0);

        if (idAsignatura == -1) {
            JOptionPane.showMessageDialog(this, "Error: no se pudo encontrar el ID de la asignatura seleccionada.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Eliminar los horarios asociados
        HorarioAsignaturaController horarioController = new HorarioAsignaturaController();
        boolean horariosEliminados = horarioController.eliminarHorariosPorIdAsignatura(idAsignatura);

        if (!horariosEliminados) {
            JOptionPane.showMessageDialog(this, "Error al eliminar los horarios de la clase.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Eliminar la clase (asignatura)
        AsignaturaController asignaturaController = new AsignaturaController();
        boolean claseEliminada = asignaturaController.eliminarAsignatura(idAsignatura);

        if (claseEliminada) {
            JOptionPane.showMessageDialog(this, "Clase eliminada correctamente.");
            cargarAsignaturas(); // Recargar las asignaturas en la tabla
        } else {
            JOptionPane.showMessageDialog(this, "Error al eliminar la clase.", "Error", JOptionPane.ERROR_MESSAGE);
        }
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

    private void EliminarProfeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EliminarProfeActionPerformed
        // Obtener la fila seleccionada en la tabla de profesores
        int selectedRow = tablaProfesores.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, selecciona un profesor para eliminar.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Confirmación de eliminación
        int confirm = JOptionPane.showConfirmDialog(this, "¿Estás seguro de que deseas eliminar este profesor?",
                "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            // Obtener ID del profesor desde la tabla
            int idProfesor = Integer.parseInt(tablaProfesores.getValueAt(selectedRow, 0).toString());

            // Controladores
            DocenteController docenteController = new DocenteController();
            UsuarioController usuarioController = new UsuarioController();

            // Intentar eliminar al usuario asociado
            boolean usuarioEliminado = usuarioController.eliminarUsuarioPorIdDocente(idProfesor);

            if (!usuarioEliminado) {
                JOptionPane.showMessageDialog(this, "Error al eliminar el usuario asociado.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Intentar eliminar al profesor
            boolean docenteEliminado = docenteController.eliminarDocente(idProfesor);

            if (docenteEliminado) {
                JOptionPane.showMessageDialog(this, "Profesor eliminado correctamente.");
                cargarProfesores(); // Recargar la tabla
            } else {
                JOptionPane.showMessageDialog(this, "Error al eliminar el profesor.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_EliminarProfeActionPerformed

    private void ModificarProfesorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ModificarProfesorActionPerformed
        // Obtener la fila seleccionada
        int selectedRow = tablaProfesores.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, selecciona un profesor para modificar.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Obtener datos del profesor seleccionado
        int idProfesor = Integer.parseInt(tablaProfesores.getValueAt(selectedRow, 0).toString());
        String nombre = tablaProfesores.getValueAt(selectedRow, 1).toString();
        String apellido = tablaProfesores.getValueAt(selectedRow, 2).toString();
        String email = tablaProfesores.getValueAt(selectedRow, 3).toString();
        String especialidad = tablaProfesores.getValueAt(selectedRow, 4).toString();
        String fechaContratacion = tablaProfesores.getValueAt(selectedRow, 5).toString();

        // Cargar datos en los campos de texto
        txtIdProfesor.setText(String.valueOf(idProfesor));
        txtNombreProfesor.setText(nombre);
        txtApellidoProfesor.setText(apellido);
        txtEmailProfesor.setText(email);
        txtEspecialidadProfesor.setText(especialidad);
        txtFechaContratacionProfesor.setText(fechaContratacion);

        // Cambiar a la pestaña de edición
        tabProfes.setSelectedComponent(AddModifProfe);
    }//GEN-LAST:event_ModificarProfesorActionPerformed

    private void AddNotaAlumno2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddNotaAlumno2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_AddNotaAlumno2ActionPerformed

    private void guardarNota2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_guardarNota2ActionPerformed
        // Obtener datos del formulario
        String nombre = txtNombreProfesor.getText().trim();
        String apellido = txtApellidoProfesor.getText().trim();
        String email = txtEmailProfesor.getText().trim();
        String especialidad = txtEspecialidadProfesor.getText().trim();
        String fechaContratacionStr = txtFechaContratacionProfesor.getText().trim();
        String nombreUsuario = txtNombreUsuario.getText().trim();
        String contrasena = txtContrasenaP.getText().trim();

        // Validar datos
        if (nombre.isEmpty() || apellido.isEmpty() || email.isEmpty() || especialidad.isEmpty() || fechaContratacionStr.isEmpty() || nombreUsuario.isEmpty() || contrasena.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Convertir fecha
            java.util.Date fechaContratacion = new SimpleDateFormat("d/MM/yy").parse(fechaContratacionStr);

            // Crear objeto Docente
            Docente nuevoDocente = new Docente(0, nombre, apellido, email, especialidad, fechaContratacion, new Date());

            // Llamar al controlador para agregar docente y usuario
            UsuarioController usuarioController = new UsuarioController();
            boolean agregado = usuarioController.agregarDocente(nuevoDocente, nombreUsuario, contrasena);

            if (agregado) {
                JOptionPane.showMessageDialog(this, "Profesor y usuario agregados correctamente.");
                cargarProfesoresEnTabla(); // Actualizar la tabla de profesores

            } else {
                JOptionPane.showMessageDialog(this, "Error al agregar el profesor y su usuario.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, "La fecha debe estar en formato 'd/MM/yy'.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_guardarNota2ActionPerformed

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
        // Obtener la asignatura seleccionada para modificar
        int selectedRow = tablaAsignaturas.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, selecciona una asignatura para editar.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int idAsignatura = (int) tablaAsignaturas.getValueAt(selectedRow, 0);

        if (idAsignatura == -1) {
            JOptionPane.showMessageDialog(this, "No se pudo encontrar la asignatura seleccionada.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Crear el objeto Asignatura con los nuevos datos
        Asignatura asignatura = new Asignatura(idAsignatura, nombreClase, idProfesor);

        // Actualizar la asignatura
        AsignaturaController asignaturaController = new AsignaturaController();
        boolean actualizado = asignaturaController.actualizarAsignatura(idProfesor, asignatura);

        if (actualizado) {
            JOptionPane.showMessageDialog(this, "Clase actualizada correctamente.");
            cargarAsignaturas(); // Recargar la tabla de asignaturas
        } else {
            JOptionPane.showMessageDialog(this, "Error al actualizar la clase.", "Error", JOptionPane.ERROR_MESSAGE);
        }

    }//GEN-LAST:event_EditNotaActionPerformed

    private void VerHorariosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_VerHorariosActionPerformed
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
            jLabel1.setText(grupoSeleccionado);
            // Cambiar a la pestaña del horario
            tabAlumnos.setSelectedComponent(jPanel9);
        } else {
            JOptionPane.showMessageDialog(this, "No se encontró el grupo seleccionado.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_VerHorariosActionPerformed

    private void EditNota3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EditNota3ActionPerformed
        String nombreGrado = (String) jComboBoxGrupos2.getSelectedItem();
        String nombreClase = txtNombreClase2.getText().trim();
        if (nombreGrado == null || nombreGrado.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, selecciona un grado válido.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (nombreClase.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre de la clase no puede estar vacío.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Obtener la asignatura seleccionada para modificar
        int selectedRow = tablaAsignaturas.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, selecciona una asignatura para editar.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int idAsignatura = (int) tablaAsignaturas.getValueAt(selectedRow, 0);
        String nombreClaseAntiguo = tablaAsignaturas.getValueAt(selectedRow, 2).toString();
        GrupoController grupo = new GrupoController();
        int idGrupoAntiguo = grupo.obtenerIdGrupoPorNombre(nombreClaseAntiguo);
        int idGrupo = grupo.obtenerIdGrupoPorNombre(nombreGrado);

        DocenteController docenteController = new DocenteController();
        String nombreProfesor = (String) tablaAsignaturas.getValueAt(selectedRow, 1);
        int idProfesor = docenteController.obtenerIdProfesorPorNombre(nombreProfesor);

        // Crear el objeto Asignatura con los nuevos datos
        Asignatura asignatura = new Asignatura(idAsignatura, nombreClase, idProfesor);

        // Actualizar la asignatura
        AsignaturaController asignaturaController = new AsignaturaController();
        boolean actualizado = asignaturaController.actualizarAsignatura(idProfesor, asignatura);
        GrupoAsignaturaController grupoAsignaturaController = new GrupoAsignaturaController();
        boolean gradoCambia = grupoAsignaturaController.actualizarGrupoAsignaturaPorGrupo(idGrupoAntiguo, idAsignatura, idGrupo);

        if (gradoCambia && actualizado) {
            JOptionPane.showMessageDialog(this, "Clase actualizada correctamente.");
            cargarAsignaturas(); // Recargar la tabla de asignaturas
        } else {
            JOptionPane.showMessageDialog(this, "Error al actualizar la clase.", "Error", JOptionPane.ERROR_MESSAGE);
        }

    }//GEN-LAST:event_EditNota3ActionPerformed

    private void btnAgregarHorarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarHorarioActionPerformed
        int selectedRow = tablaAsignaturas.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, selecciona una asignatura para agregar el horario.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validar campos
        String dia = comboDia.getSelectedItem().toString();
        String horaInicio = txtHoraInicio1.getText().trim();
        String horaFin = txtHoraFin1.getText().trim();

        if (dia.isEmpty() || horaInicio.isEmpty() || horaFin.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, completa todos los campos.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Obtener ID de la asignatura seleccionada
            int idAsignatura = (int) tablaAsignaturas.getValueAt(selectedRow, 0);
            String nombreClase = (String) tablaAsignaturas.getValueAt(selectedRow, 1);
            if (idAsignatura == -1) {
                JOptionPane.showMessageDialog(this, "Error: no se pudo encontrar la asignatura.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Crear el objeto HorarioAsignatura
            HorarioAsignatura horario = new HorarioAsignatura(
                    0, // ID autogenerado
                    idAsignatura,
                    dia,
                    Time.valueOf(horaInicio + ":00"),
                    Time.valueOf(horaFin + ":00"),
                    nombreClase
            );

            // Guardar el horario en la base de datos
            HorarioAsignaturaController horarioController = new HorarioAsignaturaController();
            boolean agregado = horarioController.agregarHorario(horario);

            if (agregado) {
                JOptionPane.showMessageDialog(this, "Horario agregado correctamente.");
                cargarHorariosDeAsignatura(idAsignatura); // Recargar la tabla de horarios
            } else {
                JOptionPane.showMessageDialog(this, "Error al agregar el horario.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }    }//GEN-LAST:event_btnAgregarHorarioActionPerformed

    private void AddHorarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddHorarioActionPerformed
        // Obtener la fila seleccionada de la tabla de asignaturas
        int selectedRow = tablaAsignaturas.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, selecciona una asignatura para agregar el horario.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Obtener valores de la fila seleccionada
        int idAsignatura = (int) tablaAsignaturas.getValueAt(selectedRow, 0);
        String nombreClase = tablaAsignaturas.getValueAt(selectedRow, 1).toString();
        String grupoSeleccionado = tablaAsignaturas.getValueAt(selectedRow, 3).toString();

        // Obtener el ID del grupo seleccionado
        GrupoController grupoController = new GrupoController();
        int idGrupo = grupoController.obtenerIdGrupoPorNombre(grupoSeleccionado);

        // Validar el grupo y asignatura
        if (idGrupo > 0) {
            txtNombreClase3.setText(nombreClase); // Setear el nombre de la clase en el formulario
            cargarHorariosDeAsignatura(idAsignatura); // Mostrar los horarios existentes
            tabAlumnos.setSelectedComponent(jPanel10); // Cambiar a la vista de agregar horario
        } else {
            JOptionPane.showMessageDialog(this, "No se encontró el grupo seleccionado.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_AddHorarioActionPerformed

    private void btnModificarHorarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnModificarHorarioActionPerformed
        // Verificar que haya una fila seleccionada
        int selectedRow = tablaHorarios.getSelectedRow();
        int selectedRowAsignaturas = tablaAsignaturas.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, selecciona un horario para modificar.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int idAsignatura = (int) tablaAsignaturas.getValueAt(selectedRowAsignaturas, 0);
        // Obtener el ID del horario de la fila seleccionada
        int idHorario = Integer.parseInt(tablaHorarios.getValueAt(selectedRow, 0).toString());

        // Obtener los valores modificados de los campos
        String dia = comboDia.getSelectedItem().toString();
        String horaInicio = txtHoraInicio1.getText().trim();
        String horaFin = txtHoraFin1.getText().trim();
        String nombreClase = txtNombreClase3.getText();
        // Validar campos
        if (dia.isEmpty() || horaInicio.isEmpty() || horaFin.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, completa todos los campos.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Convertir horas a formato Time
            Time horaInicioTime = Time.valueOf(horaInicio + ":00");
            Time horaFinTime = Time.valueOf(horaFin + ":00");

            // Actualizar el horario en la base de datos
            HorarioAsignaturaController horarioController = new HorarioAsignaturaController();
            HorarioAsignatura horarioModificado = new HorarioAsignatura(idHorario, dia, horaInicioTime, horaFinTime);

            boolean actualizado = horarioController.actualizarHorario(horarioModificado);

            if (actualizado) {
                JOptionPane.showMessageDialog(this, "Horario actualizado correctamente.");
                cargarHorariosDeAsignatura(idAsignatura); // Recargar los horarios en la tabla
            } else {
                JOptionPane.showMessageDialog(this, "Error al actualizar el horario.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, "Por favor, introduce horas válidas en formato HH:mm.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnModificarHorarioActionPerformed

    private void btnEliminarHorarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarHorarioActionPerformed
        int selectedRow = tablaHorarios.getSelectedRow();
        String nombreClase = txtNombreClase3.getText();
        int selectedRowAsignaturas = tablaAsignaturas.getSelectedRow();
        int idAsignatura = (int) tablaAsignaturas.getValueAt(selectedRowAsignaturas, 0);
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, selecciona un horario para eliminar.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Obtener el ID del horario seleccionado
        int idHorario = Integer.parseInt(tablaHorarios.getValueAt(selectedRow, 0).toString());

        int confirm = JOptionPane.showConfirmDialog(this, "¿Estás seguro de que deseas eliminar este horario?", "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            HorarioAsignaturaController horarioController = new HorarioAsignaturaController();
            boolean eliminado = horarioController.eliminarHorario(idHorario);

            if (eliminado) {
                JOptionPane.showMessageDialog(this, "Horario eliminado correctamente.");
                cargarHorariosDeAsignatura(idAsignatura); // Método para recargar los horarios en la tabla
            } else {
                JOptionPane.showMessageDialog(this, "Error al eliminar el horario.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_btnEliminarHorarioActionPerformed

    private void jComboBoxGruposMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jComboBoxGruposMousePressed
        filtrarAsignaturasPorGrupo();
    }//GEN-LAST:event_jComboBoxGruposMousePressed

    private void EditNota1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EditNota1ActionPerformed

        // Obtener valores del formulario
        int idAlumno = Integer.parseInt(txtIdAlumno.getText());
        String nombre = txtNombreAlumno.getText().trim();
        String apellido = txtApellidoAlumno.getText().trim();
        String email = txtEmailAlumno.getText().trim();
        String nuevoGrupo = comboBoxGrados1.getSelectedItem().toString();

        if (nombre.isEmpty() || apellido.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        AlumnoController alumnoController = new AlumnoController();
        Alumno alumno = new Alumno(idAlumno, nombre, apellido, email, null, null);

        // Actualizar los datos del alumno
        boolean alumnoActualizado = alumnoController.actualizarAlumno(alumno);

        // Actualizar el grupo del alumno
        GrupoController grupoController = new GrupoController();
        int nuevoIdGrupo = grupoController.obtenerIdGrupoPorNombre(nuevoGrupo);
        AlumnoGrupoController alumnogrupoController = new AlumnoGrupoController();
        boolean grupoActualizado = alumnogrupoController.actualizarAlumnoGrupo(idAlumno, nuevoIdGrupo);

        if (alumnoActualizado && grupoActualizado) {
            JOptionPane.showMessageDialog(this, "Alumno modificado correctamente.");
            cargarAlumnosEnTabla(); // Recargar la tabla de alumnos
            tabAlumnos1.setSelectedComponent(jPanel11); // Volver a la vista principal
        } else {
            JOptionPane.showMessageDialog(this, "Error al modificar el alumno.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_EditNota1ActionPerformed

    private void AddNotaAlumno1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddNotaAlumno1ActionPerformed
        DefaultTableModel modelo = (DefaultTableModel) tablaAlumnosCargaMasiva.getModel();
        modelo.addRow(new Object[]{"", "", ""}); // Agrega una fila vacía
    }//GEN-LAST:event_AddNotaAlumno1ActionPerformed

    private void ModificarNota1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ModificarNota1ActionPerformed
        // Obtener la fila seleccionada en la tabla de alumnos
        int selectedRow = TablaAlumnosTotales.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, selecciona un alumno para modificar.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Obtener valores del alumno seleccionado
        int idAlumno = Integer.parseInt(TablaAlumnosTotales.getValueAt(selectedRow, 0).toString());
        String nombre = TablaAlumnosTotales.getValueAt(selectedRow, 1).toString();
        String apellido = TablaAlumnosTotales.getValueAt(selectedRow, 2).toString();
        String email = TablaAlumnosTotales.getValueAt(selectedRow, 3).toString();

        // Mostrar los datos en los campos de texto y comboBox
        txtIdAlumno.setText(String.valueOf(idAlumno));
        txtNombreAlumno.setText(nombre);
        txtApellidoAlumno.setText(apellido);
        txtEmailAlumno.setText(email);

        comboBoxGrados1.setSelectedItem(TablaAlumnosTotales.getValueAt(selectedRow, 4).toString());

        // Cambiar al panel de modificación
        tabAlumnos1.setSelectedComponent(jPanelModificarAlumno);
    }//GEN-LAST:event_ModificarNota1ActionPerformed

    private void EliminarNota1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EliminarNota1ActionPerformed
// Obtener la fila seleccionada en la tabla de alumnos
        int selectedRow = TablaAlumnosTotales.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, selecciona un alumno para eliminar.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Confirmar eliminación
        int confirm = JOptionPane.showConfirmDialog(this, "¿Estás seguro de que deseas eliminar este alumno?",
                "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            // Obtener ID del alumno
            int idAlumno = Integer.parseInt(TablaAlumnosTotales.getValueAt(selectedRow, 0).toString());

            // Eliminar el usuario asociado al alumno
            UsuarioController usuarioController = new UsuarioController();
            boolean usuarioEliminado = usuarioController.eliminarUsuarioPorIdAlumno(idAlumno);

            if (!usuarioEliminado) {
                JOptionPane.showMessageDialog(this, "Error al eliminar el usuario asociado al alumno.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Eliminar el alumno
            AlumnoController alumnoController = new AlumnoController();
            boolean alumnoEliminado = alumnoController.eliminarAlumno(idAlumno);

            if (alumnoEliminado) {
                JOptionPane.showMessageDialog(this, "Alumno eliminado correctamente.");
                cargarAlumnosEnTabla(); // Recargar la tabla
            } else {
                JOptionPane.showMessageDialog(this, "Error al eliminar el alumno.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_EliminarNota1ActionPerformed

    private void AddNotaAlumno3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddNotaAlumno3ActionPerformed
        DefaultTableModel modelo = (DefaultTableModel) tablaAlumnosCargaMasiva.getModel();
        String grupoSeleccionado = (String) comboBoxGrupos.getSelectedItem();

        if (grupoSeleccionado == null || grupoSeleccionado.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, selecciona un grupo.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        GrupoController grupoController = new GrupoController();
        int idGrupo = grupoController.obtenerIdGrupoPorNombre(grupoSeleccionado);

        if (idGrupo == -1) {
            JOptionPane.showMessageDialog(this, "El grupo seleccionado no existe.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        AlumnoController alumnoController = new AlumnoController();
        int filasAgregadas = 0;

        for (int i = 0; i < modelo.getRowCount(); i++) {
            String nombre = (String) modelo.getValueAt(i, 0);
            String apellido = (String) modelo.getValueAt(i, 1);
            String email = (String) modelo.getValueAt(i, 2);

            if (nombre == null || nombre.isEmpty() || apellido == null || apellido.isEmpty() || email == null || email.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor, completa todos los campos de la tabla.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Alumno nuevoAlumno = new Alumno(0, nombre, apellido, email, null, null);
            if (alumnoController.agregarAlumnoConGrupo(nuevoAlumno, idGrupo)) {
                filasAgregadas++;
            }
        }

        JOptionPane.showMessageDialog(this, filasAgregadas + " alumnos agregados correctamente al grupo " + grupoSeleccionado + ".");
        cargarAlumnosEnTabla(); // Recargar la tabla de alumnos en la vista
    }//GEN-LAST:event_AddNotaAlumno3ActionPerformed

    private void modifyProfActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_modifyProfActionPerformed
        try {
            // Obtener datos del formulario
            int idProfesor = Integer.parseInt(txtIdProfesor.getText());
            String nombre = txtNombreProfesor.getText().trim();
            String apellido = txtApellidoProfesor.getText().trim();
            String email = txtEmailProfesor.getText().trim();
            String especialidad = txtEspecialidadProfesor.getText().trim();
            String fechaContratacionStr = txtFechaContratacionProfesor.getText().trim();

            if (nombre.isEmpty() || apellido.isEmpty() || email.isEmpty() || especialidad.isEmpty() || fechaContratacionStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor, completa todos los campos.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Parsear fecha de contratación
            Date fechaContratacion = new SimpleDateFormat("dd/MM/yyyy").parse(fechaContratacionStr);

            // Crear objeto Docente con los datos editados
            Docente docente = new Docente(idProfesor, nombre, apellido, email, especialidad, fechaContratacion, null);

            // Actualizar en la base de datos
            DocenteController docenteController = new DocenteController();
            boolean actualizado = docenteController.actualizarDocente(docente);

            if (actualizado) {
                JOptionPane.showMessageDialog(this, "Profesor actualizado correctamente.");
                cargarProfesores(); // Recargar la tabla de profesores
                tabProfes.setSelectedComponent(jPanel15); // Volver a la pestaña principal
            } else {
                JOptionPane.showMessageDialog(this, "Error al actualizar el profesor.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, "La fecha debe tener el formato 'dd/MM/yyyy'.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El ID del profesor no es válido.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_modifyProfActionPerformed

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
    private javax.swing.JButton AddHorario;
    private javax.swing.JPanel AddModifProfe;
    private javax.swing.JButton AddNotaAlumno1;
    private javax.swing.JButton AddNotaAlumno2;
    private javax.swing.JButton AddNotaAlumno3;
    private javax.swing.JButton AgregarClase;
    private javax.swing.JButton CambioContraseña;
    private javax.swing.JButton DescargarEv;
    private javax.swing.JLabel Descripcion;
    private javax.swing.JButton EditNota;
    private javax.swing.JButton EditNota1;
    private javax.swing.JButton EditNota3;
    private javax.swing.JButton EditarClase;
    private javax.swing.JButton EditarGrado;
    private javax.swing.JButton EliminarClase;
    private javax.swing.JButton EliminarNota1;
    private javax.swing.JButton EliminarProfe;
    private javax.swing.JButton ModifGrado;
    private javax.swing.JPanel ModifGrados;
    private javax.swing.JButton ModificarGrupo;
    private javax.swing.JButton ModificarNota1;
    private javax.swing.JButton ModificarProfesor;
    private javax.swing.JLabel Nombre;
    private javax.swing.JPanel PanelGrados;
    private javax.swing.JTable TablaAlumnosTotales;
    private javax.swing.JButton VerHorarios;
    private javax.swing.JButton addGrupo;
    private javax.swing.JPanel agregarClase;
    private javax.swing.JPanel agregarGrupo;
    private javax.swing.JButton btnAgregarHorario;
    private javax.swing.JButton btnEliminarHorario;
    private javax.swing.JButton btnGuardarClase;
    private javax.swing.JButton btnModificarHorario;
    private javax.swing.JButton closeSession1;
    private javax.swing.JComboBox<String> comboBoxDia;
    private javax.swing.JComboBox<String> comboBoxGrado;
    private javax.swing.JComboBox<String> comboBoxGrados1;
    private javax.swing.JComboBox<String> comboBoxGrupos;
    private javax.swing.JComboBox<String> comboBoxProfesor;
    private javax.swing.JComboBox<String> comboBoxProfesor1;
    private javax.swing.JComboBox<String> comboDia;
    private javax.swing.JPanel editarProfesor;
    private javax.swing.JButton guardarNota2;
    private javax.swing.JComboBox<String> jComboBoxGrupos;
    private javax.swing.JComboBox<String> jComboBoxGrupos2;
    private javax.swing.JLabel jLabel1;
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
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel57;
    private javax.swing.JLabel jLabel58;
    private javax.swing.JLabel jLabel59;
    private javax.swing.JLabel jLabel60;
    private javax.swing.JLabel jLabel61;
    private javax.swing.JLabel jLabel62;
    private javax.swing.JLabel jLabel65;
    private javax.swing.JLabel jLabel66;
    private javax.swing.JLabel jLabel67;
    private javax.swing.JLabel jLabel68;
    private javax.swing.JLabel jLabel69;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel70;
    private javax.swing.JLabel jLabel71;
    private javax.swing.JLabel jLabel72;
    private javax.swing.JLabel jLabel74;
    private javax.swing.JLabel jLabel75;
    private javax.swing.JLabel jLabel76;
    private javax.swing.JLabel jLabel77;
    private javax.swing.JLabel jLabel78;
    private javax.swing.JLabel jLabel79;
    private javax.swing.JLabel jLabel80;
    private javax.swing.JLabel jLabel81;
    private javax.swing.JLabel jLabel82;
    private javax.swing.JLabel jLabel83;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JPanel jPanelModificarAlumno;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
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
    private javax.swing.JButton modifyProf;
    private javax.swing.JTabbedPane tabAlumnos;
    private javax.swing.JTabbedPane tabAlumnos1;
    private javax.swing.JTabbedPane tabProfes;
    private javax.swing.JTable tablaAlumnosCargaMasiva;
    private javax.swing.JTable tablaAsignaturas;
    private javax.swing.JTable tablaClaseGrados;
    private javax.swing.JScrollPane tablaClases;
    private javax.swing.JTable tablaGrados;
    private javax.swing.JTable tablaHorario;
    private javax.swing.JTable tablaHorarios;
    private javax.swing.JTable tablaProfesores;
    private javax.swing.JTextField txtAddGrupo;
    private javax.swing.JTextField txtAddGrupo1;
    private javax.swing.JTextField txtApellidoAlumno;
    private javax.swing.JTextField txtApellidoProfesor;
    private javax.swing.JPasswordField txtContrasena;
    private javax.swing.JPasswordField txtContrasena2;
    private javax.swing.JTextField txtContrasenaP;
    private javax.swing.JTextField txtEmailAlumno;
    private javax.swing.JTextField txtEmailProfesor;
    private javax.swing.JTextField txtEspecialidadProfesor;
    private javax.swing.JFormattedTextField txtFechaContratacionProfesor;
    private javax.swing.JTextField txtGrado;
    private javax.swing.JTextField txtGrado1;
    private javax.swing.JTextField txtGrado3;
    private javax.swing.JFormattedTextField txtGrado4;
    private javax.swing.JFormattedTextField txtHoraFin;
    private javax.swing.JFormattedTextField txtHoraFin1;
    private javax.swing.JFormattedTextField txtHoraInicio;
    private javax.swing.JFormattedTextField txtHoraInicio1;
    private javax.swing.JTextField txtIdAlumno;
    private javax.swing.JTextField txtIdProfesor;
    private javax.swing.JTextField txtNombreAlumno;
    private javax.swing.JTextField txtNombreClase;
    private javax.swing.JTextField txtNombreClase1;
    private javax.swing.JTextField txtNombreClase2;
    private javax.swing.JTextField txtNombreClase3;
    private javax.swing.JTextField txtNombreProfesor;
    private javax.swing.JTextField txtNombreUsuario;
    // End of variables declaration//GEN-END:variables
}
