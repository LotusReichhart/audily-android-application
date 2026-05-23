package com.lotusreichhart.audily.feature.search.api

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data class SearchNavKey(val type: SearchType = SearchType.ALL) : NavKey