package src;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * ReceiptGenerator.java
 * Generates professional withdrawal receipts for employees
 * Features:
 * - Generate text-based receipts
 * - Include employee details, withdrawal info, and balance
 * - Save receipts to file
 * - Display receipts in dialog
 */
public class ReceiptGenerator {

    private static final String TAG = "[ReceiptGenerator]";
    private static final String RECEIPT_DIR = "receipts";

    static {
        // Create receipts directory if it doesn't exist
        File dir = new File(RECEIPT_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    /**
     * Generate a withdrawal receipt
     */
    public static String generateReceipt(
            String employeeName,
            String employeeId,
            double withdrawAmount,
            double balanceAfter,
            String receiptNumber,
            LocalDateTime withdrawalDate) {

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy hh:mm:ss a");
        String formattedDate = withdrawalDate.format(dateFormatter);

        StringBuilder receipt = new StringBuilder();
        receipt.append("╔═══════════════════════════════════════════════════════╗\n");
        receipt.append("║          PAYROLL SYSTEM - WITHDRAWAL RECEIPT          ║\n");
        receipt.append("║                    MIHOYO CORPORATION                  ║\n");
        receipt.append("╠═══════════════════════════════════════════════════════╣\n");
        receipt.append("║                                                       ║\n");
        receipt.append(String.format("║ Receipt Number:    %-40s ║\n", receiptNumber));
        receipt.append("║                                                       ║\n");
        receipt.append("╠═══════════════════════════════════════════════════════╣\n");
        receipt.append("║ EMPLOYEE INFORMATION                                  ║\n");
        receipt.append("╠═══════════════════════════════════════════════════════╣\n");
        receipt.append(String.format("║ Name:              %-40s ║\n", employeeName));
        receipt.append(String.format("║ Employee ID:       %-40s ║\n", employeeId));
        receipt.append("║                                                       ║\n");
        receipt.append("╠═══════════════════════════════════════════════════════╣\n");
        receipt.append("║ WITHDRAWAL DETAILS                                    ║\n");
        receipt.append("╠═══════════════════════════════════════════════════════╣\n");
        receipt.append(String.format("║ Withdrawal Amount: %-42s ║\n", "PHP " + String.format("%,.2f", withdrawAmount)));
        receipt.append(String.format("║ Balance After:     %-42s ║\n", "PHP " + String.format("%,.2f", balanceAfter)));
        receipt.append(String.format("║ Withdrawal Date:   %-40s ║\n", formattedDate));
        receipt.append("║                                                       ║\n");
        receipt.append("╠═══════════════════════════════════════════════════════╣\n");
        receipt.append("║ STATUS: ✓ APPROVED AND PROCESSED                      ║\n");
        receipt.append("╠═══════════════════════════════════════════════════════╣\n");
        receipt.append("║                                                       ║\n");
        receipt.append("║ This receipt confirms that you have successfully      ║\n");
        receipt.append("║ withdrawn the above amount from your payroll account. ║\n");
        receipt.append("║                                                       ║\n");
        receipt.append("║ Please keep this receipt for your records.            ║\n");
        receipt.append("║                                                       ║\n");
        receipt.append("║ For inquiries, please contact HR department.          ║\n");
        receipt.append("║                                                       ║\n");
        receipt.append("╚═══════════════════════════════════════════════════════╝\n");

        return receipt.toString();
    }

    /**
     * Save receipt to file
     */
    public static String saveReceiptToFile(
            String employeeName,
            String employeeId,
            double withdrawAmount,
            double balanceAfter,
            String receiptNumber,
            LocalDateTime withdrawalDate) {

        try {
            String receipt = generateReceipt(employeeName, employeeId, withdrawAmount, balanceAfter, receiptNumber, withdrawalDate);
            String fileName = RECEIPT_DIR + File.separator + receiptNumber + ".txt";

            FileWriter writer = new FileWriter(fileName);
            writer.write(receipt);
            writer.close();

            System.out.println(TAG + " Receipt saved to: " + fileName);
            return receipt;
        } catch (IOException e) {
            System.err.println(TAG + " Error saving receipt: " + e.getMessage());
            return null;
        }
    }

    /**
     * Get receipt content by receipt number
     */
    public static String getReceiptContent(String receiptNumber) {
        try {
            String fileName = RECEIPT_DIR + File.separator + receiptNumber + ".txt";
            File file = new File(fileName);

            if (!file.exists()) {
                System.err.println(TAG + " Receipt file not found: " + fileName);
                return null;
            }

            java.nio.file.Files.readAllLines(java.nio.file.Paths.get(fileName));
            String content = new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(fileName)));
            return content;
        } catch (IOException e) {
            System.err.println(TAG + " Error reading receipt: " + e.getMessage());
            return null;
        }
    }

    /**
     * Get formatted receipt for dialog display
     */
    public static String getFormattedReceiptForDisplay(
            String employeeName,
            String employeeId,
            double withdrawAmount,
            double balanceAfter,
            String receiptNumber,
            LocalDateTime withdrawalDate) {

        StringBuilder display = new StringBuilder();
        display.append("<html><body style='font-family: Monospaced; font-size: 11px;'>");
        display.append("<pre>");

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy hh:mm:ss a");
        String formattedDate = withdrawalDate.format(dateFormatter);

        display.append("╔═══════════════════════════════════════════════════════╗\n");
        display.append("║          PAYROLL SYSTEM - WITHDRAWAL RECEIPT          ║\n");
        display.append("║                    MIHOYO CORPORATION                  ║\n");
        display.append("╠═══════════════════════════════════════════════════════╣\n");
        display.append("║                                                       ║\n");
        display.append(String.format("║ Receipt Number:    %-40s ║\n", receiptNumber));
        display.append("║                                                       ║\n");
        display.append("╠═══════════════════════════════════════════════════════╣\n");
        display.append("║ EMPLOYEE INFORMATION                                  ║\n");
        display.append("╠═══════════════════════════════════════════════════════╣\n");
        display.append(String.format("║ Name:              %-40s ║\n", employeeName));
        display.append(String.format("║ Employee ID:       %-40s ║\n", employeeId));
        display.append("║                                                       ║\n");
        display.append("╠═══════════════════════════════════════════════════════╣\n");
        display.append("║ WITHDRAWAL DETAILS                                    ║\n");
        display.append("╠═══════════════════════════════════════════════════════╣\n");
        display.append(String.format("║ Withdrawal Amount: %-42s ║\n", "PHP " + String.format("%,.2f", withdrawAmount)));
        display.append(String.format("║ Balance After:     %-42s ║\n", "PHP " + String.format("%,.2f", balanceAfter)));
        display.append(String.format("║ Withdrawal Date:   %-40s ║\n", formattedDate));
        display.append("║                                                       ║\n");
        display.append("╠═══════════════════════════════════════════════════════╣\n");
        display.append("║ STATUS: ✓ APPROVED AND PROCESSED                      ║\n");
        display.append("╚═══════════════════════════════════════════════════════╝\n");

        display.append("</pre></body></html>");
        return display.toString();
    }
}
