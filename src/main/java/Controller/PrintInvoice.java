package Controller;

/**
 *
 * @author M AYAN LAPTOP
 */
import java.awt.Desktop;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.print.PrintService;
import javax.swing.table.DefaultTableModel;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import Model.Bill;
import java.awt.Image;
import java.io.ByteArrayInputStream;
import javax.imageio.ImageIO;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.PictureData;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFPicture;

public class PrintInvoice {
    public static boolean print(String filePath) {
        boolean success = false;
        File file = new File(filePath);
        Desktop desktop = Desktop.getDesktop();
        if (file.exists()) {
            try {
                desktop.print(file);
                success = true;
            } catch (IOException ex) {
                Logger.getLogger(PrintInvoice.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            System.out.println("The file does not exist.");
        }
        return success;
    }
    
//    public static void printPanel(Bill bill){
//        PrinterJob job = PrinterJob.getPrinterJob();
//        job.setJobName(bill.getBillId()); 
//        job.setPrintable((Graphics pg, PageFormat pf, int pageNum) -> {  
//            if(pageNum > 0){
//                return Printable.NO_SUCH_PAGE;
//            }
//            Graphics2D g2 = (Graphics2D)pg;
//            g2.translate(pf.getImageableX(), pf.getImageableY());
//            g2.scale(0.9,0.9);
//            int y1 = 20;
//            int yShift=10;
//            int headerRectHeight=15;
//            g2.setFont(new Font("Century Gothic",Font.BOLD,12));
//            g2.drawString("INVOICE", 0, y1);
//            y1 += yShift;
//            g2.setFont(new Font("Century Gothic",Font.BOLD,10));
//            g2.drawString("RM Fast Food Corner", 0, y1);
//            y1 += yShift;
//            g2.setFont(new Font("Century Gothic",Font.PLAIN,9));
//            g2.drawString("JalalPur Road, Gujrat", 0, y1);
//            y1 += yShift;
//            g2.drawString("+92 300 627 9757", 0, y1);
//            y1 += yShift;
//            g2.drawLine(0, y1, 240, y1);
//            y1 += headerRectHeight;
//            g2.drawString("Order ID: "+orderID, 0, y1); y1 += yShift;
//            g2.drawString("Order Type: "+billType, 0, y1); y1 += yShift;
//            g2.drawString("Customer: "+contact, 0, y1);           
//            y1 += headerRectHeight;
//            g2.drawLine(0, y1, 240, y1);
//            y1 += headerRectHeight;
//            g2.drawString("Name", 0, y1);
//            g2.drawString("Quantity", 100, y1);
//            g2.drawString("Price", 165, y1);
//            y1 += yShift;
//            g2.drawLine(0, y1, 240, y1);
//            y1 += headerRectHeight;
////            for (OrderItem i : orderItems) {
////                g2.drawString(i.toString(), 12, y1);           
////                g2.drawString(String.valueOf(i.getQuantity()), 125, y1);
////                g2.drawString("x "+String.valueOf(i.getUnitPrice()), 135, y1);
////                g2.drawString(String.valueOf(i.getTotalPrice()), 200, y1);
////                y1 = y1 + yShift;
////            }
//            for (OrderItem i : orderItems) {
//                String itemString = i.toString();
//                int itemStringStart = 0;
//                int quantityStringStart = 100;
//
//                // Split the itemString into lines if it's too long
//                String[] lines = splitString(itemString, 20);
//                int indexCount=0;
//                for (String line : lines) {
//                    g2.drawString(line, itemStringStart, y1);
//                    indexCount++;
//                    if(indexCount<lines.length){
//                        y1 += yShift;
//                    }                    
//                }
//                g2.drawString(String.valueOf(i.getQuantity()), quantityStringStart, y1);
//                g2.drawString("x " + String.valueOf(i.getUnitPrice()), quantityStringStart+10, y1);
//                g2.drawString(String.valueOf(i.getTotalPrice()), 165, y1);
//                y1 += yShift+10;
//            }
//            g2.drawLine(0, y1, 240, y1);
//            y1 += headerRectHeight;
//            g2.drawLine(0, y1, 240, y1);
//            y1 += headerRectHeight;
//            g2.drawString("Total Amount", 0, y1);
//            g2.drawString(":", 75, y1);
//            g2.drawString(totalPriceField.getText(), 165, y1);
//            y1 += yShift;
//            g2.drawLine(0, y1, 240, y1);
//            y1 += headerRectHeight;
////            g2.drawString("Paid", 10, y1);
////            g2.drawString(":", 75, y1);
////            g2.drawString(cashPaidField.getText(), 250, y1);
////            y1 += yShift;
////            g2.drawString("Change", 10, y1);
////            g2.drawString(":", 75, y1);
////            g2.drawString(cashBackField.getText(), 250, y1);
////            y1 += yShift;
////            g2.drawLine(12, y1, 280, y1);
////            y1 += headerRectHeight;
//            g2.drawString("Date", 0, y1);
//            g2.drawString(":", 65, y1);
//            g2.drawString(String.valueOf(LocalDate.now()), 135, y1);
//            y1 += yShift;
//            g2.drawString("*******************************************************************", 0, y1);
//            y1 += yShift;
//            g2.drawString("THANK YOU SO MUCH", 45, y1);
//            return Printable.PAGE_EXISTS;
//        });
//        try{
//                PrintService printService=job.getPrintService();
//                if(printService==null)
//                {
//                    job.printDialog();
//                    printService = job.getPrintService();
//                }
//                if(printService!=null) {
//                    job.defaultPage();
//                    job.print();
//                }
//        } catch (PrinterException ex) {
//            Logger.getLogger(Bill.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
    
    public static void printExcelSheet(Workbook workbook) {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setJobName("Print Invoice");

        job.setPrintable(new Printable() {
            @Override
            public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
                if (pageIndex > 0) {
                    return NO_SUCH_PAGE;
                }

                Graphics2D g2d = (Graphics2D) graphics;
                g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

                // Adjust scaling and other settings as needed
                g2d.scale(0.8, 0.8);

                // Render the workbook as an image or directly draw it
                // This is a simplified example, you might need a more complex rendering
                Sheet sheet = workbook.getSheetAt(0);
                try {
                    drawSheet(g2d, sheet, workbook);
                } catch (IOException ex) {
                    Logger.getLogger(PrintInvoice.class.getName()).log(Level.SEVERE, null, ex);
                }

                return PAGE_EXISTS;
            }
        });

        try {
            if (job.printDialog()) {
                job.print();
            }
        } catch (PrinterException ex) {
            Logger.getLogger(PrintInvoice.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void drawSheet(Graphics2D g2d, Sheet sheet, Workbook workbook) throws IOException {
        for (int rowIndex = 0; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (row != null) {
                for (int colIndex = 0; colIndex < row.getLastCellNum(); colIndex++) {
                    Cell cell = row.getCell(colIndex);
                    if (cell != null) {
                        CellStyle cellStyle = cell.getCellStyle();
                        String cellValue = cell.toString();

                        XSSFFont font = (XSSFFont) workbook.getFontAt(cellStyle.getFontIndex());
                        g2d.setFont(new Font(font.getFontName(), Font.PLAIN, font.getFontHeightInPoints()));
                        if (font.getBold()) {
                            g2d.setFont(g2d.getFont().deriveFont(Font.BOLD));
                        }

                        // Draw cell value
                        g2d.drawString(cellValue, colIndex * 100, rowIndex * 20); // Adjust positions and scaling

                        // Draw cell borders
                        g2d.drawRect(colIndex * 100, rowIndex * 20 - 15, 100, 20); // Adjust size as needed
                    }
                }
            }
        }

        // Draw images
        if (sheet.getDrawingPatriarch() != null) {
            XSSFDrawing drawing = (XSSFDrawing) sheet.getDrawingPatriarch();
            for (org.apache.poi.ss.usermodel.Shape shape : drawing.getShapes()) {
                if (shape instanceof XSSFPicture) {
                    XSSFPicture picture = (XSSFPicture) shape;
                    PictureData pictureData = picture.getPictureData();
                    byte[] data = pictureData.getData();
                    Image img = ImageIO.read(new ByteArrayInputStream(data));

                    // Get the position of the picture in the sheet
                    ClientAnchor anchor = picture.getClientAnchor();
                    int pictureRow = anchor.getRow1();
                    int pictureCol = anchor.getCol1();

                    // Adjust image position and scaling as needed
                    g2d.drawImage(img, pictureCol * 100, pictureRow * 20, null);
                }
            }
        }
    }
}

