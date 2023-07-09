package com.story.platform.core.domain.component

import com.story.platform.core.domain.resource.ResourceId
import com.story.platform.core.domain.resource.ResourceNotFoundException
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

    test("존재하지 않는 ResourceId code인 경우 NotFoundException") {
        // when & then
        shouldThrowExactly<ResourceNotFoundException> {
            ResourceId.findByCode("Unknown")
        }
    }

})
