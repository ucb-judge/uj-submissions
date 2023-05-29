package ucb.judge.ujsubmissions.dto

data class ResponseDto<T>(
    val data: T? = null,
    val message: String = "",
    val successful: Boolean = false
);
