package ru.dating.authservice.security

import io.jsonwebtoken.io.IOException
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Service
import org.springframework.web.filter.OncePerRequestFilter
import ru.dating.authservice.service.JwtService
@Service
class JwtFilter (
    private val jwtService: JwtService,
    private val userDetailsService: UserDetailsService
): OncePerRequestFilter() {
    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        // request.servletPath.contains("/api/auth/register")
        if (request.servletPath.contains("/api/auth")) {
            filterChain.doFilter(request, response)
            return
        }
        val authHeader = request.getHeader(HttpHeaders.AUTHORIZATION)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response)
            return
        }
        val jwt = authHeader.substring("Bearer ".length)
        val userEmail = jwtService.extractUsername(jwt)
        if (userEmail != null &&
            SecurityContextHolder.getContext().authentication == null) {
            val userDetails: UserDetails = userDetailsService.loadUserByUsername(userEmail)
            if (jwtService.isTokenValid(jwt, userDetails)) {
                val authToken = UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.authorities)
                authToken.details = WebAuthenticationDetailsSource()
                    .buildDetails(request)
                SecurityContextHolder.getContext().authentication = authToken
            }
        }
        filterChain.doFilter(request, response)
    }
}