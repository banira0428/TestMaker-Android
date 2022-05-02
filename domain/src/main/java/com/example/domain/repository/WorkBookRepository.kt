package com.example.domain.repository

import com.example.domain.model.Folder
import com.example.domain.model.FolderId
import com.example.domain.model.Workbook
import com.example.domain.model.WorkbookId
import kotlinx.coroutines.flow.Flow

interface WorkBookRepository {

    val createFolderFlow: Flow<Folder>
    val updateFolderFlow: Flow<Folder>
    val deleteFolderFlow: Flow<FolderId>
    val createWorkbookFlow: Flow<Workbook>
    val updateWorkbookFlow: Flow<Workbook>
    val deleteWorkbookFlow: Flow<WorkbookId>

    suspend fun getFolderList(): List<Folder>
    suspend fun getFolder(folderId: FolderId): Folder
    suspend fun createFolder(folder: Folder)
    suspend fun updateFolder(folder: Folder)
    suspend fun deleteFolder(folderId: FolderId)
    suspend fun getWorkbookList(): List<Workbook>
    suspend fun getWorkbook(workbookId: WorkbookId): Workbook
    suspend fun createWorkbook(workbook: Workbook)
    suspend fun updateWorkbook(workbook: Workbook)
    suspend fun deleteWorkbook(workbookId: WorkbookId)

    suspend fun getWorkbookListByFolderName(folderName: String): List<Workbook>
}