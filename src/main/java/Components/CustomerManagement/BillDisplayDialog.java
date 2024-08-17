/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package Components.CustomerManagement;

import Controller.BillController;
import Controller.ManagementSystemCPU;
import Model.Bill;
import Model.BillItem;
import Model.Customer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author M AYAN LAPTOP
 */
public class BillDisplayDialog extends javax.swing.JDialog {
    String billId;
    Customer selectedCustomer;
    private ActionListener onCloseListener;
    /**
     * Creates new form BillDisplayDialog
     */
    public BillDisplayDialog(java.awt.Frame parent, boolean modal, String billId, Customer ctm) {
        super(parent, modal);
        initComponents();
        this.billId=billId;
        this.selectedCustomer=ctm;
        setTitle(billId);
        setLocationRelativeTo(null);
        setResizable(false);
        updateBillTable(billId, ctm);
    }

    private void updateBillTable(String billId, Customer ctm){
        DefaultTableModel model = (DefaultTableModel)billTable.getModel();
        model.setRowCount(0);
        Bill bill = BillController.getBill(billId, ctm);
        int count = 0;
        for(BillItem currentItem: bill.getBillItems()){
            count++;
            System.out.println(currentItem.getQuantity());
            Object row[]={count,currentItem.getProduct().getProdID(), currentItem.getProduct().getProdName(),currentItem.getQuantity(),currentItem.getRatePerUnit(),currentItem.getUnitType(),currentItem.getDiscount(),currentItem.getTotal()};
            model.addRow(row);
        }        
    }
    
    private void deleteBill(){
        int result = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this bill from the customer's record?","Confirmation",JOptionPane.OK_CANCEL_OPTION,JOptionPane.WARNING_MESSAGE);
        if(result == JOptionPane.OK_OPTION){
            Bill bill = BillController.getBill(billId, selectedCustomer);
            int billCredit = bill.getCredit();
            boolean success = BillController.deleteBill(billId, billCredit,selectedCustomer.getName());
            if(success){
                ManagementSystemCPU.informationAlert(this, "Bill Deletion Successfull", billId+" was successfully deleted");
                setVisible(false);
            }else{
                ManagementSystemCPU.errorAlert(this,"Bill Deletion Failed", billId+" was not deleted");
            }
        }   
    }
    
    public void setOnCloseListener(ActionListener listener) {
        this.onCloseListener = listener;
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (!visible && onCloseListener != null) {
            onCloseListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null));
        }
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
        billTable = new javax.swing.JTable();
        deleteBtn = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        billTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "S/No.", "Product ID", "Product", "Quantity", "Rate / Unit", "Unit Type", "Discount", "Total"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Double.class, java.lang.Double.class, java.lang.Object.class, java.lang.Integer.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        billTable.setRowHeight(30);
        billTable.setRowMargin(10);
        billTable.setShowGrid(true);
        billTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(billTable);

        deleteBtn.setText("Delete");
        deleteBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 797, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(deleteBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 450, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(deleteBtn, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void deleteBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteBtnActionPerformed
        // TODO add your handling code here:
        deleteBill();
    }//GEN-LAST:event_deleteBtnActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable billTable;
    private javax.swing.JButton deleteBtn;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
