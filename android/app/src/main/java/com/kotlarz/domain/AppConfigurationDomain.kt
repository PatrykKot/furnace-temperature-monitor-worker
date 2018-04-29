package com.kotlarz.domain

import com.kotlarz.domain.enumeration.ProtocolType
import io.realm.RealmModel
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import java.util.*

@RealmClass
open class AppConfigurationDomain : RealmModel {
    companion object {
        val DEFAULT = AppConfigurationDomain()

        init {
            DEFAULT.ipAddress = "0.0.0.0"
            DEFAULT.port = 0
            DEFAULT.protocol = ProtocolType.HTTP.name
            DEFAULT.uuid = ""
        }
    }

    @PrimaryKey
    var uuid: String = UUID.randomUUID().toString()

    var protocol: String = ProtocolType.HTTP.name

    var ipAddress: String = ""

    var port: Long = 0

    fun getProcotolType(): ProtocolType {
        return ProtocolType.valueOf(protocol)
    }
}