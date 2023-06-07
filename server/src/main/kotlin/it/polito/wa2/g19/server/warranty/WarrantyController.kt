package it.polito.wa2.g19.server.warranty

import io.micrometer.observation.annotation.Observed
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@Validated
@RequestMapping("/API/warranty")
@Observed
class WarrantyController(
    private val warrantyService: WarrantyService
) {
    @GetMapping("/")
    @ResponseStatus(HttpStatus.OK)
    fun getAll(): List<WarrantyOutDTO> {
        return warrantyService.getAll()
    }

    @GetMapping("/{:warrantyId}")
    @ResponseStatus(HttpStatus.OK)
    fun getById(@PathVariable warrantyId: UUID): WarrantyOutDTO {
        return warrantyService.getById(warrantyId)
    }

    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    fun insertWarranty(@RequestBody warranty: WarrantyInDTO): WarrantyOutDTO {
        return warrantyService.insertWarranty(warranty)
    }

    @PostMapping("/{:warrantyId}/activate")
    @ResponseStatus(HttpStatus.CREATED)
    fun activateWarranty(@PathVariable warrantyId: UUID, principal: Authentication): WarrantyOutDTO {
        return warrantyService.activateWarranty(warrantyId, principal.name)
    }
}