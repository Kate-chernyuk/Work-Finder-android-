package com.thatgame.workfinder

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.widget.TextView
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.PlacemarkMapObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class VacancyActivity : AppCompatActivity() {
    private lateinit var titleTextView: TextView
    private lateinit var salaryTextView: TextView
    private lateinit var experienceTextView: TextView
    private lateinit var schedulesTextView: TextView
    private lateinit var appliedNumberTextView: TextView
    private lateinit var lookingNumberTextView: TextView
    private lateinit var companyTextView: TextView
    private lateinit var addressTextView: TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var responsibilitiesTextView: TextView
    private lateinit var respondButton: Button
    private lateinit var mapView: MapView
    private lateinit var linearLayout: LinearLayout
    private lateinit var goBack: ImageView
    private lateinit var favoriteIcon: ImageView

    private val jobDao: JobDao by lazy {
        val db = Room.databaseBuilder(applicationContext, JobDatabase::class.java, "jobs-database").build()
        db.JobDao()
    }

    private lateinit var navView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        setApiKey(savedInstanceState)
        super.onCreate(savedInstanceState)

        MapKitFactory.initialize(this)
        setContentView(R.layout.activity_vacancy)

        titleTextView = findViewById(R.id.titleTextView)
        salaryTextView = findViewById(R.id.salaryTextView)
        experienceTextView = findViewById(R.id.experienceTextView)
        schedulesTextView = findViewById(R.id.schedulesTextView)
        appliedNumberTextView = findViewById(R.id.appliedNumberTextView)
        lookingNumberTextView = findViewById(R.id.lookingNumberTextView)
        companyTextView = findViewById(R.id.companyTextView)
        addressTextView = findViewById(R.id.addressTextView)
        descriptionTextView = findViewById(R.id.descriptionTextView)
        responsibilitiesTextView = findViewById(R.id.responsibilitiesTextView)
        respondButton = findViewById(R.id.respondButton)
        linearLayout = findViewById(R.id.linearLayout)
        goBack = findViewById(R.id.goBack)
        favoriteIcon = findViewById(R.id.favoriteIcon2)

        val vacancy: Job = Gson().fromJson(intent.getStringExtra("vacancy"), Job::class.java)

        titleTextView.text = vacancy.title
        salaryTextView.text = vacancy.salary?.currency ?: ""
        experienceTextView.text = vacancy.experience?.previewText ?: ""
        schedulesTextView.text = vacancy.schedules?.joinToString(" ") ?: ""
        appliedNumberTextView.text = "${vacancy.appliedNumber} ${vacancy.appliedNumber?.let {
            getAppliedString(
                it
            )
        }} уже откликнулось"
        lookingNumberTextView.text = "${vacancy.lookingNumber} ${vacancy.lookingNumber?.let { getLookingString(it) }} сейчас смотрят"
        companyTextView.text = vacancy.company
        addressTextView.text = "${vacancy.address?.town}, ${vacancy.address?.street}, ${vacancy.address?.house}"
        descriptionTextView.text = vacancy.description?.replace("/n", " ") ?: ""
        responsibilitiesTextView.text = vacancy.responsibilities?.replace("/n", " ")
        respondButton.setOnClickListener() {
            val bottomShitDialog: BottomShitDialog? = vacancy.title?.let { it1 ->
                BottomShitDialog(
                    it1
                )
            }
            bottomShitDialog?.show(supportFragmentManager, "Modal Bottom Sheet")
        }

        mapView = findViewById(R.id.mapview)

        val addressLocation = Point(53.90454, 27.55924)
        mapView.map.move(CameraPosition(addressLocation, 12f, 0f, 0f))

        for (question in vacancy.questions!!) {
            val button = Button(this).apply {
                text = question.toString()
                setTextColor(ContextCompat.getColor(context, R.color.white))
                setBackgroundColor(ContextCompat.getColor(context, R.color.grey))

                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                setOnClickListener {
                    val bottomShitDialog: BottomShitDialog? =
                        vacancy.title?.let { it1 -> BottomShitDialog(it1, question.toString()) }
                    bottomShitDialog?.show(supportFragmentManager, "Modal Bottom Sheet")
                }
            }
            linearLayout.addView(button)
        }

        goBack.setOnClickListener() {
            val intent: Intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }


        favoriteIcon.setImageResource(if (vacancy.isFavorite) R.drawable.baseline_favorite_24 else R.drawable.baseline_favorite_border_24)
        favoriteIcon.setOnClickListener() {
            vacancy.isFavorite = !vacancy.isFavorite
            favoriteIcon.setImageResource(if (vacancy.isFavorite) R.drawable.baseline_favorite_24 else R.drawable.baseline_favorite_border_24)
            changeFavoriteStatus(vacancy)
        }

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

    private fun getAppliedString(count: Int): String {
        return when {
            //count % 10 == 1 && count % 100 != 11 -> "человек"
            count % 10 in 2..4 && count % 100 !in 12..14 -> "человека"
            else -> "человек"
        }
    }

    private fun getLookingString(count: Int): String {
        return when {
            count % 10 == 1 && count % 100 != 11 -> "человека"
            count % 10 in 2..4 && count % 100 !in 12..14 -> "человека"
            else -> "человек"
        }
    }

    private fun setApiKey(savedInstanceState: Bundle?) {
        val haveApiKey = savedInstanceState?.getBoolean("haveApiKey") ?: false
        if (!haveApiKey) {
            MapKitFactory.setApiKey("...")
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("haveApiKey", true)
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        mapView.onStart()
    }

    override fun onStop() {
        mapView.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    private fun changeFavoriteStatus(job: Job) {
        lifecycleScope.launch(Dispatchers.IO) {
            if (job.isFavorite) {
                jobDao.insert(job.copy(isFavorite = true))
                updateFavoriteIcon()
            } else {
                jobDao.delete(job)
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


