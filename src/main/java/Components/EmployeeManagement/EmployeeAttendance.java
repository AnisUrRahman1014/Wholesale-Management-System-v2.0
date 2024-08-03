/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package Components.EmployeeManagement;

import Components.CustomerManagement.ConfirmPayDialog;
import Controller.AttendanceController;
import Controller.EmployeeController;
import Controller.ManagementSystemCPU;
import Model.Employee;
import Model.Attendance;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.RowFilter;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author M AYAN LAPTOP
 */
public class EmployeeAttendance extends javax.swing.JPanel {
    private Employee currentEmployee;
    private Date currentDate;
    private static final int NO_ATTENDANCE_FOUND = 0;
    private static final int ARRIVED_ONLY = 1;
    private static final int ARRIVED_AND_LEFT = 2;
    /**
     * Creates new form EmployeeManagement
     */
    public EmployeeAttendance() {
        initComponents();
        addEmployeesTableSelectionListeners();
        attendanceTableDateChooser.getDateEditor().addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            @Override
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                if ("date".equals(evt.getPropertyName())) {
                    java.util.Date selectedDate = attendanceTableDateChooser.getDate();                    
                    if (selectedDate != null) {
                        Date sqlDate = new Date(selectedDate.getTime());
                        updateAttendanceTable(sqlDate);
                    }
                }
            }
        });
        defaultSettings();
    }
    
    private void defaultSettings(){
        currentDate = new Date(new java.util.Date().getTime());
        dateField.setText(currentDate.toString());
        updateCurrentEmployee(null);
        updateEmployeeTable();
        updateAttendanceTable(currentDate);
    }
    
    private void updateAttendanceTable(Date date){
        attendanceTableDateChooser.setDate(currentDate);
        DefaultTableModel model = (DefaultTableModel) attendanceTable.getModel();
        model.setRowCount(0);
        ArrayList<Attendance> attendanceList = AttendanceController.getAttendanceList(date);
        int count=0;
        for(Attendance atd: attendanceList){
            count++;
            Object row[]={count,atd.getEmp().getName(),atd.getArrivalTime(),atd.getLeaveTime(),atd.getWorkHrs(),atd.getPayPerHrs(),atd.getTotalPay()};
            model.addRow(row);            
        }
    }
    
    private void updateAttendanceTable(Date fromDate, Date toDate){
        attendanceTableDateChooser.setDate(currentDate);
        DefaultTableModel model = (DefaultTableModel) attendanceTable.getModel();
        model.setRowCount(0);
        ArrayList<Attendance> attendanceList = AttendanceController.getAttendanceRangeList(fromDate,toDate);
        int count=0;
        for(Attendance atd: attendanceList){
            count++;
            Object row[]={count,atd.getEmp().getName(),atd.getArrivalTime(),atd.getLeaveTime(),atd.getWorkHrs(),atd.getPayPerHrs(),atd.getTotalPay()};
            model.addRow(row);            
        }
    }
    
    private void updateEmployeeTable(){
        DefaultTableModel model = (DefaultTableModel) employeeTable.getModel();
        model.setRowCount(0);
        ArrayList<Employee> employees = EmployeeController.getEmployeesList();
        int count = 0;
        for(Employee emp: employees){
            count++;
            Object row[] = {count,emp.getName(),emp.getContact(),emp.getAddress(),emp.getCnic(),emp.getRole()};
            model.addRow(row);
        }
    }
    
    private void addNewEmployee(){
        EmployeeManagementDialog emDialog = new EmployeeManagementDialog(null, true);
        emDialog.setOnCloseListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateEmployeeTable();
            }
        });
        emDialog.setVisible(true);
    }
    
    private void manageAttendance(int rule){
        switch(rule){
            case 0 ->{
                updateArrivalBtns(true);
                updateLeaveBtns(false);
                leftCB.setSelected(false);
            }
            case 1 -> {
                updateArrivalBtns(false);
                updateLeaveBtns(true);
            }
            case 2 ->{
                updateArrivalBtns(false);
                updateLeaveBtns(false);
            }
        }
    }
    
    private void updateArrivalBtns(boolean flag){
        markInTimeBtn.setEnabled(flag);
        arrivedCB.setSelected(!flag);        
    }
    
    private void updateLeaveBtns(boolean flag){
        markOutTimeBtn.setEnabled(flag);
        leftCB.setSelected(!flag);        
    }
    
    private void updateCurrentEmployee(Employee emp){
        if(emp!=null){
            currentEmployee=emp;
            employeeField.setText(emp.getName());
            Attendance attendance = AttendanceController.getAttendance(emp,currentDate);
            if(attendance == null){
                manageAttendance(NO_ATTENDANCE_FOUND);
            }else{
                if(attendance.getLeaveTime() == null){
                    manageAttendance(ARRIVED_ONLY);
                }else{
                    manageAttendance(ARRIVED_AND_LEFT);
                }
            }
        }else{
            employeeField.setText("");
            markInTimeBtn.setEnabled(false);
            arrivedCB.setSelected(false);
            markOutTimeBtn.setEnabled(false);
            leftCB.setSelected(false);
        }
        
    }
    
    private void manageArrival(){
        Attendance attendance = new Attendance(currentEmployee,currentDate);
        Timestamp arrivalTime = new Timestamp(new java.util.Date().getTime());
        attendance.setArrivalTime(arrivalTime);
        boolean success = AttendanceController.addAttendance(attendance);
        if(success){
            ManagementSystemCPU.informationAlert(this,"Arrival Marked","Employee: "+currentEmployee.getName()+"\nTimestamp: "+arrivalTime);
            defaultSettings();
        }
    }
    
    private int confirmPayPerHour(){
        int payPerHr=currentEmployee.getPayPerHour();
        ConfirmPayDialog confirmDialog = new ConfirmPayDialog(null, true, payPerHr);
        confirmDialog.setOnCloseListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                
            }
        });
        confirmDialog.setVisible(true);
        if (!confirmDialog.isClosedByCrossButton() && confirmDialog.getPayPerHour() != 0) {
            payPerHr = confirmDialog.getPayPerHour();
        }else{
            payPerHr = -1;
        }
        return payPerHr;
    }
    
    private void manageLeave(){
        int payPerHour = confirmPayPerHour();
        if(payPerHour== -1){
            return;
        }
        Attendance attendance = new Attendance(currentEmployee,currentDate);
        attendance.setPayPerHrs(payPerHour);
        Timestamp leaveTime = new Timestamp(new java.util.Date().getTime());
        attendance.setLeaveTime(leaveTime);
        boolean success = AttendanceController.updateLeave(attendance);
        if(success){
            ManagementSystemCPU.informationAlert(this,"Leaving Marked","Employee: "+currentEmployee.getName()+"\nTimestamp: "+leaveTime);
            defaultSettings();
        }
    }
    
    private void rangeSelection(){
        DateRangeDialog rangeDialog = new DateRangeDialog(null, true);
        rangeDialog.setOnCloseListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Date fromDate = rangeDialog.getFromDate();
                Date toDate = rangeDialog.getToDate();
                if(toDate == null || fromDate ==null){
                    ManagementSystemCPU.errorAlert(null,"Range error","Invalid Range");                    
                }else{
                    manageRangeTableUpdate(fromDate,toDate);
                }                
            }
        });
        rangeDialog.setVisible(true);
    }
    
    private void manageRangeTableUpdate(Date fromDate,Date toDate){
        attendanceTableDateChooser.setDate(null);
        updateAttendanceTable(fromDate,toDate);
    }
    
    private void addEmployeesTableSelectionListeners() {
        employeeTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) { // Ensure that this event is not fired multiple times
                    int selectedRow = employeeTable.getSelectedRow();
                    if (selectedRow != -1) {
                        selectedRow = employeeTable.convertRowIndexToModel(selectedRow); // Convert to model index if filtered
                        DefaultTableModel model = (DefaultTableModel) employeeTable.getModel();
                        String name = model.getValueAt(selectedRow, 1).toString();
                        Employee emp = EmployeeController.getEmployee(name);
                        updateCurrentEmployee(emp);                        
                    }
                }
            }
        });
    }
    
    private void filterTable() {
        String input = empSearchField.getText().trim().toLowerCase();
        DefaultTableModel model = (DefaultTableModel) employeeTable.getModel();
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        employeeTable.setRowSorter(sorter);

        RowFilter<DefaultTableModel, Object> rf = new RowFilter<DefaultTableModel, Object>() {
            @Override
            public boolean include(RowFilter.Entry<? extends DefaultTableModel, ? extends Object> entry) {
                String name = entry.getStringValue(1).toLowerCase();
                String contact = entry.getStringValue(2).toLowerCase();
                String address = entry.getStringValue(3).toLowerCase();
                String cnic = entry.getStringValue(4).toLowerCase();
                return name.contains(input) || contact.contains(input) || address.contains(input) || cnic.contains(input);
            }
        };
        sorter.setRowFilter(rf);
    }
    
    private void filterAttendanceTable(){
        String input = attendanceTableSearchField.getText().trim().toLowerCase();
        DefaultTableModel model = (DefaultTableModel) attendanceTable.getModel();
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        attendanceTable.setRowSorter(sorter);

        RowFilter<DefaultTableModel, Object> rf = new RowFilter<DefaultTableModel, Object>() {
            @Override
            public boolean include(RowFilter.Entry<? extends DefaultTableModel, ? extends Object> entry) {
                String name = entry.getStringValue(1).toLowerCase();                
                return name.contains(input);
            }
        };
        sorter.setRowFilter(rf);
    }
    
    private void clearAttendanceTable(){
        updateAttendanceTable(currentDate);
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
        jLabel5 = new javax.swing.JLabel();
        deselectCustomerBtn = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        employeeTable = new javax.swing.JTable();
        empSearchField = new javax.swing.JTextField();
        manageEmployeeBtn = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel1 = new javax.swing.JLabel();
        dateField = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        markInTimeBtn = new javax.swing.JButton();
        markOutTimeBtn = new javax.swing.JButton();
        arrivedCB = new javax.swing.JCheckBox();
        leftCB = new javax.swing.JCheckBox();
        employeeField = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JSeparator();
        jScrollPane3 = new javax.swing.JScrollPane();
        attendanceTable = new javax.swing.JTable();
        attendanceTableDateChooser = new com.toedter.calendar.JDateChooser();
        deselectCustomerBtn1 = new javax.swing.JButton();
        rangeBtn = new javax.swing.JButton();
        attendanceTableSearchField = new javax.swing.JTextField();
        clearAttendanceTableSearchBtn = new javax.swing.JButton();

        jLabel5.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel5.setText("Search Employee:");

        deselectCustomerBtn.setText("X");
        deselectCustomerBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deselectCustomerBtnActionPerformed(evt);
            }
        });

        employeeTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "S/No.", "Employee ID", "Contact", "Address", "CNIC ", "Role"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
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
        jScrollPane2.setViewportView(employeeTable);
        if (employeeTable.getColumnModel().getColumnCount() > 0) {
            employeeTable.getColumnModel().getColumn(0).setPreferredWidth(5);
        }

        empSearchField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                empSearchFieldActionPerformed(evt);
            }
        });
        empSearchField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                empSearchFieldKeyReleased(evt);
            }
        });

        manageEmployeeBtn.setText("Manage Employee");
        manageEmployeeBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                manageEmployeeBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 539, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                            .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(empSearchField, javax.swing.GroupLayout.PREFERRED_SIZE, 356, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(deselectCustomerBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(manageEmployeeBtn, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(empSearchField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(deselectCustomerBtn))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(manageEmployeeBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 266, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setText("Employee Attendance");

        dateField.setEnabled(false);
        dateField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dateFieldActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel3.setText("Date");

        markInTimeBtn.setText("Mark In Time");
        markInTimeBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                markInTimeBtnActionPerformed(evt);
            }
        });

        markOutTimeBtn.setText("Mark Out Time");
        markOutTimeBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                markOutTimeBtnActionPerformed(evt);
            }
        });

        arrivedCB.setText("Arrived");
        arrivedCB.setEnabled(false);

        leftCB.setText("Left");
        leftCB.setEnabled(false);

        employeeField.setEnabled(false);
        employeeField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                employeeFieldActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel2.setText("Employee:");

        jButton1.setText("Reset");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 359, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 489, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(markInTimeBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(arrivedCB))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(leftCB, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(markOutTimeBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(dateField, javax.swing.GroupLayout.DEFAULT_SIZE, 183, Short.MAX_VALUE)
                            .addComponent(employeeField))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 47, Short.MAX_VALUE)
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(dateField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(employeeField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(19, 19, 19)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(markInTimeBtn, javax.swing.GroupLayout.DEFAULT_SIZE, 85, Short.MAX_VALUE)
                    .addComponent(markOutTimeBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(arrivedCB)
                    .addComponent(leftCB))
                .addContainerGap(70, Short.MAX_VALUE))
        );

        attendanceTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "S/No.", "Employee ID", "In Time", "Out Time", "Work Hrs", "Pay / Hr", "Total Pay"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane3.setViewportView(attendanceTable);
        if (attendanceTable.getColumnModel().getColumnCount() > 0) {
            attendanceTable.getColumnModel().getColumn(0).setPreferredWidth(5);
        }

        deselectCustomerBtn1.setText("X");
        deselectCustomerBtn1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deselectCustomerBtn1ActionPerformed(evt);
            }
        });

        rangeBtn.setText("Range");
        rangeBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rangeBtnActionPerformed(evt);
            }
        });

        attendanceTableSearchField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                attendanceTableSearchFieldActionPerformed(evt);
            }
        });
        attendanceTableSearchField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                attendanceTableSearchFieldKeyReleased(evt);
            }
        });

        clearAttendanceTableSearchBtn.setText("X");
        clearAttendanceTableSearchBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearAttendanceTableSearchBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator2)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane3)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(10, 10, 10)
                                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(rangeBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(attendanceTableSearchField, javax.swing.GroupLayout.PREFERRED_SIZE, 356, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(clearAttendanceTableSearchBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(attendanceTableDateChooser, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(deselectCustomerBtn1, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(13, 13, 13)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(attendanceTableDateChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(deselectCustomerBtn1)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(rangeBtn)
                        .addComponent(attendanceTableSearchField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(clearAttendanceTableSearchBtn)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 259, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void empSearchFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_empSearchFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_empSearchFieldActionPerformed

    private void empSearchFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_empSearchFieldKeyReleased
        // TODO add your handling code here:
        filterTable();
    }//GEN-LAST:event_empSearchFieldKeyReleased

    private void deselectCustomerBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deselectCustomerBtnActionPerformed
        // TODO add your handling code here:
        empSearchField.setText("");
        filterTable();
    }//GEN-LAST:event_deselectCustomerBtnActionPerformed

    private void dateFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dateFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_dateFieldActionPerformed

    private void employeeFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_employeeFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_employeeFieldActionPerformed

    private void deselectCustomerBtn1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deselectCustomerBtn1ActionPerformed
        // TODO add your handling code here:
        clearAttendanceTable();
    }//GEN-LAST:event_deselectCustomerBtn1ActionPerformed

    private void manageEmployeeBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_manageEmployeeBtnActionPerformed
        // TODO add your handling code here:
        addNewEmployee();
    }//GEN-LAST:event_manageEmployeeBtnActionPerformed

    private void markInTimeBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_markInTimeBtnActionPerformed
        // TODO add your handling code here:
        manageArrival();
    }//GEN-LAST:event_markInTimeBtnActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        defaultSettings();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void markOutTimeBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_markOutTimeBtnActionPerformed
        // TODO add your handling code here:
        manageLeave();
    }//GEN-LAST:event_markOutTimeBtnActionPerformed

    private void rangeBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rangeBtnActionPerformed
        // TODO add your handling code here:
        rangeSelection();
    }//GEN-LAST:event_rangeBtnActionPerformed

    private void attendanceTableSearchFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_attendanceTableSearchFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_attendanceTableSearchFieldActionPerformed

    private void attendanceTableSearchFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_attendanceTableSearchFieldKeyReleased
        // TODO add your handling code here:
        filterAttendanceTable();
    }//GEN-LAST:event_attendanceTableSearchFieldKeyReleased

    private void clearAttendanceTableSearchBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearAttendanceTableSearchBtnActionPerformed
        // TODO add your handling code here:
        attendanceTableSearchField.setText("");
        filterAttendanceTable();
    }//GEN-LAST:event_clearAttendanceTableSearchBtnActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox arrivedCB;
    private javax.swing.JTable attendanceTable;
    private com.toedter.calendar.JDateChooser attendanceTableDateChooser;
    private javax.swing.JTextField attendanceTableSearchField;
    private javax.swing.JButton clearAttendanceTableSearchBtn;
    private javax.swing.JTextField dateField;
    private javax.swing.JButton deselectCustomerBtn;
    private javax.swing.JButton deselectCustomerBtn1;
    private javax.swing.JTextField empSearchField;
    private javax.swing.JTextField employeeField;
    private javax.swing.JTable employeeTable;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JCheckBox leftCB;
    private javax.swing.JButton manageEmployeeBtn;
    private javax.swing.JButton markInTimeBtn;
    private javax.swing.JButton markOutTimeBtn;
    private javax.swing.JButton rangeBtn;
    // End of variables declaration//GEN-END:variables
}
