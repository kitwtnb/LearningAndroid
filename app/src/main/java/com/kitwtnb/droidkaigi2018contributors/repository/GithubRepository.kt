package com.kitwtnb.droidkaigi2018contributors.repository

import com.kitwtnb.droidkaigi2018contributors.datastore.data.Contributor
import com.kitwtnb.droidkaigi2018contributors.datastore.db.ContributorDao
import com.kitwtnb.droidkaigi2018contributors.datastore.db.deferredDelete
import com.kitwtnb.droidkaigi2018contributors.datastore.db.deferredFetch
import com.kitwtnb.droidkaigi2018contributors.datastore.db.deferredInsert
import com.kitwtnb.droidkaigi2018contributors.datastore.service.GithubService
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async

interface GithubRepository {
    suspend fun loadContributors(owner: String, repository: String): Deferred<List<Contributor>>
    suspend fun deleteContributors(): Deferred<Unit>
}

class GithubRepositoryImpl(
        private val service: GithubService,
        private val contributorDao: ContributorDao
) : GithubRepository {
    override suspend fun loadContributors(owner: String, repository: String): Deferred<List<Contributor>> {
        val cache = contributorDao.deferredFetch().await()
        if (cache.isNotEmpty()) {
            return async { cache }
        }

        val contributors = service.contributors(owner, repository).await()
        contributorDao.deferredInsert(contributors).await()

        return async { contributors }
    }

    override suspend fun deleteContributors(): Deferred<Unit> = contributorDao.deferredDelete()
}
