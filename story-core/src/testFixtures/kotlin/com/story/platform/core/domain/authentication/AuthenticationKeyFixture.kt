package com.story.platform.core.domain.authentication

import com.story.platform.core.common.enums.ServiceType
import com.story.platform.core.common.model.AuditingTime
import com.story.platform.core.support.RandomGenerator.generateEnum
import com.story.platform.core.support.RandomGenerator.generateString

object AuthenticationKeyFixture {

    fun create(
        serviceType: ServiceType = generateEnum(ServiceType::class.java),
        apiKey: String = generateString(),
        description: String = generateString(),
        status: AuthenticationKeyStatus = generateEnum(AuthenticationKeyStatus::class.java),
    ) = AuthenticationKey(
        key = AuthenticationKeyPrimaryKey(
            serviceType = serviceType,
            apiKey = apiKey,
        ),
        description = description,
        status = status,
        auditingTime = AuditingTime.newEntity(),
    )

}
