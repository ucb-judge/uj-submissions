package ucb.judge.ujsubmissions.utils

import org.springframework.mock.web.MockMultipartFile

class FileUtils {
    companion object {
        // map that contains the language and the extension
        val languages = mapOf(
            "c++" to "cpp",
            "java" to "java",
            "python" to "py",
        )

        @JvmStatic
        fun createSubmissionFile(code: String, language: String): MockMultipartFile {
            val extension = languages[language]
            val bytes = code.toByteArray()
            return MockMultipartFile("submission.${extension}", "submission.${extension}", "text/plain", bytes)
        }
    }
}