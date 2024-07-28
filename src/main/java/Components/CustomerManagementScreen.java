package Components;

import Controller.CustomerController;
import Model.Customer;
import Model.Bill;
import Model.Transaction;
import Controller.CustomerTransactionController;
import Controller.ManagementSystemCPU;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Timestamp;
import java.util.ArrayList;
import javax.swing.RowFilter;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author Anis Ur Rahman
 */
public class CustomerManagementScreen extends javax.swing.JPanel {

    /**
     * Creates new form NewJPanel
     */
    Customer selectedCustomer;
    public CustomerManagementScreen() {
        initComponents();
        addCustomerTableSelectionListener();
        addBillTableSelectionListener();
        defaultSettings();
    }
    
    private void defaultSettings(){
        ctmAddressField.setEnabled(false);
        ctmContactField.setEnabled(false);
        updateCustomerDetailsBtn.setEnabled(false);
        lastDepostField.setText("-");
        lastDepositDateLabel.setText("");
        remainingCreditField.setText("0");
        totalCreditField.setText("");
        totalDepositField.setText("");
        updateCustomerTable();
        updateCustomerBillsTable();
    }

    private void updateCustomerTable(){
        DefaultTableModel model = (DefaultTableModel) customerTable.getModel();
        model.setRowCount(0);
        ArrayList<Customer> customers = Customer.getCustomerList();
        int count = 0;
        for(Customer ctm: customers){
            Object row[]={++count,ctm.getName(),ctm.getContact(),ctm.getAddress()};
            model.addRow(row);
        }
    }
    
    private void updateCustomerBillsTable(){
        if(selectedCustomer == null){
            DefaultTableModel model = (DefaultTableModel)billsTable.getModel();
            model.setRowCount(0);
            return;
        }
        DefaultTableModel model = (DefaultTableModel) billsTable.getModel();
        model.setRowCount(0);
        ArrayList<Bill> bills = Bill.getBillsList(selectedCustomer);
        int count = 0;
        for(Bill bill: bills){
            Object row[]={++count,bill.getDate(),bill.getBillId(),bill.getTotalFinalBill(),bill.getDeposit(), bill.getCredit()};
            model.addRow(row);
        }
        updateTotalCreditField();
        updateRemainingCreditField();
        updateLastDepositField();
    }
    
    private void updateTotalCreditField(){
        DefaultTableModel model = (DefaultTableModel) billsTable.getModel();
        int totalCredit = 0;
        for(int i=0;i<model.getRowCount();i++){
            totalCredit+=(int)model.getValueAt(i, 5);
        }
        totalCreditField.setText(String.valueOf(totalCredit));
    }
    
    private void updateRemainingCreditField(){
        int remainingCredit = getOldCredit();
        remainingCreditField.setText(String.valueOf(remainingCredit));
    }
    
    private void updateLastDepositField(){
        Transaction lastTr = CustomerTransactionController.getLastDeposit(selectedCustomer);
        if(lastTr != null){
            lastDepostField.setText(String.valueOf(lastTr.getDeposit()));
            lastDepositDateLabel.setText(String.valueOf(lastTr.getDate()));
            depositField.setText("0");
            return;
        }
            lastDepostField.setText("-");
            lastDepositDateLabel.setText("");        
    }
    
    private void filterCustomerTable() {
        String input = ctmSearchField.getText().trim().toLowerCase();
        DefaultTableModel model = (DefaultTableModel) customerTable.getModel();
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        customerTable.setRowSorter(sorter);

        RowFilter<DefaultTableModel, Object> rf = new RowFilter<DefaultTableModel, Object>() {
            @Override
            public boolean include(RowFilter.Entry<? extends DefaultTableModel, ? extends Object> entry) {
                String name = entry.getStringValue(1).toLowerCase();
                String contact = entry.getStringValue(2).toLowerCase();
                return name.contains(input) || contact.contains(input);
            }
        };
        sorter.setRowFilter(rf);
    }
    
