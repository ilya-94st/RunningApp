package com.example.runningapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.runningapp.databinding.ItemsAdapterRunBinding
import com.example.runningapp.db.Run
import com.example.runningapp.other.TrackingUtility
import java.text.SimpleDateFormat
import java.util.*


class RunAdapter : RecyclerView.Adapter<RunAdapter.RunViewHolder>() {

    inner class RunViewHolder(var binding: ItemsAdapterRunBinding) : RecyclerView.ViewHolder(binding.root)



    private val diffCallback = object : DiffUtil.ItemCallback<Run>() {
        override fun areItemsTheSame(oldItem: Run, newItem: Run): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Run, newItem: Run): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)

    fun submitList(list: List<Run>) = differ.submitList(list)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RunViewHolder {
       val binding = ItemsAdapterRunBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RunViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: RunViewHolder, position: Int) {
        val run = differ.currentList[position]
       holder.itemView.apply {
           Glide.with(this).load(run.img).into(holder.binding.ivRunImage)
       }
          holder.binding.runDate = run
    }
}



