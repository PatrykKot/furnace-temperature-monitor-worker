package com.kotlarz.domain

import io.realm.RealmModel
import io.realm.RealmResults
import io.realm.annotations.LinkingObjects
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import java.util.*

@RealmClass
open class TemperatureLogDomain : RealmModel {
    @PrimaryKey
    var id: Long = 0

    var date: Date = Date(0)

    var value: Double = 0.0

    @LinkingObjects("temperatureLogs")
    val sensor: RealmResults<SensorDomain>? = null
}