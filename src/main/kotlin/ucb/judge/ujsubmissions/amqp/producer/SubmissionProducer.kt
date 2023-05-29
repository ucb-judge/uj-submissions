package ucb.judge.ujsubmissions.amqp.producer

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.amqp.core.AmqpTemplate
import org.springframework.amqp.core.Message
import org.springframework.amqp.core.MessageProperties
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import ucb.judge.ujsubmissions.dto.FileMetadataDto
import ucb.judge.ujsubmissions.dto.SubmissionInfoDto

@Service
class SubmissionProducer constructor(
    private val amqpTemplate: AmqpTemplate
) {

    fun sendSubmission(sourceCode: MultipartFile, submissionInfoDto: SubmissionInfoDto): Boolean {
        val contentType = sourceCode.contentType

        val messageProperties = MessageProperties()
        messageProperties.contentType = contentType
        val objectMapper = ObjectMapper()
        messageProperties.setHeader("submission", objectMapper.writeValueAsString(submissionInfoDto))
        val submission = Message(sourceCode.bytes, messageProperties)
        amqpTemplate.convertAndSend("submission2Exchange", "submission2RoutingKey", submission)
        return true
    }
}