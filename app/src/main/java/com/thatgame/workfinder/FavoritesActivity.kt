package com.thatgame.workfinder

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FavoritesActivity : AppCompatActivity(), OnFavoriteIconClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var jobAdapter: JobAdapter
    private var jobList = mutableListOf<Job>()

    private val jobDao: JobDao by lazy {
        val db = Room.databaseBuilder(applicationContext, JobDatabase::class.java, "jobs-database").build()
        db.JobDao()
    }

    private lateinit var navView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites)

        recyclerView = findViewById(R.id.favorites_recycler)
        recyclerView.layoutManager = LinearLayoutManager(this)
        jobAdapter = JobAdapter(jobList, this) { job ->
            val intent = Intent(this, VacancyActivity::class.java)
            intent.putExtra("vacancy", Gson().toJson(job))
            startActivity(intent)
        }
        recyclerView.adapter = jobAdapter

        loadFavoriteJobs()

        val textView: TextView = findViewById(R.id.countOfVacanciesTextView)
        textView.setText(getAppliedString(jobList.size))

        navView = findViewById(R.id.bottom_navigation)
        updateFavoriteIcon()
        navView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_search -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_favorites -> {
                    true
                }
                R.id.navigation_notifications -> {
                    true
                }
                R.id.navigation_messages -> {
                    true
                }
                R.id.navigation_profile -> {
                    true
                }
                else -> false
            }
        }
    }

    private fun loadFavoriteJobs() {
        lifecycleScope.launch(Dispatchers.IO) {
            jobList.addAll(jobDao.getAllFavorites())
            jobAdapter.notifyDataSetChanged()
        }
    }

    override fun onJobLongClick(job: Job) {
        lifecycleScope.launch(Dispatchers.IO) {
            if (job.isFavorite) {
                jobDao.insert(job.copy(isFavorite = true))
            } else {
                jobDao.delete(job)
            }
            launch(Dispatchers.Main) {
                updateFavoriteIcon()
                jobAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun updateFavoriteIcon() {
        CoroutineScope(Dispatchers.Main).launch {
            val favoritesCount = jobDao.getAllFavorites()

            val badgeDrawable: BadgeDrawable = navView.getOrCreateBadge(R.id.navigation_favorites)
            if (favoritesCount.isNotEmpty()) {
                badgeDrawable.number = favoritesCount.size
                badgeDrawable.isVisible = true
            } else {
                badgeDrawable.isVisible = false
            }
        }
    }

    private fun getAppliedString(count: Int): String {
        return when {
            count % 10 == 1 && count % 100 != 11 -> "вакансия"
            count % 10 in 2..4 && count % 100 !in 12..14 -> "вакансии"
            else -> "вакансий"
        }
    }
}