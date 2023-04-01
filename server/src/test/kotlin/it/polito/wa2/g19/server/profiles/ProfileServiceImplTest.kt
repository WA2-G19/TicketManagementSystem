package it.polito.wa2.g19.server.profiles

import io.mockk.every
import io.mockk.mockk
import it.polito.wa2.g19.server.products.Product
import it.polito.wa2.g19.server.products.ProductRepository
import it.polito.wa2.g19.server.products.ProductServiceImpl
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.repository.findByIdOrNull

internal class ProfileServiceImplTest {

    @Test
    fun `loading an existing profile should return a valid DTO specifying its email`() {
        val repo = mockk<ProfileRepository>();
        every { repo.findByIdOrNull("test@email.it") } answers  {
            Profile("test@email.it", "testName", "testSurname")
        }
        val service = ProfileServiceImpl(repo)
        val dto = service.getProfile("test@email.it")
        assert("test@email.it" == dto?.email)
        assert("testName" == dto?.name)
        assert("testSurname" == dto?.surname)
    }

    @Test
    fun `loading two existing profile should return the correct DTO specifying its email`() {
        val repo = mockk<ProfileRepository>();
        every { repo.findByIdOrNull("test@email.it") } answers  {
            Profile("test@email.it", "testName", "testSurname")
        }
        every { repo.findByIdOrNull("test@wrong.it") } answers  {
            Profile("test@wrong.it", "wrongName", "wrongSurname")
        }
        val service = ProfileServiceImpl(repo)
        val dto = service.getProfile("test@email.it")
        assert("test@email.it" == dto?.email)
        assert("testName" == dto?.name)
        assert("testSurname" == dto?.surname)
    }

    @Test
    fun `loading no profile should throws a ProfileNotFoundException instance`() {
        val repo = mockk<ProfileRepository>();
        every { repo.findByIdOrNull("test@email.it") } answers  {
            null
        }
        val service = ProfileServiceImpl(repo)
        assertThrows<ProfileNotFoundException> { service.getProfile("test@email.it") }
    }

    @Test
    fun `loading several profiles should returns all of them`() {
        val repo = mockk<ProfileRepository>();
        every { repo.findAll() } answers  {
            (0..9).map { idx -> Profile("${idx}email", "${idx}name", "${idx}surname") }

        }
        val service = ProfileServiceImpl(repo)
        service.getAll().sortedBy { p -> p.email }.forEachIndexed { idx, p ->
            assert("${idx}email" == p.email)
            assert("${idx}name" == p.name)
            assert("${idx}surname" == p.surname)
        }
    }

    @Test
    fun `loading no profiles should returns an empty collection`() {
        val repo = mockk<ProfileRepository>();
        every { repo.findAll() } answers  {
            listOf()
        }
        val service = ProfileServiceImpl(repo)
        val dtos = service.getAll()
        assert(dtos.isEmpty())
    }






}