package com.alfanthariq.skeleton.utils

import android.animation.Animator
import android.animation.ValueAnimator
import android.view.animation.AccelerateInterpolator
import androidx.viewpager.widget.ViewPager
import java.security.MessageDigest

object UserInterfaceUtil {
    fun animatePagerTransition(forward: Boolean, pager: ViewPager) {
        val animator = ValueAnimator.ofInt(0, pager.width - if (forward) pager.paddingLeft else pager.paddingRight)
        animator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}

            override fun onAnimationEnd(animation: Animator) {
                pager.endFakeDrag()
            }

            override fun onAnimationCancel(animation: Animator) {
                pager.endFakeDrag()
            }

            override fun onAnimationRepeat(animation: Animator) {}
        })

        animator.interpolator = AccelerateInterpolator()
        animator.addUpdateListener(object : ValueAnimator.AnimatorUpdateListener {

            private var oldDragPosition = 0

            override fun onAnimationUpdate(animation: ValueAnimator) {
                val dragPosition = animation.animatedValue as Int
                val dragOffset = dragPosition - oldDragPosition
                oldDragPosition = dragPosition
                pager.fakeDragBy((dragOffset * if (forward) -1 else 1).toFloat())
            }
        })

        animator.duration = 250
        pager.beginFakeDrag()
        animator.start()
    }

    fun md5(str : String): String {
        val md = MessageDigest.getInstance("MD5")
        val digested = md.digest(str.toByteArray())
        return digested.joinToString("") {
            String.format("%02x", it)
        }
    }
}