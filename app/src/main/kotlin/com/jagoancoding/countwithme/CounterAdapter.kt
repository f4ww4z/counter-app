package com.jagoancoding.countwithme

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class CounterAdapter(private var counts: ArrayList<Counter>)
    : RecyclerView.Adapter<CounterAdapter.CounterHolder>() {

    private lateinit var itemListener: ItemStateListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CounterHolder {
        val view = LayoutInflater
                .from(parent.context)
                .inflate(R.layout.counter_item, parent, false)
        itemListener = parent.context as ItemStateListener
        return CounterHolder(view)
    }

    override fun getItemCount(): Int = counts.size

    override fun onBindViewHolder(holder: CounterHolder, position: Int) {
        var counter: Counter = counts[position]
        holder.titleTV.text = counter.title
        holder.counterTV.text = counter.count.toString()
        // Clicks handled here
        holder.itemView.setOnClickListener({ v ->
            Log.d("RecyclerView", "item $position clicked")
            Log.d("RecyclerView", "increase count to ${counter.count + 1}")
            itemListener.incrementCount(counter)
        })
    }

    class CounterHolder(rootView: View) : RecyclerView.ViewHolder(rootView) {
        var titleTV: TextView = rootView.findViewById(R.id.tv_title)
        var counterTV: TextView = rootView.findViewById(R.id.tv_count)
    }
}