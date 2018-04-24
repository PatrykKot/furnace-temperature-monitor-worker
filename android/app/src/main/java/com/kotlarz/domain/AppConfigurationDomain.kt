package com.kotlarz.domain

import io.realm.RealmModel
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import java.util.*

@RealmClass
open class AppConfigurationDomain : RealmModel {
    companion object {
        val DEFAULT = AppConfigurationDomain()

        init {
            DEFAULT.baseUrl = "http://localhost:8080"
            DEFAULT.uuid = ""
        }
    }

    @PrimaryKey
    var uuid: String = UUID.randomUUID().toString()

    var baseUrl: String = ""
}