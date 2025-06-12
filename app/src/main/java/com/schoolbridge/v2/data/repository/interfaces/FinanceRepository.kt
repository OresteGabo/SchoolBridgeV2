package com.schoolbridge.v2.data.repository.interfaces

// This is an interface (a contract), not an implementation.
// The actual implementation (e.g., FinanceRepositoryImpl) would connect to your financial database.

// You would need to import relevant DTOs, e.g.:
// import com.schoolvridge.v2.data.dto.InvoiceDto // Assuming InvoiceDto exists
// import com.schoolvridge.v2.data.dto.PaymentDto // Assuming PaymentDto exists
// import com.schoolvridge.v2.data.dto.FeeStructureDto // Assuming FeeStructureDto exists

/**
 * Interface for the **Financial Data Repository**.
 *
 * This repository defines the contract for managing financial operations within the school,
 * including invoicing, payment processing, fee structures, and financial records.
 *
 * **Typical methods it would expose:**
 * -   Generating and retrieving invoices.
 * -   Recording and tracking payments.
 * -   Managing fee structures for different school levels or services.
 * -   Retrieving financial statements for students or parents.
 */
interface FinanceRepository {

    suspend fun getInvoiceById(invoiceId: String): Any? // Replace Any? with your InvoiceDto
    suspend fun getInvoicesByStudent(studentId: String): List<Any> // Replace Any with your InvoiceDto
    suspend fun createInvoice(invoice: Any): Any // Replace Any with your InvoiceDto
    suspend fun updateInvoiceStatus(invoiceId: String, newStatus: String): Any // Replace Any with your InvoiceDto (e.g., PAID, OVERDUE)
    suspend fun recordPayment(payment: Any): Any // Replace Any with your PaymentDto (assuming payment DTO has invoice ID)
    suspend fun getPaymentsByInvoice(invoiceId: String): List<Any> // Replace Any with your PaymentDto
    suspend fun getFeeStructureById(feeStructureId: String): Any? // Replace Any? with your FeeStructureDto
    suspend fun getFeeStructuresBySchoolLevel(schoolLevelId: String): List<Any> // Replace Any with your FeeStructureDto
    // ... potentially methods for managing refunds, financial aid, reporting, etc.
}