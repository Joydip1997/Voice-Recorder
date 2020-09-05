package com.androdude.voicerecorder.UI

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.androdude.voicerecorder.R
import com.androdude.voicerecorder.utils.TimeAgo
import java.io.File

class filesAdapter(var files_list : Array<File>,var mListener : onItemClickListener,var mListener2: onItemDeleteClickListener) : RecyclerView.Adapter<filesAdapter.fileViewHolder>() {


    //Get item position to play the audio
    interface onItemClickListener
    {
        fun getItemPos(pos : Int)
    }
    //Get item position to delete the item
    interface onItemDeleteClickListener
    {
        fun getItemDeletePos(pos:Int)
    }

    fun setNewDataset(files : Array<File>)
    {
        files_list=files
    }



   inner class fileViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView)
    {

            val name = itemView.findViewById<TextView>(R.id.file_name)
            val created_at = itemView.findViewById<TextView>(R.id.time_created)
            val delete_bttn= itemView.findViewById<ImageButton>(R.id.delete_file)

        init {
            itemView.setOnClickListener{
                if(adapterPosition != RecyclerView.NO_POSITION)
                {
                    mListener.getItemPos(adapterPosition)
                }
            }

            delete_bttn.setOnClickListener {
                if(adapterPosition != RecyclerView.NO_POSITION)
                {
                    mListener2.getItemDeletePos(adapterPosition)
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): fileViewHolder {
      return fileViewHolder(LayoutInflater.from(parent.context).inflate(
          R.layout.files_layout,parent,false
      ))
    }

    override fun getItemCount(): Int {
        return files_list.size

    }

    override fun onBindViewHolder(holder: fileViewHolder, position: Int) {
      val current = files_list[position]
        holder.name.text=current.name
        holder.created_at.text = TimeAgo.setTime(current.lastModified())

    }
}