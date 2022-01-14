package ru.ha_inc.bedcook.game

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.ha_inc.bedcook.R
import ru.ha_inc.bedcook.databinding.ItemSelectableObjectBinding

class SelectableObjectAdapter(
    private val objects: List<SelectableObject>,
    private val itemClickListener: OnItemClickListener,
) :
    RecyclerView.Adapter<SelectableObjectAdapter.ViewHolder>() {

    fun interface OnItemClickListener {
        fun onItemClicked(selectedObject: SelectableObject)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_selectable_object, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(objects[position])
    }

    override fun getItemCount() = objects.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding by viewBinding(ItemSelectableObjectBinding::bind)

        init {
            binding.root.setOnClickListener {
                itemClickListener.onItemClicked(objects[bindingAdapterPosition])
            }
        }

        fun bind(obj: SelectableObject) {
            with(binding) {
                tvName.text = obj.name
                ivImage.setImageResource(obj.image)
            }
        }
    }

}