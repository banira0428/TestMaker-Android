package com.example.usecase

import com.example.domain.model.AnswerStatus
import com.example.domain.model.WorkbookId
import com.example.domain.repository.WorkBookRepository
import com.example.usecase.model.WorkbookUseCaseModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserWorkbookCommandUseCase @Inject constructor(
    private val workBookRepository: WorkBookRepository
) {

    suspend fun createWorkbook(name: String, color: Int, folderName: String) =
        workBookRepository.createWorkbook(name, color, folderName)

    suspend fun updateWorkbook(workbookId: Long, name: String, color: Int, folderName: String) {
        val workbook = workBookRepository.getWorkbook(workbookId = WorkbookId(workbookId))
        workBookRepository.updateWorkbook(
            workbook.copy(
                name = name,
                color = color,
                folderName = folderName
            )
        )
    }

    suspend fun deleteWorkbook(workbook: WorkbookUseCaseModel) =
        workBookRepository.deleteWorkbook(WorkbookId(workbook.id))

    suspend fun exportWorkbook(workbookId: Long): String {
        val workbook = workBookRepository.getWorkbook(WorkbookId(workbookId))
        return workBookRepository.exportWorkbook(workbook).value
    }

    suspend fun swapWorkbooks(sourceWorkbookId: Long, destWorkbookId: Long) {
        val sourceWorkbook = workBookRepository.getWorkbook(WorkbookId(sourceWorkbookId))
        val destWorkbook = workBookRepository.getWorkbook(WorkbookId(destWorkbookId))
        workBookRepository.swapWorkbook(sourceWorkbook, destWorkbook)
    }

    suspend fun resetWorkbookAchievement(workbookId: Long) {
        val workbook = workBookRepository.getWorkbook(WorkbookId(workbookId))
        val newQuestionList =
            workbook.questionList.map { it.copy(answerStatus = AnswerStatus.UNANSWERED) }
        workBookRepository.updateWorkbook(workbook.copy(questionList = newQuestionList))
    }
}