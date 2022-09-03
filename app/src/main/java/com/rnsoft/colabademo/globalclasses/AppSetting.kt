package com.rnsoft.colabademo

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.annotation.IdRes
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.text.DateFormat
import java.text.DecimalFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.math.roundToInt


object AppSetting {

    var userHasLoggedIn:Boolean = false

    //const val lockAppState:String = "lockAppState"

     const val lockAppState:String = "lockAppState"

    var initialScreenLoaded:Boolean = false

    var biometricEnabled: Boolean = false
    var loanApiDateTime: String = ""
    var activeloanApiDateTime: String = ""
    var nonActiveloanApiDateTime: String = ""



    val states:ArrayList<String> = arrayListOf("Alaska", "Alabama", "Arkansas", "American Samoa", "Arizona", "California", "Colorado", "Connecticut", "District of Columbia", "Delaware", "Florida", "Georgia", "Guam", "Hawaii", "Iowa", "Idaho", "Illinois", "Indiana", "Kansas", "Kentucky", "Louisiana", "Massachusetts", "Maryland", "Maine", "Michigan", "Minnesota", "Missouri", "Mississippi", "Montana", "North Carolina", "North Dakota", "Nebraska", "New Hampshire", "New Jersey", "New Mexico", "Nevada", "New York", "Ohio", "Oklahoma", "Oregon", "Pennsylvania", "Puerto Rico", "Rhode Island", "South Carolina", "South Dakota", "Tennessee", "Texas", "Utah", "Virginia", "Virgin Islands", "Vermont", "Washington", "Wisconsin", "West Virginia", "Wyoming")

    val countries:ArrayList<String> = arrayListOf("United States", "Afghanistan", "Albania", "Algeria", "American Samoa", "Andorra", "Angola", "Anguilla", "Antarctica", "Antigua and Barbuda", "Argentina", "Armenia", "Aruba", "Australia", "Austria", "Azerbaijan", "Bahamas", "Bahrain", "Bangladesh", "Barbados", "Belarus", "Belgium", "Belize", "Benin", "Bermuda", "Bhutan", "Bolivia", "Bosnia and Herzegowina", "Botswana", "Bouvet Island", "Brazil", "Brunei Darussalam", "Bulgaria", "Burkina Faso", "Burundi", "Cambodia", "Cameroon", "Canada", "Cape Verde", "Cayman Islands", "Central African Republic", "Chad", "Chile", "China", "Christmas Island", "Colombia", "Comoros", "Congo", "Cook Islands", "Costa Rica", "Cote d'Ivoire", "Croatia (Hrvatska)", "Cuba", "Cyprus", "Czech Republic", "Denmark", "Djibouti", "Dominica", "Dominican Republic", "East Timor", "Ecuador", "Egypt", "El Salvador", "Equatorial Guinea", "Eritrea", "Estonia", "Ethiopia", "Falkland Islands (Malvinas)", "Faroe Islands", "Fiji", "Finland", "France", "Gabon", "Gambia", "Georgia", "Germany", "Ghana", "Gibraltar", "Greece", "Greenland", "Grenada", "Guadeloupe", "Guam", "Guatemala", "Guinea", "Guinea-Bissau", "Guyana", "Haiti", "Heard and Mc Donald Islands", "Holy See (Vatican City State)", "Honduras", "Hong Kong", "Hungary", "Iceland", "India", "Indonesia", "Iran (Islamic Republic of)", "Iraq", "Ireland", "Israel", "Italy", "Jamaica", "Japan", "Jordan", "Kazakhstan", "Kenya", "Kiribati", "Korea, Democratic People's Republic of", "Korea, Republic of", "Kuwait", "Kyrgyzstan", "Lao, People's Democratic Republic", "Latvia", "Lebanon", "Lesotho", "Liberia", "Libyan Arab Jamahiriya", "Liechtenstein", "Lithuania", "Luxembourg", "Macau", "Macedonia, The Former Yugoslav Republic of", "Madagascar", "Malawi", "Malaysia", "Maldives", "Mali", "Malta", "Marshall Islands", "Martinique", "Mauritania", "Mauritius", "Mayotte", "Mexico", "Micronesia, Federated States of", "Moldova, Republic of", "Monaco", "Mongolia", "Montserrat", "Morocco", "Mozambique", "Myanmar", "Namibia", "Nauru", "Nepal", "Netherlands", "Netherlands Antilles", "New Caledonia", "New Zealand", "Nicaragua", "Niger", "Nigeria", "Niue", "Norfolk Island", "Northern Mariana Islands", "Norway", "Oman", "Pakistan", "Palau", "Panama", "Papua New Guinea", "Paraguay", "Peru", "Philippines", "Pitcairn", "Poland", "Portugal", "Puerto Rico", "Qatar", "Reunion", "Romania", "Russian Federation", "Rwanda", "Saint Kitts and Nevis", "Saint Lucia", "Saint Vincent and the Grenadines", "Samoa", "San Marino", "Sao Tome and Principe", "Saudi Arabia", "Senegal", "Seychelles", "Sierra Leone", "Singapore", "Slovakia (Slovak Republic)", "Slovenia", "Solomon Islands", "Somalia", "South Africa", "South Georgia and the South Sandwich Islands", "Spain", "Sri Lanka", "St. Helena", "St. Pierre and Miquelon", "Sudan", "Suriname", "Swaziland", "Sweden", "Switzerland", "Syrian Arab Republic", "Taiwan", "Tajikistan", "Tanzania, United Republic of", "Thailand", "Togo", "Tokelau", "Tonga", "Trinidad and Tobago", "Tunisia", "Turkey", "Turkmenistan", "Turks and Caicos Islands", "Tuvalu", "Uganda", "Ukraine", "United Arab Emirates", "United Kingdom", "Uruguay", "Uzbekistan", "Vanuatu", "Venezuela", "Vietnam", "Virgin Islands", "Virgin Islands", "Wallis and Futuna Islands", "Western Sahara", "Yemen", "Yugoslavia", "Zambia", "Zimbabwe")

    

