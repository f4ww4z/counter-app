package com.jagoancoding.countwithme

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.InputType
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity(), ItemStateListener {

    // Recycler view and other views
    private lateinit var mainRV: RecyclerView
    private lateinit var mViewManager: RecyclerView.LayoutManager
    private lateinit var mAdapter: RecyclerView.Adapter<*>
    private lateinit var loadingBar: ProgressBar

    private lateinit var counterList: ArrayList<Counter>

    // Firebase
    private lateinit var mFirebase: FirebaseDatabase
    private lateinit var rootRef: DatabaseReference

    private var counterName = ""

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

                mAdapter.notifyItemChanged(counterList.size - 1)
            }

            override fun onCancelled(databaseError: DatabaseError?) {
                println("updateList:onCancelled ${databaseError?.toException()}")
            }
        }

        rootRef.addListenerForSingleValueEvent(updateListListener)
    }

    override fun incrementCount(counter: Counter) {
        loadingBar.visibility = View.VISIBLE

        val newCount = counter.count + 1

        for (i in counterList.indices) {
            if (counterList[i] == counter) {
                counterList[i].count = newCount

                rootRef.child(counter.title).setValue(newCount)
                mAdapter.notifyItemChanged(i)
            }
        }
        loadingBar.visibility = View.GONE
    }

    override fun onCounterAdd() {
        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_TEXT

        // Create a dialog requiring string input
        AlertDialog.Builder(this)
                .setView(input)
                .setMessage(R.string.input_mes)
                .setTitle(R.string.input_title)
                .setPositiveButton(R.string.ok) { dialog, which ->
                    // Pass name
                    counterName = input.text.toString().trim()

                    if (counterName.isEmpty()) {
                        dialog.cancel()
                    } else {
                        loadingBar.visibility = View.VISIBLE

                        val counter = Counter(counterName, 0)
                        rootRef.child(counter.title).setValue(counter.count)

                        counterList.add(counter)
                        mAdapter.notifyItemChanged(counterList.size - 1)

                        loadingBar.visibility = View.GONE
                    }
                }
                .setNegativeButton(R.string.cancel, null)
                .show()
    }

    override fun onItemDelete(itemId: String) {
        rootRef.child(itemId).removeValue()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.mymenu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.action_add -> {
                onCounterAdd()
                true
            }
            R.id.action_del -> {
                //TODO: Show alert dialog
                removeAllCounters()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    private fun removeAllCounters() {
        for (i in counterList) {
            onItemDelete(i.title)
        }
        counterList.clear()
        mAdapter.notifyDataSetChanged()
    }
}