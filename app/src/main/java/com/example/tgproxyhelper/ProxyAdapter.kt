package com.example.tgproxyhelper

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.tgproxyhelper.databinding.ItemProxyBinding

class ProxyAdapter(
    private val onOpen: (ProxyItem) -> Unit
) : RecyclerView.Adapter<ProxyAdapter.ProxyViewHolder>() {

    private val items = mutableListOf<ProxyItem>()

    fun submitList(newItems: List<ProxyItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProxyViewHolder {
        val binding = ItemProxyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProxyViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ProxyViewHolder, position: Int) = holder.bind(items[position])

    inner class ProxyViewHolder(private val binding: ItemProxyBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ProxyItem) {
            binding.serverText.text = "${item.server}:${item.port}"
            val state = if (item.isAlive) "alive" else "dead"
            val latency = item.latencyMs?.let { " • ${it}ms" }.orEmpty()
            binding.metaText.text = "$state$latency\n${item.source}"
            binding.openButton.isEnabled = item.isAlive
            binding.openButton.setOnClickListener { onOpen(item) }
        }
    }
}
