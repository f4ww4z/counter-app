package com.jagoancoding.countwithme

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class CounterAdapter(private var counts: ArrayList<Counter>)
    : RecyclerView.Adapter<CounterAdapter.CounterHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CounterHolder {
        val inflatedView = LayoutInflater
                .from(parent.context)
                .inflate(R.layout.counter_item, parent, false)
        return CounterHolder(inflatedView)
    }

    override fun getItemCount(): Int = counts.size

    override fun onBindViewHolder(holder: CounterHolder, position: Int) {
        var counter: Counter = counts[position]
        holder.titleTV.text = counter.title
        holder.counterTV.text = counter.count.toString()
    }

    class CounterHolder(rootView: View) : RecyclerView.ViewHolder(rootView), View.OnClickListener {
        var titleTV: TextView = rootView.findViewById(R.id.tv_title)
        var counterTV: TextView = rootView.findViewById(R.id.tv_count)

        init {
            rootView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            Log.d("Recycler view", "On Click!")
        }
    }

}