package ru.androidschool.geotracker_workmanager_demo

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.work.*
import com.google.android.gms.location.LocationSettingsResponse
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import ru.androidschool.geotracker_workmanager_demo.database.GeoInfo
import ru.androidschool.geotracker_workmanager_demo.database.GeoInfoDatabase
import ru.androidschool.geotracker_workmanager_demo.domain.LocationRepository
import java.util.concurrent.TimeUnit

class LocationWorker(private val context: Context, params: WorkerParameters) :
    RxWorker(context, params) {

    // Репозиторий для работы с таблицей БД
    private val dao by lazy { GeoInfoDatabase.getDatabase(context).wordDao() }

    @SuppressLint("CheckResult")
    override fun createWork(): Single<Result> {
        // Репозиторий для получения координат
        val repo = LocationRepository(context)
        return repo.getLocation()
            // Для работы с БД необходимо изменить поток на io()
            .observeOn(Schedulers.io())
            // После получения данных сохраняем в БД
            .flatMap {
                dao.insert(GeoInfo(0, it.latitude, it.longitude, System.currentTimeMillis()))
            }
            // Возвращаем статус success()
            .map { Result.success() }
            // Если произойдет ошибка возвращаем статус failure()
            .onErrorReturn {
                Log.d("LocationWorker", it.message)
                Result.failure()
            }
    }

    companion object {
        private const val SIMPLE_WORKER_TAG = "SimpleWorkerTag"

        private fun createConstraints() = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .setRequiresCharging(true)
            .build()

        // Метод для создания PeriodicWorkRequest
        private fun createWorkRequest(data: Data): PeriodicWorkRequest {
            return PeriodicWorkRequest.Builder(LocationWorker::class.java, 15, TimeUnit.MINUTES)
                .setConstraints(createConstraints())
                .setInputData(data)
                .addTag(SIMPLE_WORKER_TAG)
                .build()
        }

        // Метод для запуска
        fun startWork(context: Context) {
            val work = createWorkRequest(Data.EMPTY)
            WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(
                    SIMPLE_WORKER_TAG,
                    ExistingPeriodicWorkPolicy.REPLACE,
                    work
                )
        }

        // Метод для остановки
        fun cancelWork(context: Context) {
            WorkManager.getInstance(context).cancelAllWorkByTag(SIMPLE_WORKER_TAG)
        }
    }
}