    // check in Repo to query Room database....
    var hasLoanApiDataLoaded = false
    var hasActiveLoanApiDataLoaded = false
    var hasNonActiveLoanApiDataLoaded = false

    fun returnGreetingString(): String {
        val currentTimeAgain: String =
            SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        //Log.e("currentTimeAgain-time-", currentTimeAgain)

        var greetingString = ""

        val timeInArray = currentTimeAgain.split(":").map { it.toInt() }
        if (timeInArray[0] < 12)
            greetingString = "Good Morning"
        else if (timeInArray[0] in 13..16)
            greetingString = "Good Afternoon"
        else
            greetingString = "Good Evening"

        return greetingString
    }

    fun getDocumentUploadedDate(fileUploaded: String): String {
        // maths.abs used for converting - valueto plus
        var output: String = ""
        var trim: String
        if (fileUploaded.contains(":Z")) {
            trim = fileUploaded.substring(0, fileUploaded.length - 2)
        } else
            trim = fileUploaded.substring(0, fileUploaded.length - 4)

        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"))
        val dt1: Date = formatter.parse(trim)
        val firstDate: String = formatter.format(dt1)

        var secondDate = Date()
        val currentDate: String = formatter.format(secondDate)
        //Log.e("fileUploadOn", fileUploaded + " Doc name: " + docName)
        //Log.e("date1", firstDate)
        //Log.e("secondDate", "" + currentDate)

        val dBefore: LocalDateTime = LocalDateTime.parse(firstDate)
        val dAfter: LocalDateTime = LocalDateTime.parse(currentDate)

        val sec: Long = dBefore.until(dAfter, ChronoUnit.SECONDS)
        val min: Long = dBefore.until(dAfter, ChronoUnit.MINUTES)
        val hour: Long = dBefore.until(dAfter, ChronoUnit.HOURS)
        val day: Long = dBefore.until(dAfter, ChronoUnit.DAYS)
        val week: Long = dBefore.until(dAfter, ChronoUnit.WEEKS)
        val month: Long = dBefore.until(dAfter, ChronoUnit.MONTHS)
        val year: Long = dBefore.until(dAfter, ChronoUnit.YEARS)

        /*println("************************")
        println("Year is : $year")
        println("month is : $month")
        println("week is : $week")
        println("day is : $day")
        println("hour is : $hour")
        println("min is : $min")
        println("sec is : $sec") */

        if (Math.abs(year) >= 2) {
            output = Math.abs(year).toString().plus(" years ago")
            return output
        }
        if (Math.abs(year) >= 1) {
            output = "Last year"
            return output
        }
        if (Math.abs(month) >= 2) {
            output = Math.abs(month).toString().plus(" months ago")
            return output
        }
        if (Math.abs(month) >= 1) {
            output = "Last month"
            return output
        }
        if (Math.abs(week) >= 2) {
            output = Math.abs(week).toString().plus(" weeks ago")
            return output
        }
        if (Math.abs(week) >= 1) {
            output = "Last week"
            return output
        }
        if (Math.abs(day) >= 2) {
            output = Math.abs(day).toString().plus(" days ago")
            return output
        }
        if (Math.abs(day) >= 1) {
            output = "Yesterday"
            return output
        }
        if (Math.abs(hour) >= 2) {
            output = Math.abs(hour).toString().plus(" hours ago")
            return output
        }
        if (Math.abs(hour) >= 1) {
            output = "1 hour ago"
            return output
        }
        if (Math.abs(min) >= 2) {
            output = Math.abs(min).toString().plus(" minutes ago")
            return output
        }
        if (Math.abs(min) >= 1) {
            output = "1 minute ago"
            return output
        }
        if (Math.abs(sec) >= 3) {
            output = Math.abs(sec).toString().plus(" seconds ago")
            return output
        }
        if (Math.abs(sec) < 3) {
            output = "Just now"
            return output
        }
        return output
    }

