package com.example.tgproxyhelper

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import java.net.InetSocketAddress
import java.net.Socket

class ProxyChecker {
    suspend fun rank(proxies: List<ProxyItem>, timeoutMs: Int = 2500, limit: Int = 40): List<ProxyItem> = coroutineScope {
        proxies.take(limit).map { proxy ->
            async {
                withContext(Dispatchers.IO) {
                    val started = System.currentTimeMillis()
                    try {
                        Socket().use { socket ->
                            socket.connect(InetSocketAddress(proxy.server, proxy.port), timeoutMs)
                        }
                        proxy.copy(isAlive = true, latencyMs = System.currentTimeMillis() - started)
                    } catch (_: Exception) {
                        proxy.copy(isAlive = false)
                    }
                }
            }
        }.awaitAll().sortedWith(compareBy<ProxyItem>({ !it.isAlive }, { it.latencyMs ?: Long.MAX_VALUE }))
    }
}
