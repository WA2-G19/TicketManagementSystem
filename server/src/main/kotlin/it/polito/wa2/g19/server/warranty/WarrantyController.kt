package it.polito.wa2.g19.server.warranty

import io.micrometer.observation.annotation.Observed
import it.polito.wa2.g19.server.common.Util
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
import java.net.URI
import java.util.*

@RestController
@Validated
@RequestMapping("/API/warranty")
@Observed
@CrossOrigin
class WarrantyController(
    private val warrantyService: WarrantyService,
    @Qualifier("requestMappingHandlerMapping") private val handlerMapping: RequestMappingHandlerMapping

) {
    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    fun getAll(): List<WarrantyOutDTO> {
        return warrantyService.getAll()
    }

    @GetMapping("/{warrantyId}")
    @ResponseStatus(HttpStatus.OK)
    fun getById(@PathVariable warrantyId: UUID): WarrantyOutDTO {
        return warrantyService.getById(warrantyId)
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    fun insertWarranty(@RequestBody warranty: WarrantyInDTO): ResponseEntity<WarrantyOutDTO> {
        val warrantyOutDTO = warrantyService.insertWarranty(warranty)
        val headers = HttpHeaders()
        headers.location = URI.create(Util.getUri(handlerMapping, ::getById.name, warrantyOutDTO.id))
        return ResponseEntity(warrantyOutDTO, headers, HttpStatus.CREATED)
    }

    @PostMapping("/{warrantyId}/activate")
    @ResponseStatus(HttpStatus.CREATED)
    fun activateWarranty(@PathVariable warrantyId: UUID, principal: Authentication): WarrantyOutDTO {
        return warrantyService.activateWarranty(warrantyId, principal.name)
    }

    @DeleteMapping("/{warrantyId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteWarranty(@PathVariable warrantyId: UUID, principal: Authentication){
        return warrantyService.deleteWarranty(warrantyId)
    }
}