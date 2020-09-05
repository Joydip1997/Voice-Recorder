package com.androdude.voicerecorder.fragments

import android.content.DialogInterface
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.renderscript.ScriptGroup
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import com.androdude.voicerecorder.R
import com.androdude.voicerecorder.UI.filesAdapter
import com.androdude.voicerecorder.databinding.FragmentListBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.audio_player_sheet.*
import kotlinx.android.synthetic.main.fragment_record.*
import java.io.File
import java.lang.Exception


class ListFragment : Fragment(),filesAdapter.onItemClickListener,filesAdapter.onItemDeleteClickListener {

    //UI
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private var _binding : FragmentListBinding ?= null
    private val binding get() = _binding!!
    private lateinit var play_sheet : ImageButton
    private lateinit var fast_forward :ImageButton
    private lateinit var fast_rewind : ImageButton
    private lateinit var seekBar: SeekBar
    private lateinit var alertDialog: androidx.appcompat.app.AlertDialog


    //Variables
    private lateinit var path : String
    private lateinit var dir : File
    private lateinit var all_files : Array<File>
    private var isPlaying = false
    private lateinit var adapter : filesAdapter
    private lateinit var mp : MediaPlayer
    private lateinit var file_to_play : File
    private  var currentPosition: Int = 0
    private val SEEK_LENGTH = 500
    private lateinit var updateseekBar : Runnable
    private lateinit var seekbarHandler: Handler



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding  = FragmentListBinding.inflate(inflater,null,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Varibales Initialization
        play_sheet = view.findViewById(R.id.play_sheet)
        seekBar = view.findViewById(R.id.seek_bar)
        fast_forward = view.findViewById(R.id.fast_forward)
        fast_rewind = view.findViewById(R.id.fast_rewind)

        //Bottomsheet UI
        bottomSheetBehavior = BottomSheetBehavior.from(audio_player_sheet)
        bottomSheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback()
        {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if(newState == BottomSheetBehavior.STATE_HIDDEN)
                {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                }
            }

        })

        //Files path
        path = activity?.getExternalFilesDir("/")!!.absolutePath
        seekbarHandler = Handler()
        dir = File(path)
        all_files = dir.listFiles()!!

        //Recyclerview UI
        adapter= filesAdapter(all_files,this,this)
        binding.listFiles.adapter=adapter
        binding.listFiles.layoutManager = LinearLayoutManager(activity,LinearLayoutManager.VERTICAL,false)
        binding.listFiles.setHasFixedSize(true)


        //Play Audio
        play_sheet.setOnClickListener {
            if(this::file_to_play.isInitialized)
            {
                if(isPlaying)
                {
                    if(mp.currentPosition == mp.duration)
                    {
                        stop()
                        //Play new audio from second time
                        play()
                    }
                    else
                    {
                        pause()
                    }
                }
                else
                {
                    resume()
                }
            }

        }

        //Forward
        fast_forward.setOnClickListener {
            if(this::mp.isInitialized)
            {
                pause()
                mp.seekTo(mp.currentPosition+SEEK_LENGTH)
                seekbarHandler.postDelayed(Runnable { resume() },100)
            }

        }

        //Rewind
        fast_rewind.setOnClickListener {
            if(this::mp.isInitialized)
            {
                pause()
                mp.seekTo(mp.currentPosition-SEEK_LENGTH)
                seekbarHandler.postDelayed(Runnable { resume() },100)
            }
        }


        //Seekbar update
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener
        {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {

            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
                pause()
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                checkMediaplayer(p0!!.progress)
            }

        })



    }

    //Check if mediaplayer is initialized
    private fun checkMediaplayer(progress: Int?) {
        if(this::mp.isInitialized)
        {
            mp.seekTo(progress!!)
            resume()
        }

    }


    override fun getItemPos(pos: Int) {
        file_to_play=all_files[pos]
        if(isPlaying)
        {
            stop()
            //Stop and play the new auio from second time
            play()
        }
        else
        {
            play()
        }
    }



    fun play()
    {
        //UI
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        is_playing.setText("Playing")
        audio_name.setText(file_to_play.name)
        play_sheet.setBackground(resources.getDrawable(R.drawable.ic_pause))
        //Mediaplayer Works
        mp = MediaPlayer()
        try {
            mp.setDataSource(file_to_play.absolutePath)
            mp.prepare()
            mp.start()
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
        mp.setOnCompletionListener {
            is_playing.setText("Stopped")
            play_sheet.setBackground(resources.getDrawable(R.drawable.ic_play))
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
        //Update Seekbar
        seekBar.max = mp.duration
        updateSeekBar()
        isPlaying=true

    }

    fun stop()
    {
        mp.stop()
        //UI
        is_playing.setText("Stopped")
        play_sheet.setBackground(resources.getDrawable(R.drawable.ic_play))
        isPlaying=false
    }



    fun resume()
    {
        mp.start()
        //UI
        play_sheet.setBackground(resources.getDrawable(R.drawable.ic_pause))
        //Update Seekbar
        seekbarHandler.removeCallbacks(updateseekBar)
        updateSeekBar()
        seekbarHandler.postDelayed(updateseekBar,0)
        isPlaying=true
    }

    fun pause()
    {
        mp.pause()
        //UI
        play_sheet.setBackground(resources.getDrawable(R.drawable.ic_play))
        //Update Seekbar
        seekbarHandler.removeCallbacks(updateseekBar)
        updateSeekBar()
        seekbarHandler.postDelayed(updateseekBar,0)
        isPlaying=false
    }

    private fun updateSeekBar() {
        updateseekBar = Runnable {
            try {
                currentPosition = mp.getCurrentPosition()
            } catch ( e : Exception) {
                e.printStackTrace()
            }
            seekBar.setProgress(currentPosition)
            seekbarHandler.postDelayed(updateseekBar,300)
        }
        seekbarHandler.postDelayed(updateseekBar,0)
    }



    override fun getItemDeletePos(pos: Int) {
        deleteDialog(pos)
    }

    fun deleteDialog(pos : Int)
    {
        //AlertDialog
        alertDialog = MaterialAlertDialogBuilder(activity)
            .setCancelable(false)
            .setMessage("Do you want to delete this recording?")
            .setTitle("Hey!!")
            .setPositiveButton("Yes", object : DialogInterface.OnClickListener {
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    //Delete The File Is Array Is Not Empty
                    if(all_files.size>0)
                    {
                        if(isPlaying)
                        {
                            stop()
                            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                            isPlaying=false
                        }
                        all_files[pos].delete()
                        all_files=getNewFilesList(all_files,pos)
                        adapter.setNewDataset(all_files)
                        adapter.notifyDataSetChanged()
                    }
                    alertDialog.dismiss()

                }

            }).setNegativeButton("No", object : DialogInterface.OnClickListener {
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    alertDialog.dismiss()
                }

            }).create()
        alertDialog.show()

    }

    //Remove The File From Array
    fun getNewFilesList(files : Array<File>,pos : Int) : Array<File>
    {
        var newFiles : Array<File> = Array<File>(files.size-1, init= {i:Int -> files[0]})
        var  i = pos
        var j = 0
        while(i<files.size-1)
        {
            files[i]=files[i+1]
            i++
        }
        while(j<files.size-1)
        {
            newFiles[j] = files[j]
            j++
        }
        return newFiles!!
    }

    override fun onDestroy() {
        super.onDestroy()

        if(isPlaying)
        {
            mp.stop()
            mp.release()
        }

    }


}