package com.jp.jumpeak.data.dto

import androidx.room.Embedded
import com.jp.jumpeak.data.entity.Parties

data class PartiesDTO(
    @Embedded
    val parties: Parties,
    val balance: Double
)