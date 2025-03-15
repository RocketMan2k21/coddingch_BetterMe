package app.bettermetesttask.domaincore.utils.coroutines

import app.bettermetesttask.domaincore.utils.Result
import kotlinx.coroutines.delay

suspend fun <T> retry(
    times: Int,
    delayMillis: Long = 1000,
    block: suspend () -> Result<T>
): Result<T> {
    repeat(times - 1) {
        val result = block()
        if (result is Result.Success) return result
        delay(delayMillis)
    }
    return block()
}