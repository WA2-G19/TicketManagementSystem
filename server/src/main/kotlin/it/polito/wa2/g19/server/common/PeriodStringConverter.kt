package it.polito.wa2.g19.server.common

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import java.time.Period

@Converter
class PeriodStringConverter: AttributeConverter<Period, String> {
    override fun convertToDatabaseColumn(attribute: Period): String {
        return attribute.toString()
    }

    override fun convertToEntityAttribute(dbData: String): Period {
        return Period.parse(dbData)
    }
}