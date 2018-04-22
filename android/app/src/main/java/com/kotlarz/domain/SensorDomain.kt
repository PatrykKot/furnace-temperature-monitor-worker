package com.kotlarz.domain

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class SensorDomain : RealmObject() {
    @PrimaryKey
    var id: Long = 0

    var address: String = ""

    var temperatureLogs: RealmList<TemperatureLogDomain>? = null
}