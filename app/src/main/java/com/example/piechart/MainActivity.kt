package com.example.piechart

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import org.json.JSONException
import java.util.*

class MainActivity : AppCompatActivity() {
    private var mQ: RequestQueue? = null
    private var pieChart: PieChart? = null
    private var realSum = 0f
    private var dataVals = ArrayList<PieEntry>()
    private var colorClassArray = intArrayOf(Color.RED, Color.BLUE, Color.MAGENTA, Color.GREEN, Color.CYAN)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        pieChart = findViewById(R.id.pie_chart)
        mQ = Volley.newRequestQueue(this)
        val url = "https://bemoss-e8288.firebaseio.com/state.json"
        val req = JsonObjectRequest(Request.Method.GET, url, null,
                Response.Listener { response ->
                    try {
                        val names = response.names()
                        for (i in 0 until names.length()) {
                            val jsonArray = response.getJSONArray(names[i] as String)
                            for (j in 0 until jsonArray.length()) {
                                val obj = jsonArray.getJSONObject(j)
                                realSum += try {
                                    obj.getDouble("power").toFloat()
                                } catch (e: JSONException) {
                                    0f
                                }
                            }
                            dataVals.add(PieEntry(realSum, names[i].toString()))
                            realSum = 0f
                        }
                        val pieDataSet = PieDataSet(dataVals, "Power (Watt)")
                        pieDataSet.setColors(*colorClassArray)
                        val pieData = PieData(pieDataSet)
                        pieDataSet.valueTextSize = 16f
                        pieDataSet.valueTextColor = Color.BLACK
                        pieChart!!.setDrawEntryLabels(true)
                        pieChart!!.setUsePercentValues(false)
                        pieChart!!.setNoDataText("No Data")
                        pieChart!!.setNoDataTextColor(Color.RED)
                        val description = Description()
                        description.text = "Approximate Energy Usage by Categories"
                        description.textColor = Color.BLACK
                        description.textSize = 17f
                        pieChart!!.description = description
                        val legend = pieChart!!.legend
                        legend.textSize = 15f
                        pieChart!!.data = pieData
                        pieChart!!.invalidate()
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }, Response.ErrorListener { error -> error.printStackTrace() })
        mQ?.add(req)
    }
}