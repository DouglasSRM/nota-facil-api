package br.unipar.notafacil.configuration

import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.firestore.Firestore
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.cloud.FirestoreClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import io.github.cdimascio.dotenv.dotenv
import java.io.ByteArrayInputStream

@Configuration
class FirestoreConfig {

    @Bean
    fun firestore(): Firestore {
        // Tenta primeiro via variável de ambiente real (produção)
        var credentialsJson = System.getenv("GOOGLE_CREDENTIALS_JSON")

        // Se estiver rodando localmente, tenta usar o .env
        if (credentialsJson.isNullOrBlank()) {
            val dotenv = dotenv()
            credentialsJson = dotenv["GOOGLE_CREDENTIALS_JSON"]
        }

        if (credentialsJson.isNullOrBlank()) {
            throw IllegalStateException("GOOGLE_CREDENTIALS_JSON not found in environment or .env file.")
        }

        val credentialsStream = ByteArrayInputStream(credentialsJson.toByteArray())

        val options = FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.fromStream(credentialsStream))
            .build()

        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options)
        }

        return FirestoreClient.getFirestore()
    }
}