package ucb.judge.ujsubmissions.service

import io.minio.GetObjectArgs
import io.minio.GetPresignedObjectUrlArgs
import io.minio.MinioClient
import io.minio.http.Method
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class MinioService constructor(
    private val minioClient: MinioClient
) {
    @Value("\${minio.url")
    private lateinit var minioUrl: String

    fun getSharedUrl(bucket: String, filename: String): String {
        return minioClient.getPresignedObjectUrl(
            GetPresignedObjectUrlArgs.builder()
                .method(Method.GET)
                .bucket(bucket)
                .`object`(filename)
                .expiry(60 * 60)
                .build()
        )
    }

    fun getFile(bucket: String, filename: String): ByteArray {
        val inputStream = minioClient.getObject(
            GetObjectArgs.builder()
                .bucket(bucket)
                .`object`(filename)
                .build()
        )
        return inputStream.readAllBytes()
    }
}