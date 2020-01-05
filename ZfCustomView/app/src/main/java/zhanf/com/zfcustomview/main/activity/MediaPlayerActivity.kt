package zhanf.com.zfcustomview.main.activity

import android.graphics.SurfaceTexture
import android.os.Bundle
import android.text.TextUtils
import android.util.DisplayMetrics
import android.view.Surface
import android.view.TextureView
import android.widget.FrameLayout
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_media_player.*
import zhanf.com.zfcustomview.R
import zhanf.com.zfcustomview.mediamanager.mediaplayer.MediaPlayerController

/**
 * @author sincerity
 */
class MediaPlayerActivity : AppCompatActivity() {

    private var surfaceView: Surface? = null
    private var mediaPlayerController: MediaPlayerController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media_player)
        initTextureSize()
        initListener()
    }

    private fun initTextureSize() {
        val layoutParams = tvMediaPlayer.layoutParams as FrameLayout.LayoutParams
        val outMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(outMetrics)
        val width = outMetrics.widthPixels
        layoutParams.width = width
        layoutParams.height = width * 9 / 16
        tvMediaPlayer.layoutParams = layoutParams
    }

    private fun initListener() {
        stvStart.setOnClickListener {
            stvStart!!.text = if (TextUtils.equals("暂停", stvStart!!.text.toString().trim { it <= ' ' })) "播放" else "暂停"
            //手动暂停/播放状态调用此方法
            mediaPlayerController!!.start()
            mediaPlayerController!!.play()
        }

        stvNext.setOnClickListener {
            mediaPlayerController!!.next("")
        }

        sbProgress!!.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {}

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                //手指触摸seekBar时停止自己更新seekBar进度条
                mediaPlayerController!!.removeCallbacksAndMessages()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                //将视频跳转到进度条进度处
                mediaPlayerController!!.seekTo(seekBar.progress)
                //手指停止触摸seekBar时停止更新seekBar进度条
                mediaPlayerController!!.sendPlayMessage()
            }
        })

        tvMediaPlayer.surfaceTextureListener = object : TextureView.SurfaceTextureListener {

            override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
                surfaceView = Surface(surface)
                if (null == mediaPlayerController) {
                    mediaPlayerController = MediaPlayerController("")//FIXME 添加url
                    //设置MediaPlayer Prepared成功的回调，得到视频时长设置给SeekBar设置最大进度
                    mediaPlayerController!!.setOnPreparedListener { duration -> sbProgress!!.max = duration }
                    //得到MediaPlayer当前播放进度回调并用于更新SeekBar进度
                    mediaPlayerController!!.setCurrentPositionCallback { currentPosition ->
                        println("currentPosition_MediaPlayerActivity:$currentPosition")
                        sbProgress!!.progress = currentPosition
                    }
                }
                //前台状态，开始播放
                mediaPlayerController!!.playerForeground()
                mediaPlayerController!!.setSurface(surfaceView)
                mediaPlayerController!!.autoPlay()
            }

            override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {}

            override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
                //后台状态，暂停播放
                mediaPlayerController!!.playerBackground()
                mediaPlayerController!!.pause()
                return false
            }

            override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {}
        }
    }

    override fun onDestroy() {
        //销毁MediaPlayer释放资源
        mediaPlayerController!!.destroy()
        mediaPlayerController = null
        super.onDestroy()
    }
}
