package online.motohub.util

import android.content.Context
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.LoadControl
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelection
import com.google.android.exoplayer2.trackselection.TrackSelector
import com.google.android.exoplayer2.upstream.BandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultAllocator
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter

class ExoPlayerUtils private constructor(val context: Context) {

    val exoPlayer: SimpleExoPlayer
        get() {
            val bandwidthMeter: BandwidthMeter = DefaultBandwidthMeter()
            val trackSelectionFactory: TrackSelection.Factory = AdaptiveTrackSelection.Factory(bandwidthMeter)
            val trackSelector: TrackSelector = DefaultTrackSelector(trackSelectionFactory)
            val defaultAllocator = DefaultAllocator(true, 16 * 1024)
            val loadControl: LoadControl = DefaultLoadControl.Builder().setAllocator(defaultAllocator)
                    .setBufferDurationsMs(25000, 30000, 2500, 5000)
                    .setPrioritizeTimeOverSizeThresholds(true)
                    .setTargetBufferBytes(2500)
                    .setBackBuffer(2500, true).createDefaultLoadControl()
            return ExoPlayerFactory.newSimpleInstance(context, trackSelector, loadControl)
        }

    companion object {
        @JvmStatic
        var instance: ExoPlayerUtils? = null
            private set

        fun init(context: Context) {
            var context = context
            if (instance == null) {
                context = context.applicationContext
                instance = ExoPlayerUtils(context)
            }
        }

    }

}