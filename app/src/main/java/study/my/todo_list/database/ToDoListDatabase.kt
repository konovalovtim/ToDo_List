package study.my.todo_list.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ToDoListDataEntity::class], version = 1 )
abstract class ToDoListDatabase : RoomDatabase(){

    abstract fun toDoListDao() : ToDoListDAO

    companion object{
        @Volatile
        private var instanse : ToDoListDatabase? = null

        fun getInstance(context: Context): ToDoListDatabase? {
            if (instanse == null) {
                synchronized(ToDoListDatabase::class) {
                    instanse = Room.databaseBuilder(context.applicationContext,
                        ToDoListDatabase::class.java, "todolistdb")
                        .allowMainThreadQueries()
                        .build()
                }
            }
            return instanse
        }
    }
}