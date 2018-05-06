package com.kotlarz.unit.main.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kotlarz.R
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.fragment_temperature_display.*

class TemperatureDisplayFragment : Fragment() {

    val createdViewSubject: PublishSubject<View> = PublishSubject.create()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_temperature_display, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        createdViewSubject.onNext(view)
        createdViewSubject.onComplete()
    }

    fun setTemperature(value: Double) {
        val resolvedTemperature = resolveTemperature(value)

        if (this.createdViewSubject.hasComplete()) {
            this.temperatureDisplayTextView.text = resolvedTemperature
        } else {
            createdViewSubject
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .subscribe { this.temperatureDisplayTextView.text = resolvedTemperature }
        }

    }

    private fun resolveTemperature(value: Double): String {
        return String.format("%.2f", value);
    }
}
