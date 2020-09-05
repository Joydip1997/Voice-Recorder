package com.androdude.voicerecorder.utils

import java.util.*
import java.util.concurrent.TimeUnit

class TimeAgo {



    companion object
    {
        //Helper Function
        fun setTime(duration : Long) : String
        {
            val now = Date()

            val secs = Integer.parseInt(TimeUnit.MILLISECONDS.toSeconds(now.time - duration).toString())
            val mins = Integer.parseInt(TimeUnit.MILLISECONDS.toMinutes(now.time - duration).toString())
            val hours = Integer.parseInt(TimeUnit.MILLISECONDS.toHours(now.time - duration).toString())
            val days = Integer.parseInt(TimeUnit.MILLISECONDS.toDays(now.time - duration).toString())

            if(secs < 60)
            {
                return "just now"
            }
            else if(mins == 1)
            {
                return "a minute ago"
            }
            else if (mins > 1 && mins < 60)
            {
                return mins.toString() + " minutes ago"
            }
            else if(hours == 1)
            {
                return "an hour ago"
            }
            else if(hours > 1 && hours < 24)
            {
                return hours.toString() + " hours ago"
            }
            else if( days == 1)
            {
                return "1 day ago"
            }
            else
            {
               return days.toString() + " days ago"
            }

        }
    }
}