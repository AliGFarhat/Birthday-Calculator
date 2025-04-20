package com.example.birthdaycalculator

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.NumberPicker
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.util.Calendar
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private val monthFacts = arrayOf(
        "January: Garnet • Capricorn/Aquarius",
        "February: Amethyst • Aquarius/Pisces",
        "March: Aquamarine • Pisces/Aries",
        "April: Diamond • Aries/Taurus",
        "May: Emerald • Taurus/Gemini",
        "June: Pearl • Gemini/Cancer",
        "July: Ruby • Cancer/Leo",
        "August: Peridot • Leo/Virgo",
        "September: Sapphire • Virgo/Libra",
        "October: Opal • Libra/Scorpio",
        "November: Topaz • Scorpio/Sagittarius",
        "December: Turquoise • Sagittarius/Capricorn"
    )

    private val dayFacts = arrayOf(
        "1st-10th: Natural leaders",
        "11th-20th: Balanced personalities",
        "21st-31st: Detail-oriented"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_BirthdayCalculator)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val monthPicker = findViewById<NumberPicker>(R.id.monthPicker)
        val dayPicker = findViewById<NumberPicker>(R.id.dayPicker)
        val btnCalculate = findViewById<Button>(R.id.btnCalculate)
        val resultsContainer = findViewById<View>(R.id.resultsContainer)

        // Configure pickers
        monthPicker.minValue = 1
        monthPicker.maxValue = 12
        monthPicker.displayedValues = arrayOf("Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec")

        dayPicker.minValue = 1
        dayPicker.maxValue = 31

        // Show instructions
        showFirstRunInstructions()

        btnCalculate.setOnClickListener {
            val selectedMonth = monthPicker.value - 1
            val selectedDay = dayPicker.value

            val today = Calendar.getInstance()
            val nextBirthday = calculateNextBirthday(selectedMonth, selectedDay, today)
            val diffDays = TimeUnit.MILLISECONDS.toDays(nextBirthday.timeInMillis - today.timeInMillis)

            resultsContainer.visibility = View.VISIBLE
            findViewById<TextView>(R.id.tvResult).text = buildResultText(
                diffDays,
                monthFacts[selectedMonth],
                dayFacts[when {
                    selectedDay <= 10 -> 0
                    selectedDay <= 20 -> 1
                    else -> 2
                }],
                getSpecialFact(selectedMonth, selectedDay)
            )
        }
    }

    private fun showFirstRunInstructions() {
        val prefs = getPreferences(Context.MODE_PRIVATE)
        if (prefs.getBoolean("first_run", true)) {
            val dialogView = layoutInflater.inflate(R.layout.dialog_instructions, null)
            val dialog = AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(false)
                .create()

            dialog.show()

            dialogView.findViewById<Button>(R.id.btnGotIt).setOnClickListener {
                prefs.edit().putBoolean("first_run", false).apply()
                dialog.dismiss()
            }
        }
    }

    private fun calculateNextBirthday(month: Int, day: Int, today: Calendar): Calendar {
        return Calendar.getInstance().apply {
            set(today.get(Calendar.YEAR), month, day)
            if (before(today) || equals(today)) add(Calendar.YEAR, 1)
        }
    }

    private fun buildResultText(days: Long, monthFact: String, dayFact: String, specialFact: String): String {
        return """
            ${if (days == 0L) "🎉 HAPPY BIRTHDAY!" else "Days left: $days"}
            
            📅 $monthFact
            📆 $dayFact
            
            ✨ Fun Fact:
            $specialFact
        """.trimIndent()
    }

    private fun getSpecialFact(month: Int, day: Int): String {
        return when (month) {
            0 -> "New Year month!"
            11 -> "Holiday season!"
            else -> "Unique birthday month!"
        }
    }
}