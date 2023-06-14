package it.polito.wa2.g19.server

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories

@SpringBootApplication
@EnableR2dbcRepositories("it.polito.wa2.g19.server.repositories.reactive")
@Import(DataSourceAutoConfiguration::class)
@EnableJpaRepositories("it.polito.wa2.g19.server.repositories.jpa")
class ServerApplication

fun main(args: Array<String>) {
	runApplication<ServerApplication>(*args)
}


