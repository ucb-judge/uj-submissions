package ucb.judge.ujsubmissions.service

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import ucb.judge.ujsubmissions.dto.LanguageDto
import ucb.judge.ujsubmissions.dto.ProblemMinimalDto
import ucb.judge.ujsubmissions.dto.ResponseDto
import ucb.judge.ujsubmissions.dto.TestcaseDto

@FeignClient(name = "uj-problems")
interface UjProblemsService {
    @GetMapping("/api/v1/problems/{id}/exists")
    fun problemExists(@PathVariable id: Long, @RequestHeader("Authorization") token: String): ResponseDto<Boolean>

    @GetMapping("/api/v1/problems/{id}/testcases")
    fun getProblemTestcases(@PathVariable id: Long, @RequestHeader("Authorization") token: String): ResponseDto<List<TestcaseDto>>

    @GetMapping("/api/v1/problems/{id}/minimal")
    fun getProblemMinimal(@PathVariable id: Long, @RequestHeader("Authorization") token: String): ResponseDto<ProblemMinimalDto>

    @GetMapping("/api/v1/problems/{id}/languages")
    fun getAllAdmittedLanguages(@PathVariable id: Long, @RequestHeader("Authorization") token: String): ResponseDto<List<LanguageDto>>
}