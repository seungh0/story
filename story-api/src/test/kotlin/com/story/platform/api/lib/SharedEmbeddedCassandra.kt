package com.story.platform.api.lib

import com.datastax.oss.driver.api.core.CqlSession
import com.github.nosan.embedded.cassandra.Cassandra
import com.github.nosan.embedded.cassandra.CassandraBuilder
import com.github.nosan.embedded.cassandra.Settings
import com.github.nosan.embedded.cassandra.SimpleSeedProviderConfigurator
import com.github.nosan.embedded.cassandra.cql.CqlScript
import java.io.IOException
import java.net.InetSocketAddress
import java.net.ServerSocket

object SharedEmbeddedCassandra {

    private val CASSANDRA: Cassandra = CassandraBuilder()
        .version("4.0.3")
        .addSystemProperty("cassandra.native_transport_port", findAvailableRandomPort())
        .addSystemProperty("cassandra.rpc_port", findAvailableRandomPort())
        .addSystemProperty("cassandra.storage_port", findAvailableRandomPort())
        .addSystemProperty("cassandra.jmx.local.port", findAvailableRandomPort())
        .configure(SimpleSeedProviderConfigurator("localhost:0"))
        .registerShutdownHook(true)
        .build()

    private fun findAvailableRandomPort(): Int {
        try {
            ServerSocket(0).use { server ->
                server.reuseAddress = true
                return server.localPort
            }
        } catch (exception: IOException) {
            throw IllegalArgumentException("사용 가능한 랜덤 포트를 찾는 중 에러가 발생했습니다", exception)
        }
    }

    @Synchronized
    fun start() {
        if (isRunning) {
            return
        }
        CASSANDRA.start()

        initScript()
    }

    private fun initScript() {
        val settings: Settings = getSettings()
        val session = CqlSession.builder()
            .addContactPoint(InetSocketAddress(settings.address, settings.port))
            .withLocalDatacenter("datacenter1")
            .build()

        CqlScript.ofClassPath("cql/schema.cql").forEachStatement {
            session.execute(it)
        }
    }

    @Synchronized
    fun stop() {
        if (isRunning) {
            CASSANDRA.stop()
        }
    }

    @get:Synchronized
    val isRunning: Boolean = CASSANDRA.isRunning

    @Synchronized
    fun getSettings(): Settings = CASSANDRA.settings

    @Synchronized
    fun getPort(): Int = getSettings().port

}
