package com.example.usecase

import com.example.domain.repository.SharedWorkbookRepository
import com.example.domain.repository.UserRepository
import com.example.usecase.model.SharedWorkbookUseCaseModel
import com.example.usecase.utils.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedWorkbookListWatchUseCase @Inject constructor(
    private val repository: SharedWorkbookRepository,
    private val userRepository: UserRepository,
) {

    private val _flow: MutableStateFlow<Resource<List<SharedWorkbookUseCaseModel>>> =
        MutableStateFlow(Resource.Empty)
    val flow: StateFlow<Resource<List<SharedWorkbookUseCaseModel>>> = _flow

    fun setup(scope: CoroutineScope) {
    }

    suspend fun load() {
        _flow.emit(Resource.Loading)
        val user = userRepository.getUserOrNull()

        if (user != null) {
            val workbookList = repository.getWorkbookListByUserId(userId = user.id)
            _flow.emit(Resource.Success(workbookList.map {
                SharedWorkbookUseCaseModel.fromSharedWorkbook(it)
            }))
        } else {
            _flow.emit(Resource.Failure("does not exist user"))
        }
    }
}

