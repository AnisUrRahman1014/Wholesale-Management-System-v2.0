package Components.ReportManagement;

import Components.EmployeeManagement.DateRangeDialog;
import Controller.ManagementSystemCPU;
import Controller.ReportController;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import Model.Product;
import Model.Report;
import java.sql.Date;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;
/**
 *
 * @author Anis Ur Rahman
 */
public class ReportMenu extends javax.swing.JPanel {
    public static final int TODAY = 0;
    public static final int LAST_WEEK = 1;
    public static final int LAST_MONTH = 2;
    public static final int RANGE = 3;
    public static final int NO_RANGE_SELECTED = -1;
    Product selectedProduct = null;    
    Report report;
    String reportType;
    Date fromDate;
    Date toDate;
    
    ArrayList<Report> reportList;
    /**
     * Creates new form ReportMenu
     */
    public ReportMenu() {
        initComponents();
        groupRadioButtons();
        defaultSettings();
    }
    
    private void groupRadioButtons(){
        reportTypeRBGroup.add(productRB);
        reportTypeRBGroup.add(completeRB);
    }
    
    private void defaultSettings(){
        customRangeCB.setSelected(false);
        handleCustomRangeCB();
        generateBtn.setEnabled(false);
        emptyFields();
    }
    
    private void updateReportTable(){
        try {            
            DefaultTableModel model = (DefaultTableModel) reportTable.getModel();
            model.setRowCount(0);
            int count = 0;
            for(Report r: reportList){
                count++;
                Object row[]={count,r.getProductName(),r.getTotalQuantitySold(),r.getAvgCostPerUnit(),r.getSoldCost(),r.getAvgSalePerUnit(),r.getTotalSale(),(r.getGrossProfit()*100)};
                model.addRow(row);
            }
        } catch (Exception ex) {
            Logger.getLogger(ReportMenu.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    private void emptyFields(){
        reportType = null;
        report = null;
        fromDate = null;
        toDate = null;
        selectedProduct = null;
        
        //DESELECT RADIO BUTTONS
//        productRB.setSelected(false);
//        completeRB.setSelected(false);
        reportTypeRBGroup.clearSelection();
        
        // EMPTY FIELDS
        prodNameField.setText("");
        reportTypeField.setText("");
        availableCostField.setText("");
        availableQuantityField.setText("");
        avgCostPerUnitField.setText("");
        avgSalePerUnitField.setText("");
        totalSaleField.setText("");
        soldCostField.setText("");
        grossProfitField.setText("");
        soldQuantityField.setText("");
    }
    
    private void allowRangeButtons(boolean flag){
        todayBtn.setEnabled(flag);
        lastWeekBtn.setEnabled(flag);
        lastMonthBtn.setEnabled(flag);
    }
    
    private void handleCustomRangeCB(){
        if(customRangeCB.isSelected()){
            if(!validateReport()){
                productRB.requestFocus();
                customRangeCB.setSelected(false);
                ManagementSystemCPU.errorAlert(null,"Error generating report","Please choose a report type first");
                return;
            }            
            handleReportRange(RANGE);
            fromDateField.setValue(fromDate);
            toDateField.setValue(toDate);
            allowRangeButtons(false);
        }else{
            if(!validateReport()){
                return;
            }
            fromDateField.setValue(null);
            toDateField.setValue(null);
            handleReportRange(NO_RANGE_SELECTED);
            allowRangeButtons(true);
        }
    }
    
    private boolean validateReport(){
        return report!=null;
    }
    
    private boolean validateDates(){
        return (customRangeCB.isSelected() && fromDate != null && toDate!=null);
    }
    
    private void handleReportRange(int range){
        if(!validateReport()){
            ManagementSystemCPU.errorAlert(null,"Error generating report","Please choose a report type first");
            return;
        }
        report.setReportRange(range);
        switch(range){
            case TODAY->{
                updateReportTypeField("Today i.e., "+new Date(new java.util.Date().getTime()));
            }
            case LAST_WEEK->{
                updateReportTypeField("Last 7 days");
            }
            case LAST_MONTH->{
                updateReportTypeField("Last 30 days");
            }
            case RANGE->{
                DateRangeDialog dialog = new DateRangeDialog(null, true);
                dialog.setOnCloseListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                       fromDate = dialog.getFromDate();
                       toDate = dialog.getToDate();                       
                    }
                });
                dialog.setVisible(true);
                if(!validateDates()){
                    customRangeCB.setSelected(false);
                    ManagementSystemCPU.errorAlert(null,"Error generating report","Please choose a proper range");
                    return;
                }
                updateReportTypeField("Range");  
                report.setFromDate(fromDate);
                report.setToDate(toDate);
            }
            case NO_RANGE_SELECTED->{
                updateReportTypeField("");
                generateBtn.setEnabled(false);
                return;
            }
        }
        generateBtn.setEnabled(true);
    }
    
