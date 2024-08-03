package View;

import Components.BillManagment.BillingMenu;
import Components.CustomerManagement.CustomerManagementScreen;
import Components.EmployeeManagement.EmployeeAttendance;
import Components.EmployeeManagement.PayrollManagementScreen;
import Components.ReportManagement.ReportMenu;
import Controller.ManagementSystemCPU;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;

/**
 *
 * @author Anis Ur Rahman
 */
public class HomeScreen extends javax.swing.JFrame {
    public HomeScreen() {
        FlatDarkLaf.setup();
        initComponents();
        tabbedContainer.add("Billing Module", new BillingMenu());
        tabbedContainer.add("Manage Customers", new CustomerManagementScreen());
//        tabbedContainer.add("Employee Attendance", new EmployeeAttendance());
//        tabbedContainer.add("Manage Payrolls", new PayrollManagementScreen());
        tabbedContainer.add("Report Managment",new ReportMenu());
        setMaximumSize(ManagementSystemCPU.getScreenSize());
        setTitle("Ghussia Shoppers Management System | Main Menu");   
        pack();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tabbedContainer = new javax.swing.JTabbedPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.LINE_AXIS));

        tabbedContainer.setTabPlacement(javax.swing.JTabbedPane.LEFT);
        jScrollPane1.setViewportView(tabbedContainer);

        getContentPane().add(jScrollPane1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

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
            java.util.logging.Logger.getLogger(HomeScreen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(HomeScreen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(HomeScreen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(HomeScreen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new HomeScreen().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTabbedPane tabbedContainer;
    // End of variables declaration//GEN-END:variables
}
