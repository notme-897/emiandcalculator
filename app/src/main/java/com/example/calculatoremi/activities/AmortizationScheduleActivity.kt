package com.example.calculatoremi.activities

import android.os.Build
import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.calculatoremi.R
import com.example.calculatoremi.adapter.AmortizationAdapter
import com.example.calculatoremi.model.PaymentScheduleItem
import java.io.Serializable

class AmortizationScheduleActivity : BaseResultActivity() {

    private var scheduleList: ArrayList<PaymentScheduleItem> = arrayListOf()

    override fun getResultLayoutResId(): Int = R.layout.activity_amortization_schedule

    override fun getResultTitle(): String = "Payment Schedule"

    override fun getShareText(): String {
        return "Payment Schedule - ${scheduleList.size} Installments calculated via Finance Hub"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)

        val schedule = getSerializableExtraCompat<ArrayList<PaymentScheduleItem>>("SCHEDULE")
        if (schedule != null) {
            scheduleList = schedule
        }

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerSchedule)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = AmortizationAdapter(scheduleList)

        // Spring entrance layout animation for schedule items
        val resId = R.anim.layout_animation_spring
        val animation = AnimationUtils.loadLayoutAnimation(this, resId)
        recyclerView.layoutAnimation = animation
        recyclerView.scheduleLayoutAnimation()
    }

    private inline fun <reified T : Serializable> getSerializableExtraCompat(key: String): T? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra(key, T::class.java)
        } else {
            @Suppress("DEPRECATION", "UNCHECKED_CAST")
            intent.getSerializableExtra(key) as? T
        }
    }
}

