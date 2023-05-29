package ucb.judge.ujsubmissions.bl

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import ucb.judge.ujsubmissions.dto.KeycloakTokenReqDto
import ucb.judge.ujsubmissions.service.KeycloakService

@Service
class KeycloakBl constructor(
    private val keycloakService: KeycloakService
) {
    @Value("\${keycloak.resource}")
    private lateinit var resource: String

    @Value("\${keycloak.credentials.secret}")
    private lateinit var credentialsSecret: String

    fun getToken(): String {
        val tokenReqDto = KeycloakTokenReqDto(
            "client_credentials",
            resource,
            credentialsSecret
        )
        val response = keycloakService.getToken(tokenReqDto)
        return response.accessToken;
    }
}
