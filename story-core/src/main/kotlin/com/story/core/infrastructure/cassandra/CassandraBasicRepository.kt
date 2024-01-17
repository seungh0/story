package com.story.core.infrastructure.cassandra

import kotlinx.coroutines.flow.Flow
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.data.repository.NoRepositoryBean
import org.springframework.data.repository.Repository
import reactor.core.publisher.Mono

@NoRepositoryBean
interface CassandraBasicRepository<T, K> : Repository<T, K> {

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

    /**
     * Returns whether an entity with the given id exists.
     *
     * @param id must not be `null`.
     * @return true if an entity with the given id exists, false otherwise.
     * @throws IllegalArgumentException in case the given id is `null`.
     */
    suspend fun existsById(id: K): Boolean

    /**
     * Saves a given entity. Use the returned instance for further operations as the save operation might have changed the
     * entity instance completely.
     *
     * @param entity must not be null.
     * @return the saved entity.
     * @throws IllegalArgumentException in case the given entity is `null`.
     * @throws OptimisticLockingFailureException when the entity uses optimistic locking and has a version attribute with a different value from that
     *           found in the persistence store. Also thrown if the entity is assumed to be present but does not exist in the database.
     */
    suspend fun <S : T> save(entity: S): T

    /**
     * Saves all given entities.
     *
     * @param entities must not be null.
     * @return [Flow] emitting the saved entities.
     * @throws IllegalArgumentException in case the given [entities][Flow] or one of its entities is
     * `null`.
     * @throws OptimisticLockingFailureException when at least one entity uses optimistic locking and has a version attribute with a different value from that
     *           found in the persistence store. Also thrown if at least one entity is assumed to be present but does not exist in the database.
     */
    fun <S : T> saveAll(entities: Iterable<S>): Flow<S>

    /**
     * Deletes a given entity.
     *
     * @param entity must not be `null`.
     * @throws IllegalArgumentException in case the given entity is `null`.
     * @throws OptimisticLockingFailureException when the entity uses optimistic locking and has a version attribute with a different value from that
     *           found in the persistence store. Also thrown if the entity is assumed to be present but does not exist in the database.
     */
    suspend fun delete(entity: T)

    /**
     * Deletes all instances of the type {@code T} with the given IDs.
     * <p>
     * Entities that aren't found in the persistence store are silently ignored.
     *
     * @param ids must not be `null` nor contain any `null` values.
     * @throws IllegalArgumentException in case the given [ids][Iterable] or one of its items is `null`.
     * @since 2.5
     */
    suspend fun deleteAllById(ids: Iterable<K>)

    /**
     * Deletes the given entities.
     *
     * @param entities must not be `null`.
     * @throws IllegalArgumentException in case the given [entities][Iterable] or one of its entities is
     * `null`.
     */
    suspend fun deleteAll(entities: Iterable<T>)

}
