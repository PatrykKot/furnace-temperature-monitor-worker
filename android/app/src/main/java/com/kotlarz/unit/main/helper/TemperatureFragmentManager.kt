package com.kotlarz.unit.main.helper

import android.content.res.Resources
import android.support.v4.app.FragmentTransaction
import com.google.android.flexbox.FlexboxLayout
import com.kotlarz.R
import com.kotlarz.unit.main.activity.MainActivity
import com.kotlarz.unit.main.fragment.TemperatureDisplayFragment
import com.kotlarz.unit.main.service.logs.dto.NewTemperatureLog
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.*
import java.util.stream.Collectors

class TemperatureFragmentManager(val context: MainActivity) {
    private var fragmentsMap = HashMap<String, TemperatureDisplayFragment>()

    fun register(logs: List<NewTemperatureLog>) {
        val fragmentManager = context.supportFragmentManager
        val transaction = fragmentManager.beginTransaction()

        updateFragmentsInActivity(transaction, logs)
        logs.forEach { updateFragment(fragmentsMap[it.address]!!, it) }

        transaction.commitAllowingStateLoss()
    }

    private fun updateFragmentsInActivity(transaction: FragmentTransaction, logs: List<NewTemperatureLog>) {
        val newFragments = mutableListOf<TemperatureDisplayFragment>()
        logs.forEach { log ->
            addFragmentIfNotExist(log.address, transaction)
                    .ifPresent { newFragments.add(it) }
        }
        removeOldFragments(logs.stream()
                .map { it.address }
                .collect(Collectors.toList()), transaction)

        newFragments.forEach { initLayout(it) }
    }

    private fun removeOldFragments(adresses: List<String>, transaction: FragmentTransaction) {
        val toRemove = fragmentsMap.keys.stream()
                .filter { !adresses.contains(it) }
                .collect(Collectors.toList())

        toRemove.forEach { transaction.remove(fragmentsMap[it]) }
    }

    private fun addFragmentIfNotExist(address: String, transaction: FragmentTransaction): Optional<TemperatureDisplayFragment> {
        return if (!fragmentsMap.containsKey(address)) {
            val fragment = TemperatureDisplayFragment()
            fragmentsMap[address] = fragment

            transaction.add(R.id.temperatureDisplayContainer, fragment)
            Optional.of(fragment)
        } else {
            Optional.empty()
        }
    }

    private fun initLayout(fragment: TemperatureDisplayFragment) {
        val layoutParams = FlexboxLayout.LayoutParams(toPx(150), toPx(100))
        layoutParams.setMargins(0, toPx(10), 0, toPx(10))

        fragment.createdViewSubject
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe { it.layoutParams = layoutParams }
    }

    private fun updateFragment(fragment: TemperatureDisplayFragment, log: NewTemperatureLog) {
        fragment.setTemperature(log.value)
    }

    private fun toPx(dp: Int): Int {
        return (dp * Resources.getSystem().displayMetrics.density).toInt()
    }
}