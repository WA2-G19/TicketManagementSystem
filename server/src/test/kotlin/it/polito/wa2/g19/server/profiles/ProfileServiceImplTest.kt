package it.polito.wa2.g19.server.profiles

import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.springframework.data.repository.findByIdOrNull

internal class ProfileServiceImplTest {

    @Test
    fun `loading an existing profile should return a valid DTO specifying its email`() {
        val repo = mockk<ProfileRepository>()
        every { repo.findByIdOrNull("test@email.it") } answers {
            Profile("test@email.it", "testName", "testSurname")
        }
        val service = ProfileServiceImpl(repo)
        val dto = service.getProfile("test@email.it")
        assert("test@email.it" == dto.email)
        assert("testName" == dto.name)
        assert("testSurname" == dto.surname)
    }

    @Test
    fun `loading an existing profile should return a valid DTO specifying its email even if it is in uppercase`() {
        val repo = mockk<ProfileRepository>()
        every { repo.findByIdOrNull("test@email.it") } answers {
            Profile("test@email.it", "testName", "testSurname")
        }
        val service = ProfileServiceImpl(repo)
        val dto = service.getProfile("test@email.it".uppercase())
        assert("test@email.it" == dto.email)
        assert("testName" == dto.name)
        assert("testSurname" == dto.surname)
    }

    @Test
    fun `loading two existing profile should return the correct DTO specifying its email`() {
        val repo = mockk<ProfileRepository>()
        every { repo.findByIdOrNull("test@email.it") } answers {
            Profile("test@email.it", "testName", "testSurname")
        }
        every { repo.findByIdOrNull("test@wrong.it") } answers {
            Profile("test@wrong.it", "wrongName", "wrongSurname")
        }
        val service = ProfileServiceImpl(repo)
        val dto = service.getProfile("test@email.it")
        assert("test@email.it" == dto.email)
        assert("testName" == dto.name)
        assert("testSurname" == dto.surname)
    }

    @Test
    fun `loading no profile should throws a ProfileNotFoundException instance`() {
        val repo = mockk<ProfileRepository>()
        every { repo.findByIdOrNull("test@email.it") } answers {
            null
        }
        val service = ProfileServiceImpl(repo)
        assertThrows<ProfileNotFoundException> { service.getProfile("test@email.it") }
    }

    @Test
    fun `loading several profiles should returns all of them`() {
        val repo = mockk<ProfileRepository>()
        every { repo.findAll() } answers {
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
        val repo = mockk<ProfileRepository>()
        every { repo.findAll() } answers {
            listOf()
        }
        val service = ProfileServiceImpl(repo)
        val profiles = service.getAll()
        assert(profiles.isEmpty())
    }


    @Test
    fun `inserting a profile should returns nothing`() {
        val repo = mockk<ProfileRepository>()
        val p = Profile("test@email.it", "testName", "testSurname")

        every { repo.existsById(p.email) } answers {
            false
        }
        every { repo.save(p) } answers {
            p
        }

        val service = ProfileServiceImpl(repo)
        assertDoesNotThrow {
            service.insertProfile(p.toDTO())
        }
    }


    @Test
    fun `inserting a profile with an already existing email should throws a DuplicateEmailException instance`() {
        val repo = mockk<ProfileRepository>()
        val p = Profile("test@email.it", "testName", "testSurname")

        every { repo.existsById(p.email) } answers {
            true
        }
        val service = ProfileServiceImpl(repo)
        assertThrows<DuplicateEmailException> { service.insertProfile(p.toDTO()) }
    }

    @Test
    fun `updating a existing profile should returns nothing`() {
        val repo = mockk<ProfileRepository>()
        val p = Profile("test@email.it", "testName", "testSurname")
        every { repo.findByIdOrNull(p.email) } answers {
            p
        }

        every { repo.save(p) } answers {
            p
        }
        val service = ProfileServiceImpl(repo)
        val res = service.updateProfile(p.email, p.toDTO())
        assert(res == Unit)

    }

    @Test
    fun `updating a no existing profile should throws a ProfileNotFoundException instance`() {
        val repo = mockk<ProfileRepository>()
        val p = ProfileDTO("test@email.it", "testName", "testSurname")
        val notExistingEmail = "notexisting@email.it"
        every { repo.findByIdOrNull(notExistingEmail) } answers {
            null
        }
        val service = ProfileServiceImpl(repo)
        assertThrows<ProfileNotFoundException> { service.updateProfile(notExistingEmail, p) }
    }


}