package com.smartvoice.recorder.domain.ai

import com.google.gson.Gson

data class SummaryResult(
    val summary: String,
    val keyPoints: List<String>,
    val actionItems: List<ActionItemResult>
)

data class ActionItemResult(
    val text: String,
    val priority: String = "medium",
    val deadline: String = ""
)

/**
 * Handles AI-powered summarization and analysis
 * Uses LLM API (OpenAI, Claude, etc.)
 */
class SummarizationService {
    private val gson = Gson()

    suspend fun summarizeTranscript(transcript: String): SummaryResult {
        // TODO: Implement actual LLM API call
        // For now, return mock data
        return SummaryResult(
            summary = "会议讨论了新产品的核心功能和开发计划，确认团队角色分配。",
            keyPoints = listOf(
                "三个核心功能模块已确认",
                "产品开发计划下周一启动，预期三个月完成",
                "团队角色分配完毕，前后端和设计均已落实"
            ),
            actionItems = listOf(
                ActionItemResult("张三提交前端设计稿", "high", "本周五"),
                ActionItemResult("李四定义API接口文档", "high", "下周一"),
                ActionItemResult("王五完成交互原型", "high", "下周一")
            )
        )
    }

    fun formatActionItems(items: List<ActionItemResult>): String {
        return gson.toJson(items)
    }

    fun formatKeyPoints(points: List<String>): String {
        return points.joinToString(", ")
    }
}
