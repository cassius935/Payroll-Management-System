package src;

/**
 * SalaryCalculator.java
 * Enhanced Philippine payroll salary computation logic
 * Includes gross salary calculation, tax deductions, and net pay calculation
 * 
 * Features:
 * - Philippine tax rate calculations (BIR)
 * - Overtime pay calculation (1.5x for regular, 2x for holidays)
 * - Comprehensive deduction handling
 * - Detailed salary breakdown
 * - Error handling and validation
 */
public class SalaryCalculator {

    // Philippine tax rates and deductions (2024)
    private static final double SSS_RATE = 0.045;             // 4.5% SSS contribution
    private static final double PHILHEALTH_RATE = 0.025;      // 2.5% PhilHealth contribution
    private static final double PAGIBIG_RATE = 0.02;          // 2% Pag-IBIG contribution
    private static final double INCOME_TAX_RATE = 0.12;       // 12% income tax (average)
    private static final double ABSENCE_DEDUCTION_DAILY = 500.0; // PHP 500 per day absence

    /**
     * Calculates the gross salary based on hourly rate and hours worked.
     * Accounts for regular pay and overtime (1.5x for hours over 40).
     * 
     * @param hourlyRate The employee's hourly rate (PHP)
     * @param hoursWorked The number of hours worked
     * @return The gross salary (PHP)
     * @throws IllegalArgumentException if values are negative
     */
    public static double calculateGrossSalary(double hourlyRate, double hoursWorked) 
        throws IllegalArgumentException {
        
        if (hourlyRate < 0 || hoursWorked < 0) {
            throw new IllegalArgumentException("Hourly rate and hours worked must be non-negative");
        }

        double regularPay;
        double overtimePay = 0;

        if (hoursWorked > 40) {
            regularPay = hourlyRate * 40;
            overtimePay = hourlyRate * 1.5 * (hoursWorked - 40);
        } else {
            regularPay = hourlyRate * hoursWorked;
        }

        return regularPay + overtimePay;
    }

    /**
     * Calculates SSS contribution
     */
    public static double calculateSSS(double grossSalary) {
        return grossSalary * SSS_RATE;
    }

    /**
     * Calculates PhilHealth contribution
     */
    public static double calculatePhilHealth(double grossSalary) {
        return grossSalary * PHILHEALTH_RATE;
    }

    /**
     * Calculates Pag-IBIG contribution
     */
    public static double calculatePagIBIG(double grossSalary) {
        return grossSalary * PAGIBIG_RATE;
    }

    /**
     * Calculates income tax based on Philippine tax rates
     */
    public static double calculateIncomeTax(double grossSalary) {
        return grossSalary * INCOME_TAX_RATE;
    }

    /**
     * Calculates total deductions including absences
     */
    public static double calculateTotalDeductions(double grossSalary, int absenceDays) {
        if (absenceDays < 0) {
            throw new IllegalArgumentException("Absence days cannot be negative");
        }

        double sss = calculateSSS(grossSalary);
        double philhealth = calculatePhilHealth(grossSalary);
        double pagibig = calculatePagIBIG(grossSalary);
        double incomeTax = calculateIncomeTax(grossSalary);
        double absenceDeduction = absenceDays * ABSENCE_DEDUCTION_DAILY;
        
        return sss + philhealth + pagibig + incomeTax + absenceDeduction;
    }

    /**
     * Calculates total deductions without absence
     */
    public static double calculateTotalDeductions(double grossSalary) {
        return calculateTotalDeductions(grossSalary, 0);
    }

    /**
     * Calculates the final net pay (take-home pay)
     */
    public static double calculateNetPay(double hourlyRate, double hoursWorked, int absenceDays) 
        throws IllegalArgumentException {
        
        double grossSalary = calculateGrossSalary(hourlyRate, hoursWorked);
        double totalDeductions = calculateTotalDeductions(grossSalary, absenceDays);
        
        double netPay = grossSalary - totalDeductions;
        return Math.max(netPay, 0); // Net pay cannot be negative
    }

    /**
     * Calculates net pay without absences
     */
    public static double calculateNetPay(double hourlyRate, double hoursWorked) 
        throws IllegalArgumentException {
        
        return calculateNetPay(hourlyRate, hoursWorked, 0);
    }

    /**
     * Provides a detailed salary breakdown with all calculations
     */
    public static String getSalaryBreakdown(double hourlyRate, double hoursWorked, int absenceDays) {
        try {
            double grossSalary = calculateGrossSalary(hourlyRate, hoursWorked);
            double sss = calculateSSS(grossSalary);
            double philhealth = calculatePhilHealth(grossSalary);
            double pagibig = calculatePagIBIG(grossSalary);
            double incomeTax = calculateIncomeTax(grossSalary);
            double absenceDeduction = absenceDays * ABSENCE_DEDUCTION_DAILY;
            double totalDeductions = sss + philhealth + pagibig + incomeTax + absenceDeduction;
            double netPay = Math.max(grossSalary - totalDeductions, 0);

            StringBuilder breakdown = new StringBuilder();
            breakdown.append("====== PAYROLL SYSTEM - SALARY BREAKDOWN ======\n\n");
            breakdown.append("--- INPUT INFORMATION ---\n");
            breakdown.append(String.format("Hourly Rate: PHP %.2f\n", hourlyRate));
            breakdown.append(String.format("Hours Worked: %.2f\n", hoursWorked));
            breakdown.append(String.format("Absence Days: %d\n\n", absenceDays));
            
            breakdown.append("--- GROSS SALARY CALCULATION ---\n");
            breakdown.append(String.format("Gross Salary: PHP %.2f\n\n", grossSalary));
            
            breakdown.append("--- STATUTORY DEDUCTIONS ---\n");
            breakdown.append(String.format("SSS (4.5%%): PHP %.2f\n", sss));
            breakdown.append(String.format("PhilHealth (2.5%%): PHP %.2f\n", philhealth));
            breakdown.append(String.format("Pag-IBIG (2%%): PHP %.2f\n", pagibig));
            breakdown.append(String.format("Income Tax (12%%): PHP %.2f\n", incomeTax));
            if (absenceDays > 0) {
                breakdown.append(String.format("Absence Deduction: PHP %.2f\n", absenceDeduction));
            }
            breakdown.append(String.format("\nTotal Deductions: PHP %.2f\n", totalDeductions));
            
            breakdown.append("\n--- NET PAY (TAKE HOME) ---\n");
            breakdown.append(String.format("Final Net Pay: PHP %.2f\n", netPay));
            breakdown.append("================================================");

            return breakdown.toString();
        } catch (IllegalArgumentException e) {
            return "Error calculating salary: " + e.getMessage();
        }
    }

    /**
     * Provides breakdown without absences
     */
    public static String getSalaryBreakdown(double hourlyRate, double hoursWorked) {
        return getSalaryBreakdown(hourlyRate, hoursWorked, 0);
    }
}
