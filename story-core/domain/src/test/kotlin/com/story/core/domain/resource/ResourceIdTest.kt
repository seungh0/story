package com.story.core.domain.resource

import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class ResourceIdTest : FunSpec({

    test("ResourceId code를 통해서 ResourceId를 찾는다") {
        // when
        val sut = ResourceId.findByCode("posts")

        // then
        sut shouldBe ResourceId.POSTS
    }

    test("ResourceId code를 통해서 ResourceId를 찾을때 대소문자를 구분하지 않는다") {
        // when
        val sut = ResourceId.findByCode("POsts")

        // then
        sut shouldBe ResourceId.POSTS
    }

    test("존재하지 않는 ResourceId code인 경우 ResourceNotExistsException 를 반환한다") {
        // when & then
        shouldThrowExactly<ResourceNotExistsException> {
            ResourceId.findByCode("Unknown")
        }
    }

})