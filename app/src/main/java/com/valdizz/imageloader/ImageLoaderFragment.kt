package com.valdizz.imageloader

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Bitmap
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_imageloader.*

/**
 * [ImageLoaderFragment] contains [Button] that loads big image into [ImageView].
 * Download uses a foreground service with the progress bar.
 *
 * @author Vlad Kornev
 */
class ImageLoaderFragment : Fragment() {

    private var isBound = false
    private var imageLoaderService: ImageLoaderService? = null
    private var intentService: Intent? = null
    private var bitmap: Bitmap? = null
    private var imageView: ImageView? = null

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            imageLoaderService = (service as ImageLoaderService.LocalBinder).getService()
            isBound = true
        }

        override fun onServiceDisconnected(className: ComponentName) {
            isBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        intentService = Intent(activity, ImageLoaderService::class.java)
        activity?.startService(intentService)
    }

    override fun onStart() {
        super.onStart()
        activity?.bindService(intentService, connection, Context.BIND_AUTO_CREATE)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_imageloader, container, false)
        imageView = view.findViewById(R.id.iv)
        if (bitmap != null) {
            imageView?.setImageBitmap(bitmap)
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        fab.setOnClickListener { _ ->
            loadImage()
        }
    }

    override fun onStop() {
        super.onStop()
        activity?.unbindService(connection)
    }

    override fun onDestroy() {
        super.onDestroy()
        activity?.stopService(intentService)
    }

    private fun loadImage() {
        Thread{
            try {
                bitmap = imageLoaderService?.loadImage(IMAGE_URL)
                imageView?.post {
                    imageView?.setImageBitmap(bitmap)
                }
            }
            catch (e: Throwable) {
                Snackbar.make(imageView as View, "Error: ${e.localizedMessage}", Snackbar.LENGTH_LONG).show()
            }
            finally {
                imageLoaderService?.stopProgress()
            }
        }.start()
    }

    companion object {
        private const val IMAGE_URL = "https://img3.goodfon.com/original/6360x4240/8/c9/kanada-vankuver-noch-zdaniya.jpg"
        fun newInstance() = ImageLoaderFragment()
    }
}