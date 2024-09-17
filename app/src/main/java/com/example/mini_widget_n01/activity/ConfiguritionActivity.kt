package com.example.mini_widget_n01.activity
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.RemoteViews
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.mini_widget_n01.R
import com.example.mini_widget_n01.db.AppDatabase
import com.example.mini_widget_n01.db.entity.WidgetClick
import com.example.mini_widget_n01.service.ImageWidgetService
import com.example.mini_widget_n01.service.WidgetClickReceiver
import com.example.mini_widget_n01.widget.ImageWidgetProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ConfiguritionActivity : AppCompatActivity() {

    private val PERMISSION_REQUEST_CODE = 100
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_configurition)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        sharedPreferences = getSharedPreferences("WidgetPrefs", MODE_PRIVATE)
    }

    override fun onStart() {
        super.onStart()
        val appWidgetManager = AppWidgetManager.getInstance(this)
        val appWidgetId = intent.extras?.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)

        if (appWidgetId != null && appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
            if (checkPermission()) {
                handleWidgetCreation(appWidgetId)
            } else {
                requestStoragePermission(appWidgetId)
            }
        } else {
            Toast.makeText(this, "Invalid AppWidget ID", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun handleWidgetCreation(appWidgetId: Int) {
        if (isWidgetConfigured(appWidgetId)) {
            showConfigurationScreen(appWidgetId)
        } else {
            updateWidget(appWidgetId)
        }
    }

    private fun updateWidget(appWidgetId: Int) {
        val appWidgetManager = AppWidgetManager.getInstance(this)
        val widget = ComponentName(this, ImageWidgetProvider::class.java)
        val view = RemoteViews(packageName, R.layout.layout_widget)

        // Bind the service to the AdapterViewFlipper
        val serviceIntent = Intent(this, ImageWidgetService::class.java).apply {
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        }
        view.setRemoteAdapter(R.id.adapterViewFlipper, serviceIntent)
        view.setEmptyView(R.id.adapterViewFlipper, R.id.widgetImageView)

        // Set up the PendingIntent for click events
        val clickIntent = Intent(this, WidgetClickReceiver::class.java).apply {
            action = "com.example.mini_widget_n01.WIDGET_CLICK"
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        }

        val clickPendingIntent = PendingIntent.getBroadcast(
            this, appWidgetId, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        view.setPendingIntentTemplate(R.id.adapterViewFlipper, clickPendingIntent)

        appWidgetManager.updateAppWidget(appWidgetId, view)
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.adapterViewFlipper)

        saveWidgetId(appWidgetId)
//        val db = AppDatabase.getDatabase(this)
//        val widgetClickDao = db.widgetClickDao()
//        lifecycleScope.launch(Dispatchers.IO) {
//            widgetClickDao.insertOrUpdateClickCount(WidgetClick(appWidgetId, 0))
//        }
        // Finish activity
        val resultValue = Intent().apply {
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        }
        setResult(RESULT_OK, resultValue)
        finish()
    }


    private fun isWidgetConfigured(appWidgetId: Int): Boolean {
        return sharedPreferences.getBoolean("widget_configured_$appWidgetId", false)
    }

    private fun saveWidgetId(appWidgetId: Int) {
        with(sharedPreferences.edit()) {
            putBoolean("widget_configured_$appWidgetId", true)
            apply()
        }
    }

    private fun showConfigurationScreen(appWidgetId: Int) {
        // Start the configuration activity or screen
        // This can be your configuration UI or a new activity if needed
        Toast.makeText(this, "Widget is already configured. Showing configuration screen.", Toast.LENGTH_SHORT).show()
        //finish() // or you might start a configuration activity here if required
    }

    private fun checkPermission(): Boolean {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestStoragePermission(appWidgetId: Int) {
        val permission = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            android.Manifest.permission.READ_MEDIA_IMAGES
        } else {
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        }
        ActivityCompat.requestPermissions(this, arrayOf(permission), PERMISSION_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val appWidgetId = intent.extras?.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
                if (appWidgetId != null && appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
                    handleWidgetCreation(appWidgetId)
                }
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}
