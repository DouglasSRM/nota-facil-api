package br.unipar.notafacil.configuration

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class SecurityConfig(
    @Value("\${app.cors.allowed-origins}") private val allowedOriginsRaw: String
) {
    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .cors(Customizer.withDefaults())
            .csrf { it.disable() }
            .authorizeHttpRequests {
                it
                    .requestMatchers(
                        "/notafacil/get",
                        "/notafacil/post",
                        "/notafacil/update/**",
                        "/notafacil/delete"
                    ).permitAll()
                    .anyRequest().authenticated()
            }

        return http.build()
    }

    @Bean
    fun corsFilter(): CorsFilter {
        val allowedOrigins = allowedOriginsRaw.split(",").map { it.trim() }

        val config = CorsConfiguration()
        config.allowedOrigins = allowedOrigins
        config.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS")
        config.allowedHeaders = listOf("*")
        config.allowCredentials = true

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", config)
        return CorsFilter(source)
    }

    @Configuration
    class WebConfig : WebMvcConfigurer {
        override fun addCorsMappings(registry: CorsRegistry) {
            registry.addMapping("/**")
                .allowedOrigins(System.getenv("APP_CORS_ALLOWED_ORIGINS"))
                .allowedMethods("*")
        }
    }
}

