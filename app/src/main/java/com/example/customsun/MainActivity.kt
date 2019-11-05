package com.example.customsun

import android.annotation.TargetApi
import android.content.SharedPreferences
import android.media.ImageReader
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.customsun.calc.AstronomicalCalendar
import com.example.customsun.calc.GeoLocation
import kotlinx.android.synthetic.main.activity_main.*
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

const val FILE_NAME = "au_locations.txt"

class MainActivity : AppCompatActivity(){

    private val locations: MutableList<GeoLocation> = mutableListOf()

    private var spinner: Spinner? = null

    private var adapter: ArrayAdapter<GeoLocation>? = null

    private var sunsetTextView: TextView? = null

    private var sunriseTextView: TextView? = null

    private val datePicker: DatePicker by lazy { findViewById<DatePicker>(R.id.datePicker) }

    private val addButton: Button by lazy { findViewById<Button>(R.id.addButton) }

    private var nameCustomEditText: EditText? = null

    private var latitudeCustomEditText: EditText? = null

    private var longitudeCustomEditText: EditText? = null

    private var timeZoneCustomEditText: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        readFile()
        initialiseUi()

        addButton.setOnClickListener {
            writeFile()
            initialiseUi()
            return@setOnClickListener
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private fun initialiseUi() {
        spinner = findViewById(R.id.locationSpinner)
        sunsetTextView = findViewById(R.id.sunsetTimeTV)
        sunriseTextView = findViewById(R.id.sunriseTimeTV)

        adapter = GeolocationsAdapter(this, android.R.layout.simple_dropdown_item_1line, locations)
        spinner?.adapter = adapter
        spinner?.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val location: GeoLocation = locations[position]
                update(location)
            }
        }
        datePicker.setOnDateChangedListener { view, year, monthOfYear, dayOfMonth ->
            val geolocation = spinner?.selectedItem
            check(geolocation is GeoLocation)
            update(geolocation)
        }

    }

    fun update(geoLocation: GeoLocation){
        val ac = AstronomicalCalendar(geoLocation)
        ac.calendar.set(datePicker.year, datePicker.month, datePicker.dayOfMonth)
        val srise = ac.sunrise
        val sset = ac.sunset
        val sdf = SimpleDateFormat("HH:mm")
        sunsetTextView?.text = sdf.format(srise)
        sunriseTextView?.text = sdf.format(sset)
    }

    fun readFile() {
//        val inputStream: InputStream = resources.openRawResource(R.raw.au_locations)
        val inputStream: InputStream = File(filesDir, FILE_NAME).inputStream()
        val reader = BufferedReader(InputStreamReader(inputStream))
        var line: String?
        line = reader.readLine()
        while (line != null) {
            val lineValues: List<String> = line.split(",")
            val name: String = lineValues[0]
            val latitude: Double = lineValues[1].toDouble()
            val longtitude: Double = lineValues[2].toDouble()
            val timeZone: TimeZone = TimeZone.getTimeZone(lineValues[3])
            val location = GeoLocation(name, latitude, longtitude, timeZone)
            locations.add(location)
            line = reader.readLine()
        }
        reader.close()
        inputStream.close()
    }

    fun writeFile(){
//        check(outputStream is File)
        nameCustomEditText = findViewById(R.id.customName)
        latitudeCustomEditText = findViewById(R.id.customLatitude)
        longitudeCustomEditText = findViewById(R.id.customLongitude)
        timeZoneCustomEditText = findViewById(R.id.customTimezone)

        val name: String = nameCustomEditText?.text.toString()
        val latitude: Double = latitudeCustomEditText?.text.toString().toDoubleOrNull()?:0.0
        val longtitude: Double = longitudeCustomEditText?.text.toString().toDoubleOrNull()?:0.0
        val timezone = timeZoneCustomEditText?.text.toString()
        val timeZone = TimeZone.getTimeZone(timezone)

        val addlocation = GeoLocation(name, latitude, longtitude, timeZone)

        val outputStream: FileOutputStream = File(filesDir, FILE_NAME).outputStream()
        val writer = BufferedWriter(OutputStreamWriter(outputStream))

        locations.forEach { location ->
            writer.write("${location.locationName},${location.latitude},${location.longitude},${location.timeZone.id}")
            writer.newLine()
        }

        val newLocation = "${addlocation.locationName},${addlocation.latitude},${addlocation.longitude},${addlocation.timeZone.id}"
        writer.append(newLocation)
        writer.newLine()

        locations.add(addlocation)

        writer.close()
        outputStream.close()
    }
}
