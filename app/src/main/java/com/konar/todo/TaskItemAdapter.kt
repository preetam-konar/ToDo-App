package com.konar.todo

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.konar.todo.databinding.ListItemBinding
import com.konar.todo.db.TaskEntity

class TaskItemAdapter(
    private val items: ArrayList<TaskEntity>,
    val setCheckedListener: (id: Int, isChecked: Boolean) -> Unit,
) :
    RecyclerView.Adapter<TaskItemAdapter.ViewHolder>() {

    class ViewHolder(binding: ListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val tvTitle = binding.tvTitle
        val tvDescription = binding.tvDesc
        val clRowItem = binding.clRowItem
        val cbSelect = binding.cbSelect
        val tvPriority = binding.tvPriorityChoice
        val ivPriorityFlag = binding.ivPriorityFlag
        val tvStatus = binding.tvStatus

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.tvTitle.text = item.title
        holder.tvDescription.text = item.desc
        holder.tvPriority.text = item.priority
        holder.tvStatus.text = item.status

        holder.cbSelect.setOnCheckedChangeListener(null)

        holder.cbSelect.isChecked = item.isChecked

        holder.cbSelect.setOnClickListener {
            setCheckedListener.invoke(
                item.id,
                holder.cbSelect.isChecked
            )
        }


        when (holder.tvPriority.text.toString()) {
            "High" -> {
                ImageViewCompat.setImageTintList(
                    holder.ivPriorityFlag,
                    ColorStateList.valueOf(Color.parseColor("#FF0000"))
                )
            }

            "Medium" -> {
                ImageViewCompat.setImageTintList(
                    holder.ivPriorityFlag,
                    ColorStateList.valueOf(Color.parseColor("#FFA500"))
                )
            }

            "Low" -> {
                ImageViewCompat.setImageTintList(
                    holder.ivPriorityFlag,
                    ColorStateList.valueOf(Color.parseColor("#FFFF00"))
                )
            }
        }

        if (holder.tvStatus.text == "Completed") {
            ImageViewCompat.setImageTintList(
                holder.ivPriorityFlag,
                ColorStateList.valueOf(Color.parseColor("#00FF00"))
            )
        }


        if (position % 2 == 0) {
            holder.clRowItem.setBackgroundColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.colorLightGrey
                )
            )
        } else {
            holder.clRowItem.setBackgroundColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.white
                )
            )
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
}