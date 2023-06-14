package it.polito.wa2.g19.server

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories

@SpringBootTest(classes = [ServerApplicationTests::class])
class ServerApplicationTests {

	@Test
	fun contextLoads() {
	}

}
