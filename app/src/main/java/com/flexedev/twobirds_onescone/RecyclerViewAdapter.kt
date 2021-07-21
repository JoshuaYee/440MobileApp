package com.flexedev.twobirds_onescone

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.flexedev.twobirds_onescone.data.entities.SconeWithRatings
import com.squareup.picasso.Picasso

class RecyclerViewAdapter(private var sconesList: List<SconeWithRatings>, private val onSconeListener: OnSconeListener) : RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder>() {


    class MyViewHolder(
        itemView: View,
        shareBtn: ImageButton,
        directionBtn: ImageButton,
        private val onSconeListener: OnSconeListener
    ) :
        RecyclerView.ViewHolder(itemView), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
            shareBtn.setOnClickListener(this)
            directionBtn.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            Log.d("SWITCH STATE SHARE ID", R.id.shareBtn.toString())
            Log.d("SWITCH STATE DIRECTION", R.id.directionBtn.toString())
            Log.d("ID SWITCH VIEW ID", v?.id.toString())
            when (v?.id) {
                R.id.custom_row -> onSconeListener.onSconeClick(absoluteAdapterPosition)
                R.id.shareBtn -> onSconeListener.onShareClick(absoluteAdapterPosition)
                R.id.directionBtn -> onSconeListener.onDirectionClick(absoluteAdapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.custom_scone_row, parent, false
        )
        val shareBtn = view.findViewById<ImageButton>(R.id.shareBtn)
        val directionBtn = view.findViewById<ImageButton>(R.id.directionBtn)
        return MyViewHolder(view, shareBtn,directionBtn, onSconeListener)
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        try {
            val currentScone = sconesList[position]
            val sconeScore = currentScone.ratings[0].score
            val photoView = holder.itemView.findViewById<ImageView>(R.id.customImageView)
            val photoURL = currentScone.scone.image.toUri()

            holder.itemView.findViewById<TextView>(R.id.sconeStore).text =
                currentScone.scone.sconeBusiness
            holder.itemView.findViewById<TextView>(R.id.sconeName).text =
                currentScone.scone.sconeName
            holder.itemView.findViewById<TextView>(R.id.sconeNotes).text =
                currentScone.ratings[0].notes
            holder.itemView.findViewById<RatingBar>(R.id.scoreBar).rating = sconeScore.toFloat()


            // The resolution is 100x100 pixels to allow for old phones
            Picasso.get()
                .load(photoURL)
                .placeholder(R.drawable.placeholderscone)
                .resize(100, 100)
                .centerCrop()
                .into(photoView)


        } catch (e: IndexOutOfBoundsException) {
            Log.d("RECYCLER VIEW", "index out of bounds error")
        }
    }


    override fun getItemCount(): Int {
        return sconesList.size
    }


    fun setScone(sconeWithR: List<SconeWithRatings>) {
        this.sconesList = sconeWithR
        notifyDataSetChanged()
    }

    fun deleteItem(pos: Int) {
        val currentScone = this.sconesList[pos]
        onSconeListener.onSconeSwipe(currentScone)
        notifyItemRemoved(pos)
    }

    interface OnSconeListener {
        fun onSconeClick(position: Int)
        fun onSconeSwipe(scone: SconeWithRatings)
        fun onShareClick(position: Int)
        fun onDirectionClick(position: Int)
    }

}