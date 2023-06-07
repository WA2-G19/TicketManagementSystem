package it.polito.wa2.g19.server.warranty

import java.util.UUID

interface WarrantyService {
    fun getAll(): List<WarrantyOutDTO>

    fun getById(id: UUID): WarrantyOutDTO

    fun getByCustomerEmail(email: String): List<WarrantyOutDTO>

    fun getByCustomerId(id: UUID): List<WarrantyOutDTO>

    fun getByVendorEmail(email: String): List<WarrantyOutDTO>

    fun getByVendorId(id: UUID): List<WarrantyOutDTO>

    fun insertWarranty(warranty: WarrantyInDTO): WarrantyOutDTO

    fun activateWarranty(warrantyId: UUID, customerEmail: String): WarrantyOutDTO
}