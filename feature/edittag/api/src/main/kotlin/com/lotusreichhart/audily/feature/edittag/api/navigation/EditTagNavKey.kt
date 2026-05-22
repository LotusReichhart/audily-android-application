package com.lotusreichhart.audily.feature.edittag.api.navigation

import com.lotusreichhart.audily.core.navigation.SingleInstanceKey
import kotlinx.serialization.Serializable

@Serializable
data class EditTagNavKey(val songId: Long) : SingleInstanceKey
