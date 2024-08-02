package Components;

import Controller.BillController;
import Controller.ExcelInvoiceGenerator;
import Controller.ManagementSystemCPU;
import Controller.PrintInvoice;
import Controller.ProductController;
import Model.BillItem;
import Model.Customer;
import Model.Product;
import Model.Bill;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Date;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Box;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.RowFilter;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
/**
 *
 * @author Anis Ur Rahman
 */
public class BillingMenu extends javax.swing.JPanel {
    Date currentDate;
    ArrayList<Product> products;
    BillItem currentItem = null;
    int totalBill;
    Customer selectedCustomer = null;
    Product selectedProduct = null;
    Bill currentBill = null;
    public BillingMenu() {
        initComponents();
        createRadioBtnGroups();
        defaultSettings();
        addTableMouseListener();
        addTableModelListener();
        addProductTableSelectionListener();
        Dimension screenDimension = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension panelDimension = new Dimension(screenDimension.width-200,screenDimension.height-50);
        ContentPanel.setMaximumSize(panelDimension);
        ContentPanel.repaint();
    }
    
    private void createRadioBtnGroups(){
        unitTypeBtnGroup.add(pieceRB);
        unitTypeBtnGroup.add(weightRB);
    }
    
    private void defaultSettings(){
        DefaultTableModel model=(DefaultTableModel) billTable.getModel();
        model.setRowCount(0);
        products = new ArrayList<>();
        currentDate = new Date(new java.util.Date().getTime());
        billDateChooser.setDate(currentDate);
        pieceRB.setSelected(true);
        quantityField.setText("1");
        prodDiscountField_Rs.setText("0");
        totalBill=0;
        totalBillField.setText(String.valueOf(totalBill));
        finalTotalBillField.setText("0");
        totalDiscountField.setText("0");
        selectedCtmLabel.setText("");
        depositField.setText("0");
        creditField.setText("");
        currentBill = null;
        selectedCustomer = new Customer("Ghusia","","");
        quantityField.setText("1");
        ratePerUnitField.setText("0");
        updateBillId();
        updateTaxField();
        updateProductTable();
        emptyBillTable();
    }
    
    private void updateDate(){
        java.util.Date choosenDate = billDateChooser.getDate();
        currentDate = new Date(choosenDate.getTime());
        updateBillId();
    }
    
    private void updateBillId(){
        String billId;
        String subDateString = currentDate.toString().replace("-","");
        // Remove the first two characters of the year (assuming year is always first 4 characters)
        subDateString = subDateString.substring(2);
        billId = subDateString;
        int count = BillController.getBillCount(currentDate);
        billId=billId.concat("-"+count);
        billIdField.setText(billId);
    }
    
    private void updateTaxField(){
        taxField.setText("0");
    }

