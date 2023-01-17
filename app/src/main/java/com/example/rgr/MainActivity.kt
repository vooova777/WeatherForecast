package com.example.rgr

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    val CITY: String ="manchester, uk"
    val API: String = "specific API key"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val executor = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())
        executor.execute {

            /* Відображення процесу завантаження(ProgressBar), приховування основного дизайну */
            findViewById<ProgressBar>(R.id.loader).visibility = View.VISIBLE
            findViewById<LinearLayout>(R.id.mainContainer).visibility = View.GONE
            findViewById<TextView>(R.id.errortext).visibility = View.GONE

            var response:String?
            try{
                response = URL("https://api.openweathermap.org/data/2.5/weather?q=$CITY&units=metric&appid=$API")
                    .readText(Charsets.UTF_8)
            }catch (e: Exception){
                response = null
            }

            handler.post {
                /* Витяг JSON повертається з API */
                try {
                    val jsonObj = JSONObject(response)
                    val main = jsonObj.getJSONObject("main")
                    val sys = jsonObj.getJSONObject("sys")
                    val wind = jsonObj.getJSONObject("wind")
                    val weather = jsonObj.getJSONArray("weather").getJSONObject(0)
                    val temp = main.getString("temp")+"°C"
                    val tempMin = main.getString("temp_min")+"°C"
                    val tempMax = main.getString("temp_max")+"°C"
                    val pressure = main.getString("pressure")
                    val humidity = main.getString("humidity")

                    val sunrise:Long = sys.getLong("sunrise")
                    val sunset:Long = sys.getLong("sunset")
                    val windSpeed = wind.getString("speed")
                    val weatherDescription = weather.getString("description")

                    val address = jsonObj.getString("name")+", "+sys.getString("country")

                    /* Заповнення добутими даними наші представлення */
                    findViewById<TextView>(R.id.address).text = address
                    findViewById<TextView>(R.id.status).text = weatherDescription
                    findViewById<TextView>(R.id.temp).text = temp
                    findViewById<TextView>(R.id.temp_min).text = tempMin
                    findViewById<TextView>(R.id.temp_max).text = tempMax
                    findViewById<TextView>(R.id.sunrise).text = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunrise*1000))
                    findViewById<TextView>(R.id.sunset).text = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunset*1000))
                    findViewById<TextView>(R.id.wind).text = windSpeed
                    findViewById<TextView>(R.id.pressure).text = pressure
                    findViewById<TextView>(R.id.humidity).text = humidity

                    /* Передсталення заповнені, Приховування завантажувача, Показ основного дизайну */
                    findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
                    findViewById<LinearLayout>(R.id.mainContainer).visibility = View.VISIBLE

                } catch (e: Exception) {
                    findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
                    findViewById<TextView>(R.id.errortext).visibility = View.VISIBLE
                }
            }
        }

    }

}
