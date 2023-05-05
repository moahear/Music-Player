package com.gamil.moahear.musicplayer

import android.media.AudioManager
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.format.DateUtils
import com.gamil.moahear.musicplayer.databinding.ActivityMainBinding
import com.google.android.material.slider.Slider
import com.google.android.material.slider.Slider.OnSliderTouchListener
import java.util.Timer
import java.util.TimerTask

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var isUserChangeSlider = false
    private lateinit var timer: Timer
    private  var currentVolume: Int=0
    private val mediaPlayer by lazy {
        MediaPlayer.create(this, R.raw.music_3)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //initial timer
        timer = Timer()
        binding.apply {
            val audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
            //get current volume
            currentVolume=audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
            //region volume mute and unmute
            imgVolumeOnOff.setOnClickListener {
                if (audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) == 0) {
                    audioManager.setStreamVolume(
                        AudioManager.STREAM_MUSIC,
                        currentVolume,
                        AudioManager.FLAG_SHOW_UI
                    )
                    imgVolumeOnOff.setImageResource(R.drawable.ic_volume_on)
                } else {
                    currentVolume=audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
                    audioManager.setStreamVolume(
                        AudioManager.STREAM_MUSIC,
                        0,
                        AudioManager.FLAG_SHOW_UI
                    )
                    imgVolumeOnOff.setImageResource(R.drawable.ic_volume_off)
                }

                /*  if (audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) == 0) {
                      audioManager.adjustVolume(AudioManager.ADJUST_UNMUTE, AudioManager.FLAG_SHOW_UI)
                      imgVolumeOnOff.setImageResource(R.drawable.ic_volume_on)

                  } else {
                      audioManager.adjustVolume(AudioManager.ADJUST_MUTE, AudioManager.FLAG_SHOW_UI)
                      imgVolumeOnOff.setImageResource(R.drawable.ic_volume_off)
                  }*/
            }
            //endregion
            //region forward music 15 seconds
            imgGoAfter.setOnClickListener {
                if (mediaPlayer.currentPosition < mediaPlayer.duration - 16000) {
                    mediaPlayer.seekTo(mediaPlayer.currentPosition + 15000)
                }
            }
            //endregion
            //region bring back music 15 seconds
            imgGoBefore.setOnClickListener {
                if (mediaPlayer.currentPosition >= 15000) {
                    mediaPlayer.seekTo(mediaPlayer.currentPosition - 15000)
                }
            }
            //endregion
            //region play and pause music
            imgPlayPause.setOnClickListener {
                if (mediaPlayer.isPlaying) {
                    mediaPlayer.pause()
                    imgPlayPause.setImageResource(R.drawable.ic_play)
                } else {
                    mediaPlayer.start()
                    imgPlayPause.setImageResource(R.drawable.ic_pause)
                }
            }
            //endregion

            //region set max value for slider
            sliderMusicControll.valueTo = mediaPlayer.duration.toFloat()
            //endregion

            //region timer to run every second and set music duration to value of slider
            timer.schedule(object : TimerTask() {
                override fun run() {
                    runOnUiThread {
                        if (!isUserChangeSlider && mediaPlayer.currentPosition.toFloat() <= 219220) {
                            sliderMusicControll.value = mediaPlayer.currentPosition.toFloat()
                        }
                    }
                }
            }, 1000, 1000)
            //endregion

            //region show duration of music
            txtFullDuration.text = DateUtils.formatElapsedTime(mediaPlayer.duration / 1000.toLong())
            //endregion

            //region setup slider listener
            sliderMusicControll.apply {
                addOnChangeListener { slider, value, fromUser ->
                    txtTimeLeft.text =
                        DateUtils.formatElapsedTime((value / 1000).toLong()/*mediaPlayer.currentPosition/1000.toLong()*/)
                    isUserChangeSlider = fromUser
                }
                addOnSliderTouchListener(object : OnSliderTouchListener {
                    override fun onStartTrackingTouch(slider: Slider) {

                    }
                    override fun onStopTrackingTouch(slider: Slider) {
                        mediaPlayer.seekTo(slider.value.toInt())
                        sliderMusicControll.value = mediaPlayer.currentPosition.toFloat()
                    }
                })
            }
            //endregion

            //region on finished music
            mediaPlayer.setOnCompletionListener {
                imgPlayPause.setImageResource(R.drawable.ic_play)
                sliderMusicControll.value = 0.0f
            }
            //endregion
        }

    }
}