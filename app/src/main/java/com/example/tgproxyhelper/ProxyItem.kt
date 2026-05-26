package com.example.tgproxyhelper

data class ProxyItem(
    val server: String,
    val port: Int,
    val secret: String,
    val source: String,
    val latencyMs: Long? = null,
    val isAlive: Boolean = false
) {
    fun toTelegramUrl(): String =
        "tg://proxy?server=$server&port=$port&secret=${java.net.URLEncoder.encode(secret, "UTF-8")}"
}
