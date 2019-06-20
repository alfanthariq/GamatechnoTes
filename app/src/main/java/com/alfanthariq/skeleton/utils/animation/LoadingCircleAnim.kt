package alfanthariq.com.signatureapp.utils.animation

import android.view.View
import android.view.animation.Animation
import android.view.animation.Transformation

class LoadingCircleAnim (view : View , width : Int) : Animation() {
    private var mStartWidth = view.width
    private val mWidth = width
    private val mView = view

    override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
        val newWidth = mStartWidth + ((mWidth - mStartWidth) * interpolatedTime)

        mView.layoutParams.width = newWidth.toInt()
        mView.background = mView.background
        mView.requestLayout()
    }

    override fun willChangeBounds(): Boolean {
        return true
    }
}