package com.c242_ps246.mentalq.data.remote.response

data class GeminiResponse(
    val candidates: List<GeminiCandidate>?,
    val modelVersion: String?,
    val responseId: String?,
    val usageMetadata: GeminiUsageMetadata?
)

data class GeminiCandidate(
    val content: GeminiContent?,
    val finishReason: String?,
    val safetyRatings: List<GeminiSafetyRating>?
)

data class GeminiContent(
    val parts: List<GeminiPart>?,
    val role: String? // "model" or "user"
)

data class GeminiPart(
    val text: String?
)

data class GeminiSafetyRating(
    val category: String?,
    val probability: String?
)

data class GeminiUsageMetadata(
    val promptTokenCount: Int?,
    val candidatesTokenCount: Int?,
    val totalTokenCount: Int?
)

data class GeminiRequest(
    val contents: List<GeminiRequestContent>,
    val safetySettings: List<GeminiSafetySettings>? = null
)

data class GeminiRequestContent(
    val role: String = "user",
    val parts: List<GeminiPart>
)

data class GeminiSafetySettings(
    val category: String,
    val threshold: String
)
