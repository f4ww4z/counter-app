package com.jagoancoding.countwithme

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {

    private lateinit var mainRV: RecyclerView
    private lateinit var mViewManager: RecyclerView.LayoutManager
    private lateinit var mAdapter: RecyclerView.Adapter<*>
    private lateinit var loadingBar: ProgressBar

    private lateinit var counterList: ArrayList<Counter>

    // Firebase
    private lateinit var mFirebase: FirebaseDatabase
    private lateinit var rootRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize realtime database
        mFirebase = FirebaseDatabase.getInstance()
        rootRef = mFirebase.reference

        counterList = arrayListOf()

        mViewManager = LinearLayoutManager(this)
        mAdapter = CounterAdapter(counterList)

        mainRV = findViewById<RecyclerView>(R.id.rv_main).apply {
            layoutManager = mViewManager
            adapter = mAdapter
        }

        loadingBar = findViewById(R.id.pb_main)

        loadingBar.visibility = View.GONE

        // Show counters
        val updateListListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot?) {
                for (data in dataSnapshot!!.children) {
                    counterList.add(Counter(data.key, data.value as Long))
                }
            }

            override fun onCancelled(databaseError: DatabaseError?) {
                println("updateList:onCancelled ${databaseError?.toException()}")
            }
        }

        rootRef.addListenerForSingleValueEvent(updateListListener)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.mymenu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.action_add -> {
                addCounter()
                true
            }
            R.id.action_del -> {
                removeAllCounters()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    private fun addCounter() {
        val counter = Counter("counter${counterList.size + 1}", 0)

        rootRef.child(counter.title).setValue(counter.count)

        counterList.add(counter)
        mAdapter.notifyItemChanged(counterList.size - 1)
    }

    private fun removeAllCounters() {
        for (i in counterList.indices) {
            rootRef.child(counterList[i].title).removeValue()
        }
        counterList.clear()
        mAdapter.notifyDataSetChanged()
        addCounter()
    }
}
