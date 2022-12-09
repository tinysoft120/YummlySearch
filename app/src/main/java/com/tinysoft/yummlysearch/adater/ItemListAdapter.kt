package com.tinysoft.yummlysearch.adater

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.tinysoft.yummlysearch.Constants
import com.tinysoft.yummlysearch.GlideApp
import com.tinysoft.yummlysearch.databinding.ItemLastBinding
import com.tinysoft.yummlysearch.databinding.ItemListBinding
import com.tinysoft.yummlysearch.databinding.ItemLoadingBinding
import com.tinysoft.yummlysearch.network.model.Matche

class ItemListAdapter(
    private val activity: FragmentActivity,
    private var dataSet: List<Matche>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class LoadingViewHolder(binding: ItemLoadingBinding) : RecyclerView.ViewHolder(binding.root)
    inner class LastViewHolder(val binding: ItemLastBinding) : RecyclerView.ViewHolder(binding.root)
    inner class ItemViewHolder(val binding: ItemListBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.contentContainer.setOnClickListener {
                val item = dataSet[absoluteAdapterPosition]
//                activity.supportFragmentManager.beginTransaction()
//                    .replace(R.id.container, SearchFragment.newInstance())
//                    .commitNow()
//                activity.findNavController(R.id.container).navigate(
//                    R.id.container,
//                    bundleOf(EXTRA_RECIPE_ID to item),
//                    null,
//                    FragmentNavigatorExtras(image!! to "${item.id}")
//                )
            }
        }
    }

    /**
     * The getAllResult is a flag to show done items all from api
     * */
    private var getAllResult: Boolean = false

    fun setGetAllResult(getAllResult: Boolean) {
        this.getAllResult = getAllResult
    }

    @SuppressLint("NotifyDataSetChanged")
    fun swapDataSet(dataSet: List<Matche>, addedCount: Int) {
        this.dataSet = dataSet
        setGetAllResult(addedCount != Constants.SEARCH_LIMIT)
        if (dataSet.size == addedCount) {
            notifyDataSetChanged()
        } else {
            // insert new data
            notifyItemRangeInserted(dataSet.size - addedCount, addedCount)
            // reload last cell
            notifyItemChanged(dataSet.size)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < dataSet.size) {
            VIEW_TYPE_ITEM
        } else if (getAllResult) {
            VIEW_TYPE_LAST
        } else {
            VIEW_TYPE_LOADING
        }
    }

    override fun getItemCount(): Int {
        return dataSet.size + 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_LOADING -> LoadingViewHolder(
                ItemLoadingBinding.inflate(LayoutInflater.from(activity), parent, false)
            )
            VIEW_TYPE_LAST -> LastViewHolder(
                ItemLastBinding.inflate(LayoutInflater.from(activity), parent, false)
            )
            else -> ItemViewHolder(
                ItemListBinding.inflate(LayoutInflater.from(activity), parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ItemViewHolder -> {
                val item = dataSet[position]
                with (holder.binding) {
                    title.text = item.sourceDisplayName
                    content1.text = "${item.ingredients.size}"
                    content2.text = "--"
                    content3.text = "${item.totalTimeInMins}m"
                    item.smallImageUrls?.getOrNull(0)?.let {
                        GlideApp.with(activity).asDrawable()
                            .load(it)
                            .into(image)
                        image.let { iv ->
                            ViewCompat.setTransitionName(iv, item.id)
                        }
                    }
                }
            }
            is LastViewHolder -> {
                holder.binding.title.text = "Totally found ${dataSet.size} items"
            }
        }
    }

    companion object {
        const val VIEW_TYPE_LOADING = 0
        const val VIEW_TYPE_LAST = 1
        const val VIEW_TYPE_ITEM = 2

    }
}