    private void updateReportTypeField(String reportRange){
        String temp = reportType;
        temp = reportType.concat(" | "+reportRange);
        reportTypeField.setText(temp);
    }
    
    private void showProductSelectionDialog(){
        ProductSelectionDialog dialog = new ProductSelectionDialog(null, true);
        dialog.setOnCloseListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedProduct = dialog.getSelectedProduct();
            }
        });
        dialog.setVisible(true);
    }
    
    private void handleProductReportType(){
        reportType = Report.PRODUCT_REPORT;
        showProductSelectionDialog();
        if (selectedProduct!=null){
            prodNameField.setText(selectedProduct.getProdName());
            report = new Report(reportType);
            report.setProductName(selectedProduct.getProdName());
            reportTypeField.setText(report.getReportType());
        }else{
            ManagementSystemCPU.errorAlert(null,"No product selected", "Please select a product first");
            reportTypeRBGroup.clearSelection();
        }
       }
    
    private void handleCompleteReportType(){
        reportType = Report.COMPLETE_REPORT;
        report = new Report(reportType);
        report.setProductName("---");
        reportTypeField.setValue(report.getReportType());
    }
    
    private void generateReport(){
        if(validateReport()){
            try {
                reportList = ReportController.generateReport(report);
                updateReportTable();
                Report summaryReport = ReportController.getReportSummary();
                availableCostField.setValue(summaryReport.getAvailableCost());
                availableQuantityField.setValue(summaryReport.getAvailableQuantity());
                avgCostPerUnitField.setValue(summaryReport.getAvgCostPerUnit());
                
                soldCostField.setValue(summaryReport.getSoldCost());
                soldQuantityField.setValue(summaryReport.getTotalQuantitySold());
                avgSalePerUnitField.setValue(summaryReport.getAvgSalePerUnit());
                totalSaleField.setValue(summaryReport.getTotalSale());                
                grossProfitField.setValue(summaryReport.getGrossProfit());
            } catch (Exception ex) {
                Logger.getLogger(ReportMenu.class.getName()).log(Level.SEVERE, null, ex);
            }
        }else{
            ManagementSystemCPU.errorAlert(null,"Error generating report", "Cannot find a report instance.");
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

        reportTypeRBGroup = new javax.swing.ButtonGroup();
        jLabel9 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jSeparator3 = new javax.swing.JSeparator();
        jLabel10 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jSeparator4 = new javax.swing.JSeparator();
        jLabel11 = new javax.swing.JLabel();
        lastWeekBtn = new javax.swing.JButton();
        todayBtn = new javax.swing.JButton();
        lastMonthBtn = new javax.swing.JButton();
        productRB = new javax.swing.JRadioButton();
        completeRB = new javax.swing.JRadioButton();
        jSeparator5 = new javax.swing.JSeparator();
        customRangeCB = new javax.swing.JCheckBox();
        generateBtn = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        jSeparator6 = new javax.swing.JSeparator();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        reportTypeField = new javax.swing.JFormattedTextField();
        prodNameField = new javax.swing.JFormattedTextField();
        availableQuantityField = new javax.swing.JFormattedTextField();
        availableCostField = new javax.swing.JFormattedTextField();
        soldQuantityField = new javax.swing.JFormattedTextField();
        totalSaleField = new javax.swing.JFormattedTextField();
        grossProfitField = new javax.swing.JFormattedTextField();
        avgSalePerUnitField = new javax.swing.JFormattedTextField();
        avgCostPerUnitField = new javax.swing.JFormattedTextField();
        generateBtn1 = new javax.swing.JButton();
        jSeparator7 = new javax.swing.JSeparator();
        jSeparator8 = new javax.swing.JSeparator();
        jLabel22 = new javax.swing.JLabel();
        soldCostField = new javax.swing.JFormattedTextField();
        clearBtn = new javax.swing.JButton();
        fromDateField = new javax.swing.JFormattedTextField();
        toDateField = new javax.swing.JFormattedTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        reportTable = new javax.swing.JTable();

        jLabel9.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel9.setText("Choose:");

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel7.setText("Report Type ");

        jLabel10.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel10.setText("From:");

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel8.setText("Choose Date/s");

        jLabel11.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel11.setText("To:");

        lastWeekBtn.setText("Last 7 Days");
        lastWeekBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lastWeekBtnActionPerformed(evt);
            }
        });

        todayBtn.setText("Today");
        todayBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                todayBtnActionPerformed(evt);
            }
        });

        lastMonthBtn.setText("Last 30 Days");
        lastMonthBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lastMonthBtnActionPerformed(evt);
            }
        });

        productRB.setText("Product");
        productRB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                productRBActionPerformed(evt);
            }
        });

        completeRB.setText("Complete");
        completeRB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                completeRBActionPerformed(evt);
            }
        });

        customRangeCB.setText("Custom Range");
        customRangeCB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                customRangeCBActionPerformed(evt);
            }
        });

        generateBtn.setText("Generate");
        generateBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                generateBtnActionPerformed(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel12.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel12.setText("Report Details");

        jLabel13.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel13.setText("Report Type");

        jLabel14.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel14.setText("Product/s");

        jLabel15.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel15.setText("Available Quant. / Products");

        jLabel16.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel16.setText("Avg. Cost / Unit");

        jLabel17.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel17.setText("Worth");

        jLabel18.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel18.setText("Sold Quantity / Products");

        jLabel19.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel19.setText("Avg. Sale / Unit");

        jLabel20.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel20.setText("Total Sale");

        jLabel21.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel21.setText("Gross Profit");

        reportTypeField.setEditable(false);
        reportTypeField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        reportTypeField.setEnabled(false);
        reportTypeField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                reportTypeFieldFocusLost(evt);
            }
        });
        reportTypeField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reportTypeFieldActionPerformed(evt);
            }
        });
        reportTypeField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                reportTypeFieldKeyReleased(evt);
            }
        });

        prodNameField.setEditable(false);
        prodNameField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        prodNameField.setEnabled(false);
        prodNameField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                prodNameFieldFocusLost(evt);
            }
        });
        prodNameField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                prodNameFieldActionPerformed(evt);
            }
        });
        prodNameField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                prodNameFieldKeyReleased(evt);
            }
        });

        availableQuantityField.setEditable(false);
        availableQuantityField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,###.00"))));
        availableQuantityField.setEnabled(false);
        availableQuantityField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                availableQuantityFieldFocusLost(evt);
            }
        });
        availableQuantityField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                availableQuantityFieldActionPerformed(evt);
            }
        });
        availableQuantityField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                availableQuantityFieldKeyReleased(evt);
            }
        });

        availableCostField.setEditable(false);
        availableCostField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0"))));
        availableCostField.setEnabled(false);
        availableCostField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                availableCostFieldFocusLost(evt);
            }
        });
        availableCostField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                availableCostFieldActionPerformed(evt);
            }
        });
        availableCostField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                availableCostFieldKeyReleased(evt);
            }
        });

        soldQuantityField.setEditable(false);
        soldQuantityField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,###.00"))));
        soldQuantityField.setEnabled(false);
        soldQuantityField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                soldQuantityFieldFocusLost(evt);
            }
        });
        soldQuantityField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                soldQuantityFieldActionPerformed(evt);
            }
        });
        soldQuantityField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                soldQuantityFieldKeyReleased(evt);
            }
        });

        totalSaleField.setEditable(false);
        totalSaleField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0"))));
        totalSaleField.setEnabled(false);
        totalSaleField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                totalSaleFieldFocusLost(evt);
            }
        });
        totalSaleField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                totalSaleFieldActionPerformed(evt);
            }
        });
        totalSaleField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                totalSaleFieldKeyReleased(evt);
            }
        });

        grossProfitField.setEditable(false);
        grossProfitField.setForeground(new java.awt.Color(102, 255, 51));
        grossProfitField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.00%"))));
        grossProfitField.setEnabled(false);
        grossProfitField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                grossProfitFieldFocusLost(evt);
            }
        });
        grossProfitField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                grossProfitFieldActionPerformed(evt);
            }
        });
        grossProfitField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                grossProfitFieldKeyReleased(evt);
            }
        });

        avgSalePerUnitField.setEditable(false);
        avgSalePerUnitField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0"))));
        avgSalePerUnitField.setEnabled(false);
        avgSalePerUnitField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                avgSalePerUnitFieldFocusLost(evt);
            }
        });
        avgSalePerUnitField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                avgSalePerUnitFieldActionPerformed(evt);
            }
        });
        avgSalePerUnitField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                avgSalePerUnitFieldKeyReleased(evt);
            }
        });

        avgCostPerUnitField.setEditable(false);
        avgCostPerUnitField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0"))));
        avgCostPerUnitField.setEnabled(false);
        avgCostPerUnitField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                avgCostPerUnitFieldFocusLost(evt);
            }
        });
        avgCostPerUnitField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                avgCostPerUnitFieldActionPerformed(evt);
            }
        });
        avgCostPerUnitField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                avgCostPerUnitFieldKeyReleased(evt);
            }
        });

        generateBtn1.setText("Save PDF");

        jLabel22.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel22.setText("Total Cost");

        soldCostField.setEditable(false);
        soldCostField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0"))));
        soldCostField.setEnabled(false);
        soldCostField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                soldCostFieldFocusLost(evt);
            }
        });
        soldCostField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                soldCostFieldActionPerformed(evt);
            }
        });
        soldCostField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                soldCostFieldKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(grossProfitField)
                        .addGap(426, 426, 426))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(generateBtn1, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(124, 124, 124))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jSeparator6, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jSeparator7, javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel15, javax.swing.GroupLayout.DEFAULT_SIZE, 191, Short.MAX_VALUE)
                                    .addComponent(jLabel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(availableCostField, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(availableQuantityField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel16)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(avgCostPerUnitField, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(113, 113, 113))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(reportTypeField))
                                .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(prodNameField, javax.swing.GroupLayout.PREFERRED_SIZE, 329, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel18, javax.swing.GroupLayout.DEFAULT_SIZE, 191, Short.MAX_VALUE)
                                    .addComponent(jLabel22, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(soldQuantityField, javax.swing.GroupLayout.DEFAULT_SIZE, 118, Short.MAX_VALUE)
                                    .addComponent(soldCostField))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel20)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(totalSaleField))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(avgSalePerUnitField, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addComponent(jSeparator8))
                        .addGap(113, 113, 113))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator6, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel13)
                            .addComponent(reportTypeField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel14)
                            .addComponent(prodNameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel15)
                            .addComponent(availableQuantityField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel16)
                            .addComponent(avgCostPerUnitField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel17)
                            .addComponent(availableCostField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator7, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(2, 2, 2)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel18)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel19)
                                .addComponent(avgSalePerUnitField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel20)
                            .addComponent(totalSaleField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel22)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(soldQuantityField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(soldCostField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel21)
                    .addComponent(grossProfitField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(3, 3, 3)
                .addComponent(jSeparator8, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(generateBtn1, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        clearBtn.setText("Clear");
        clearBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearBtnActionPerformed(evt);
            }
        });

        fromDateField.setEditable(false);
        fromDateField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.DateFormatter()));
        fromDateField.setEnabled(false);

        toDateField.setEditable(false);
        toDateField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.DateFormatter()));
        toDateField.setEnabled(false);
        toDateField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toDateFieldActionPerformed(evt);
            }
        });

        reportTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "S/No.", "Product", "Unit/s Sold", "Avg. C/U", "Total Cost", "Avg. S/U", "Total Sale", "Profit %"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class
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
        reportTable.setRowHeight(30);
        reportTable.setShowGrid(true);
        jScrollPane1.setViewportView(reportTable);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(fromDateField, javax.swing.GroupLayout.PREFERRED_SIZE, 217, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(customRangeCB))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(toDateField, javax.swing.GroupLayout.PREFERRED_SIZE, 217, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(productRB)
                        .addGap(18, 18, 18)
                        .addComponent(completeRB))
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(clearBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addComponent(generateBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 550, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jSeparator3, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jSeparator4, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jSeparator5, javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                            .addComponent(lastMonthBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(101, 101, 101)
                            .addComponent(lastWeekBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 136, Short.MAX_VALUE)
                            .addComponent(todayBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 615, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel9)
                            .addComponent(productRB)
                            .addComponent(completeRB))
                        .addGap(12, 12, 12)
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lastWeekBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(todayBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lastMonthBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jSeparator5, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel10)
                                .addComponent(fromDateField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(5, 5, 5)
                                .addComponent(customRangeCB)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel11)
                            .addComponent(toDateField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(generateBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(clearBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void completeRBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_completeRBActionPerformed
        // TODO add your handling code here:
        handleCompleteReportType();
    }//GEN-LAST:event_completeRBActionPerformed

    private void reportTypeFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_reportTypeFieldFocusLost
        // TODO add your handling code here:
        
    }//GEN-LAST:event_reportTypeFieldFocusLost

    private void reportTypeFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reportTypeFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_reportTypeFieldActionPerformed

    private void reportTypeFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_reportTypeFieldKeyReleased
        // TODO add your handling code here:

    }//GEN-LAST:event_reportTypeFieldKeyReleased

    private void prodNameFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_prodNameFieldFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_prodNameFieldFocusLost

    private void prodNameFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_prodNameFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_prodNameFieldActionPerformed

    private void prodNameFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_prodNameFieldKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_prodNameFieldKeyReleased

    private void availableQuantityFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_availableQuantityFieldFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_availableQuantityFieldFocusLost

    private void availableQuantityFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_availableQuantityFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_availableQuantityFieldActionPerformed

    private void availableQuantityFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_availableQuantityFieldKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_availableQuantityFieldKeyReleased

    private void availableCostFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_availableCostFieldFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_availableCostFieldFocusLost

    private void availableCostFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_availableCostFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_availableCostFieldActionPerformed

    private void availableCostFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_availableCostFieldKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_availableCostFieldKeyReleased

    private void soldQuantityFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_soldQuantityFieldFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_soldQuantityFieldFocusLost

    private void soldQuantityFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_soldQuantityFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_soldQuantityFieldActionPerformed

    private void soldQuantityFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_soldQuantityFieldKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_soldQuantityFieldKeyReleased

    private void totalSaleFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_totalSaleFieldFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_totalSaleFieldFocusLost

    private void totalSaleFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_totalSaleFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_totalSaleFieldActionPerformed

    private void totalSaleFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_totalSaleFieldKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_totalSaleFieldKeyReleased

    private void grossProfitFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_grossProfitFieldFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_grossProfitFieldFocusLost

    private void grossProfitFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_grossProfitFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_grossProfitFieldActionPerformed

    private void grossProfitFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_grossProfitFieldKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_grossProfitFieldKeyReleased

    private void avgSalePerUnitFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_avgSalePerUnitFieldFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_avgSalePerUnitFieldFocusLost

    private void avgSalePerUnitFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_avgSalePerUnitFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_avgSalePerUnitFieldActionPerformed

    private void avgSalePerUnitFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_avgSalePerUnitFieldKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_avgSalePerUnitFieldKeyReleased

    private void avgCostPerUnitFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_avgCostPerUnitFieldFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_avgCostPerUnitFieldFocusLost

    private void avgCostPerUnitFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_avgCostPerUnitFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_avgCostPerUnitFieldActionPerformed

    private void avgCostPerUnitFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_avgCostPerUnitFieldKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_avgCostPerUnitFieldKeyReleased

    private void todayBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_todayBtnActionPerformed
        // TODO add your handling code here:
        handleReportRange(TODAY);
    }//GEN-LAST:event_todayBtnActionPerformed

    private void productRBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_productRBActionPerformed
        // TODO add your handling code here:
        handleProductReportType();
    }//GEN-LAST:event_productRBActionPerformed

    private void customRangeCBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_customRangeCBActionPerformed
        // TODO add your handling code here:
        handleCustomRangeCB();
    }//GEN-LAST:event_customRangeCBActionPerformed

    private void lastMonthBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lastMonthBtnActionPerformed
        // TODO add your handling code here:
        handleReportRange(LAST_MONTH);
    }//GEN-LAST:event_lastMonthBtnActionPerformed

    private void lastWeekBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lastWeekBtnActionPerformed
        // TODO add your handling code here:
        handleReportRange(LAST_WEEK);
    }//GEN-LAST:event_lastWeekBtnActionPerformed

    private void generateBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_generateBtnActionPerformed
        // TODO add your handling code here:
        generateReport();
    }//GEN-LAST:event_generateBtnActionPerformed

    private void toDateFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_toDateFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_toDateFieldActionPerformed

    private void soldCostFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_soldCostFieldFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_soldCostFieldFocusLost

    private void soldCostFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_soldCostFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_soldCostFieldActionPerformed

    private void soldCostFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_soldCostFieldKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_soldCostFieldKeyReleased

    private void clearBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearBtnActionPerformed
        // TODO add your handling code here:
        defaultSettings();
    }//GEN-LAST:event_clearBtnActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JFormattedTextField availableCostField;
    private javax.swing.JFormattedTextField availableQuantityField;
    private javax.swing.JFormattedTextField avgCostPerUnitField;
    private javax.swing.JFormattedTextField avgSalePerUnitField;
    private javax.swing.JButton clearBtn;
    private javax.swing.JRadioButton completeRB;
    private javax.swing.JCheckBox customRangeCB;
    private javax.swing.JFormattedTextField fromDateField;
    private javax.swing.JButton generateBtn;
    private javax.swing.JButton generateBtn1;
    private javax.swing.JFormattedTextField grossProfitField;
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
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JSeparator jSeparator7;
    private javax.swing.JSeparator jSeparator8;
    private javax.swing.JButton lastMonthBtn;
    private javax.swing.JButton lastWeekBtn;
    private javax.swing.JFormattedTextField prodNameField;
    private javax.swing.JRadioButton productRB;
    private javax.swing.JTable reportTable;
    private javax.swing.JFormattedTextField reportTypeField;
    private javax.swing.ButtonGroup reportTypeRBGroup;
    private javax.swing.JFormattedTextField soldCostField;
    private javax.swing.JFormattedTextField soldQuantityField;
    private javax.swing.JFormattedTextField toDateField;
    private javax.swing.JButton todayBtn;
    private javax.swing.JFormattedTextField totalSaleField;
    // End of variables declaration//GEN-END:variables
}
