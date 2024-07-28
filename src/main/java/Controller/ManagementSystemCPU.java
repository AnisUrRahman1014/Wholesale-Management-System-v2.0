package Controller;

/**
 *
 * @author Anis Ur Rahman
 */
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.print.PrintService;
import javax.swing.JOptionPane;
import Model.Payroll;
public class ManagementSystemCPU {
    public static final int ACTIVE = 0;
    public static final int NON_ACTIVE = 1;
    public static final String PIECE = "Piece";
    public static final String WEIGHT = "Weight";
    
    static PrinterJob job=null;
    
    public static Dimension getScreenSize(){
        int screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height-10;
        int screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
        Dimension screenSize = new Dimension(screenWidth, screenHeight);
        return screenSize;
    }
    
    public static void errorAlert(Object parent,String title, String message){
        JOptionPane.showMessageDialog((Component)parent, message,title,JOptionPane.ERROR_MESSAGE);
    }
    
    public static void informationAlert(Object parent,String title, String message){
        JOptionPane.showMessageDialog((Component)parent, message,title,JOptionPane.INFORMATION_MESSAGE);
    }
    
    public static void printPanel(Payroll payroll){
        job = PrinterJob.getPrinterJob();
        java.sql.Date sqlDate= new java.sql.Date(new java.util.Date().getTime());
        job.setJobName(sqlDate+" _"+String.valueOf(new java.util.Date().getTime())); 
        PageFormat page=job.defaultPage();
        Paper paper = new Paper();
        paper.setImageableArea(0, 0, 200.0, 1000.0);
        page.setPaper(paper);
        job.setPrintable((Graphics pg, PageFormat pf, int pageNum) -> {  
            if(pageNum > 0){
                return Printable.NO_SUCH_PAGE;
            }
            Graphics2D g2 = (Graphics2D)pg;
            g2.translate(pf.getImageableX(), pf.getImageableY());
            g2.scale(0.9,0.9);
            int y1 = 20;
            int yShift=10;
            int headerRectHeight=15;
            g2.setFont(new Font("Century Gothic",Font.BOLD,12));
            g2.drawString("Invoice", 0, y1);
            y1 += yShift;
            g2.setFont(new Font("Century Gothic",Font.BOLD,10));
            g2.drawString("New Volvic Pakistan", 0, y1);
            y1 += yShift;
            g2.setFont(new Font("Century Gothic",Font.PLAIN,9));
            g2.drawString("Near Sadar Police Station, Hamza Chowk, Gujrat", 0, y1);
            y1 += yShift;
            g2.drawString("+92 300 096 5242", 0, y1);
            y1 += yShift;
            g2.drawLine(0, y1, 240, y1);
            y1 += headerRectHeight;
            g2.drawString("Employee", 0, y1);
            g2.drawString(":", 75, y1);
            g2.drawString(payroll.getEmp().getName(), 165, y1);
            y1 += yShift;
            g2.drawLine(0, y1, 240, y1);
            y1 += headerRectHeight;
            g2.drawString("Invoice Summary",0,y1); y1+=yShift;
            g2.drawLine(0, y1, 240, y1);
            y1 += headerRectHeight;
            yShift = 15;
            g2.drawString("Invoice#", 0, y1);
            g2.drawString(":", 75, y1);
            g2.drawString(String.valueOf(payroll.getPayrollId()), 165, y1);
            y1 += yShift;
            
            g2.drawString("Invoice Date", 0, y1);
            g2.drawString(":", 75, y1);
            g2.drawString(payroll.getInvoiceDate().toString(), 165, y1);
            y1 += yShift;
            
            g2.drawString("Work Days", 0, y1);
            g2.drawString(":", 75, y1);
            g2.drawString(String.valueOf(payroll.getTotalDays()), 165, y1);
            y1 += yShift;
            
            g2.drawString("Total Work Hours", 0, y1);
            g2.drawString(":", 75, y1);
            g2.drawString(String.valueOf(payroll.getTotalHrs()), 165, y1);
            y1 += yShift;
            
            g2.drawString("Payment", 0, y1);
            g2.drawString(":", 75, y1);
            g2.drawString(String.valueOf(payroll.getPayment()), 165, y1);
            y1 += yShift;
            g2.drawLine(0, y1, 240, y1);
            yShift = 10;
            y1 += headerRectHeight;
            g2.drawString("Date", 0, y1);
            g2.drawString(":", 75, y1);
            g2.drawString(String.valueOf(LocalDate.now()), 145, y1);
            y1 += yShift;
            g2.drawString("*******************************************************************", 0, y1);
            y1 += yShift;
            g2.drawString("THANK YOU SO MUCH", 45, y1);
            return Printable.PAGE_EXISTS;
        },page);
        try{
                PrintService printService=job.getPrintService();
                if(printService==null)
                {
                    job.printDialog();
                    printService = job.getPrintService();
                }
                if(printService!=null) {
                    
                    job.print();
                }
        } catch (PrinterException ex) {
            errorAlert(null,"Printing Error", ex.getMessage());
        }
    }
}
