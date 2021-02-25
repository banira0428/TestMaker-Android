package jp.gr.java_conf.foobar.testmaker.service.infra.repository

import com.google.firebase.auth.FirebaseUser
import jp.gr.java_conf.foobar.testmaker.service.domain.Group
import jp.gr.java_conf.foobar.testmaker.service.infra.firebase.RemoteDataSource

class GroupRepository(private val dataSource: RemoteDataSource) {

    suspend fun getGroups(userId: String) = dataSource.getGroups(userId)
    suspend fun createGroup(group: Group) = dataSource.createGroup(group)
    suspend fun joinGroup(userId: String, group: Group) = dataSource.joinGroup(userId, group)
    fun createUser(user: FirebaseUser) = dataSource.setUser(user)
    suspend fun getTests(groupId: String) = dataSource.getTests(groupId = groupId)

}