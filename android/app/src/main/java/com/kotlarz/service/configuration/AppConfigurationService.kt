package com.kotlarz.service.configuration

import com.kotlarz.domain.AppConfigurationDomain
import io.reactivex.Single
import io.realm.Realm
import io.realm.kotlin.deleteFromRealm
import io.realm.kotlin.where

class AppConfigurationService {
    fun getConfiguration(): Single<AppConfigurationDomain> {
        return Single.create<AppConfigurationDomain> { emitter ->
            Realm.getDefaultInstance().use { realm ->
                emitter.onSuccess(getConfiguration(realm))
            }
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

    fun save(configuration: AppConfigurationDomain): Single<Any> {
        return Single.create { emitter ->
            Realm.getDefaultInstance().use { database ->
                database.executeTransaction { realm ->
                    save(configuration, realm)
                }
            }

            emitter.onSuccess(Any())
        }
    }

    private fun save(configuration: AppConfigurationDomain, realm: Realm) {
        realm.delete(AppConfigurationDomain::class.java)
        realm.insertOrUpdate(configuration)
    }

    fun isConfigured(): Single<Boolean> {
        return Single.create { emitter ->
            Realm.getDefaultInstance().use { realm ->
                emitter.onSuccess(isConfigured(realm))
            }
        }
    }

    private fun isConfigured(realm: Realm): Boolean {
        return realm.where(AppConfigurationDomain::class.java).count() > 0
    }
}