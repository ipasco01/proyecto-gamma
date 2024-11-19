/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package proyectogamma.views;

import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.ui.FlatComboBoxUI;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import proyectogamma.model.Usuario;
import proyectogamma.model.Alumno;
import proyectogamma.controller.UsuarioController;
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
    }
    initComponents();
    jTabbedPane4.setUI(new com.formdev.flatlaf.ui.FlatTabbedPaneUI());
    
    setLocationRelativeTo(null); // Centrar la ventana

    if (alumno != null) {
        lblBienvenida.setText("Bienvenido, " + alumno.getNombre() + " " + alumno.getApellido());
    } else {
        lblBienvenida.setText("Bienvenido, Alumno desconocido");
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
        listaNotificaciones3 = new javax.swing.JList<>();
        jScrollPane9 = new javax.swing.JScrollPane();
        listaNotificacionesPersonal3 = new javax.swing.JList<>();
        jLabel8 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jComboBox1 = new javax.swing.JComboBox<>();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jScrollPane10 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList<>();
        jLabel11 = new javax.swing.JLabel();
        jScrollPane11 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jLabel14 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        txtNombreUsuario = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        txtContrasena = new javax.swing.JPasswordField();
        btnLogin = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Bienvenido");
        setBackground(java.awt.Color.white);
        setBounds(new java.awt.Rectangle(0, 0, 810, 600));
        setFont(new java.awt.Font("Trebuchet MS", 1, 12)); // NOI18N
        setForeground(new java.awt.Color(204, 204, 204));
        setMaximumSize(new java.awt.Dimension(800, 600));
        setMinimumSize(new java.awt.Dimension(800, 600));
        setUndecorated(true);
        setPreferredSize(new java.awt.Dimension(800, 600));
        setResizable(false);
        setSize(new java.awt.Dimension(800, 500));

        jPanel8.setBackground(new java.awt.Color(255, 255, 255));
        jPanel8.setPreferredSize(new java.awt.Dimension(800, 500));
        jPanel8.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel9.setBackground(new java.awt.Color(0, 0, 204));
        jPanel9.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblBienvenida.setFont(new java.awt.Font("Poppins Medium", 3, 14)); // NOI18N
        lblBienvenida.setForeground(new java.awt.Color(255, 255, 255));
        lblBienvenida.setText("jLabel2");
        jPanel9.add(lblBienvenida, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 310, -1));

        closeSession.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        closeSession.setText("Cerrar Sesión");
        closeSession.setBorder(null);
        closeSession.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        closeSession.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeSessionActionPerformed(evt);
            }
        });
        jPanel9.add(closeSession, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 7, 97, 28));

        jPanel8.add(jPanel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 800, 40));

        mainPanel3.setBackground(new java.awt.Color(255, 255, 255));
        mainPanel3.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        mainPanel3.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        mainPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jTabbedPane4.setBackground(new java.awt.Color(0, 0, 204));
        jTabbedPane4.setForeground(new java.awt.Color(255, 255, 255));
        jTabbedPane4.setToolTipText("");
        jTabbedPane4.setFocusable(false);
        jTabbedPane4.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        jTabbedPane4.setRequestFocusEnabled(false);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel4.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        jLabel4.setText("Notificaciones para tu grupo");

        listaNotificaciones3.setFont(new java.awt.Font("Poppins", 0, 10)); // NOI18N
        listaNotificaciones3.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        listaNotificaciones3.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        listaNotificaciones3.setSelectionBackground(new java.awt.Color(0, 0, 204));
        listaNotificaciones3.setSelectionForeground(new java.awt.Color(255, 255, 255));
        jScrollPane8.setViewportView(listaNotificaciones3);

        listaNotificacionesPersonal3.setFont(new java.awt.Font("Poppins", 0, 10)); // NOI18N
        listaNotificacionesPersonal3.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane9.setViewportView(listaNotificacionesPersonal3);

        jLabel8.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        jLabel8.setText("Notificaciones para ti");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 359, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, 359, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addGap(37, 37, 37))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(7, 7, 7)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 380, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, 380, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(112, Short.MAX_VALUE))
        );

        jTabbedPane4.addTab("Ver Notificaciones", jPanel1);

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));
        jPanel6.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jComboBox1.setFont(new java.awt.Font("Trebuchet MS", 3, 14)); // NOI18N
        jComboBox1.setForeground(java.awt.SystemColor.textHighlight);
        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox1.setBorder(null);
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });
        jScrollPane1.setViewportView(jComboBox1);

        jPanel6.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 30, 255, 50));

        jLabel9.setFont(new java.awt.Font("Trebuchet MS", 1, 18)); // NOI18N
        jLabel9.setText("Notas");
        jPanel6.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(298, 6, -1, 20));

        jLabel10.setFont(new java.awt.Font("Trebuchet MS", 1, 18)); // NOI18N
        jLabel10.setText("Asignaturas");
        jPanel6.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 6, -1, 20));

        jList1.setBorder(null);
        jList1.setFont(new java.awt.Font("Trebuchet MS", 0, 12)); // NOI18N
        jList1.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jList1.setSelectionBackground(new java.awt.Color(0, 0, 204));
        jScrollPane10.setViewportView(jList1);

        jPanel6.add(jScrollPane10, new org.netbeans.lib.awtextra.AbsoluteConstraints(298, 30, 456, 454));

        jLabel11.setFont(new java.awt.Font("Trebuchet MS", 1, 18)); // NOI18N
        jLabel11.setText("Promedios");
        jPanel6.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 177, -1, 20));

        jTextArea1.setColumns(20);
        jTextArea1.setFont(new java.awt.Font("Trebuchet MS", 0, 12)); // NOI18N
        jTextArea1.setRows(5);
        jScrollPane11.setViewportView(jTextArea1);

        jPanel6.add(jScrollPane11, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 203, 255, 281));
        jPanel6.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 98, 255, 73));

        jTabbedPane4.addTab("Ver Asignaturas", jPanel6);

        jPanel7.setBackground(new java.awt.Color(255, 255, 255));
        jPanel7.setDoubleBuffered(false);
        jPanel7.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        txtNombreUsuario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNombreUsuarioActionPerformed(evt);
            }
        });
        jPanel7.add(txtNombreUsuario, new org.netbeans.lib.awtextra.AbsoluteConstraints(457, 122, 195, -1));

        jLabel12.setFont(new java.awt.Font("Trebuchet MS", 1, 14)); // NOI18N
        jLabel12.setText("Usuario");
        jPanel7.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(457, 98, -1, -1));

        jLabel13.setFont(new java.awt.Font("Trebuchet MS", 1, 14)); // NOI18N
        jLabel13.setText("Contraseña");
        jPanel7.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(457, 162, -1, -1));
        jPanel7.add(txtContrasena, new org.netbeans.lib.awtextra.AbsoluteConstraints(457, 206, 195, -1));

        btnLogin.setText("Ingresar");
        btnLogin.setActionCommand("login");
        btnLogin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLoginlogin(evt);
            }
        });
        jPanel7.add(btnLogin, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 244, -1, -1));

        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/logoPeque.png"))); // NOI18N
        jPanel7.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 30, 80, 70));

        jTabbedPane4.addTab("Cambio de Contraseña", jPanel7);

        mainPanel3.add(jTabbedPane4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 3, 800, 560));

        jPanel8.add(mainPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 36, 800, 540));

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

    private void btnLoginlogin(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLoginlogin

    }//GEN-LAST:event_btnLoginlogin

    private void txtNombreUsuarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNombreUsuarioActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNombreUsuarioActionPerformed

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox1ActionPerformed

    private void closeSessionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeSessionActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_closeSessionActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
         try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
         
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                Usuario usuarioPrueba = new Usuario(1, "juanperez", "12345", "Alumno", 1, null, null);
        new AlumnoView(usuarioPrueba).setVisible(true);
                
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnLogin;
    private javax.swing.JButton closeSession;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JList<String> jList1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane11;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JTabbedPane jTabbedPane4;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JLabel lblBienvenida;
    private javax.swing.JList<String> listaNotificaciones3;
    private javax.swing.JList<String> listaNotificacionesPersonal3;
    private javax.swing.JPanel mainPanel3;
    private javax.swing.JPasswordField txtContrasena;
    private javax.swing.JTextField txtNombreUsuario;
    // End of variables declaration//GEN-END:variables
}
