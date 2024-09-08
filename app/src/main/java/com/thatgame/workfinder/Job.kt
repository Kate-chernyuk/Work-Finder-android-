package com.thatgame.workfinder

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.jetbrains.annotations.Nullable

@Entity(tableName = "favorite_jobs")
@TypeConverters(Converters::class)
data class Job(
    @PrimaryKey val id: String,
    val lookingNumber: Int?,
    val title: String?,
    val address: Address?,
    val company: String?,
    val experience: Experience?,
    val publishedDate: String?,
    var isFavorite: Boolean,
    val salary: Salary?,
    val schedules: List<String>?,
    val appliedNumber: Int?,
    val description: String?,
    val responsibilities: String?,
    val questions: List<String>?
)

data class Address(val town: String, val street: String, val house: String)
data class Experience(val years: Int, val previewText: String)
data class Salary(val amount: Double, val currency: String)


data class JobResponse(
    val offers: List<Offer>,
    val vacancies: List<Job>
)

data class Offer(
    val id: String? = "",
    val title: String,
    val button: Button? = null,
    val link: String
)

data class Button(
    var text: String
)

@Dao
interface JobDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(job: Job)

    @Query("SELECT * FROM favorite_jobs")
    suspend fun getAllFavorites(): List<Job>

    @Query("SELECT COUNT(*) FROM favorite_jobs WHERE id = :jobId")
    fun exists(jobId: String): Int

    @Delete
    suspend fun delete(job: Job)
}

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromAddress(value: Address): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toAddress(value: String): Address {
        val listType = object : TypeToken<Address>() {}.type
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    fun fromExperience(value: Experience): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toExperience(value: String): Experience {
        val listType = object : TypeToken<Experience>() {}.type
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    fun fromSalary(value: Salary): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toSalary(value: String): Salary {
        val listType = object : TypeToken<Salary>() {}.type
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    fun fromStringList(value: List<String>?): String? {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toStringList(value: String?): List<String>? {
        val listType = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, listType)
    }
}

@Database(entities = [Job::class], version = 1)
@TypeConverters(Converters::class)
abstract class JobDatabase: RoomDatabase() {
    abstract fun JobDao(): JobDao
}