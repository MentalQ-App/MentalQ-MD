package com.c242_ps246.mentalq.data.remote.response

data class GeminiResponse(
    val candidates: List<GeminiCandidate>
)

data class GeminiRequest(
    val contents: GeminiRequestContent
)

data class GeminiRequestContent(
    val parts: GeminiPart
)

data class GeminiCandidate(
    val content: GeminiContent
)

data class GeminiContent(
    val parts: List<GeminiPart>
)

data class GeminiPart(
    val text: String,
)