    fun getFullDate(dateStr: String) : String{
        val localDateTime = LocalDateTime.parse(dateStr)
        val formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy")
        val output = formatter.format(localDateTime)
        return output
    }

    fun getFullDate1(dateStr: String) : String{
        val localDateTime = LocalDateTime.parse(dateStr)
        val formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
        val output = formatter.format(localDateTime)
        return output
    }

    fun getMonthAndYear(dateString : String, isReturnFormatSlash: Boolean) : String{
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val oldDate: Date? = formatter.parse(dateString)
        val oldMillis = oldDate?.time
        var finalTimeInFormat = ""
         oldMillis?.let {
             if(isReturnFormatSlash)
                 finalTimeInFormat = getDate(oldMillis, "MM/yyyy")
             else
                finalTimeInFormat = getDate(oldMillis, "MM-yyyy")
        }
        return finalTimeInFormat
    }

    fun getMonthAndYearValue(dateStr : String) : String{
        val localDateTime = LocalDateTime.parse(dateStr)
        val formatter = DateTimeFormatter.ofPattern("MMM")
        val month = formatter.format(localDateTime)

        val yearformat = DateTimeFormatter.ofPattern("yyyy")
        val year = yearformat.format(localDateTime)
        val output = month.plus(" ").plus(year)
        return output
    }

    fun reverseDateFormat(dateString : String) : String{
        var input = dateString.replace(" ", "")
        val formatter = SimpleDateFormat("dd/MM/yyyy")
        val oldDate: Date? = formatter.parse(input)
        val postFormater = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        val newDateStr = postFormater.format(oldDate)

        return newDateStr
    }

    fun returnLongTimeNow(input: String): String {

        var lastSeen = input

        if (input.contains(":Z"))
            lastSeen = input.substring(0, input.length - 2).toString()
        else
            lastSeen = input.substring(0, input.length - 4)

        //Log.e("input-time--", lastSeen)

        //val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.US)
        val oldDate: Date? = formatter.parse(lastSeen)


        val oldMillis = oldDate?.time

        oldMillis?.let {
            //Log.e("oldMillis", "Date in milli :: FOR API >= 26 >>> $oldMillis")
            //Log.e("lastseen", "Date in milli :: FOR API >= 26 >>>"+ lastseen(oldMillis))
            lastSeen = lastseen(oldMillis)
        }

        return lastSeen

    }

    fun returnAmountFormattedString(amount: Double): String {
        val df2 = DecimalFormat()
        df2.maximumFractionDigits = 0
        //Log.e("new-deci-format", df2.format(amount).toString())
        return df2.format(amount).toString()
    }

    fun returnNotificationTime(input: String): String {
        var lastSeen = input
        // "2021-07-02T11:43:07.205Z"
        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
        val oldDate: Date? = formatter.parse(input)
        val oldMillis = oldDate?.time
        oldMillis?.let {
            lastSeen = lastseen(oldMillis)
        }

        return lastSeen
    }

    fun documentDetailDateTimeFormat(input: String): String {
        var receivedTimeString = input
        receivedTimeString = if (input.contains(":Z"))
            input.substring(0, input.length - 2).toString()
        else
            input.substring(0, input.length - 4)

        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.US)
        val oldDate: Date? = formatter.parse(receivedTimeString)
        val oldMillis = oldDate?.time
        var finalTimeInFormat = ""

        //val dateFormatter = SimpleDateFormat("E MMM dd,yyyy hh:mm a");
        // System.out.println("Format 2:   " + dateFormatter.format(now));
        oldMillis?.let {
            finalTimeInFormat = getDate(it, "E MMM dd,yyyy hh:mm a")
        }

