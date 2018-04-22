package com.kotlarz.domain

import io.realm.RealmObject
import io.realm.RealmResults
import io.realm.annotations.LinkingObjects
import io.realm.annotations.PrimaryKey
import java.util.*

open class TemperatureLogDomain : RealmObject() {
    @PrimaryKey
    var id: Long = 0

    var date: Date = Date(0)

    var value: Double = 0.0

    @LinkingObjects("temperatureLogs")
    val sensor: RealmResults<SensorDomain>? = null
}