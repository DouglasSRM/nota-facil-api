package br.unipar.notafacil.configuration

import com.google.firebase.auth.FirebaseAuth
import jakarta.servlet.*
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component

@Component
class FirebaseTokenFilter : Filter {

    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        val httpRequest = request as HttpServletRequest
        val httpResponse = response as HttpServletResponse
        val authHeader = httpRequest.getHeader("Authorization")

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            val idToken = authHeader.removePrefix("Bearer ").trim()
            try {
                val decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken)
                val uid = decodedToken.uid
                val email = decodedToken.email ?: "unknown"

                // Autentica o usuário no contexto do Spring
                val authorities = listOf(SimpleGrantedAuthority("ROLE_USER")) // ou outros roles
                val auth = UsernamePasswordAuthenticationToken(uid, null, authorities)
                SecurityContextHolder.getContext().authentication = auth

                // (opcional) adicionar uid no request para uso fora do Spring Security
                request.setAttribute("uid", uid)
            } catch (e: Exception) {
                httpResponse.status = HttpServletResponse.SC_UNAUTHORIZED
                httpResponse.writer.write("Invalid or expired token")
                return
            }
        }

        chain.doFilter(request, response)
    }
}
