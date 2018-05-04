package com.kotlarz.unit.configuration.domain

import io.realm.RealmList
import io.realm.RealmModel
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass

@RealmClass
open class SensorDomain : RealmModel {
    @PrimaryKey
    var uuid: Long = 0

    var address: String = ""

    var temperatureLogs: RealmList<TemperatureLogDomain>? = null
}