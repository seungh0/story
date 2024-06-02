package com.story.core.lib

import com.story.core.domain.reaction.ReactionEntity
import com.story.core.domain.reaction.ReactionEntityFixture
import com.story.core.domain.reaction.ReactionPrimaryKey
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.toList

class StubCassandraBasicRepositoryTest : StringSpec({

    val repository = StubCassandraBasicRepository<ReactionEntity, ReactionPrimaryKey>()

    afterEach {
        repository.clear()
    }

    "save" {
        // given
        val reaction = ReactionEntityFixture.create()

        // when
        repository.save(reaction)

        // then
        val sut = repository.findAll().toList()
        sut shouldHaveSize 1
        sut.first() shouldBe reaction
    }

    "delete" {
        // given
        val reaction = ReactionEntityFixture.create()
        repository.save(reaction)

        // when
        repository.delete(reaction)

        // then
        val sut = repository.findAll().toList()
        sut shouldHaveSize 0
    }

    "delete another entity" {
        // given
        val reaction1 = ReactionEntityFixture.create()
        val reaction2 = ReactionEntityFixture.create()
        repository.save(reaction1)

        // when
        repository.delete(reaction2)

        // then
        val sut = repository.findAll().toList()
        sut shouldHaveSize 1
        sut.first() shouldBe reaction1
    }

})
