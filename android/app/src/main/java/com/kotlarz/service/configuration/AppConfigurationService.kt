package com.kotlarz.service.configuration

import android.util.Log
import com.kotlarz.domain.AppConfigurationDomain
import io.realm.Realm
import io.realm.kotlin.deleteFromRealm
import io.realm.kotlin.where
import java.net.InetAddress
import java.net.Socket

class AppConfigurationService {
    fun getConfiguration(): AppConfigurationDomain {
        Realm.getDefaultInstance().use { realm ->
            return getConfiguration(realm)
        }
    }

    private fun getConfiguration(realm: Realm): AppConfigurationDomain {
        val allResults = realm.copyFromRealm(realm.where<AppConfigurationDomain>()
                .findAll())

        return if (allResults.isEmpty()) {
            AppConfigurationDomain.DEFAULT
        } else {
            val firstResult = allResults[0]

            if (allResults.size > 1) {
                val toRemoveList = allResults.subList(1, allResults.size - 1)
                toRemoveList.forEach { toRemove -> toRemove.deleteFromRealm() }
            }

            firstResult!!
        }
    }

    fun save(configuration: AppConfigurationDomain) {
        Realm.getDefaultInstance().use { database ->
            database.executeTransaction { realm ->
                save(configuration, realm)
            }
        }
    }

    private fun save(configuration: AppConfigurationDomain, realm: Realm) {
        realm.delete(AppConfigurationDomain::class.java)
        realm.insertOrUpdate(configuration)
    }

    fun isConfigured(): Boolean {
        Realm.getDefaultInstance().use { realm ->
            return isConfigured(realm)
        }
    }

    private fun isConfigured(realm: Realm): Boolean {
        return realm.where(AppConfigurationDomain::class.java).count() > 0
    }

    private fun checkConnection(): Boolean {
        return try {
            val configuration = getConfiguration()
            val address = InetAddress.getByName(configuration.ipAddress)
            val socket = Socket(address, configuration.port.toInt())
            socket.close()
            true
        } catch (ex: Exception) {
            Log.e(this.javaClass.name, ex.message, ex)
            false
        }

    }
}