        return finalTimeInFormat
    }


    fun getAppStatusDateFormat(input: String): String {
        var receivedTimeString = input
        receivedTimeString = if (input.contains(":Z"))
            input.substring(0, input.length - 2).toString()
        else
            input.substring(0, input.length - 4)

        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.US)
        val oldDate: Date? = formatter.parse(receivedTimeString)
        val oldMillis = oldDate?.time
        var finalTimeInFormat = ""

        oldMillis?.let {
            //finalTimeInFormat = getDate(it, "E MMM dd,yyyy hh:mm")
            finalTimeInFormat = getDate(it, "dd MMM, yyyy hh:mm")
        }

        return finalTimeInFormat
    }

    private fun getDate(milliSeconds: Long, dateFormat: String?): String {
        // Create a DateFormatter object for displaying date in specified format.
        val formatter = SimpleDateFormat(dateFormat)

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = milliSeconds
        return formatter.format(calendar.time)
    }

    fun lastseen(time: Long): String {
        val difference = (System.currentTimeMillis() - time) / 1000

        return if (difference < 60) {
            "just now"
        } else if (difference < 60 * 2) {
            "1 minute ago"
        } else if (difference < 60 * 60) {
            val minutes = (difference / 60.0).roundToInt()
            "$minutes minutes ago"
        } else if (difference < 60 * 60 * 2) {
            "1 hour ago"
        } else if (difference < 60 * 60 * 24) {
            val hours = (difference / (60.0 * 60.0)).roundToInt()
            "$hours hours ago"
        } else if (difference < 60 * 60 * 48) {
            "1 day ago"
        } else {
            val days = (difference / (60.0 * 60.0 * 24.0)).roundToInt()
            "$days days ago"
        }
    }

    fun showBadge(context: Context?, bottomNavigationView: BottomNavigationView, @IdRes itemId: Int, value: String?) {
        removeBadge(bottomNavigationView, itemId)
        val itemView: BottomNavigationItemView = bottomNavigationView.findViewById(itemId)
        val badge: View = LayoutInflater.from(context)
            .inflate(R.layout.layout_news_badge, bottomNavigationView, false)
        val text: TextView = badge.findViewById(R.id.badge_text_view)
        text.text = value
        itemView.addView(badge)
    }

    fun removeBadge(bottomNavigationView: BottomNavigationView, @IdRes itemId: Int) {
        val itemView: BottomNavigationItemView = bottomNavigationView.findViewById(itemId)
        if (itemView.childCount == 3) {
            itemView.removeViewAt(2)
        }
    }

    /*
    fun isDateInCurrentWeek(date: Date?): Boolean {
        val currentCalendar = Calendar.getInstance()
        val week = currentCalendar[Calendar.WEEK_OF_YEAR]
        val year = currentCalendar[Calendar.YEAR]
        val targetCalendar = Calendar.getInstance()
        targetCalendar.time = date
        val targetWeek = targetCalendar[Calendar.WEEK_OF_YEAR]
        val targetYear = targetCalendar[Calendar.YEAR]
        return week == targetWeek && year == targetYear
    }


    fun getMilliFromDate(dateFormat: String?): Long {
        var date = Date()
        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        try {
            date = formatter.parse(dateFormat)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        println("Today is $date")
        return date.time
    }



    //1 minute = 60 seconds
    //1 hour = 60 x 60 = 3600
    //1 day = 3600 x 24 = 86400
    fun getTimeDifference(startDate: Long, endDate: Long): String? {
        //milliseconds
        var different = endDate - startDate

        //        System.out.println("startDate : " + startDate);
        //        System.out.println("endDate : " + endDate);
        //        System.out.println("different : " + different);
        val secondsInMilli: Long = 1000
        val minutesInMilli = secondsInMilli * 60
        val hoursInMilli = minutesInMilli * 60
        val daysInMilli = hoursInMilli * 24
        val elapsedDays = different / daysInMilli
        different = different % daysInMilli
        val elapsedHours = different / hoursInMilli
        different = different % hoursInMilli
        val elapsedMinutes = different / minutesInMilli
        different = different % minutesInMilli
        val elapsedSeconds = different / secondsInMilli

        //        System.out.printf("%d days, %d hours, %d minutes, %d seconds%n",
        //                elapsedDays, elapsedHours, elapsedMinutes, elapsedSeconds);
        return if (elapsedDays > 0) {
            MessageFormat.format(
                "{0}d {1}h {2}m {3}s",
                elapsedDays,
                elapsedHours,
                elapsedMinutes,
                if (elapsedSeconds > 0) elapsedSeconds else 0
            )
        } else if (elapsedHours > 0) {
            MessageFormat.format(
                "{0}h {1}m {2}s",
                elapsedHours,
                elapsedMinutes,
                if (elapsedSeconds > 0) elapsedSeconds else 0
            )
        } else if (elapsedMinutes > 0) {
            MessageFormat.format(
                "{0}m {1}s",
                elapsedMinutes,
                if (elapsedSeconds > 0) elapsedSeconds else 0
            )
        } else {
            MessageFormat.format("{0} s", if (elapsedSeconds > 0) elapsedSeconds else 0)
        }
    }

     */

}