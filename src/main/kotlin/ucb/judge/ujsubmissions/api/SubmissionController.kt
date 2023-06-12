package ucb.judge.ujsubmissions.api

import org.springframework.data.domain.Page
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import ucb.judge.ujsubmissions.bl.SubmissionBl
import ucb.judge.ujsubmissions.dto.*

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

    /**
     * Method to view the details of a submission. For this, the user should have the role of student.
     * A professor may view the details of a submission if the submission is from a student that is enrolled in a course
     * that the professor is teaching.
     * @param submissionId: Id of the submission.
     * @return ResponseEntity<ResponseDto<SubmissionDto>>: Response with the submission.
     */
    @GetMapping("/{id}")
    fun getSubmissionById(@PathVariable id: Long): ResponseEntity<ResponseDto<SubmissionDto>> {
        val submission = submissionBl.getSubmissionById(id)
        return ResponseEntity(ResponseDto(data = submission, message = "Submission retrieved successfully", successful = true), HttpStatus.OK)
    }

    /**
     * Method to view the status of a submission. For this, the user should have the role of student.
     * This method is used when the user wants to know if the submission has been judged.
     * @param submissionId: Id of the submission.
     * @return ResponseEntity<ResponseDto<SubmissionStatusDto>>: Response with the submission.
     */
    @GetMapping("/{id}/status")
    fun getSubmissionStatusById(@PathVariable id: Long): ResponseEntity<ResponseDto<SubmissionStatusDto>> {
        val submission = submissionBl.getSubmissionStatus(id)
        return ResponseEntity(ResponseDto(data = submission, message = "Submission retrieved successfully", successful = true), HttpStatus.OK)
    }

    @GetMapping
    fun getAllSubmissions(
        @RequestParam page: Int,
        @RequestParam size: Int
    ): ResponseEntity<ResponseDto<Page<SubmissionTableDto>>> {
        val submissions: Page<SubmissionTableDto> = submissionBl.getAllSubmissions(page, size);
        return ResponseEntity(ResponseDto(data = submissions, message = "Submissions retrieved successfully", successful = true), HttpStatus.OK)
    }
}