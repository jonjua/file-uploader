package com.interactivestandard.challenge.service

import com.interactivestandard.challenge.repository.FilesStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import org.springframework.web.reactive.function.client.toEntity
import org.springframework.web.servlet.function.ServerResponse
import java.time.Duration
import java.util.*
import kotlin.random.Random
import kotlin.system.exitProcess

@Service
class ImageHandlerService(private val filesStorage: FilesStorage, private val client: WebClient) : CommandLineRunner {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Value("\${app.file.download.url}")
    private val baseUrl = ""

    private var number: Int? = null
    fun initRequestNumberConf() {
        val input = Scanner(System.`in`)
        var number: Int? = null
        while (number == null) {
            print("Введите количество загружаемых картинок (целое число): ")
            try {
                number = input.nextInt()
                if (number < 0) {
                    number = null
                    println("Ошибка: введите целое положительное число ")
                }
            } catch (e: InputMismatchException) {
                println("Ошибка: введите целое число")
                input.nextLine()
            }
        }
        logger.info("Requested {} images", number)
        runBlocking(Dispatchers.Default) {

            val jobs = List(number) {
                async {

                    val uri = "/" + Random.nextInt(5001) + "/" + Random.nextInt(5001)
                    var clientResponse:ClientResponse
                    val redirect = client
                        .get()
                        .uri(uri)
                        .accept(MediaType.ALL).exchangeToMono { response ->
                           clientResponse =response
                            response. toEntity<String>()
                        }
                        .block()

                    val location = redirect?.headers?.location
                    val image = location?.let { it1 ->
                        client.get()
                            .uri(it1.path)
                            .accept(MediaType.ALL).retrieve().toEntity<ByteArray>().block()
                    }

                    val contentType = image?.headers?.contentType
                    if (contentType != null && image != null && image.body != null) {
                        val uuid =
                            filesStorage.saveFile(
                                baseUrl + "/" + location.path,
                                image.body!!.size?.toLong() ?: 0,
                                contentType,
                                image.body!!
                            )
                        logger.info(
                            "Request uri $baseUrl{}  redirect to $baseUrl{}, received image  save with id {}",
                            uri,
                            location,
                            uuid.block()
                        )
                    }


                }
            }
            jobs.awaitAll()

        }
        exitProcess(0);
    }

    override fun run(vararg args: String?) {
        initRequestNumberConf()
    }
}
