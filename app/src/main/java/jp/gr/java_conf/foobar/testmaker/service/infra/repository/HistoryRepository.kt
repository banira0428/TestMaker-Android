package jp.gr.java_conf.foobar.testmaker.service.infra.repository

import jp.gr.java_conf.foobar.testmaker.service.domain.History
import jp.gr.java_conf.foobar.testmaker.service.infra.firebase.RemoteDataSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HistoryRepository @Inject constructor(
    private val dataSource: RemoteDataSource
) {
    suspend fun getHistories(documentId: String) = dataSource.getHistories(documentId)
    suspend fun createHistory(documentId: String, history: History) =
        dataSource.createHistory(documentId, history)
}