package ucb.judge.ujsubmissions.amqp.producer

import org.springframework.amqp.core.AmqpTemplate
import org.springframework.amqp.core.Message
import org.springframework.amqp.core.MessageProperties
import org.springframework.web.multipart.MultipartFile
import ucb.judge.ujsubmissions.dto.FileMetadataDto

class SubmissionProducer constructor(
    private val amqpTemplate: AmqpTemplate
) {

    fun sendSubmission(sourceCode: MultipartFile, metadata: FileMetadataDto): Boolean {
        val contentType = sourceCode.contentType

        val messageProperties = MessageProperties()
        messageProperties.contentType = contentType
        messageProperties.setHeader("metadata", metadata)
        val submission = Message(sourceCode.bytes, messageProperties)
        val response = amqpTemplate.convertAndSend("submission2Exchange", "submission2RoutingKey", submission)
        return true
    }
}