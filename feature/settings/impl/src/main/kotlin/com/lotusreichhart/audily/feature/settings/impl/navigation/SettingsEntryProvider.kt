package com.lotusreichhart.audily.feature.settings.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.lotusreichhart.audily.core.navigation.Navigator
import com.lotusreichhart.audily.feature.settings.api.navigation.AboutNavKey
import com.lotusreichhart.audily.feature.settings.api.navigation.AudioPlaybackNavKey
import com.lotusreichhart.audily.feature.settings.api.navigation.HelpNavKey
import com.lotusreichhart.audily.feature.settings.api.navigation.LanguageNavKey
import com.lotusreichhart.audily.feature.settings.api.navigation.LibraryStorageNavKey
import com.lotusreichhart.audily.feature.settings.api.navigation.LyricsNetworkNavKey
import com.lotusreichhart.audily.feature.settings.api.navigation.PersonalizationNavKey
import com.lotusreichhart.audily.feature.settings.api.navigation.SettingsNavKey
import com.lotusreichhart.audily.feature.settings.impl.SettingsScreen
import com.lotusreichhart.audily.feature.settings.impl.about.AboutScreen
import com.lotusreichhart.audily.feature.settings.impl.audioplayback.AudioPlaybackScreen
import com.lotusreichhart.audily.feature.settings.impl.help.HelpScreen
import com.lotusreichhart.audily.feature.settings.impl.language.LanguageScreen
import com.lotusreichhart.audily.feature.settings.impl.librarystoreage.LibraryStorageScreen
import com.lotusreichhart.audily.feature.settings.impl.lyricsnetwork.LyricsNetworkScreen
import com.lotusreichhart.audily.feature.settings.impl.personalization.PersonalizationScreen

fun EntryProviderScope<NavKey>.settingsEntry(
    navigator: Navigator
) {
    entry<SettingsNavKey> {
        SettingsScreen(
            onPersonalization = { navigator.navigate(PersonalizationNavKey) },
            onAudio = { navigator.navigate(AudioPlaybackNavKey) },
            onLibrary = { navigator.navigate(LibraryStorageNavKey) },
            onLanguage = { navigator.navigate(LanguageNavKey) },
            onLyricsNetwork = { navigator.navigate(LyricsNetworkNavKey) },
            onHelp = { navigator.navigate(HelpNavKey) },
            onAbout = { navigator.navigate(AboutNavKey) }
        )
    }

    entry<PersonalizationNavKey> {
        PersonalizationScreen(
            onBack = { navigator.goBack() }
        )
    }

    entry<AudioPlaybackNavKey> {
        AudioPlaybackScreen(
            onBack = { navigator.goBack() }
        )
    }

    entry<LibraryStorageNavKey> {
        LibraryStorageScreen(
            onBack = { navigator.goBack() }
        )
    }

    entry<LanguageNavKey> {
        LanguageScreen(
            onBack = { navigator.goBack() }
        )
    }

    entry<LyricsNetworkNavKey> {
        LyricsNetworkScreen(
            onBack = { navigator.goBack() }
        )
    }

    entry<HelpNavKey> {
        HelpScreen(
            onBack = { navigator.goBack() }
        )
    }

    entry<AboutNavKey> {
        AboutScreen(
            onBack = { navigator.goBack() }
        )
    }
}