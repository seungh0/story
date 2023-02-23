package com.story.platform.core.support.cassandra

import com.story.platform.core.CoreRoot
import com.story.platform.core.common.converter.VersionReadConverter
import com.story.platform.core.common.converter.VersionWriteConverter
import org.springframework.boot.autoconfigure.cassandra.CassandraProperties
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.data.cassandra.config.AbstractReactiveCassandraConfiguration
import org.springframework.data.cassandra.config.SchemaAction
import org.springframework.data.cassandra.core.convert.CassandraCustomConversions
import org.springframework.data.cassandra.core.cql.keyspace.CreateKeyspaceSpecification
import org.springframework.data.cassandra.core.cql.keyspace.DropKeyspaceSpecification
import org.springframework.data.cassandra.repository.config.EnableReactiveCassandraRepositories

@Profile("test")
@EntityScan(basePackageClasses = [CoreRoot::class])
@EnableReactiveCassandraRepositories(basePackageClasses = [CoreRoot::class])
@Configuration
class TestReactiveCassandraJpaConfig(
    private val cassandraProperties: CassandraProperties,
    private val versionWriteConverter: VersionWriteConverter,
    private val versionReadConverter: VersionReadConverter,
) : AbstractReactiveCassandraConfiguration() {

    override fun getKeyspaceCreations(): List<CreateKeyspaceSpecification> {
        val specification: CreateKeyspaceSpecification =
            CreateKeyspaceSpecification.createKeyspace(cassandraProperties.keyspaceName)
                .ifNotExists()
        return listOf(specification)
    }

    override fun getKeyspaceDrops(): List<DropKeyspaceSpecification> {
        val specification = DropKeyspaceSpecification.dropKeyspace(cassandraProperties.keyspaceName)
            .ifExists()
        return listOf(specification)
    }

    override fun getKeyspaceName(): String = cassandraProperties.keyspaceName

    override fun getSchemaAction(): SchemaAction {
        return SchemaAction.RECREATE
    }

    override fun customConversions(): CassandraCustomConversions {
        return CassandraCustomConversions(
            listOf(
                versionWriteConverter,
                versionReadConverter,
            )
        )
    }

}
