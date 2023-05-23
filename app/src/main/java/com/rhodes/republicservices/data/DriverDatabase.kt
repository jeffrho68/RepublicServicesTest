package com.rhodes.republicservices.data

import android.content.Context
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase

//Entities

@Entity(tableName = "drivers")
data class Driver(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    val id: Int,

    @ColumnInfo(name = "firstName")
    val firstName: String,

    @ColumnInfo(name = "lastName")
    val lastName: String
)

@Entity(tableName = "routes")
data class Route(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    val id: Int,

    @ColumnInfo(name = "type")
    val type: RouteType,

    @ColumnInfo(name = "name")
    val name: String
)

enum class RouteType {
    C,
    I,
    R
}

//DAOs

@Dao
interface DriverDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDriver(driver: Driver)

    @Query("SELECT * FROM drivers")
    fun getDrivers(): List<Driver>

    @Query("SELECT * FROM drivers WHERE id == :driverId")
    fun getDriver(driverId: Int): Driver
}

@Dao
interface RoutesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRoute(route: Route)

    @Query("SELECT * FROM routes")
    fun getRoutes(): List<Route>
}

//Database

@Database(entities = [Driver::class, Route::class], version = 1, exportSchema = false)
abstract class DriverDatabase : RoomDatabase() {

    abstract fun driverDao(): DriverDao
    abstract fun routeDao(): RoutesDao

    companion object {
        private const val DB_NAME = "driver_db"

        private var instance: DriverDatabase? = null

        fun getInstance(context: Context): DriverDatabase {
            return instance ?: synchronized(this) {
                Room.databaseBuilder(context, DriverDatabase::class.java, DB_NAME)
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { instance = it }
            }
        }
    }
}




