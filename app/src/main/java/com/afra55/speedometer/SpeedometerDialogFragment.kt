package com.afra55.speedometer

import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.util.toRange
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_speedometer_dialog.*

/**
 * @author Afra55
 * @date 2020/8/28
 * A smile is the best business card.
 * 没有成绩，连呼吸都是错的。
 */
class SpeedometerDialogFragment(val speedometerDialogResId: Int) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_speedometer_dialog, container, false)
    }

    val testHandler:Handler by lazy {
        Handler(Handler.Callback {
            if (isResume) {
                mySpeedometerDialog?.setCurrentNumber((0..180).random().toFloat())
            }
            testHandler.sendEmptyMessageDelayed(0, 500)
            false

        })
    }


    var mySpeedometerDialog: SpeedometerDialog? = null
    var isResume = false
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val view = LayoutInflater.from(context).inflate(speedometerDialogResId, null, false)
        val test_speedometer: View? = view.findViewById<View>(R.id.test_speedometer)
        if (test_speedometer is SpeedometerDialog) {
            mySpeedometerDialog = test_speedometer
            item_root.addView(view, 0)
            mySpeedometerDialog!!.setLimitNumber(120)
            mySpeedometerDialog!!.setMaxNumber(180F)

            testHandler.sendEmptyMessage(0)
        }

    }

    override fun onResume() {
        super.onResume()
        isResume = true
    }

    override fun onPause() {
        super.onPause()
        isResume = false
    }

    override fun onDetach() {
        testHandler.removeMessages(0)
        super.onDetach()
    }
}

