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

    private fun getCredentialsJson(): String {
        return System.getenv("GOOGLE_CREDENTIALS_JSON")
            ?: dotenv()["GOOGLE_CREDENTIALS_JSON"]
            ?: throw IllegalStateException("GOOGLE_CREDENTIALS_JSON not found in environment or .env file.")
    }

    @Bean
    fun firestore(): Firestore {
        val credentialsJson = getCredentialsJson()
        val credentialsStream = ByteArrayInputStream(credentialsJson.toByteArray())

        if (FirebaseApp.getApps().isEmpty()) {
            val options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(credentialsStream))
                .build()
            FirebaseApp.initializeApp(options)
        }

        return FirestoreClient.getFirestore()
    }
}
