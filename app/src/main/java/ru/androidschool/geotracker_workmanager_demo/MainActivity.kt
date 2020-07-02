package ru.androidschool.geotracker_workmanager_demo

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_SHORT
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import ru.androidschool.geotracker_workmanager_demo.domain.LocationRepository

const val COARSE_LOCATION_CODE = 123

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
            // По нажатию на FAB будет выполняться метод onClick()
            onClick()
        }
    }

    // Здесь проверяем есть ли права
    private fun onClick() {
        if (isPermissionGranted()) {
            startWork()
        } else {
            // Если нет - то запрашиваем
            ActivityCompat.requestPermissions(
                this,
                listOf(Manifest.permission.ACCESS_COARSE_LOCATION).toTypedArray(),
                COARSE_LOCATION_CODE
            )
        }
    }

    // Метод проверки есть ли права на получение координат
    private fun isPermissionGranted(): Boolean {
        return (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED)
    }

    // Если права есть, то в методе getLocation() через репозиторий получаем координаты
    @SuppressLint("CheckResult")
    private fun getLocation() {
        val repo = LocationRepository(this)
        repo.getLocation()
            .subscribe({
                Log.d("LocationWorker", "${it.latitude.toString()};  ${it.longitude.toString()}")
            }, {
                Log.d("LocationWorker", "Error$it")
            })
    }

    private fun startWork() {
        getLocation()
    }

    // После того как пользователь разрешил, через репозиторий получаем координаты
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            COARSE_LOCATION_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    startWork()
                } else {
                    // Если прав нет - показываем предупреждение
                    Snackbar.make(
                        fab,
                        "Вы не дали разрешение на получение геолокации",
                        LENGTH_SHORT
                    ).show()
                }
                return
            }
        }
    }
}