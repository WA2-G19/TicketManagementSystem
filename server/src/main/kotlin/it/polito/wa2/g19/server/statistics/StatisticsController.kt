package it.polito.wa2.g19.server.statistics

import io.micrometer.observation.annotation.Observed
import it.polito.wa2.g19.server.profiles.staff.StaffService
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@Validated
@CrossOrigin
@RequestMapping("/API/stats")
@Observed
class StatisticsController(
    private val statisticsService: StatisticsService,
    private val staffService: StaffService
) {

    @GetMapping("/all")
    fun getAllStatistics(): Map<String, StatisticsDTO> {
        return staffService.getAll().map {
            it.email to StatisticsDTO(
                statisticsService.getTicketClosedByExpert(it.email),
                statisticsService.getTicketsInProgressByExpert(it.email),
                statisticsService.getAverageTimedByExpert(it.email)
            )
        }.toMap()
    }

    @GetMapping("/all/{expertMail}")
    fun getAllStatisticsByExpert(
        @PathVariable
        expertMail: String
    ): StatisticsDTO {
        return StatisticsDTO(
            statisticsService.getTicketClosedByExpert(expertMail),
            statisticsService.getTicketsInProgressByExpert(expertMail),
            statisticsService.getAverageTimedByExpert(expertMail)
        )
    }

    @GetMapping("/tickets-closed/{expertMail}")
    fun getTicketClosedByExpert(
        @PathVariable
        expertMail: String
    ): Int {
        return statisticsService.getTicketClosedByExpert(expertMail)
    }

    @GetMapping("/tickets-in-progress/{expertMail}")
    fun getTicketsInProgressByExpert(
       @PathVariable
       expertMail: String
    ): Int {
        return statisticsService.getTicketsInProgressByExpert(expertMail)
    }

    @GetMapping("/average-time/{expertMail}")
    fun getAverageTimedByExpert(
        @PathVariable
        expertMail: String
    ): Float {
        return statisticsService.getAverageTimedByExpert(expertMail)
    }

}