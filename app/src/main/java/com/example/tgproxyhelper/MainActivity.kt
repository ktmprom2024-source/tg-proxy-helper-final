package com.example.tgproxyhelper

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tgproxyhelper.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val repository = ProxyRepository()
    private val checker = ProxyChecker()
    private val adapter = ProxyAdapter(::openProxyInTelegram)
    private var bestProxy: ProxyItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.proxyRecycler.layoutManager = LinearLayoutManager(this)
        binding.proxyRecycler.adapter = adapter

        binding.refreshButton.setOnClickListener { refresh() }
        binding.openBestButton.setOnClickListener {
            bestProxy?.let(::openProxyInTelegram) ?: toast("No live proxy available yet")
        }
        binding.swipeRefresh.setOnRefreshListener { refresh() }

        refresh()
    }

    private fun refresh() {
        binding.openBestButton.isEnabled = false
        binding.statusText.text = "Loading proxy sources..."
        binding.swipeRefresh.isRefreshing = true

        lifecycleScope.launch {
            runCatching {
                val proxies = withContext(Dispatchers.IO) { repository.fetchAll() }
                withContext(Dispatchers.Main) {
                    binding.statusText.text = "Loaded ${proxies.size} proxies. Checking top candidates..."
                }
                val ranked = checker.rank(proxies)
                ranked
            }.onSuccess { ranked ->
                adapter.submitList(ranked)
                bestProxy = ranked.firstOrNull { it.isAlive }
                val alive = ranked.count { it.isAlive }
                binding.statusText.text = if (bestProxy != null) {
                    "Checked ${ranked.size}, alive: $alive, best: ${bestProxy!!.server}:${bestProxy!!.port} (${bestProxy!!.latencyMs} ms)"
                } else {
                    "Checked ${ranked.size}. No live proxy found in current batch."
                }
                binding.openBestButton.isEnabled = bestProxy != null
                binding.swipeRefresh.isRefreshing = false
            }.onFailure { e ->
                binding.statusText.text = "Error: ${e.message}"
                binding.swipeRefresh.isRefreshing = false
            }
        }
    }

    private fun openProxyInTelegram(proxy: ProxyItem) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(proxy.toTelegramUrl()))
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            toast("Telegram is not installed")
        }
    }

    private fun toast(message: String) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}
