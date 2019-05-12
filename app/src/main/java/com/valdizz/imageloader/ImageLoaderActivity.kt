package com.valdizz.imageloader

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_imageloader.*

/**
 * [ImageLoaderActivity] has a fragment that loads a big image.
 *
 * @author Vlad Kornev
 */
class ImageLoaderActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_imageloader)
        setSupportActionBar(toolbar)
        if (savedInstanceState == null) {
            createImageLoaderFragment()
        }
    }

    private fun createImageLoaderFragment() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, ImageLoaderFragment.newInstance())
            .commit()
    }
}
