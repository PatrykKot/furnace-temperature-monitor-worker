package com.kotlarz.unit.configuration.service

import com.kotlarz.unit.configuration.domain.AppConfigurationDomain
import io.reactivex.Observable
import io.realm.Realm
import io.realm.kotlin.deleteFromRealm
import io.realm.kotlin.where
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Url
import java.util.concurrent.atomic.AtomicBoolean

class AppConfigurationService(private val retrofit: Retrofit) {
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

    fun checkConnection(configuration: AppConfigurationDomain): Boolean {
        val success = AtomicBoolean()
        val checkConnectionApi = retrofit.create(CheckConnectionApi::class.java)
        val url = configuration.url
        checkConnectionApi.checkConnection(url)
                .subscribe({ response ->
                    success.set(response.code() > 0)
                }, {
                    success.set(false)
                }, {})
        return success.get()
    }

    private interface CheckConnectionApi {
        @GET
        fun checkConnection(@Url url: String): Observable<Response<Void>>
    }
}