    private void filterBillTable() {
        String input = billSearchField.getText().trim().toLowerCase();
        DefaultTableModel model = (DefaultTableModel) billsTable.getModel();
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        billsTable.setRowSorter(sorter);

        RowFilter<DefaultTableModel, Object> rf = new RowFilter<DefaultTableModel, Object>() {
            @Override
            public boolean include(RowFilter.Entry<? extends DefaultTableModel, ? extends Object> entry) {
                String name = entry.getStringValue(1).toLowerCase();
                String contact = entry.getStringValue(2).toLowerCase();
                return name.contains(input) || contact.contains(input);
            }
        };
        sorter.setRowFilter(rf);
    }
    
    private void addCustomerTableSelectionListener() {
        customerTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) { // Ensure that this event is not fired multiple times
                    int selectedRow = customerTable.getSelectedRow();
                    if (selectedRow != -1) {
                        selectedRow = customerTable.convertRowIndexToModel(selectedRow); // Convert to model index if filtered
                        DefaultTableModel model = (DefaultTableModel) customerTable.getModel();
                        String name = model.getValueAt(selectedRow, 1).toString();
                        String contact = model.getValueAt(selectedRow, 2).toString();
                        String address = model.getValueAt(selectedRow, 3).toString();
                        handleSelectedCustomer(name, contact, address);
                    }
                }
            }
        });
    }

    private void addBillTableSelectionListener() {
        billsTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) { // Ensure that this event is not fired multiple times
                    int selectedRow = billsTable.getSelectedRow();
                    if (selectedRow != -1) {
                        selectedRow = billsTable.convertRowIndexToModel(selectedRow); // Convert to model index if filtered
                        DefaultTableModel model = (DefaultTableModel) billsTable.getModel();
                        String billId = model.getValueAt(selectedRow, 2).toString();
                        System.out.println(billId);
                        BillDisplayDialog billDialog = new BillDisplayDialog(null, true, billId, selectedCustomer);
                        billDialog.setOnCloseListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                updateCustomerBillsTable();
                            }
                        });
                        billDialog.setVisible(true);
                    }
                }
            }
        });
    }
    
    private void handleSelectedCustomer(String name, String contact, String address) {
        // Handle the selected customer data as needed
        selectedCustomer = new Customer(name,contact,address);
        updateSelectedCustomer(selectedCustomer);
    }
    
    private void updateSelectedCustomer(Customer ctm){
        if(ctm == null){
            ctmNameField.setText("");
            ctmContactField.setText("");
            ctmAddressField.setText("");
            selectedCustomer = null;
        }else{
            ctmNameField.setText(ctm.getName());
            ctmContactField.setText(ctm.getContact());
            ctmAddressField.setText(ctm.getAddress());
            updateCustomerBillsTable();
        }
    }
    
    private void allowCtmDetailsUpdate(boolean flag){
            ctmContactField.setEnabled(flag);
            ctmAddressField.setEnabled(flag);
            updateCustomerDetailsBtn.setEnabled(flag);
    }
    
    private void addDeposit(){
        if(depositField.getText().isBlank()){
            ManagementSystemCPU.errorAlert(this,"Validation Failed","Cannot proceed with empty deposit field");
            return;
        }
        Transaction tr;
        Timestamp timestampDate = new Timestamp(new java.util.Date().getTime());
        int oldCredit = Integer.valueOf(remainingCreditField.getText());
        int deposit = Integer.valueOf(depositField.getText());
        int remainingCredit = oldCredit - deposit;
        tr = new Transaction(selectedCustomer, timestampDate, oldCredit, deposit, remainingCredit);
        boolean success = CustomerTransactionController.addTransaction(tr);
        if(success){
            ManagementSystemCPU.informationAlert(this, "Transaction Successful", selectedCustomer.getName().concat(" deposited: " +deposit+" Rs."));
            updateRemainingCreditField();
            updateLastDepositField();
        }else{
            ManagementSystemCPU.errorAlert(this,"Transaction Failed", "Failed to record the transaction");
        }
    }
    
    private int getOldCredit(){
        int oldCredit = 0;
        int totalPrevDeposit = CustomerTransactionController.getTotalDeposit(selectedCustomer);
        totalDepositField.setText(String.valueOf(totalPrevDeposit));
        if(totalPrevDeposit==0){
            DefaultTableModel model = (DefaultTableModel) billsTable.getModel();
            for(int i=0;i<model.getRowCount();i++){
                oldCredit += (int) model.getValueAt(i, 5);
            }
        }else{
            Transaction lastTransaction = CustomerTransactionController.getLastDeposit(selectedCustomer);
            oldCredit = lastTransaction.getRemainingCredit();
        }
        return oldCredit;
    }
    
    private void deselectCustomer(){
        updateSelectedCustomer(null);
        defaultSettings();
    }
    
    private void updateCustomerDetails(){
        if(ctmAddressField.getText().isBlank() || ctmContactField.getText().isBlank()){
            ManagementSystemCPU.errorAlert(this,"Validation Error","Address and Contact fields cannot be empty");
            return;
        }
        Customer newCtm = new Customer(selectedCustomer.getName(),ctmAddressField.getText().trim(),ctmContactField.getText().trim());
        boolean success = CustomerController.updateCustomer(newCtm);
        if(success){
            ManagementSystemCPU.informationAlert(this,"Customer updated","Customer "+newCtm.getName()+" was updated");
            updateCustomerTable();
        }else{
            ManagementSystemCPU.errorAlert(this,"Request failed","Customer was not updated");
        }
    }
    
    private void viewTransactionRecord(){
        TransactionHistoryDialog historyDialog = new TransactionHistoryDialog(null, true, selectedCustomer);
        historyDialog.setVisible(true);
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0));
        jPanel1 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        customerTable = new javax.swing.JTable();
        ctmSearchField = new javax.swing.JTextField();
        deselectCustomerBtn = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jSeparator3 = new javax.swing.JSeparator();
        jLabel6 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        ctmNameField = new javax.swing.JTextField();
        ctmAddressField = new javax.swing.JTextField();
        ctmContactField = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jSeparator4 = new javax.swing.JSeparator();
        updateCustomerDetailsBtn = new javax.swing.JButton();
        allowInfoUpdateCB = new javax.swing.JCheckBox();
        jScrollPane3 = new javax.swing.JScrollPane();
        billsTable = new javax.swing.JTable();
        billSearchField = new javax.swing.JTextField();
        resetBillsTableBtn = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        resetBtn = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        totalDepositField = new javax.swing.JTextField();
        addDepositBtn = new javax.swing.JButton();
        viewHistoryBtn = new javax.swing.JButton();
        jLabel12 = new javax.swing.JLabel();
        depositField = new javax.swing.JFormattedTextField();
        jLabel18 = new javax.swing.JLabel();
        totalCreditField = new javax.swing.JTextField();
        lastDepositDateLabel = new javax.swing.JLabel();
        remainingCreditField = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        lastDepostField = new javax.swing.JTextField();

        customerTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "S/No.", "Customer ID", "Contact", "Address"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(customerTable);

        ctmSearchField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ctmSearchFieldActionPerformed(evt);
            }
        });
        ctmSearchField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                ctmSearchFieldKeyReleased(evt);
            }
        });

        deselectCustomerBtn.setText("X");
        deselectCustomerBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deselectCustomerBtnActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel5.setText("Customer ID:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(ctmSearchField, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(deselectCustomerBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(ctmSearchField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(deselectCustomerBtn))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 217, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel7.setText("Customer Deatils");

        jLabel6.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel6.setText("Name:");

        jLabel8.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel8.setText("Address:");

        jLabel9.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel9.setText("Contact:");

        ctmNameField.setEnabled(false);
        ctmNameField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ctmNameFieldActionPerformed(evt);
            }
        });
        ctmNameField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                ctmNameFieldKeyReleased(evt);
            }
        });

        ctmAddressField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ctmAddressFieldActionPerformed(evt);
            }
        });
        ctmAddressField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                ctmAddressFieldKeyReleased(evt);
            }
        });

        ctmContactField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ctmContactFieldActionPerformed(evt);
            }
        });
        ctmContactField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                ctmContactFieldKeyReleased(evt);
            }
        });

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel10.setText("Customer Records");

        updateCustomerDetailsBtn.setText("Update");
        updateCustomerDetailsBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateCustomerDetailsBtnActionPerformed(evt);
            }
        });

        allowInfoUpdateCB.setText("Allow Info Update");
        allowInfoUpdateCB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                allowInfoUpdateCBActionPerformed(evt);
            }
        });

        billsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "S/No.", "Date", "Bill ID", "Total Bill", "Deposit", "Credit"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class
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
        jScrollPane3.setViewportView(billsTable);
        if (billsTable.getColumnModel().getColumnCount() > 0) {
            billsTable.getColumnModel().getColumn(3).setResizable(false);
            billsTable.getColumnModel().getColumn(4).setResizable(false);
            billsTable.getColumnModel().getColumn(5).setResizable(false);
        }

        billSearchField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                billSearchFieldActionPerformed(evt);
            }
        });
        billSearchField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                billSearchFieldKeyReleased(evt);
            }
        });

        resetBillsTableBtn.setText("X");
        resetBillsTableBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetBillsTableBtnActionPerformed(evt);
            }
        });

        jLabel11.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel11.setText("Search Bill:");

        resetBtn.setText("Reset");
        resetBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetBtnActionPerformed(evt);
            }
        });

        totalDepositField.setEditable(false);
        totalDepositField.setFont(new java.awt.Font("MonospaceTypewriter", 0, 18)); // NOI18N
        totalDepositField.setForeground(new java.awt.Color(0, 204, 0));
        totalDepositField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                totalDepositFieldActionPerformed(evt);
            }
        });

        addDepositBtn.setText("Add deposit");
        addDepositBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addDepositBtnActionPerformed(evt);
            }
        });

        viewHistoryBtn.setText("View History");
        viewHistoryBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewHistoryBtnActionPerformed(evt);
            }
        });

        jLabel12.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel12.setText("Total Remaining Credit");

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

        jLabel18.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel18.setText("Deposit");

        totalCreditField.setEditable(false);
        totalCreditField.setFont(new java.awt.Font("MonospaceTypewriter", 0, 18)); // NOI18N
        totalCreditField.setForeground(new java.awt.Color(0, 204, 0));
        totalCreditField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                totalCreditFieldActionPerformed(evt);
            }
        });

        lastDepositDateLabel.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        remainingCreditField.setEditable(false);
        remainingCreditField.setFont(new java.awt.Font("MonospaceTypewriter", 0, 18)); // NOI18N
        remainingCreditField.setForeground(new java.awt.Color(0, 204, 0));
        remainingCreditField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                remainingCreditFieldActionPerformed(evt);
            }
        });

        jLabel16.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel16.setText("Total Deposit");

        jLabel15.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel15.setText("Total Credit");

        jLabel13.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel13.setText("Last Deposit");

        lastDepostField.setEditable(false);
        lastDepostField.setFont(new java.awt.Font("MonospaceTypewriter", 0, 18)); // NOI18N
        lastDepostField.setForeground(new java.awt.Color(0, 204, 0));
        lastDepostField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lastDepostFieldActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lastDepositDateLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGap(0, 6, Short.MAX_VALUE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(totalCreditField, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(totalDepositField, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(remainingCreditField, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lastDepostField, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(viewHistoryBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(16, 16, 16)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(addDepositBtn)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(depositField, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(depositField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(4, 4, 4)))
                        .addComponent(addDepositBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(40, 40, 40))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(remainingCreditField)
                                    .addComponent(jLabel12, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(lastDepostField)))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(totalCreditField)
                                    .addComponent(jLabel15, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(totalDepositField))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(lastDepositDateLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(viewHistoryBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap())))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator4)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(resetBtn))
                            .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 406, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 77, Short.MAX_VALUE))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(allowInfoUpdateCB)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(updateCustomerDetailsBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(ctmNameField, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(ctmAddressField, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(ctmContactField, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane3)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(billSearchField, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(resetBillsTableBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(resetBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(ctmNameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel8)
                            .addComponent(ctmAddressField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel9)
                            .addComponent(ctmContactField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(updateCustomerDetailsBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(allowInfoUpdateCB))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(billSearchField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(resetBillsTableBtn)
                    .addComponent(jLabel11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 248, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(12, 12, 12))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void ctmSearchFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ctmSearchFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ctmSearchFieldActionPerformed

    private void ctmSearchFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ctmSearchFieldKeyReleased
        // TODO add your handling code here:
        filterCustomerTable();
    }//GEN-LAST:event_ctmSearchFieldKeyReleased

    private void deselectCustomerBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deselectCustomerBtnActionPerformed
        // TODO add your handling code here:
        ctmSearchField.setText("");
        filterCustomerTable();
    }//GEN-LAST:event_deselectCustomerBtnActionPerformed

    private void ctmNameFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ctmNameFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ctmNameFieldActionPerformed

    private void ctmNameFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ctmNameFieldKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_ctmNameFieldKeyReleased

    private void ctmAddressFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ctmAddressFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ctmAddressFieldActionPerformed

    private void ctmAddressFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ctmAddressFieldKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_ctmAddressFieldKeyReleased

    private void ctmContactFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ctmContactFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ctmContactFieldActionPerformed

    private void ctmContactFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ctmContactFieldKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_ctmContactFieldKeyReleased

    private void updateCustomerDetailsBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateCustomerDetailsBtnActionPerformed
        // TODO add your handling code here:
        updateCustomerDetails();
    }//GEN-LAST:event_updateCustomerDetailsBtnActionPerformed

    private void billSearchFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_billSearchFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_billSearchFieldActionPerformed

    private void billSearchFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_billSearchFieldKeyReleased
        // TODO add your handling code here:
        filterBillTable();
    }//GEN-LAST:event_billSearchFieldKeyReleased

    private void resetBillsTableBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetBillsTableBtnActionPerformed
        // TODO add your handling code here:
        billSearchField.setText("");
        filterBillTable();
    }//GEN-LAST:event_resetBillsTableBtnActionPerformed

    private void remainingCreditFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_remainingCreditFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_remainingCreditFieldActionPerformed

    private void depositFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_depositFieldFocusLost
        // TODO add your handling code here:
        if(depositField.getText().isBlank()){
            depositField.setText("0");
        }
    }//GEN-LAST:event_depositFieldFocusLost

    private void depositFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_depositFieldKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_depositFieldKeyReleased

    private void lastDepostFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lastDepostFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_lastDepostFieldActionPerformed

    private void viewHistoryBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewHistoryBtnActionPerformed
        // TODO add your handling code here:
        viewTransactionRecord();
    }//GEN-LAST:event_viewHistoryBtnActionPerformed

    private void addDepositBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addDepositBtnActionPerformed
        // TODO add your handling code here:
        addDeposit();
    }//GEN-LAST:event_addDepositBtnActionPerformed

    private void allowInfoUpdateCBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_allowInfoUpdateCBActionPerformed
        // TODO add your handling code here:
        if(allowInfoUpdateCB == null){
            return;
        }
        
        if(allowInfoUpdateCB.isSelected()){
            allowCtmDetailsUpdate(true);
        }else{
            allowCtmDetailsUpdate(false);
        }
    }//GEN-LAST:event_allowInfoUpdateCBActionPerformed

    private void totalCreditFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_totalCreditFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_totalCreditFieldActionPerformed

    private void totalDepositFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_totalDepositFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_totalDepositFieldActionPerformed

    private void resetBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetBtnActionPerformed
        // TODO add your handling code here:
        deselectCustomer();
    }//GEN-LAST:event_resetBtnActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addDepositBtn;
    private javax.swing.JCheckBox allowInfoUpdateCB;
    private javax.swing.JTextField billSearchField;
    private javax.swing.JTable billsTable;
    private javax.swing.JTextField ctmAddressField;
    private javax.swing.JTextField ctmContactField;
    private javax.swing.JTextField ctmNameField;
    private javax.swing.JTextField ctmSearchField;
    private javax.swing.JTable customerTable;
    private javax.swing.JFormattedTextField depositField;
    private javax.swing.JButton deselectCustomerBtn;
    private javax.swing.Box.Filler filler1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JLabel lastDepositDateLabel;
    private javax.swing.JTextField lastDepostField;
    private javax.swing.JTextField remainingCreditField;
    private javax.swing.JButton resetBillsTableBtn;
    private javax.swing.JButton resetBtn;
    private javax.swing.JTextField totalCreditField;
    private javax.swing.JTextField totalDepositField;
    private javax.swing.JButton updateCustomerDetailsBtn;
    private javax.swing.JButton viewHistoryBtn;
    // End of variables declaration//GEN-END:variables
}
