package com.interactivestandard.challenge.repository

import org.springframework.http.MediaType
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono


@Repository
class FilesStorage(private val client: DatabaseClient) {

    fun saveFile(url: String, size: Long, contenType: MediaType, content: ByteArray): Mono<Any?> {
        return client.sql("INSERT INTO  files ( url , size , content_type , content) VALUES (:url , :size , :content_type , :content)")
            .filter { statement, executeFunction -> statement.returnGeneratedValues("id").execute() }
            .bind("url", url)
            .bind("size", size)
            .bind("content_type", contenType.toString())
            .bind("content", content)
            .fetch()
            .first()
            .map { r -> r.get("id") }
    }

    fun updateSummary() {
        client.sql("DELETE FROM summary").then().block()
        client.sql("INSERT INTO summary (files_count, files_size ) SELECT count(*), sum(size) from files")
            .then().block()
    }



}
