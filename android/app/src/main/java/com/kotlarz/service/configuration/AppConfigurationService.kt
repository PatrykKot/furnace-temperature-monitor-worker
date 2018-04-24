package com.kotlarz.service.configuration

import com.kotlarz.domain.AppConfigurationDomain
import io.reactivex.Single
import io.realm.Realm
import io.realm.kotlin.deleteFromRealm
import io.realm.kotlin.where

class AppConfigurationService {
    fun getConfiguration(): Single<AppConfigurationDomain> {
        return Single.create<AppConfigurationDomain> { emitter ->
            Realm.getDefaultInstance().use { database ->
                database.executeTransaction { realm ->
                    val allResults = realm.where<AppConfigurationDomain>()
                            .findAll()

                    if (allResults.isEmpty()) {
                        emitter.onSuccess(AppConfigurationDomain.DEFAULT)
                    } else {
                        val firstResult = allResults[0]

                        if (allResults.size > 1) {
                            val toRemoveList = allResults.subList(1, allResults.size - 1)
                            toRemoveList.forEach { toRemove -> toRemove.deleteFromRealm() }
                        }

                        emitter.onSuccess(firstResult!!)
                    }
                }
            }
        }
    }

    fun save(configurationDomain: AppConfigurationDomain) {
        Realm.getDefaultInstance().use { database ->
            database.executeTransaction { realm ->
                realm.delete(AppConfigurationDomain::class.java)
                realm.insert(configurationDomain)
            }
        }
    }
}