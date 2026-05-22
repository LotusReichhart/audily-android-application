package com.lotusreichhart.audily.core.data.util

import com.lotusreichhart.audily.core.domain.util.NetworkMonitor
import com.lotusreichhart.audily.core.network.util.ConnectivityManagerNetworkMonitor
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class NetworkMonitorImpl @Inject constructor(
    connectivityManagerNetworkMonitor: ConnectivityManagerNetworkMonitor
) : NetworkMonitor {
    override val isOnline: Flow<Boolean> = connectivityManagerNetworkMonitor.isOnline
}