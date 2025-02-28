package com.ts.musiccoursehub.ui
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.snackbar.Snackbar
import com.ts.musiccoursehub.R

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var progressBar: LinearProgressIndicator
    private lateinit var text: TextView
    private lateinit var rootView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        progressBar = findViewById(R.id.progressBar)
        text = findViewById(R.id.text)
        rootView = findViewById(R.id.llRoot)

        if (isInternetAvailable(this)) {
            startProgressBarAndNavigate()
        } else {
            showNoInternetMessage()
        }
    }

    private fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                when {
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> return true
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> return true
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> return true
                }
            }
        }
        return false
    }

    private fun showNoInternetMessage() {
        text.text = "No Internet Connection"
        Snackbar.make(rootView, "Please connect to the internet and try again.", Snackbar.LENGTH_INDEFINITE)
            .setAction("Retry") {
                if (isInternetAvailable(this)) {
                    startProgressBarAndNavigate()
                } else {
                    showNoInternetMessage() // Retry again if still no internet
                }
            }
            .show()
    }

    private fun startProgressBarAndNavigate() {
        text.text = "Loading App Content..."
        progressBar.visibility = View.VISIBLE
        progressBar.progress = 0
        val handler = Handler(Looper.getMainLooper())
        var progressValue = 0

        val progressRunnable = object : Runnable {
            override fun run() {
                progressValue += 10
                progressBar.progress = progressValue

                if (progressValue < 100) {
                    handler.postDelayed(this, 300)
                } else {
                    // Progress completed, navigate to MainActivity
                    navigateToMainActivity()
                }
            }
        }
        handler.post(progressRunnable)
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}