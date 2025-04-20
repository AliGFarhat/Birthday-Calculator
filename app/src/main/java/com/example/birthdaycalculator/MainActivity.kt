package com.example.birthdaycalculator

import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Paint
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

    // Month facts array (index 0-11)
    private val monthFacts = arrayOf(
        "January: Birthstone - Garnet • Zodiac - Capricorn/Aquarius",
        "February: Birthstone - Amethyst • Zodiac - Aquarius/Pisces",
        "March: Birthstone - Aquamarine • Zodiac - Pisces/Aries",
        "April: Birthstone - Diamond • Zodiac - Aries/Taurus",
        "May: Birthstone - Emerald • Zodiac - Taurus/Gemini",
        "June: Birthstone - Pearl • Zodiac - Gemini/Cancer",
        "July: Birthstone - Ruby • Zodiac - Cancer/Leo",
        "August: Birthstone - Peridot • Zodiac - Leo/Virgo",
        "September: Birthstone - Sapphire • Zodiac - Virgo/Libra",
        "October: Birthstone - Opal • Zodiac - Libra/Scorpio",
        "November: Birthstone - Topaz • Zodiac - Scorpio/Sagittarius",
        "December: Birthstone - Turquoise • Zodiac - Sagittarius/Capricorn"
    )

    // Day facts array (early/mid/late month)
    private val dayFacts = arrayOf(
        "1st-10th: Natural leaders, ambitious starters",
        "11th-20th: Balanced personalities, adaptable",
        "21st-31st: Observant, detail-oriented"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val monthPicker = findViewById<NumberPicker>(R.id.monthPicker)
        val dayPicker = findViewById<NumberPicker>(R.id.dayPicker)
        val btnCalculate = findViewById<Button>(R.id.btnCalculate)
        val tvResult = findViewById<TextView>(R.id.tvResult)
        val resultsContainer = findViewById<View>(R.id.resultsContainer)

        // Configure month picker
        monthPicker.minValue = 1
        monthPicker.maxValue = 12
        monthPicker.displayedValues = arrayOf(
            "Jan", "Feb", "Mar", "Apr", "May", "Jun",
            "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
        )
        setNumberPickerTextColor(monthPicker, Color.BLACK)

        // Configure day picker
        dayPicker.minValue = 1
        dayPicker.maxValue = 31
        setNumberPickerTextColor(dayPicker, Color.BLACK)

        btnCalculate.setOnClickListener {
            val selectedMonth = monthPicker.value - 1  // Calendar months are 0-based
            val selectedDay = dayPicker.value

            // Calculate days until next birthday
            val today = Calendar.getInstance()
            val nextBirthday = calculateNextBirthday(selectedMonth, selectedDay, today)
            val diffDays = TimeUnit.MILLISECONDS.toDays(
                nextBirthday.timeInMillis - today.timeInMillis
            )

            // Get facts
            val monthFact = monthFacts[selectedMonth]
            val dayFact = when {
                selectedDay <= 10 -> dayFacts[0]
                selectedDay <= 20 -> dayFacts[1]
                else -> dayFacts[2]
            }
            val specialFact = getSpecialFacts(selectedMonth, selectedDay)

            // Build result text
            val resultText = """
                ${if (diffDays == 0L) "🎉 HAPPY BIRTHDAY! 🎉" else "Days until birthday: $diffDays"}
                
                📅 $monthFact
                📆 $dayFact
                
                ✨ Did You Know?
                $specialFact
            """.trimIndent()

            // Display results
            tvResult.text = resultText
            resultsContainer.visibility = View.VISIBLE
        }

        // Show instructions on first launch (after splash screen)
        showInstructionsIfFirstRun()
    }

    private fun showInstructionsIfFirstRun() {
        val prefs: SharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
        if (prefs.getBoolean("first_run", true)) {
            val dialog = AlertDialog.Builder(this)
                .setView(R.layout.dialog_instructions)  // Inflate directly
                .setCancelable(false)
                .create()

            dialog.show()

            // Find view after dialog is shown
            dialog.findViewById<Button>(R.id.btnGotIt)?.setOnClickListener {
                prefs.edit().putBoolean("first_run", false).apply()
                dialog.dismiss()
            }
        }
    }

    private fun setNumberPickerTextColor(picker: NumberPicker, color: Int) {
        try {
            val selectorField = picker::class.java.getDeclaredField("mSelectorWheelPaint")
            selectorField.isAccessible = true
            (selectorField.get(picker) as Paint).color = color
            picker.invalidate()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun calculateNextBirthday(month: Int, day: Int, today: Calendar): Calendar {
        return Calendar.getInstance().apply {
            set(Calendar.YEAR, today.get(Calendar.YEAR))
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, day)

            // If birthday already passed this year, move to next year
            if (before(today) || equals(today)) {
                add(Calendar.YEAR, 1)
            }
        }
    }

    private fun getSpecialFacts(month: Int, day: Int): String {
        return when (month) {
            0 -> when (day) {  // January
                1 -> "New Year baby! First to celebrate each year!"
                in 20..31 -> "Aquarius season - innovative and humanitarian!"
                else -> "Capricorn season - disciplined and practical!"
            }
            1 -> when (day) {  // February
                29 -> "Leap year baby! Official birthday Feb 28/Mar 1 in non-leap years"
                in 14..20 -> "Valentine's week birthday!"
                else -> "Shortest month but packed with holidays!"
            }
            3 -> when (day) {  // April
                22 -> "Earth Day birthday - nature lover!"
                in 1..19 -> "Aries energy - bold and adventurous!"
                else -> "Taurus season - reliable and sensual!"
            }
            5 -> when (day) {  // June
                21 -> "Summer solstice - longest day of the year!"
                in 1..20 -> "Gemini season - social and curious!"
                else -> "Cancer season - nurturing and intuitive!"
            }
            9 -> when (day) {  // October
                in 28..31 -> "Halloween birthday - spooky celebrations!"
                in 23..27 -> "Scorpio season - intense and passionate!"
                else -> "Libra season - balanced and harmonious!"
            }
            11 -> when (day) {  // December
                25 -> "Christmas birthday - double celebration!"
                21 -> "Winter solstice - rebirth and new beginnings!"
                else -> "Holiday season - festive birthday month!"
            }
            else -> listOf(  // Random facts for other months
                "Your birth month has ${when (month) { 3,5,8,10 -> 30 else -> 31 }} days!",
                "Shared with ${listOf("famous inventors", "artists", "world leaders").random()}!",
                "Perfect time for ${listOf("outdoor parties", "cozy gatherings", "travel adventures").random()}!",
                "${listOf("Summer", "Winter", "Spring", "Fall").random()} birthdays have unique charms!"
            ).random()
        }
    }
}