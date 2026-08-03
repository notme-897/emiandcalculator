package com.example.calculatoremi.utils

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.View
import android.view.animation.AnimationUtils
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.TextView
import com.example.calculatoremi.R
import java.text.DecimalFormat

object AnimationUtilsHelper {

    private val currencyFormat = DecimalFormat("#,##,###.##")

    /**
     * Shakes view horizontally for invalid inputs / error feedback.
     */
    fun shakeView(context: Context, view: View) {
        val shakeAnim = AnimationUtils.loadAnimation(context, R.anim.shake)
        view.startAnimation(shakeAnim)
        view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
    }

    /**
     * Pulses button on click with scale up and spring back.
     */
    fun pulseView(view: View) {
        view.animate()
            .scaleX(1.06f)
            .scaleY(1.06f)
            .setDuration(120)
            .withEndAction {
                view.animate()
                    .scaleX(1.0f)
                    .scaleY(1.0f)
                    .setInterpolator(OvershootInterpolator(2.5f))
                    .setDuration(180)
                    .start()
            }
            .start()
    }

    /**
     * Starts continuous soft breathing pulse on CTA button.
     */
    fun startBreathingAnimation(context: Context, view: View) {
        val anim = AnimationUtils.loadAnimation(context, R.anim.breathing_pulse)
        view.startAnimation(anim)
    }

    /**
     * Starts gentle floating up and down animation on header illustration.
     */
    fun startFloatingAnimation(context: Context, view: View) {
        val anim = AnimationUtils.loadAnimation(context, R.anim.float_up_down)
        view.startAnimation(anim)
    }

    /**
     * Performs a 3D card flip transition between front and back views.
     */
    fun flipCard(context: Context, frontView: View, backView: View) {
        val scale = context.resources.displayMetrics.density * 8000
        frontView.cameraDistance = scale
        backView.cameraDistance = scale

        val flipOut = AnimatorInflater.loadAnimator(context, R.animator.card_flip_left_out) as AnimatorSet
        val flipIn = AnimatorInflater.loadAnimator(context, R.animator.card_flip_left_in) as AnimatorSet

        flipOut.setTarget(frontView)
        flipIn.setTarget(backView)

        flipOut.start()
        flipIn.start()
    }

    /**
     * Attaches tactile spring touch listener to any View (Card, Button, Chip).
     */
    fun attachSpringTouchFeedback(view: View) {
        view.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    v.animate()
                        .scaleX(0.96f)
                        .scaleY(0.96f)
                        .translationZ(8f)
                        .setDuration(100)
                        .start()
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    v.animate()
                        .scaleX(1.0f)
                        .scaleY(1.0f)
                        .translationZ(0f)
                        .setInterpolator(OvershootInterpolator(2.0f))
                        .setDuration(200)
                        .start()
                }
            }
            false
        }
    }

    /**
     * Smoothly interpolates text numeric value counter.
     */
    fun countNumber(textView: TextView, targetValue: Double, prefix: String? = null) {
        val symbol = prefix ?: CurrencyManager.getCurrencySymbol(textView.context)
        val animator = ValueAnimator.ofFloat(0f, targetValue.toFloat())
        animator.duration = 550
        animator.interpolator = DecelerateInterpolator()
        animator.addUpdateListener { anim ->
            val value = (anim.animatedValue as Float).toDouble()
            textView.text = symbol + currencyFormat.format(value)
        }
        animator.start()
    }

    /**
     * Applies subtle 3D tilt effect on scroll.
     */
    fun apply3DTiltOnScroll(view: View, scrollY: Int) {
        val rotation = (scrollY * 0.02f).coerceIn(-6f, 6f)
        view.rotationX = rotation
    }
}
