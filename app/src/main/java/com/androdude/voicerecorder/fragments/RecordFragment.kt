package com.androdude.voicerecorder.fragments

import android.content.DialogInterface
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.androdude.voicerecorder.R
import com.androdude.voicerecorder.databinding.FragmentRecordBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.fragment_record.*
import java.text.SimpleDateFormat
import java.util.*


class RecordFragment : Fragment() {
    //UI
    private lateinit var alertDialog: androidx.appcompat.app.AlertDialog
    private var _binding : FragmentRecordBinding ?= null
    private val binding get() = _binding!!
    private lateinit var navigation : NavController

    //Variables
    private var isRecording = false
    private var mediaRecorder: MediaRecorder ?= null
    private lateinit var record_file_name : String

    //Permissions
    private val permissions = arrayOf(
        android.Manifest.permission.READ_EXTERNAL_STORAGE,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        android.Manifest.permission.RECORD_AUDIO
    )




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentRecordBinding.inflate(inflater,null,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navigation = Navigation.findNavController(binding.root)
        binding.audioList.setOnClickListener {
            if(isRecording)
            {
                check(false)
            }
            else
            {
                navigation.navigate(R.id.action_recordFragment_to_listFragment)
            }

        }
        binding.recordAudio.setOnClickListener {
            if(isRecording)
            {
                check(true)

                Toast.makeText(activity,"Recording Ended",Toast.LENGTH_SHORT).show()
            }
            else
            {
                if(chekPermissions())
                {

                    startRecording()
                    Toast.makeText(activity,"Recording Started",Toast.LENGTH_SHORT).show()
                    isRecording=true
                }
            }
        }
    }

    //Asking Runtime Permissions
    private fun chekPermissions():Boolean  {
        if(ActivityCompat.checkSelfPermission(requireActivity(),android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(requireActivity(),android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(requireActivity(),android.Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)
        {
            return true
        }
        else{
            ActivityCompat.requestPermissions(requireActivity(),permissions,5000)

        }

        return false
    }


    private fun startRecording() {
        //Chronometer UI
        binding.recordTimer.base = SystemClock.elapsedRealtime()
        binding.recordTimer.start()

        //Getting Record Path And Mediaplayer Works
        val recordPath = activity?.getExternalFilesDir("/")!!.absolutePath
        val format = SimpleDateFormat("yyyy_MM_dd_hh__ss", Locale.CANADA)
        val now = Date()
        record_file_name = "Recoding_"+format.format(now)+".3gp"
        binding.recorAudioName.text =  record_file_name
        mediaRecorder= MediaRecorder()
        mediaRecorder!!.setAudioSource(MediaRecorder.AudioSource.MIC)
        mediaRecorder!!.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        mediaRecorder!!.setOutputFile(recordPath + "/" + record_file_name)
        mediaRecorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
        mediaRecorder!!.prepare()
        mediaRecorder!!.start()
    }

    private fun stopRecording()
    {
        binding.recordTimer.stop()
        mediaRecorder!!.stop()
        mediaRecorder!!.release()
        mediaRecorder=null
        isRecording=false
        binding.recorAudioName.text = "Press The Mic Button To Record"

    }



    fun check(flag : Boolean)
    {
        //Chronometer UI
        var timeWhenStopped: Long = 0
        timeWhenStopped = record_timer.getBase() - SystemClock.elapsedRealtime()
        record_timer.stop()
        mediaRecorder!!.pause()

        //Alert Dialog
         alertDialog = MaterialAlertDialogBuilder(activity)
             .setCancelable(false)
            .setMessage("Do you want to stop the recording?")
            .setTitle("Hey!!")
            .setPositiveButton("Stop", object : DialogInterface.OnClickListener {
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    if(flag)
                    {
                        record_timer.stop()
                        stopRecording()
                    }
                    else
                    {
                        record_timer.stop()
                        stopRecording()
                        navigation.navigate(R.id.action_recordFragment_to_listFragment)
                    }
                    alertDialog.dismiss()

                }

            }).setNegativeButton("No", object : DialogInterface.OnClickListener {
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    record_timer.setBase(SystemClock.elapsedRealtime() + timeWhenStopped)
                    record_timer.start()
                    alertDialog.dismiss()
                    mediaRecorder!!.resume()
                }

            }).create()



        alertDialog.show()



    }



    override fun onDestroy() {
        super.onDestroy()
        mediaRecorder!!.release()
        mediaRecorder=null

    }





}