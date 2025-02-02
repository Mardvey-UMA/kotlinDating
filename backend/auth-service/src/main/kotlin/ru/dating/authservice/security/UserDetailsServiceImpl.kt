package ru.dating.authservice.security

import jakarta.transaction.Transactional
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import ru.dating.authservice.repository.UserRepository

@Service
class UserDetailsServiceImpl(
    private val userRepository: UserRepository
): UserDetailsService{

    @Transactional
    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(userEmail: String): UserDetails {
        val user = userRepository.findByEmail(userEmail)
            ?: throw UsernameNotFoundException("User with email $userEmail not found")
        return user
    }
}