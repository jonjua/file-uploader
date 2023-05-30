package com.interactivestandard.challenge.service

import com.interactivestandard.challenge.repository.FilesStorage
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service

class SchedulerService(private val filesStorage: FilesStorage) {
    @Scheduled(fixedDelay = 10000)
    fun updateSummary () {
        filesStorage.updateSummary()
    }

}