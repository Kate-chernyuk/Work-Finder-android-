package com.thatgame.workfinder

import android.content.Intent
import android.net.Uri
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import com.google.android.material.badge.BadgeDrawable

class MainActivity : AppCompatActivity(), OnFavoriteIconClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var recommendationsRecyclerView: RecyclerView
    private lateinit var jobAdapter: JobAdapter
    private lateinit var offersAdapter: OffersAdapter
    private val jobList = mutableListOf<Job>()
    private val offersList = mutableListOf<Offer>()

    private lateinit var viewMoreButton: Button

    private var btnClicked: Boolean = false

    private lateinit var navView: BottomNavigationView

    private val jobDao: JobDao by lazy {
        val db = Room.databaseBuilder(applicationContext, JobDatabase::class.java, "jobs-database").build()
        db.JobDao()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewMoreButton = findViewById(R.id.view_more_button)
        viewMoreButton.setOnClickListener() {
            viewMoreButton.visibility = View.GONE
            btnClicked = true
            loadJobData()
            loadFavoritesToBD()
            jobAdapter.updateItems(jobList)
            recommendationsRecyclerView.visibility = View.VISIBLE
        }

        recyclerView = findViewById(R.id.vacancies_recycler)
        recyclerView.addItemDecoration(
            DividerItemDecoration(this,
            DividerItemDecoration.VERTICAL)
        )

        jobAdapter = JobAdapter(jobList, this) { job ->
            val intent = Intent(this, VacancyActivity::class.java)
            intent.putExtra("vacancy", Gson().toJson(job))
            startActivity(intent)
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = jobAdapter

        offersAdapter = OffersAdapter(offersList) { link ->
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
            startActivity(intent)
        }

        recommendationsRecyclerView = findViewById(R.id.recommendation_recycler)
        recommendationsRecyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        recommendationsRecyclerView.adapter = offersAdapter

        if (jobList.isEmpty()) {loadJobData()}
        loadFavoritesToBD()

        navView = findViewById(R.id.bottom_navigation)
        updateFavoriteIcon()
        navView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_search -> {
                    true
                }
                R.id.navigation_favorites -> {
                    val intent = Intent(this, FavoritesActivity::class.java)
                    startActivity(intent)
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


    private fun loadJobData() {
        val jsonString = readJsonFromAssets("jobs.json")
        val gson = Gson()
        val jobResponse = gson.fromJson(jsonString, JobResponse::class.java)

        if (!btnClicked) {
            viewMoreButton.text = "Показать ещё ${jobResponse.vacancies.size-2} вакансии"
            jobList.addAll(jobResponse.vacancies.subList(0,3)) }
        else {
            jobList.addAll(jobResponse.vacancies)
        }

        jobAdapter.notifyDataSetChanged()

        offersList.clear()
        offersList.addAll(jobResponse.offers)
        offersAdapter.notifyDataSetChanged()
    }

    private fun readJsonFromAssets(fileName: String): String {
        return try {
            val inputStream = assets.open(fileName)
            inputStream.bufferedReader().use { it.readText() }
        } catch (e: IOException) {
            e.printStackTrace()
            ""
        }
    }

    private fun loadFavoritesToBD() {
        lifecycleScope.launch(Dispatchers.IO) {
            val favoriteJobs = jobDao.getAllFavorites()
            if (favoriteJobs.isEmpty()) {
                jobList.forEach { job ->
                    if (job.isFavorite) {
                        jobDao.insert(job)
                    }
                }
            } else {
                jobList.forEach { job ->
                    val count = jobDao.exists(job.id)
                    job.isFavorite = count > 0
                }
            }
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

    protected fun updateFavoriteIcon() {
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

}
