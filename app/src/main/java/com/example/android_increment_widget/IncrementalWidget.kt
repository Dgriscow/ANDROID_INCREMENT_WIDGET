package com.example.android_increment_widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.widget.RemoteViews

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in [IncrementalWidgetConfigureActivity]
 */

//Tutorial used for incremental and reciving help
//https://www.youtube.com/watch?v=VCtGjrmi4a4
class IncrementalWidget : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("Counts", Context.MODE_PRIVATE)
        //Curr VALUE
        val currValue = sharedPreferences.getInt("currCount", 0)
        Log.d("INCC", "CURR VALUE $currValue")

        for (appWidgetId in appWidgetIds) {
            Log.e("EEEE", "inc amount: $appWidgetId")

            updateAppWidget(context, appWidgetManager, appWidgetId)

        }
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        // When the user deletes the widget, delete the preference associated with it.
        for (appWidgetId in appWidgetIds) {
            deleteTitlePref(context, appWidgetId)

        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)

        // override the standard behavior for the intent broadcast
        val action = intent!!.action ?: ""

        if (context != null && action == "increase"){
            //when the action is INCREASE, increase the Preference Value

            val preferences = context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
            //update the internal preferences
            preferences.edit().putString("widgetValue",
                //Preferences GET the widget value string, with a default value
                // convert the value to a int, with the int add 1 to it, then convert it to a string
                (   (preferences.getString("widgetValue", "0") ?: "0").toInt() + 1).toString()

            ).apply()

            updateWidgets(context)


        }//next CHECK FOR DECREMENT
        else if (context != null && action == "decrease"){
            val preferences = context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
            //update the internal preferences
            preferences.edit().putString("widgetValue",
                //Preferences GET the widget value string, with a default value
                // convert the value to a int, with the int add 1 to it, then convert it to a string
                (   (preferences.getString("widgetValue", "0") ?: "0").toInt() + -1).toString()

            ).apply()

            updateWidgets(context)
        }
    }




    //Update ALL Widgets
    private fun updateWidgets(context: Context){
        val manager = AppWidgetManager.getInstance(context)
        val ids = manager.getAppWidgetIds(ComponentName(context, javaClass))

        //update EVERY widget
        ids.forEach { id -> updateAppWidget(context, manager, id) }
    }

    private fun pendingIntent(
        context: Context?,
        action:String
    ): PendingIntent? {
        val intent = Intent(context, javaClass)
        intent.action = action
        // return the pending intent
        //Specifically return a pending intent with a broadcast to update values
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)
    }




    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        //get the widget text value preferences
        val preferences = context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)

        //widget text gets the current value of the text
        val widgetText = preferences.getString("widgetValue", "0"

        )

        //listeners and 
        // Construct the RemoteViews object
        val views = RemoteViews(context.packageName, R.layout.incremental_widget)
        views.setTextViewText(R.id.countTxt, widgetText)

        //set on click pending intent to launch a pending intent to INCREASE the value
        views.setOnClickPendingIntent(R.id.IncrementButton, pendingIntent(context, "increase"))

        //create on click listener for DECREASE
        views.setOnClickPendingIntent(R.id.DecrementButton, pendingIntent(context, "decrease"))


        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }


}
