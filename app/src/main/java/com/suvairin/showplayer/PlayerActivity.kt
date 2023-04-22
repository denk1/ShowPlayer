package com.suvairin.showplayer

import android.content.Context
import android.icu.text.TimeZoneFormat
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.AudioManager.OnAudioFocusChangeListener
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.SeekBar
import androidx.recyclerview.widget.LinearLayoutManager
import java.text.SimpleDateFormat
import com.suvairin.showplayer.databinding.ActivityPlayerBinding
import java.time.Duration
import java.util.*

class PlayerActivity : AppCompatActivity() {
    private  lateinit var binding: ActivityPlayerBinding
    lateinit var runnable: Runnable
    private var handler = Handler(Looper.myLooper()!!)
    private var timePos : SimpleDateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    private var duration: kotlin.time.Duration? = null
    private var mediaPlayer: MediaPlayer? = null
    private var curr_pos: Int = 0
    private lateinit  var audioManager: AudioManager
    private lateinit var playbackAttributes: AudioAttributes

    private var data: Uri? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        // AudioManager initialization
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        playbackAttributes = AudioAttributes.Builder().
                             setUsage(AudioAttributes.USAGE_GAME).
                             setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).
                             build()
        val focusRequest: AudioFocusRequest  = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
            .setAudioAttributes(playbackAttributes)
            .setAcceptsDelayedFocusGain(true)
            .setOnAudioFocusChangeListener { focusChange ->
                if (focusChange == AudioManager.AUDIOFOCUS_GAIN)
                {
                    mediaPlayer?.start()
                    mediaPlayer?.seekTo(curr_pos)

                } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT)
                {
                    mediaPlayer?.pause()
                    curr_pos = mediaPlayer?.currentPosition!!

                } else if(focusChange == AudioManager.AUDIOFOCUS_LOSS)
                {

                }

            }
            .build()
        val audioFocusRequest: Int = audioManager.requestAudioFocus(focusRequest)
        // the end of AudioManager initialization
        setContentView(binding.root)
        val intent = intent
        val title = intent.extras!!.getString("title")
        val location = intent.extras!!.getString("location")
        binding.musicTitle.text = title
        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            setDataSource(location)
            prepare() // might take long! (for buffering, etc)
            if(checkAudioFocus(audioFocusRequest)) {
                start()
                binding.playBtn.setImageResource(R.drawable.ic_baseline_pause_24)
            }
        }
        val timeZone:SimpleTimeZone = SimpleTimeZone(0, "UTC")
        timePos.timeZone = timeZone
        binding.endPos.text = timePos.format(mediaPlayer?.duration)
        binding.seekbar.progress = 0
        binding.seekbar.max = mediaPlayer!!.duration

        binding.playBtn.setOnClickListener {
            if(!mediaPlayer!!.isPlaying) {
                if(checkAudioFocus(audioFocusRequest))
                    mediaPlayer?.start()
                binding.playBtn.setImageResource(R.drawable.ic_baseline_pause_24)
            } else {
                mediaPlayer?.pause()
                binding.playBtn.setImageResource(R.drawable.ic_baseline_play_arrow_24)

            }
        }


        binding.seekbar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, pos: Int, changed: Boolean) {
                if (changed) {
                    mediaPlayer?.seekTo(pos)
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {

            }
        })

        runnable = Runnable {
            binding.seekbar.progress = if (mediaPlayer != null) mediaPlayer!!.currentPosition else 0
            binding.curPos.text = timePos.format(binding.seekbar.progress)
            handler.postDelayed(runnable, 1000)
        }

        handler.postDelayed(runnable, 1000)
        mediaPlayer?.setOnCompletionListener {
            binding.playBtn.setImageResource(R.drawable.ic_baseline_play_arrow_24)
            binding.seekbar.progress = 0
        }

        binding.backward.setOnClickListener {
            mediaPlayer!!.seekTo(mediaPlayer!!.currentPosition - 1000)
        }

        binding.further.setOnClickListener {
            mediaPlayer!!.seekTo(mediaPlayer!!.currentPosition + 1000)
        }
    }

    private fun checkAudioFocus(audioFocusRequest: Int) : Boolean {
        return audioFocusRequest == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }

}