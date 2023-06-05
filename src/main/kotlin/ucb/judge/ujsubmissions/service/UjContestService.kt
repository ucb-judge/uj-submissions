package ucb.judge.ujsubmissions.service

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam
import ucb.judge.ujsubmissions.dto.ContestDto
import ucb.judge.ujsubmissions.dto.ResponseDto

@FeignClient(name = "uj-contests")
interface UjContestService {
    @GetMapping("/api/v1/contests/{id}")
    fun getContestById(
        @PathVariable id: Long,
        @RequestHeader("Authorization") token: String
    ): ResponseDto<ContestDto>

    @GetMapping("/api/v1/contests/{id}/validate")
    fun validateContestSubmission(
        @PathVariable id: Long,
        @RequestHeader("Authorization") token: String,
        @RequestParam(name = "kcUuid") kcUuid: String,
        @RequestParam(name = "problemId") problemId: Long,
    ): ResponseDto<Boolean>
}