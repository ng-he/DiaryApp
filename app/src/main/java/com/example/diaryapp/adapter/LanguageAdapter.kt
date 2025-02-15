package com.example.diaryapp.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.diaryapp.DiaryApp
import com.example.diaryapp.utils.Language
import com.example.diaryapp.R
import com.example.diaryapp.utils.SharedPrefsManager

class LanguageAdapter(
    private val languages: List<Language>,
    private val onItemClick: (String) -> Unit = {}
) : RecyclerView.Adapter<LanguageAdapter.LanguageViewHolder>() {

    private var selectedLanguage = DiaryApp.language

    inner class LanguageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val flagIcon: ImageView = itemView.findViewById(R.id.flagIcon)
        val languageName: TextView = itemView.findViewById(R.id.languageName)
        val radioButton: RadioButton = itemView.findViewById(R.id.radioButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LanguageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.language_item_layout, parent, false)
        return LanguageViewHolder(view)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: LanguageViewHolder, position: Int) {
        val language = languages[position]
        holder.flagIcon.setImageResource(language.icon)
        holder.languageName.text = DiaryApp.instance.getString(language.displayName)
        holder.radioButton.isChecked = language.code == selectedLanguage

        if (language.code == selectedLanguage) {
            holder.itemView.setBackgroundResource(R.drawable.choosed_language_item_background)
        } else {
            holder.itemView.setBackgroundResource(R.drawable.language_item_background)
        }

        val onItemViewClick = {
            selectedLanguage = language.code
            onItemClick(language.code)
            notifyDataSetChanged()
        }

        holder.itemView.setOnClickListener {
            onItemViewClick()
        }

        holder.radioButton.setOnClickListener {
            onItemViewClick()
        }
    }

    override fun getItemCount(): Int = languages.size
}
