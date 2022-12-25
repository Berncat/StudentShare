package ie.wit.studentshare.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ie.wit.studentshare.databinding.CardStudentShareBinding
import ie.wit.studentshare.models.StudentShareModel
import ie.wit.studentshare.ui.listings.ListingsFragment

interface LettingsClickListener {
    fun onLettingsClick(letting: StudentShareModel)
}


class Adapter(private var lettings: ArrayList<StudentShareModel>,
              private val listener: LettingsClickListener,
              private val favourites: Boolean)
    : RecyclerView.Adapter<Adapter.MainHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        val binding = CardStudentShareBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return MainHolder(binding,favourites)
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val letting = lettings[holder.adapterPosition]
        holder.bind(letting,listener)
    }

    fun removeAt(position: Int) {
        lettings.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun getItemCount(): Int = lettings.size

    inner class MainHolder(val binding : CardStudentShareBinding, private val favourites : Boolean) :
        RecyclerView.ViewHolder(binding.root) {

        val favouritesRow = favourites

        fun bind(letting: StudentShareModel, listener: LettingsClickListener) {
            binding.root.tag = letting
            binding.letting = letting
            binding.root.setOnClickListener { listener.onLettingsClick(letting) }
            binding.executePendingBindings()
        }
    }
}

