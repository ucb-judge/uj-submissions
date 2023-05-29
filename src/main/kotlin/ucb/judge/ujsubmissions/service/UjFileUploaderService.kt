package ucb.judge.ujsubmissions.service

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.multipart.MultipartFile
import ucb.judge.ujsubmissions.dto.FileDto
import ucb.judge.ujsubmissions.dto.ResponseDto

@FeignClient(name = "uj-file-uploader")
interface UjFileUploaderService {

    @PostMapping("/api/v1/files", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun uploadFile(
        @RequestPart(value = "file") file: MultipartFile,
        @RequestPart(value = "bucket") bucket: String,
        @RequestPart(value = "customFilename") customFilename: Boolean = false
    ): ResponseDto<FileDto>
}