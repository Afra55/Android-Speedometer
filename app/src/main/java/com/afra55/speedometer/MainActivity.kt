package com.afra55.speedometer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val speedometerAdapter = SpeedometerAdapter(this)
        speedometer_vp.adapter = speedometerAdapter
        speedometer_vp.offscreenPageLimit = speedometerAdapter.itemCount
        speedometer_vp_indicator.attach(speedometer_vp)
    }
}


class SpeedometerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    val itemList by lazy {
        val list = mutableListOf<Int>()
        list.add(R.layout.view_speedometer_1)
        list.add(R.layout.view_speedometer_2)
        list.add(R.layout.view_speedometer_3)
        list
    }
    override fun getItemCount(): Int {
        return itemList.size
    }
    override fun createFragment(position: Int): Fragment {
        return SpeedometerDialogFragment(itemList[position])
    }
}