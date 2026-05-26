package com.example.tgproxyhelper

import okhttp3.OkHttpClient
import okhttp3.Request

class ProxyRepository {
    private val client = OkHttpClient()

    private val sources = listOf(
        "https://raw.githubusercontent.com/SoliSpirit/mtproto/master/all_proxies.txt",
        "https://raw.githubusercontent.com/Grim1313/mtproto-for-telegram/master/all.txt"
    )

    fun fetchAll(): List<ProxyItem> {
        val result = mutableListOf<ProxyItem>()
        sources.forEach { url ->
            val request = Request.Builder().url(url).build()
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return@forEach
                val body = response.body?.string().orEmpty()
                body.lineSequence().forEach { line ->
                    parseProxy(line.trim(), url)?.let(result::add)
                }
            }
        }
        return result.distinctBy { "${it.server}:${it.port}:${it.secret}" }
    }

    private fun parseProxy(line: String, source: String): ProxyItem? {
        if (line.isBlank()) return null
        val cleaned = line.removePrefix("https://t.me/proxy?").removePrefix("tg://proxy?")
        val pairs = cleaned.split('&').mapNotNull {
            val p = it.split('=', limit = 2)
            if (p.size == 2) p[0] to p[1] else null
        }.toMap()
        val server = pairs["server"] ?: return null
        val port = pairs["port"]?.toIntOrNull() ?: return null
        val secret = pairs["secret"] ?: return null
        return ProxyItem(server = server, port = port, secret = secret, source = source)
    }
}
