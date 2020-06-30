package ru.androidschool.geotracker_workmanager_demo

import android.content.Context
import androidx.work.*
import ru.androidschool.geotracker_workmanager_demo.database.GeoInfo
import ru.androidschool.geotracker_workmanager_demo.database.GeoInfoDatabase
import java.util.concurrent.TimeUnit

class SimpleWorker(private val context: Context, params: WorkerParameters) :
    Worker(context, params) {

    override fun doWork(): Result {
        val dao = GeoInfoDatabase.getDatabase(context).wordDao()
        dao.insert(GeoInfo(0, 37.419857, -122.078827, System.currentTimeMillis()))
        return Result.success()
    }

    companion object {
        private const val SIMPLE_WORKER_TAG = "SimpleWorkerTag"

        // Метод для создания OneTimeWorkRequest
        private fun createWorkRequest(data: Data): OneTimeWorkRequest {
            return OneTimeWorkRequest.Builder(SimpleWorker::class.java)
                .setInputData(data)
                .addTag(SIMPLE_WORKER_TAG)
                .build()
        }

        // Метод для запуска
        fun startWork(context: Context) {
            val work = createWorkRequest(Data.EMPTY)
            WorkManager.getInstance(context)
                .enqueueUniqueWork(
                    SIMPLE_WORKER_TAG,
                    ExistingWorkPolicy.APPEND,
                    work
                )
        }

        // Метод для остановки
        fun cancelWork(context: Context) {
            WorkManager.getInstance(context).cancelAllWorkByTag(SIMPLE_WORKER_TAG)
        }
    }
}

