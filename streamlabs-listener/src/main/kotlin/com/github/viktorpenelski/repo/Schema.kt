package com.github.viktorpenelski.repo

import com.github.viktorpenelski.initializeDotEnv
import com.github.viktorpenelski.initializeJdbcConfig

fun recreateSchema(config: JdbcConfig) {
    config.getConnection().use { conn ->
        conn.createStatement().use { statement ->
            statement.queryTimeout = 30 // set timeout to 30 sec.
            statement.executeUpdate("drop table if exists $donationsTableName")
            statement.executeUpdate(
                """create table $donationsTableName (
                    id integer GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY, 
                    _id VARCHAR NOT NULL UNIQUE, 
                    amount float NOT NULL, 
                    currency VARCHAR NOT NULL,
                    sender VARCHAR NOT NULL, 
                    message VARCHAR NOT NULL, 
                    tag VARCHAR,
                    date_created TIMESTAMP NOT NULL DEFAULT NOW()
            )
        """.trimIndent()
            )
        }
    }
}

fun main() {
    val config = initializeJdbcConfig(initializeDotEnv())
    println(
        """You are about to DROP ALL DATA and RECREATE schema 
        |[y] to continue, any key to abort.
        |""".trimMargin()
    )
    if (readLine() == "y") {
        recreateSchema(config)
        println("Schema recreated.")
    } else {
        println("Aborted.")
    }
}