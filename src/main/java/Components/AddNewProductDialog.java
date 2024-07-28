package Components;

import Controller.ManagementSystemCPU;
import Controller.ProductController;
import Model.Product;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Anis Ur Rahman
 */
public class AddNewProductDialog extends javax.swing.JDialog {
    private String currentUnit = null;
    private ActionListener onCloseListener;
    private ArrayList<Product> products;
    private JPopupMenu popupMenu;
    public AddNewProductDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        defaultSettings();
        addTableMouseListener();
    }
    
    private void defaultSettings(){
        unitBtnGroup.add(pieceRB);
        unitBtnGroup.add(weightRB);
        updateBtn.setEnabled(false);
        confirmBtn.setEnabled(true);
        prodIdField.setEnabled(true);
        prodNameField.setEnabled(true);
        products = ProductController.getProductList();
        DefaultTableModel model=(DefaultTableModel) productsTable.getModel();
        model.setRowCount(0);
        int count = model.getRowCount();
        for(Product prod: products){
            Object row[]={++count,prod.getProdID(), prod.getProdName()};
            model.addRow(row);
        }
        prodIdField.setText("");
        prodNameField.setText("");        
        pieceRB.setSelected(true);
        pieceRB.setEnabled(true);
        weightRB.setEnabled(true);
        totalCostField.setText("0");
        costPerPieceField.setText(String.valueOf(0));
        salePerPieceField.setText(String.valueOf(0));
        handleUnitChange();
        // Initialize the popup menu
        popupMenu = new JPopupMenu();
        JMenuItem deleteItem = new JMenuItem("Delete");
        deleteItem.addActionListener((ActionEvent e) -> {
            deleteSelectedRow();
        });
        popupMenu.add(deleteItem);
    }
    
    private void handleUnitChange(){
        if(pieceRB.isSelected()){
            handleQuantityFormatUpdate();
            // Setting default text
            quantityField.setText("1");
            // Updating currentUnit
            currentUnit = ManagementSystemCPU.PIECE;
        }else if(weightRB.isSelected()){
            handleQuantityFormatUpdate();
            // Setting default text
            quantityField.setText("1.00");
            // Updating currentUnit
            currentUnit = ManagementSystemCPU.WEIGHT;
        }
    }
    
    private void handleQuantityFormatUpdate(){
        if(pieceRB.isSelected()){
            // Changing text field formating
            quantityField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        }else{
            // Changing text field formating
            quantityField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.00"))));
        }
    }
    
    private void handleUnitType(String unitType){
        if(unitType.equals(ManagementSystemCPU.PIECE)){
            pieceRB.setSelected(true);
            pieceRB.setEnabled(true);
            weightRB.setEnabled(false);
        }else{
            weightRB.setSelected(true);
            weightRB.setEnabled(true);
            pieceRB.setEnabled(false);
        }
    }
    
    private boolean validateFields(){
        boolean success = true;
        if(prodIdField.getText().isBlank() || prodNameField.getText().isBlank() || currentUnit == null){
            success=false;
        }
        return success;
    }
    
    private void createNewProduct(){
        if(!validateFields()){
            JOptionPane.showMessageDialog(this,"Please check the fields first.","Failure",JOptionPane.ERROR_MESSAGE);
            return;
        }
        Product prod = new Product(prodIdField.getText(),prodNameField.getText(),Double.valueOf(quantityField.getText()),Double.valueOf(totalCostField.getText()),Double.valueOf(salePerPieceField.getText()),Double.valueOf(costPerPieceField.getText()),currentUnit,true);
        boolean isUploaded = ProductController.storeProductToDB(prod);
        if(isUploaded){
            JOptionPane.showMessageDialog(this,"Product \""+prod.getProdName()+"\" uploaded successfully","Product added",JOptionPane.INFORMATION_MESSAGE);
            setVisible(false);
        }else{
            JOptionPane.showMessageDialog(this,"Product \""+prod.getProdName()+"\" was not uploaded","Failure",JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void plotFieldsWithSelectedProduct(){
        DefaultTableModel model = (DefaultTableModel)productsTable.getModel();
        int selectedRow = productsTable.getSelectedRow();
        String selectedProdName = model.getValueAt(selectedRow, 2).toString();
        Product selectedProd = ProductController.getProduct(selectedProdName);
        
        // PLOTTING FIELDS
        prodIdField.setText(selectedProd.getProdID());
        prodNameField.setText(selectedProd.getProdName());
        totalCostField.setText(String.valueOf((int)selectedProd.getTotalCost()));
        costPerPieceField.setText(String.valueOf(selectedProd.getCostPerUnit()));
        salePerPieceField.setText(String.valueOf((int)selectedProd.getSalePerUnit()));
        String unitType = selectedProd.getUnit();
        handleUnitType(unitType); 
        handleQuantityFormatUpdate();
        if(unitType.equals(ManagementSystemCPU.PIECE)){
            quantityField.setText(String.valueOf((int)selectedProd.getQuantity()));
        }else{
            quantityField.setText(String.valueOf(selectedProd.getQuantity()));
        }
        
        updateBtn.setEnabled(true);
        confirmBtn.setEnabled(false);
        prodIdField.setEnabled(false);
        prodNameField.setEnabled(false);
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
    
    private void addTableMouseListener() {
        productsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showPopup(e);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showPopup(e);
                }
            }

            private void showPopup(MouseEvent e) {
                int row = productsTable.rowAtPoint(e.getPoint());
                if (row >= 0 && row < productsTable.getRowCount()) {
                    productsTable.setRowSelectionInterval(row, row);
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
    }
    
    private void deleteSelectedRow() {
        int selectedRow = productsTable.getSelectedRow();
        if (selectedRow >= 0) {
            DefaultTableModel model = (DefaultTableModel) productsTable.getModel();
            String prodName = model.getValueAt(selectedRow, 2).toString();
            boolean isDeleted = ProductController.deleteProduct(prodName);
            if (isDeleted) {
                model.removeRow(selectedRow);
                JOptionPane.showMessageDialog(this, "Product \"" + prodName + "\" deleted successfully", "Product deleted", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Product \"" + prodName + "\" could not be deleted", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void updateSelectedProduct(){
        Product prod = new Product(prodIdField.getText(),prodNameField.getText(),Double.valueOf(quantityField.getText()),Double.valueOf(totalCostField.getText()),Double.valueOf(salePerPieceField.getText()),Double.valueOf(costPerPieceField.getText()),currentUnit, true);
        boolean isUploaded = ProductController.updateProduct(prod);
        if(isUploaded){
            JOptionPane.showMessageDialog(this,"Product \""+prod.getProdName()+"\" updated successfully","Product updated",JOptionPane.INFORMATION_MESSAGE);
            setVisible(false);
        }else{
            JOptionPane.showMessageDialog(this,"Product \""+prod.getProdName()+"\" was not updated","Failure",JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateCostPerPiece(){
        int costPerPiece = 0;
        if(!totalCostField.getText().isBlank()){
            double quantity = Double.valueOf(quantityField.getText());
            int totalCost = Integer.valueOf(totalCostField.getText());
            double temp =(totalCost / quantity);
            System.out.println(temp + " " + Math.round(temp));
            costPerPiece =(int)Math.round(temp);
        }
        costPerPieceField.setText(String.valueOf(costPerPiece));
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        unitBtnGroup = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel5 = new javax.swing.JLabel();
        prodIdField = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        prodNameField = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        costPerPieceField = new javax.swing.JFormattedTextField();
        jLabel12 = new javax.swing.JLabel();
        salePerPieceField = new javax.swing.JFormattedTextField();
        jSeparator2 = new javax.swing.JSeparator();
        confirmBtn = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JSeparator();
        cancelBtn = new javax.swing.JButton();
        jLabel15 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        productsTable = new javax.swing.JTable();
        clearBtn = new javax.swing.JButton();
        updateBtn = new javax.swing.JButton();
        jLabel16 = new javax.swing.JLabel();
        quantityField = new javax.swing.JFormattedTextField();
        pieceRB = new javax.swing.JRadioButton();
        weightRB = new javax.swing.JRadioButton();
        jLabel10 = new javax.swing.JLabel();
        totalCostField = new javax.swing.JFormattedTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Provide details of the new product");
        setAlwaysOnTop(true);
        setModal(true);
        setType(java.awt.Window.Type.POPUP);
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.LINE_AXIS));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setText("Add New Product");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel5.setText("Product ID");

        prodIdField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                prodIdFieldActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel6.setText("Product Name");

        prodNameField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                prodNameFieldActionPerformed(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel9.setText("Cost / Piece");

        costPerPieceField.setEditable(false);
        costPerPieceField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        costPerPieceField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                costPerPieceFieldFocusLost(evt);
            }
        });

        jLabel12.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel12.setText("Sale / Price");

        salePerPieceField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        salePerPieceField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                salePerPieceFieldFocusLost(evt);
            }
        });

        confirmBtn.setText("Confirm");
        confirmBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                confirmBtnActionPerformed(evt);
            }
        });

        cancelBtn.setText("Cancel");
        cancelBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelBtnActionPerformed(evt);
            }
        });

        jLabel15.setFont(new java.awt.Font("Segoe UI", 2, 12)); // NOI18N
        jLabel15.setText("Note: Wholesale (WS), Retail Sale (RS)");

        productsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "S/No.", "Prod. Id", "Product"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        productsTable.setRowHeight(30);
        productsTable.setRowMargin(10);
        productsTable.setShowGrid(true);
        productsTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                productsTableMouseClicked(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                productsTableMouseReleased(evt);
            }
        });
        jScrollPane1.setViewportView(productsTable);
        if (productsTable.getColumnModel().getColumnCount() > 0) {
            productsTable.getColumnModel().getColumn(0).setResizable(false);
            productsTable.getColumnModel().getColumn(0).setPreferredWidth(5);
        }

        clearBtn.setText("Clear");
        clearBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearBtnActionPerformed(evt);
            }
        });

        updateBtn.setText("Update");
        updateBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateBtnActionPerformed(evt);
            }
        });

        jLabel16.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel16.setText("Quantity / Weight");

        quantityField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("0.00"))));
        quantityField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                quantityFieldFocusLost(evt);
            }
        });

        pieceRB.setText("Piece");
        pieceRB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pieceRBActionPerformed(evt);
            }
        });

        weightRB.setText("Weight");
        weightRB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                weightRBActionPerformed(evt);
            }
        });

        jLabel10.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel10.setText("Total Cost");

        totalCostField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        totalCostField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                totalCostFieldFocusLost(evt);
            }
        });
        totalCostField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                totalCostFieldKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(35, 35, 35)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(prodIdField, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(prodNameField, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(costPerPieceField, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel12)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(salePerPieceField, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel16)
                                    .addComponent(jLabel10))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(totalCostField, javax.swing.GroupLayout.DEFAULT_SIZE, 95, Short.MAX_VALUE)
                                    .addComponent(quantityField))
                                .addGap(12, 12, 12)
                                .addComponent(pieceRB)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(weightRB)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jSeparator1)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 426, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 32, Short.MAX_VALUE)
                                .addComponent(clearBtn))
                            .addComponent(jSeparator2)
                            .addComponent(jLabel15, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jSeparator3))
                        .addGap(9, 9, 9))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(cancelBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(updateBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(confirmBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 565, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(9, 9, 9))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(clearBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(prodIdField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(prodNameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel16)
                            .addComponent(quantityField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(pieceRB)
                            .addComponent(weightRB))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel10)
                            .addComponent(totalCostField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(12, 12, 12)
                        .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(11, 11, 11)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel9)
                            .addComponent(costPerPieceField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel12)
                            .addComponent(salePerPieceField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel15)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(confirmBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cancelBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(updateBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(46, 46, 46)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void prodIdFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_prodIdFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_prodIdFieldActionPerformed

    private void prodNameFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_prodNameFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_prodNameFieldActionPerformed

    private void cancelBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelBtnActionPerformed
        // TODO add your handling code here:
        dispose();
    }//GEN-LAST:event_cancelBtnActionPerformed

    private void confirmBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_confirmBtnActionPerformed
        // TODO add your handling code here:
        createNewProduct();
    }//GEN-LAST:event_confirmBtnActionPerformed

    private void productsTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_productsTableMouseClicked
        // TODO add your handling code here:
        plotFieldsWithSelectedProduct();
    }//GEN-LAST:event_productsTableMouseClicked

    private void clearBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearBtnActionPerformed
        // TODO add your handling code here:
        defaultSettings();
    }//GEN-LAST:event_clearBtnActionPerformed

    private void productsTableMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_productsTableMouseReleased
        // TODO add your handling code here:
        
    }//GEN-LAST:event_productsTableMouseReleased

    private void updateBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateBtnActionPerformed
        // TODO add your handling code here:
        updateSelectedProduct();
    }//GEN-LAST:event_updateBtnActionPerformed

    private void costPerPieceFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_costPerPieceFieldFocusLost
        // TODO add your handling code here:
        if(costPerPieceField.getText().isBlank()){
            costPerPieceField.setText("0");
        }
    }//GEN-LAST:event_costPerPieceFieldFocusLost

    private void salePerPieceFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_salePerPieceFieldFocusLost
        // TODO add your handling code here:
        if(salePerPieceField.getText().isBlank()){
            salePerPieceField.setText("0");
        }
    }//GEN-LAST:event_salePerPieceFieldFocusLost

    private void quantityFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_quantityFieldFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_quantityFieldFocusLost

    private void totalCostFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_totalCostFieldFocusLost
        // TODO add your handling code here:
        updateCostPerPiece();
    }//GEN-LAST:event_totalCostFieldFocusLost

    private void pieceRBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pieceRBActionPerformed
        // TODO add your handling code here:
        handleUnitChange();
    }//GEN-LAST:event_pieceRBActionPerformed

    private void weightRBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_weightRBActionPerformed
        // TODO add your handling code here:
        handleUnitChange();
    }//GEN-LAST:event_weightRBActionPerformed

    private void totalCostFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_totalCostFieldKeyReleased
        // TODO add your handling code here:
        
    }//GEN-LAST:event_totalCostFieldKeyReleased

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
            java.util.logging.Logger.getLogger(AddNewProductDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(AddNewProductDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(AddNewProductDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AddNewProductDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                AddNewProductDialog dialog = new AddNewProductDialog(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelBtn;
    private javax.swing.JButton clearBtn;
    private javax.swing.JButton confirmBtn;
    private javax.swing.JFormattedTextField costPerPieceField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JRadioButton pieceRB;
    private javax.swing.JTextField prodIdField;
    private javax.swing.JTextField prodNameField;
    private javax.swing.JTable productsTable;
    private javax.swing.JFormattedTextField quantityField;
    private javax.swing.JFormattedTextField salePerPieceField;
    private javax.swing.JFormattedTextField totalCostField;
    private javax.swing.ButtonGroup unitBtnGroup;
    private javax.swing.JButton updateBtn;
    private javax.swing.JRadioButton weightRB;
    // End of variables declaration//GEN-END:variables
}
