package com.kotlarz.service.database

import java.io.File

object Database {
    val instance = org.dizitart.kno2.nitrite {
        compress = true
        file = File("db/database.db")
    }
}
