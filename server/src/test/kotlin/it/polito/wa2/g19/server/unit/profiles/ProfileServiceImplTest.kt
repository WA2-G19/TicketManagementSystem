package it.polito.wa2.g19.server.unit.profiles

import io.mockk.every
import io.mockk.mockk
import it.polito.wa2.g19.server.profiles.*
import it.polito.wa2.g19.server.profiles.customers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows

internal class ProfileServiceImplTest {

    @Test
    fun `loading an existing profile should return a valid DTO specifying its email`() {
        val repo = mockk<CustomerRepository>()
        every { repo.findByEmailIgnoreCase("test@email.it") } answers {
            Customer("test@email.it", "testName", "testSurname", "testAddress")
        }
        val service = CustomerServiceImpl(repo)
        val dto = service.getProfile("test@email.it")
        assert("test@email.it" == dto.email)
        assert("testName" == dto.name)
        assert("testSurname" == dto.surname)
    }

    @Test
    fun `loading an existing profile should return a valid DTO specifying its email even if it is in uppercase`() {
        val repo = mockk<CustomerRepository>()
        every { repo.findByEmailIgnoreCase("test@email.it") } answers {
            Customer("test@email.it", "testName", "testSurname", "testAddress")
        }
        val service = CustomerServiceImpl(repo)
        val dto = service.getProfile("test@email.it".uppercase())
        assert("test@email.it" == dto.email)
        assert("testName" == dto.name)
        assert("testSurname" == dto.surname)
    }

    @Test
    fun `loading two existing profile should return the correct DTO specifying its email`() {
        val repo = mockk<CustomerRepository>()
        every { repo.findByEmailIgnoreCase("test@email.it") } answers {
            Customer("test@email.it", "testName", "testSurname", "testAddress")
        }
        every { repo.findByEmailIgnoreCase("test@wrong.it") } answers {
            Customer("test@wrong.it", "wrongName", "wrongSurname", "testAddress")
        }
        val service = CustomerServiceImpl(repo)
        val dto = service.getProfile("test@email.it")
        assert("test@email.it" == dto.email)
        assert("testName" == dto.name)
        assert("testSurname" == dto.surname)
    }

    @Test
    fun `loading no profile should throws a ProfileNotFoundException instance`() {
        val repo = mockk<CustomerRepository>()
        every { repo.findByEmailIgnoreCase("test@email.it") } answers {
            null
        }
        val service = CustomerServiceImpl(repo)
        assertThrows<ProfileNotFoundException> { service.getProfile("test@email.it") }
    }

    @Test
    fun `loading several profiles should returns all of them`() {
        val repo = mockk<CustomerRepository>()
        every { repo.findAll() } answers {
            (0..9).map { idx -> Customer("${idx}email", "${idx}name", "${idx}surname", "${idx}address") }
        }
        val service = CustomerServiceImpl(repo)
        service.getAll().sortedBy { p -> p.email }.forEachIndexed { idx, p ->
            assert("${idx}email" == p.email)
            assert("${idx}name" == p.name)
            assert("${idx}surname" == p.surname)
        }
    }

    @Test
    fun `loading no profiles should returns an empty collection`() {
        val repo = mockk<CustomerRepository>()
        every { repo.findAll() } answers {
            listOf()
        }
        val service = CustomerServiceImpl(repo)
        val profiles = service.getAll()
        assert(profiles.isEmpty())
    }


    @Test
    fun `inserting a profile should return nothing`() {
        val repo = mockk<CustomerRepository>()
        val p = Customer("test@email.it", "testName", "testSurname", "testAddress")



        every { repo.existsByEmailIgnoreCase(p.email) } answers {
            false
        }



        every { repo.save(any()) } answers {
            p
        }

        val service = CustomerServiceImpl(repo)
        assertDoesNotThrow {
            service.insertProfile(p.toDTO())
        }
    }


    @Test
    fun `inserting a profile with an already existing email should throws a DuplicateEmailException instance`() {
        val repo = mockk<CustomerRepository>()
        val p = Customer("test@email.it", "testName", "testSurname", "testAddress")

        every { repo.existsByEmailIgnoreCase(p.email) } answers {
            true
        }
        val service = CustomerServiceImpl(repo)
        assertThrows<DuplicateEmailException> { service.insertProfile(p.toDTO()) }
    }

    @Test
    fun `updating a existing profile should returns nothing`() {
        val repo = mockk<CustomerRepository>()
        val p = Customer("test@email.it", "testName", "testSurname", "testAddress")
        every { repo.findByEmailIgnoreCase(p.email) } answers {
            p
        }

        every { repo.save(p) } answers {
            p
        }
        val service = CustomerServiceImpl(repo)
        val res = service.updateProfile(p.email, p.toDTO())
        assert(res == Unit)

    }


    @Test
    fun `updating a no existing profile should throws a ProfileNotFoundException instance`() {
        val repo = mockk<CustomerRepository>()
        val p = CustomerDTO("test@email.it", "testName", "testSurname", "testAddress")
        val notExistingEmail = "notexisting@email.it"
        every { repo.findByEmailIgnoreCase(notExistingEmail) } answers {
            null
        }
        val service = CustomerServiceImpl(repo)
        assertThrows<ProfileNotFoundException> { service.updateProfile(notExistingEmail, p) }
    }


}