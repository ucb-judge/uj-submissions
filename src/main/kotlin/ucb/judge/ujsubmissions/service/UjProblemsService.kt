package ucb.judge.ujsubmissions.service

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import ucb.judge.ujsubmissions.dto.ResponseDto

@FeignClient(name = "uj-problems")
interface UjProblemsService {
    @GetMapping("/api/v1/problems/{id}/exists")
    fun problemExists(@PathVariable id: Long, @RequestHeader("Authorization") token: String): ResponseDto<Boolean>
}