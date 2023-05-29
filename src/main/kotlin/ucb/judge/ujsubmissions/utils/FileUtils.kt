package ucb.judge.ujsubmissions.utils

import org.springframework.mock.web.MockMultipartFile

class FileUtils {
    companion object {

        @JvmStatic
        fun createSubmissionFile(code: String, extension: String): MockMultipartFile {
            val bytes = code.toByteArray()
            return MockMultipartFile("submission.${extension}", "submission.${extension}", "text/plain", bytes)
        }
    }
}