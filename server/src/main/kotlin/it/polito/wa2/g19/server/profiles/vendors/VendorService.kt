package it.polito.wa2.g19.server.profiles.vendors

interface VendorService {
    fun getAll(): List<VendorDTO>
    fun getVendor(email: String): VendorDTO
    fun insertVendor(credentials: VendorCredentialsDTO)
    fun updateVendor(email: String, profile: VendorDTO)
}