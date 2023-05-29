package ucb.judge.ujsubmissions.api

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ucb.judge.ujsubmissions.bl.SubmissionBl
import ucb.judge.ujsubmissions.dto.NewSubmissionDto
import ucb.judge.ujsubmissions.dto.ResponseDto

@RestController
@RequestMapping("/api/v1/submissions")
class SubmissionController constructor(
    private val submissionBl: SubmissionBl
) {

    /**
     * Method to submit a solution to a problem. For this, the user should have the role of student.
     * @param submission: DTO with the information of the submission.
     * @return ResponseEntity<ResponseDto<Long>>: Response with the id of the new submission.
     */
    @PostMapping
    fun createSubmission(@RequestBody submission: NewSubmissionDto): ResponseEntity<ResponseDto<Long>> {
        val id = submissionBl.createSubmission(submission)
        return ResponseEntity(ResponseDto(data = id, message = "Submission created successfully", successful = true), HttpStatus.CREATED)
    }
}