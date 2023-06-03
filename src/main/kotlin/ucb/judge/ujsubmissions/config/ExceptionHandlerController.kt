package ucb.judge.ujsubmissions.config

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import ucb.judge.ujsubmissions.dto.ResponseDto
import ucb.judge.ujsubmissions.exception.UjBadRequestException
import ucb.judge.ujsubmissions.exception.UjForbiddenException
import ucb.judge.ujsubmissions.exception.UjNotFoundException

@ControllerAdvice
class ExceptionHandlerController {
    companion object {
        private val logger = LoggerFactory.getLogger(ExceptionHandlerController::class.java)
    }

    @ExceptionHandler(UjNotFoundException::class)
    fun handleUjNotFoundException(e: UjNotFoundException): ResponseEntity<ResponseDto<Nothing>> {
        val message = e.message ?: "Not found"
        logger.error("UjNotFoundException: ${message}")
        return ResponseEntity(ResponseDto(
            data = null,
            message = message,
            successful = false
        ), HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(UjBadRequestException::class)
    fun handleUjBadRequestException(e: UjBadRequestException): ResponseEntity<ResponseDto<Nothing>> {
        val message = e.message ?: "Bad request"
        logger.error("UjBadRequestException: $message")
        return ResponseEntity(ResponseDto(
            data = null,
            message = message,
            successful = false
        ), HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(UjForbiddenException::class)
    fun handleUjForbiddenException(e: UjForbiddenException): ResponseEntity<ResponseDto<Nothing>> {
        val message = e.message ?: "Forbidden"
        logger.error("UjForbiddenException: $message")
        return ResponseEntity(ResponseDto(
            data = null,
            message = message,
            successful = false
        ), HttpStatus.FORBIDDEN)
    }
}