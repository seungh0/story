package com.story.datacenter.core.common.partition

import com.story.datacenter.core.common.error.InternalServerException
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.ServerSocket
import java.util.*

object ProcessUtils {
    private val OS = System.getProperty("os.name").lowercase(Locale.getDefault())

    @Throws(IOException::class)
    fun isRunningPort(port: Int): Boolean {
        return isRunning(executeGrepProcessCommand(port))
    }

    fun findAvailableRandomPort(): Int {
        try {
            ServerSocket(0).use { server ->
                server.reuseAddress = true
                return server.localPort
            }
        } catch (exception: IOException) {
            throw InternalServerException("사용 가능한 랜덤 포트를 찾는 중 에러가 발생했습니다", exception)
        }
    }

    private fun executeGrepProcessCommand(port: Int): Process {
        if (isWindows) {
            throw InternalServerException("프로세스 실행 여부 확인은 Windows OS는 지원하지 않습니다")
        }
        val command = String.format("netstat -nat | grep LISTEN|grep %d", port)
        val shell = arrayOf("/bin/sh", "-c", command)
        return Runtime.getRuntime().exec(shell)
    }

    private val isWindows: Boolean
        get() = OS.contains("win")

    private fun isRunning(process: Process): Boolean {
        val pidInfo = StringBuilder()
        runCatching {
            BufferedReader(InputStreamReader(process.inputStream)).use { input ->
                val line = input.readLine()
                while (line != null) {
                    pidInfo.append(line)
                }
            }
        }.onFailure { throwable ->
            throw InternalServerException("프로세스 사용 여부를 확인 중 에러가 발생하였습니다", throwable)
        }
        return pidInfo.isNotBlank()
    }

}
