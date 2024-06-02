package com.story.core.infrastructure.cassandra

import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.NoRepositoryBean
import org.springframework.data.repository.Repository
import reactor.core.publisher.Mono

@NoRepositoryBean
interface CassandraCounterRepository<T, K> : Repository<T, K> {

    /**
     * Returns all instances of the type.
     *
     * @return [Flow] emitting all entities.
     */
    fun findAll(): Flow<T>

    /**
     * Retrieves an entity by its id.
     *
     * @param id must not be `null`.
     * @return [Mono] emitting the entity with the given id or empty if none found.
     * @throws IllegalArgumentException in case the given id is `null`.
     */
    suspend fun findById(id: K): T?

}
