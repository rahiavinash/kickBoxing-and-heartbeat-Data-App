package com.app.kickboxing.Activitys.presentation.Ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.app.kickboxing.R

class ActivityAdapter
    (
    private val context: Context,
    private val dataList: List<String>
    ) : BaseAdapter() {

        override fun getCount(): Int {
            return dataList.size
        }

        override fun getItem(position: Int): Any {
            return dataList[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val view: View
            val holder: ViewHolder

            if (convertView == null) {
                 view = LayoutInflater.from(context).inflate(R.layout.listview_main, parent, false)
                holder = ViewHolder(view)
                view.tag = holder
            } else {
                view = convertView
                holder = view.tag as ViewHolder
            }

            val activity = getItem(position) as String
            holder.nameTextView.text = activity
            holder.descriptionTextView.text = "Start New Workout"

            return view
        }

        private class ViewHolder(view: View) {
            val nameTextView: TextView = view.findViewById(R.id.list_text_name)
            val descriptionTextView: TextView = view.findViewById(R.id.textView)
        }
    }

