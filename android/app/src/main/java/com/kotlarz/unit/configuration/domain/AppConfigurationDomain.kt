package com.kotlarz.unit.configuration.domain

import com.kotlarz.unit.configuration.domain.enumeration.ProtocolType
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

    val protocolType: ProtocolType
        get() {
            return ProtocolType.valueOf(protocol)
        }

    val url: String
        get() {
            return """${protocol.toLowerCase()}://$ipAddress:$port"""
        }

    val webSocketUrl: String
        get() {
            return "ws://$ipAddress:$port/websocket"
        }
}