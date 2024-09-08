package com.thatgame.workfinder

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class JobAdapter(private var jobList: List<Job>, private val listener: OnFavoriteIconClickListener, private val onJobClick: (Job) -> Unit) :
    RecyclerView.Adapter<JobAdapter.JobViewHolder>() {

    inner class JobViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val viewingCount: TextView = itemView.findViewById(R.id.viewingCount)
        val jobTitle: TextView = itemView.findViewById(R.id.jobTitle)
        val jobLocation: TextView = itemView.findViewById(R.id.jobLocation)
        val companyName: TextView = itemView.findViewById(R.id.companyName)
        val experience: TextView = itemView.findViewById(R.id.experience)
        val publishedDate: TextView = itemView.findViewById(R.id.publishedDate)
        val favoriteIcon: ImageView = itemView.findViewById(R.id.favoriteIcon)
        val applyButton: Button = itemView.findViewById(R.id.applyButton)

        init {
            itemView.setOnClickListener {
                onJobClick(jobList[bindingAdapterPosition])
            }
            favoriteIcon.setOnClickListener {
                val job = jobList[adapterPosition]
                job.isFavorite = !job.isFavorite
                updateFavoriteIcon(favoriteIcon, job.isFavorite)
                listener.onJobLongClick(job)
            }
        }
    }

    private fun updateFavoriteIcon(icon: ImageView, isFavorite: Boolean) {
        icon.setImageResource(if (isFavorite) R.drawable.baseline_favorite_24 else R.drawable.baseline_favorite_border_24)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_job, parent, false)
        return JobViewHolder(view)
    }

    override fun onBindViewHolder(holder: JobViewHolder, position: Int) {
        val job = jobList[position]

        holder.viewingCount.text = job.lookingNumber?.let { "Сейчас просматривает $it ${getPersonWord(it)}" }
        holder.jobTitle.text = job.title
        holder.jobLocation.text = job.address?.town ?: ""
        holder.companyName.text = job.company
        holder.experience.text = job.experience?.previewText ?: ""
        holder.publishedDate.text = job.publishedDate?.let { formatDate(it) }
        holder.favoriteIcon.setImageResource(if (job.isFavorite) R.drawable.baseline_favorite_24 else R.drawable.baseline_favorite_border_24)

    }

    override fun getItemCount() = jobList.size

    fun updateItems(newItems: List<Job>) {
        this.jobList = newItems
        notifyDataSetChanged()
    }

    private fun getPersonWord(lookingNumber: Int): String {
        return when {
            lookingNumber % 10 == 1 && lookingNumber % 100 != 11 -> "человек"
            lookingNumber % 10 in 2..4 && (lookingNumber % 100 < 10 || lookingNumber % 100 >= 20) -> "человека"
            else -> "человек"
        }
    }

    private fun formatDate(publishedDate: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMMM", Locale.getDefault())

        val date: Date = inputFormat.parse(publishedDate) ?: return ""
        return "Опубликовано ${outputFormat.format(date)}"
    }


}