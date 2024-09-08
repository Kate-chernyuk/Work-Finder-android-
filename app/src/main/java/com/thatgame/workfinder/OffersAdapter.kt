package com.thatgame.workfinder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Button
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class OffersAdapter(private val offers: List<Offer>, private val onOfferClick: (String) -> Unit) : RecyclerView.Adapter<OffersAdapter.OfferViewHolder>() {

    inner class OfferViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.offerImage)
        val title: TextView = itemView.findViewById(R.id.offerTitle)
        val button: Button = itemView.findViewById(R.id.offerButton)

        init {
            itemView.setOnClickListener {
                onOfferClick(offers[adapterPosition].link)
            }
            button.setOnClickListener {
                onOfferClick(offers[adapterPosition].link)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OfferViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_offer, parent, false)
        return OfferViewHolder(view)
    }

    override fun onBindViewHolder(holder: OfferViewHolder, position: Int) {
        val offer = offers[position]
        holder.title.text = offer.title
        holder.button.text = offer.button?.text ?: ""
        holder.button.visibility = if (offer.button != null) View.VISIBLE else View.GONE
        when (offer.id) {
            "near_vacancies" -> holder.image.setImageResource(R.drawable.img1)
            "level_up_resume" -> holder.image.setImageResource(R.drawable.img2)
            "temporary_job" -> holder.image.setImageResource(R.drawable.img3)
            else -> true
        }

    }

    override fun getItemCount() = offers.size
}