    private void addTableMouseListener() {
        billTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) { // Right-click
                    int row = billTable.rowAtPoint(e.getPoint());
                    int column = billTable.columnAtPoint(e.getPoint());
                    if (row != -1 && column != -1) {
                        billTable.setRowSelectionInterval(row, row);                        
                            showContextMenu(e.getX(), e.getY(), row);
                    }
                }
            }
        });
    }
    
    private void addTableModelListener() {
        billTable.getModel().addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                int row = e.getFirstRow();
                int column = e.getColumn();

                if (column == 3 || column == 6 || column==4) { // Quantity or Discount columns or Rate Per Unit
                    updateRowTotal(row,column);
                }
            }
        });
    }
    
    private void updateRowTotal(int row, int col) {
        DefaultTableModel model = (DefaultTableModel) billTable.getModel();
        Product product = products.get(row); // Assuming you have a way to map row to product
        
        int ratePerUnit = (int) product.getSalePerUnit();
        if(col == 4){
            ratePerUnit =(int) model.getValueAt(row, col);
        }else{
            model.setValueAt(ratePerUnit, row, 4);
        }        
        int discount = Integer.valueOf(model.getValueAt(row, 6).toString());
        double quantity =(double) model.getValueAt(row, 3);
        int total = (int)((quantity * ratePerUnit)-discount);
        model.setValueAt(total, row, 7);
        updateTotalBill();
    }

    private void updateTotalBill() {
        int rowCount = billTable.getRowCount();
        int totalBill = 0;
        for (int i = 0; i < rowCount; i++) {
            totalBill += Integer.parseInt(billTable.getValueAt(i, 7).toString());
        }
        totalBillField.setText(String.valueOf(totalBill));
        finalTotalBillField.setText(String.valueOf(totalBill));
        depositField.setText(String.valueOf(totalBill));
    }

    private void showContextMenu(int x, int y, int row) {
        JPopupMenu contextMenu = new JPopupMenu();
        JMenuItem deleteItem = new JMenuItem("Delete");
        deleteItem.addActionListener(e -> deleteRow(row));
        contextMenu.add(deleteItem);
        contextMenu.show(billTable, x, y);
    }

    private void deleteRow(int row) {
        DefaultTableModel model = (DefaultTableModel) billTable.getModel();
        model.removeRow(row);
        // Update total bill
        updateTotalBill();
    }
    
    private void emptyBillTable(){
        DefaultTableModel model=(DefaultTableModel) billTable.getModel();
        model.setRowCount(0);
    }
    
    private void updateProductTable(){
        DefaultTableModel model = (DefaultTableModel) productTable.getModel();
        model.setRowCount(0);
        products = ProductController.getProductList();
        int count = 0;
        for(Product prod: products){
            Object row[]={++count,prod.getProdID(), prod.getProdName(),prod.getQuantity(),prod.getCostPerUnit(),prod.getSalePerUnit()};
            model.addRow(row);
        }
    }
    
    private void updateCurrentItem() throws Exception{   
        if(currentItem==null){
            return;
        }
        try{
            if(quantityField.getText().isBlank()){
                quantityField.setText("1");
                updateQuantity(quantityField.getText());
            }else{
                updateQuantity(quantityField.getText());
            }
        }catch(Exception e){
            throw new Exception(e);
        }
        
        updateRate(ratePerUnitField.getText());
        
        updateTotal();
        updateDiscount();
    }
    
    private void updateQuantity(String quantity) throws Exception{
        if(currentItem!=null){
            double q = Double.valueOf(quantity);
            if(q>selectedProduct.getQuantity()){
                throw new Exception("Insufficient stock");
            }
            currentItem.setQuantity(q);
        }else{
            JOptionPane.showMessageDialog(this,"No product selected. Please choose a product first","Error",JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateRate(String rate){
        if(currentItem!=null){
            if(rate.isBlank() || rate.equals("0")){
                ManagementSystemCPU.errorAlert(null, "Invalid quantity", "Please enter a valid quantity");
                return;
            }
            currentItem.setRatePerUnit(Integer.valueOf(rate));
        }else{
            JOptionPane.showMessageDialog(this,"No product selected. Please choose a product first","Error",JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateTotal(){
        if(currentItem!=null){
            currentItem.setTotal((int)Math.round(currentItem.getQuantity() * currentItem.getRatePerUnit()));
        }else{
            JOptionPane.showMessageDialog(this,"No product selected. Please choose a product first","Error",JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateDiscount(){
        if(currentItem!=null){
            if(prodDiscountField_Percentage.getText().isBlank() && !prodDiscountField_Rs.getText().isBlank()){
                int tempTotal = currentItem.getTotal();
                int tempDiscount = Integer.valueOf(prodDiscountField_Rs.getText());
                int newTotal = tempTotal - tempDiscount;
                currentItem.setDiscount(tempDiscount);
                currentItem.setTotal(newTotal);
            }else if(prodDiscountField_Rs.getText().isBlank() && !prodDiscountField_Percentage.getText().isBlank()){
                int tempTotal = currentItem.getTotal();
                int tempDiscountPercentage = Integer.valueOf(prodDiscountField_Percentage.getText());
                int tempDiscount = tempTotal * tempDiscountPercentage / 100;
                int newTotal = tempTotal - tempDiscount;
                currentItem.setDiscount(tempDiscount);
                currentItem.setTotal(newTotal);
            }
        }else{
            JOptionPane.showMessageDialog(this,"No product selected. Please choose a product first","Error",JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void resetFields(){
        quantityField.setText("1");
        ratePerUnitField.setText("0");
        currentItem = null;
        selectedProduct = null;
        prodIDField.setText("");
        prodNameField.setText("");
        updateProductTable();
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
    
    private void addBillItemToBill(){
        if(currentItem != null){
            try {
                updateCurrentItem();
            } catch (Exception ex) {
                ManagementSystemCPU.errorAlert(null,ex.getMessage(),"You don't have enough stock in the inventory.");
                return;
            }
//            CHECK IF ALREADY EXISTS
            DefaultTableModel model = (DefaultTableModel) billTable.getModel();
            for(int row=0;row<model.getRowCount();row++){
                String prodId = model.getValueAt(row, 1).toString();
                String prodName = model.getValueAt(row, 2).toString();
                int discount = (int)model.getValueAt(row, 6);
                int ratePerUnit = (int)model.getValueAt(row,4);
                
                if(currentItem.getProduct().getProdID().equals(prodId) && currentItem.getProduct().getProdName().equals(prodName) && currentItem.getRatePerUnit() == ratePerUnit && currentItem.getDiscount() == discount){
                    JOptionPane.showMessageDialog(this, "Product already exists in the table","Redundancy Error",JOptionPane.ERROR_MESSAGE);
                    resetFields();
                    return;                   
                }                
            }
            // CHECK IF THE SAME PRODUCT HAS ENOUGH STOCK IN INVENTORY
            double checkQuant = 0;
            for(int row=0;row<model.getRowCount();row++){ 
                String prodId = model.getValueAt(row, 1).toString();
                String prodName = model.getValueAt(row, 2).toString();
                if(currentItem.getProduct().getProdID().equals(prodId) && currentItem.getProduct().getProdName().equals(prodName)){
                    double quantity = (double)model.getValueAt(row, 3);
                    checkQuant += quantity;
                }
                System.out.println(checkQuant);
            }
            checkQuant+=currentItem.getQuantity();
            if(checkQuant>selectedProduct.getQuantity()){
                JOptionPane.showMessageDialog(this, "Insufficient Stock","You don't have enough stock",JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            int count = model.getRowCount()+1;
            Object row[]={count,currentItem.getProduct().getProdID(), currentItem.getProduct().getProdName(),currentItem.getQuantity(),currentItem.getRatePerUnit(),currentItem.getUnitType(),currentItem.getDiscount(),currentItem.getTotal()};
            model.addRow(row);
            totalBill += currentItem.getTotal();
            totalBillField.setText(String.valueOf(totalBill));
            finalTotalBillField.setText(String.valueOf(totalBill));
            depositField.setText(String.valueOf(totalBill));
            resetFields();
        }else{
            JOptionPane.showMessageDialog(this,"No product selected. Please choose a product first","Error",JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateFinalBillField(){
        int totalDiscountPercentage = 0;
        int totalDiscount = 0;
        if(totalDiscountField.getText().isBlank()){
            finalTotalBillField.setText(String.valueOf(totalBill));
            return;
        }
        totalDiscountPercentage= Integer.valueOf(totalDiscountField.getText());
        totalDiscount = totalBill * totalDiscountPercentage / 100;
        int finalBill = totalBill - totalDiscount;
        finalTotalBillField.setText(String.valueOf(finalBill));
    }
    
    private void filterProductTable() {
        String input = prodSearchField.getText().trim().toLowerCase();
        DefaultTableModel model = (DefaultTableModel) productTable.getModel();
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        productTable.setRowSorter(sorter);

        RowFilter<DefaultTableModel, Object> rf = new RowFilter<DefaultTableModel, Object>() {
            @Override
            public boolean include(Entry<? extends DefaultTableModel, ? extends Object> entry) {
                String prodId = entry.getStringValue(1).toLowerCase();
                String prodName = entry.getStringValue(2).toLowerCase();
                return prodId.contains(input) || prodName.contains(input);
            }
        };
        sorter.setRowFilter(rf);
    }
    
    private void addProductTableSelectionListener() {
        productTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) { // Ensure that this event is not fired multiple times
                    int selectedRow = productTable.getSelectedRow();
                    if (selectedRow != -1) {
                        selectedRow = productTable.convertRowIndexToModel(selectedRow); // Convert to model index if filtered
                        DefaultTableModel model = (DefaultTableModel) productTable.getModel();
                        String prodId = model.getValueAt(selectedRow, 1).toString();
                        handleSelectedProduct(prodId);
                    }
                }
            }
        });
    }

    private void handleSelectedProduct(String prodId) {
        // Handle the selected customer data as needed
        selectedProduct = ProductController.getProduct(prodId);
        prodIDField.setText(selectedProduct.getProdID());
        prodNameField.setText(selectedProduct.getProdName());
        ratePerUnitField.setText(String.valueOf((int)selectedProduct.getSalePerUnit()));
        if(selectedProduct.getUnit().equals(ManagementSystemCPU.PIECE)){
            pieceRB.setSelected(true);
            weightRB.setSelected(false);
        }else{
            weightRB.setSelected(true);
            pieceRB.setSelected(false);
        }
        handleQuantityFormatUpdate();
        quantityField.setText("1");
        currentItem =  new BillItem(selectedProduct);
        currentItem.setRatePerUnit((int)Math.round(selectedProduct.getSalePerUnit()));
        currentItem.setUnitType(selectedProduct.getUnit());
    }
    
    private void handleProceed(){
        DefaultTableModel model = (DefaultTableModel) billTable.getModel();
        if(model.getRowCount()==0 || depositField.getText().isBlank()){
            ManagementSystemCPU.errorAlert(this,"Invalid Request","Please check the bill and customer.");
            return;
        }
        ArrayList<BillItem> billItems = new ArrayList<>();
            // Iterate through the rows of the bill table
        for (int i = 0; i < model.getRowCount(); i++) {
            String prodName = (String) model.getValueAt(i, 2);
            double quantity = (double) model.getValueAt(i, 3);
            int ratePerUnit = (int) model.getValueAt(i, 4);
            String unitType = (String) model.getValueAt(i, 5);
            int discount = (int) model.getValueAt(i, 6);
            int total = (int) model.getValueAt(i, 7);

            // Retrieve the Product object using prodID (Assuming a method to get Product by ID)
            Product product = ProductController.getProduct(prodName); // Implement this method to fetch product details

            // Create a BillItem object
            BillItem billItem = new BillItem(product);
            billItem.setQuantity(quantity);
            billItem.setDiscount(discount);
            billItem.setRatePerUnit(ratePerUnit);
            billItem.setUnitType(unitType);
            billItem.setTotal(total);

            // Add the created BillItem to the list
            billItems.add(billItem);
        }
        // Create a new Bill object with the gathered data
        currentBill = new Bill(
            billIdField.getText(),
            currentDate,
            selectedCustomer,
            billItems,
            Integer.valueOf(totalBillField.getText()),
            Integer.valueOf(finalTotalBillField.getText())+Integer.valueOf(taxField.getText()),
            Integer.valueOf(totalDiscountField.getText()),
            Integer.valueOf(taxField.getText()),
            Integer.valueOf(depositField.getText())                
        );
//        PrintInvoice.printExcelSheet(currentBill);
        boolean success = BillController.recordBill(currentBill) && ExcelInvoiceGenerator.generateExcel(currentBill);
        if(!success){
            ManagementSystemCPU.errorAlert(this,"Printing error","Failed to print the bill");
        }else{
            ExcelInvoiceGenerator.generateExcel(currentBill);
            ManagementSystemCPU.informationAlert(this,"Bill saved","Bill was successfully saved");
            defaultSettings();
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

        unitTypeBtnGroup = new javax.swing.ButtonGroup();
        unitSizeBtnGroup = new javax.swing.ButtonGroup();
        jSeparator2 = new javax.swing.JSeparator();
        headerPanel = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        billIdField = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        taxField = new javax.swing.JFormattedTextField();
        billDateChooser = new com.toedter.calendar.JDateChooser();
        ContentPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        billTable = new javax.swing.JTable();
        billingDetailsPanel = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        addToBillBtn = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JSeparator();
        jSeparator4 = new javax.swing.JSeparator();
        jLabel9 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        quantityField = new javax.swing.JFormattedTextField();
        prodDiscountField_Percentage = new javax.swing.JFormattedTextField();
        prodDiscountField_Rs = new javax.swing.JFormattedTextField();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        addNewProductBtn = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        pieceRB = new javax.swing.JRadioButton();
        weightRB = new javax.swing.JRadioButton();
        selectedCtmLabel = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        prodIDField = new javax.swing.JTextField();
        prodNameField = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        ratePerUnitField = new javax.swing.JFormattedTextField();
        customerDetailsPanel = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        productTable = new javax.swing.JTable();
        jLabel4 = new javax.swing.JLabel();
        jSeparator5 = new javax.swing.JSeparator();
        selectCtmBtn = new javax.swing.JButton();
        prodSearchField = new javax.swing.JTextField();
        clearProductFilterBtn = new javax.swing.JButton();
        footerPanel = new javax.swing.JPanel();
        proceedBtn = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        totalBillField = new javax.swing.JTextField();
        dropBillBtn = new javax.swing.JButton();
        finalTotalBillField = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        totalDiscountField = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        depositField = new javax.swing.JFormattedTextField();
        jLabel20 = new javax.swing.JLabel();
        creditField = new javax.swing.JFormattedTextField();
        checkBtn = new javax.swing.JButton();
        jLabel21 = new javax.swing.JLabel();

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel2.setText("Bill ID");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel3.setText("Date");

        billIdField.setEnabled(false);
        billIdField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                billIdFieldActionPerformed(evt);
            }
        });

        jLabel19.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel19.setText("Tax Rs.");

        taxField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        taxField.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        taxField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                taxFieldFocusLost(evt);
            }
        });

        billDateChooser.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                billDateChooserMouseClicked(evt);
            }
        });
        billDateChooser.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                billDateChooserPropertyChange(evt);
            }
        });

        javax.swing.GroupLayout headerPanelLayout = new javax.swing.GroupLayout(headerPanel);
        headerPanel.setLayout(headerPanelLayout);
        headerPanelLayout.setHorizontalGroup(
            headerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(headerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addGap(18, 18, 18)
                .addComponent(billIdField, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(billDateChooser, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel19)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(taxField, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        headerPanelLayout.setVerticalGroup(
            headerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(headerPanelLayout.createSequentialGroup()
                .addGroup(headerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(headerPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(headerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(taxField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel19)))
                    .addGroup(headerPanelLayout.createSequentialGroup()
                        .addGap(8, 8, 8)
                        .addGroup(headerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(billDateChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(headerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel2)
                                .addComponent(billIdField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel3)))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        billTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "S/No.", "Product ID", "Product", "Quantity", "Rate / Unit", "Unit Type", "Discount", "Total"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Double.class, java.lang.Integer.class, java.lang.Object.class, java.lang.Integer.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, true, false, false, true, false
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
        if (billTable.getColumnModel().getColumnCount() > 0) {
            billTable.getColumnModel().getColumn(0).setResizable(false);
            billTable.getColumnModel().getColumn(0).setPreferredWidth(5);
            billTable.getColumnModel().getColumn(3).setResizable(false);
            billTable.getColumnModel().getColumn(4).setResizable(false);
        }

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel7.setText("Create Bill");

        addToBillBtn.setText("Add to bill");
        addToBillBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addToBillBtnActionPerformed(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel9.setText("Quantity");

        jLabel6.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel6.setText("Product:");

        jLabel12.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel12.setText("Prod. Discount");
        jLabel12.setEnabled(false);

        quantityField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        quantityField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                quantityFieldFocusLost(evt);
            }
        });
        quantityField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                quantityFieldActionPerformed(evt);
            }
        });
        quantityField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                quantityFieldKeyReleased(evt);
            }
        });

        prodDiscountField_Percentage.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        prodDiscountField_Percentage.setEnabled(false);
        prodDiscountField_Percentage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                prodDiscountField_PercentageActionPerformed(evt);
            }
        });
        prodDiscountField_Percentage.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                prodDiscountField_PercentageKeyReleased(evt);
            }
        });

        prodDiscountField_Rs.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        prodDiscountField_Rs.setEnabled(false);
        prodDiscountField_Rs.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                prodDiscountField_RsFocusLost(evt);
            }
        });
        prodDiscountField_Rs.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                prodDiscountField_RsKeyReleased(evt);
            }
        });

        jLabel13.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel13.setText("Rs. /");
        jLabel13.setEnabled(false);

        jLabel14.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel14.setText("%");
        jLabel14.setEnabled(false);

        addNewProductBtn.setText("+");
        addNewProductBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addNewProductBtnActionPerformed(evt);
            }
        });

        jLabel10.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel10.setText("Unit Type");

        pieceRB.setText("Piece");
        pieceRB.setEnabled(false);
        pieceRB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pieceRBActionPerformed(evt);
            }
        });

        weightRB.setText("Weight");
        weightRB.setEnabled(false);
        weightRB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                weightRBActionPerformed(evt);
            }
        });

        selectedCtmLabel.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        selectedCtmLabel.setForeground(new java.awt.Color(0, 204, 0));
        selectedCtmLabel.setText(" ");

        jLabel17.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel17.setText("Selected:");

        prodIDField.setEditable(false);
        prodIDField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                prodIDFieldActionPerformed(evt);
            }
        });

        prodNameField.setEditable(false);
        prodNameField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                prodNameFieldActionPerformed(evt);
            }
        });

        jLabel22.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel22.setText("Name:");

        jLabel23.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel23.setText("Rate / Unit");

        ratePerUnitField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        ratePerUnitField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                ratePerUnitFieldFocusLost(evt);
            }
        });
        ratePerUnitField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ratePerUnitFieldActionPerformed(evt);
            }
        });
        ratePerUnitField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                ratePerUnitFieldKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout billingDetailsPanelLayout = new javax.swing.GroupLayout(billingDetailsPanel);
        billingDetailsPanel.setLayout(billingDetailsPanelLayout);
        billingDetailsPanelLayout.setHorizontalGroup(
            billingDetailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(billingDetailsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(billingDetailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(billingDetailsPanelLayout.createSequentialGroup()
                        .addGroup(billingDetailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(billingDetailsPanelLayout.createSequentialGroup()
                                .addComponent(jLabel17)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(selectedCtmLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(98, 98, 98)
                                .addComponent(addToBillBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(billingDetailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jSeparator4, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jSeparator3, javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, billingDetailsPanelLayout.createSequentialGroup()
                                    .addGap(29, 29, 29)
                                    .addGroup(billingDetailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(billingDetailsPanelLayout.createSequentialGroup()
                                            .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGap(18, 18, 18)
                                            .addComponent(quantityField, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(billingDetailsPanelLayout.createSequentialGroup()
                                            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGap(18, 18, 18)
                                            .addComponent(pieceRB)
                                            .addGap(18, 18, 18)
                                            .addComponent(weightRB))
                                        .addGroup(billingDetailsPanelLayout.createSequentialGroup()
                                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGap(18, 18, 18)
                                            .addComponent(prodIDField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(jLabel22)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(prodNameField, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(billingDetailsPanelLayout.createSequentialGroup()
                                            .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGap(18, 18, 18)
                                            .addComponent(prodDiscountField_Rs, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGap(5, 5, 5)
                                            .addComponent(jLabel13)
                                            .addGap(18, 18, 18)
                                            .addComponent(prodDiscountField_Percentage, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(jLabel14))
                                        .addGroup(billingDetailsPanelLayout.createSequentialGroup()
                                            .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGap(18, 18, 18)
                                            .addComponent(ratePerUnitField, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(addNewProductBtn)))
                .addContainerGap(39, Short.MAX_VALUE))
        );
        billingDetailsPanelLayout.setVerticalGroup(
            billingDetailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(billingDetailsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(billingDetailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(addNewProductBtn)
                    .addComponent(prodIDField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(prodNameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel22))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(billingDetailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(pieceRB)
                    .addComponent(weightRB))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(billingDetailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(quantityField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(billingDetailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel23)
                    .addComponent(ratePerUnitField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                .addGroup(billingDetailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(prodDiscountField_Percentage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(prodDiscountField_Rs, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13)
                    .addComponent(jLabel14))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(billingDetailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(addToBillBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17)
                    .addComponent(selectedCtmLabel))
                .addContainerGap())
        );

        productTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "S/No.", "Prod ID", "Prod Name", "In-Stock", "Avg. C/U", "Sale / Unit"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Double.class, java.lang.Integer.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        productTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(productTable);
        if (productTable.getColumnModel().getColumnCount() > 0) {
            productTable.getColumnModel().getColumn(0).setPreferredWidth(5);
        }

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel4.setText("Products Details");

        selectCtmBtn.setText("Select Customer");
        selectCtmBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectCtmBtnActionPerformed(evt);
            }
        });

        prodSearchField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                prodSearchFieldActionPerformed(evt);
            }
        });
        prodSearchField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                prodSearchFieldKeyReleased(evt);
            }
        });

        clearProductFilterBtn.setText("X");
        clearProductFilterBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearProductFilterBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout customerDetailsPanelLayout = new javax.swing.GroupLayout(customerDetailsPanel);
        customerDetailsPanel.setLayout(customerDetailsPanelLayout);
        customerDetailsPanelLayout.setHorizontalGroup(
            customerDetailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, customerDetailsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(customerDetailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane2)
                    .addComponent(jSeparator5, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, customerDetailsPanelLayout.createSequentialGroup()
                        .addComponent(prodSearchField)
                        .addGap(18, 18, 18)
                        .addComponent(clearProductFilterBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, customerDetailsPanelLayout.createSequentialGroup()
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(selectCtmBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        customerDetailsPanelLayout.setVerticalGroup(
            customerDetailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, customerDetailsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(customerDetailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(selectCtmBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jSeparator5, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addGroup(customerDetailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(prodSearchField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(clearProductFilterBtn))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        proceedBtn.setText("Proceed");
        proceedBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                proceedBtnActionPerformed(evt);
            }
        });

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel11.setText("Total Bill");

        totalBillField.setEditable(false);
        totalBillField.setFont(new java.awt.Font("MonospaceTypewriter", 0, 18)); // NOI18N
        totalBillField.setForeground(new java.awt.Color(0, 204, 0));
        totalBillField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                totalBillFieldActionPerformed(evt);
            }
        });

        dropBillBtn.setBackground(new java.awt.Color(255, 51, 0));
        dropBillBtn.setForeground(new java.awt.Color(242, 242, 242));
        dropBillBtn.setText("Drop bill");
        dropBillBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dropBillBtnActionPerformed(evt);
            }
        });

        finalTotalBillField.setEditable(false);
        finalTotalBillField.setFont(new java.awt.Font("Monospaced", 0, 36)); // NOI18N
        finalTotalBillField.setForeground(new java.awt.Color(0, 204, 51));
        finalTotalBillField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        finalTotalBillField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                finalTotalBillFieldActionPerformed(evt);
            }
        });

        jLabel16.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel16.setText("Final Bill");

        totalDiscountField.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        totalDiscountField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                totalDiscountFieldFocusLost(evt);
            }
        });
        totalDiscountField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                totalDiscountFieldActionPerformed(evt);
            }
        });
        totalDiscountField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                totalDiscountFieldKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                totalDiscountFieldKeyReleased(evt);
            }
        });

        jLabel15.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel15.setText("Discount");

        jLabel18.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel18.setText("Deposit");

        depositField.setForeground(new java.awt.Color(0, 204, 204));
        depositField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        depositField.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        depositField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                depositFieldFocusLost(evt);
            }
        });
        depositField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                depositFieldKeyReleased(evt);
            }
        });

        jLabel20.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel20.setText("Credit");

        creditField.setEditable(false);
        creditField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        creditField.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        creditField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                creditFieldFocusLost(evt);
            }
        });

        checkBtn.setText("Check");
        checkBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBtnActionPerformed(evt);
            }
        });

        jLabel21.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel21.setText("%");

        javax.swing.GroupLayout footerPanelLayout = new javax.swing.GroupLayout(footerPanel);
        footerPanel.setLayout(footerPanelLayout);
        footerPanelLayout.setHorizontalGroup(
            footerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(footerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(footerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(footerPanelLayout.createSequentialGroup()
                        .addComponent(jLabel15)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(totalDiscountField, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(footerPanelLayout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(totalBillField, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(checkBtn)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel16)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(finalTotalBillField, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(footerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(footerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(creditField, javax.swing.GroupLayout.DEFAULT_SIZE, 216, Short.MAX_VALUE)
                    .addComponent(depositField))
                .addGap(18, 18, 18)
                .addComponent(dropBillBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(proceedBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        footerPanelLayout.setVerticalGroup(
            footerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(finalTotalBillField)
            .addGroup(footerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(footerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(footerPanelLayout.createSequentialGroup()
                        .addGroup(footerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(proceedBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(dropBillBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(8, Short.MAX_VALUE))
                    .addGroup(footerPanelLayout.createSequentialGroup()
                        .addGroup(footerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(totalBillField, javax.swing.GroupLayout.DEFAULT_SIZE, 37, Short.MAX_VALUE)
                            .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(checkBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(footerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, footerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(totalDiscountField)
                                .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel15, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))))
            .addGroup(footerPanelLayout.createSequentialGroup()
                .addGroup(footerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jLabel18, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(depositField, javax.swing.GroupLayout.Alignment.LEADING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(footerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(creditField, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );

        javax.swing.GroupLayout ContentPanelLayout = new javax.swing.GroupLayout(ContentPanel);
        ContentPanel.setLayout(ContentPanelLayout);
        ContentPanelLayout.setHorizontalGroup(
            ContentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ContentPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(ContentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(ContentPanelLayout.createSequentialGroup()
                        .addComponent(billingDetailsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(customerDetailsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(footerPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        ContentPanelLayout.setVerticalGroup(
            ContentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ContentPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(ContentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(billingDetailsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(customerDetailsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(footerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jSeparator2)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(headerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, 18))))
            .addGroup(layout.createSequentialGroup()
                .addComponent(ContentPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(headerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ContentPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void billIdFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_billIdFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_billIdFieldActionPerformed

    private void addToBillBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addToBillBtnActionPerformed
        // TODO add your handling code here:
        addBillItemToBill();
    }//GEN-LAST:event_addToBillBtnActionPerformed

    private void proceedBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_proceedBtnActionPerformed
        // TODO add your handling code here:
        handleProceed();
    }//GEN-LAST:event_proceedBtnActionPerformed

    private void dropBillBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dropBillBtnActionPerformed
        // TODO add your handling code here:
        defaultSettings();
    }//GEN-LAST:event_dropBillBtnActionPerformed

    private void totalBillFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_totalBillFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_totalBillFieldActionPerformed

    private void addNewProductBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addNewProductBtnActionPerformed
        // TODO add your handling code here:
        AddNewProductDialog dialog = new AddNewProductDialog(null, true);
        dialog.setOnCloseListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateProductTable();
            }
        });
        dialog.setVisible(true);
    }//GEN-LAST:event_addNewProductBtnActionPerformed

    private void quantityFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_quantityFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_quantityFieldActionPerformed

    private void pieceRBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pieceRBActionPerformed
        // TODO add your handling code here:
        
    }//GEN-LAST:event_pieceRBActionPerformed

    private void weightRBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_weightRBActionPerformed
        // TODO add your handling code here:        
        
    }//GEN-LAST:event_weightRBActionPerformed

    private void quantityFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_quantityFieldKeyReleased
        // TODO add your handling code here:
        
    }//GEN-LAST:event_quantityFieldKeyReleased

    private void finalTotalBillFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_finalTotalBillFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_finalTotalBillFieldActionPerformed

    private void totalDiscountFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_totalDiscountFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_totalDiscountFieldActionPerformed

    private void prodDiscountField_RsKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_prodDiscountField_RsKeyReleased
        // TODO add your handling code here:
        if(!prodDiscountField_Percentage.getText().isBlank()){
            prodDiscountField_Percentage.setText("");
        }
        updateDiscount();
    }//GEN-LAST:event_prodDiscountField_RsKeyReleased

    private void prodDiscountField_PercentageKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_prodDiscountField_PercentageKeyReleased
        // TODO add your handling code here:
        if(!prodDiscountField_Rs.getText().isBlank()){
            prodDiscountField_Rs.setText("");
        }
        updateDiscount();
    }//GEN-LAST:event_prodDiscountField_PercentageKeyReleased

    private void totalDiscountFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_totalDiscountFieldFocusLost
        // TODO add your handling code here:
        if(totalDiscountField.getText().isBlank()){
            totalDiscountField.setText("0");
        }
    }//GEN-LAST:event_totalDiscountFieldFocusLost

    private void prodDiscountField_RsFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_prodDiscountField_RsFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_prodDiscountField_RsFocusLost

    private void quantityFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_quantityFieldFocusLost
        // TODO add your handling code here:
        if(quantityField.getText().isBlank()){
            quantityField.setText("0");
        }else
        if(!quantityField.getText().isBlank())
        try {
            updateQuantity(quantityField.getText());
        } catch (Exception ex) {
            ManagementSystemCPU.errorAlert(null,"No sufficient stock available","You don't have enough stock in the inventory.");
        }
    }//GEN-LAST:event_quantityFieldFocusLost

    private void totalDiscountFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_totalDiscountFieldKeyReleased
        // TODO add your handling code here:
        if(totalBill > 0){
            updateFinalBillField();
        }
    }//GEN-LAST:event_totalDiscountFieldKeyReleased

    private void depositFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_depositFieldFocusLost
        // TODO add your handling code here:
        if(depositField.getText().isBlank()){
            depositField.setText("0");
        }
    }//GEN-LAST:event_depositFieldFocusLost

    private void taxFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_taxFieldFocusLost
        // TODO add your handling code here:
        if(taxField.getText().isBlank()){
            taxField.setText("0");
        }
    }//GEN-LAST:event_taxFieldFocusLost

    private void creditFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_creditFieldFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_creditFieldFocusLost

    private void depositFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_depositFieldKeyReleased
        // TODO add your handling code here:
        if (Character.isDigit(evt.getKeyChar()) || evt.getKeyCode() == java.awt.event.KeyEvent.VK_BACK_SPACE) {
        // Ensure depositField is not blank
        if (!depositField.getText().isBlank()) {
            try {
                int credit = Integer.parseInt(finalTotalBillField.getText()) - Integer.parseInt(depositField.getText());
                creditField.setText(String.valueOf(credit));
            } catch (NumberFormatException e) {
                // Handle the case where the text fields do not contain valid integers
                System.out.println("Invalid number format in one of the fields");
            }
        }else{
            creditField.setText(finalTotalBillField.getText());
        }
    }
    }//GEN-LAST:event_depositFieldKeyReleased

    private void checkBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkBtnActionPerformed
        // TODO add your handling code here:
        updateTotalBill();
    }//GEN-LAST:event_checkBtnActionPerformed

    private void totalDiscountFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_totalDiscountFieldKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_totalDiscountFieldKeyPressed

    private void prodDiscountField_PercentageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_prodDiscountField_PercentageActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_prodDiscountField_PercentageActionPerformed

    private void prodSearchFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_prodSearchFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_prodSearchFieldActionPerformed

    private void selectCtmBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectCtmBtnActionPerformed
        // TODO add your handling code here:
        CustomerSelectionDialog dialog = new CustomerSelectionDialog(null, true);
        dialog.setOnCloseListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedCustomer = dialog.getSelectedCustomer();
            }
        });
        System.out.println(selectedCustomer);
        dialog.setVisible(true);
    }//GEN-LAST:event_selectCtmBtnActionPerformed

    private void clearProductFilterBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearProductFilterBtnActionPerformed
        // TODO add your handling code here:
        prodSearchField.setText("");
        filterProductTable();
    }//GEN-LAST:event_clearProductFilterBtnActionPerformed

    private void prodIDFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_prodIDFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_prodIDFieldActionPerformed

    private void prodNameFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_prodNameFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_prodNameFieldActionPerformed

    private void prodSearchFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_prodSearchFieldKeyReleased
        // TODO add your handling code here:
        filterProductTable();
    }//GEN-LAST:event_prodSearchFieldKeyReleased

    private void ratePerUnitFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_ratePerUnitFieldFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_ratePerUnitFieldFocusLost

    private void ratePerUnitFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ratePerUnitFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ratePerUnitFieldActionPerformed

    private void ratePerUnitFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ratePerUnitFieldKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_ratePerUnitFieldKeyReleased

    private void billDateChooserMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_billDateChooserMouseClicked
        // TODO add your handling code here:
        updateDate();
    }//GEN-LAST:event_billDateChooserMouseClicked

    private void billDateChooserPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_billDateChooserPropertyChange
        // TODO add your handling code here:
        updateDate();
    }//GEN-LAST:event_billDateChooserPropertyChange


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel ContentPanel;
    private javax.swing.JButton addNewProductBtn;
    private javax.swing.JButton addToBillBtn;
    private com.toedter.calendar.JDateChooser billDateChooser;
    private javax.swing.JTextField billIdField;
    private javax.swing.JTable billTable;
    private javax.swing.JPanel billingDetailsPanel;
    private javax.swing.JButton checkBtn;
    private javax.swing.JButton clearProductFilterBtn;
    private javax.swing.JFormattedTextField creditField;
    private javax.swing.JPanel customerDetailsPanel;
    private javax.swing.JFormattedTextField depositField;
    private javax.swing.JButton dropBillBtn;
    private javax.swing.JTextField finalTotalBillField;
    private javax.swing.JPanel footerPanel;
    private javax.swing.JPanel headerPanel;
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
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JRadioButton pieceRB;
    private javax.swing.JButton proceedBtn;
    private javax.swing.JFormattedTextField prodDiscountField_Percentage;
    private javax.swing.JFormattedTextField prodDiscountField_Rs;
    private javax.swing.JTextField prodIDField;
    private javax.swing.JTextField prodNameField;
    private javax.swing.JTextField prodSearchField;
    private javax.swing.JTable productTable;
    private javax.swing.JFormattedTextField quantityField;
    private javax.swing.JFormattedTextField ratePerUnitField;
    private javax.swing.JButton selectCtmBtn;
    private javax.swing.JLabel selectedCtmLabel;
    private javax.swing.JFormattedTextField taxField;
    private javax.swing.JTextField totalBillField;
    private javax.swing.JTextField totalDiscountField;
    private javax.swing.ButtonGroup unitSizeBtnGroup;
    private javax.swing.ButtonGroup unitTypeBtnGroup;
    private javax.swing.JRadioButton weightRB;
    // End of variables declaration//GEN-END:variables

}

