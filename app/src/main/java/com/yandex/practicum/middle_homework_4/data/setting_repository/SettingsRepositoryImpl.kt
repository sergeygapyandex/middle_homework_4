package com.yandex.practicum.middle_homework_4.data.setting_repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import com.yandex.practicum.middle_homework_4.ui.contract.SettingsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SettingsRepositoryImpl(
    private val dataStore: DataStore<Preferences>,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : SettingsRepository {
    private val refreshPeriodKey = longPreferencesKey("REFRESH_PERIOD")
    private val firstLaunchDelayKey = longPreferencesKey("FIRST_LAUNCH_DELAY")
    private val _state = MutableStateFlow(SettingContainer.initial)
    override val state = _state.asStateFlow()

    init {
        CoroutineScope(Job() + dispatcher).launch {
            readSetting()
        }
    }

    override suspend fun saveSetting(periodic: Long, delayed: Long) {
        withContext(dispatcher) {
            dataStore.edit { pref: MutablePreferences ->
                pref[refreshPeriodKey] = periodic
                pref[firstLaunchDelayKey] = delayed
            }
            _state.value = SettingContainer(periodic, delayed)
        }
    }


    override suspend fun readSetting() {
        withContext(dispatcher) {
            dataStore.data.collect { pref: Preferences ->
                val periodic = pref[refreshPeriodKey] ?: SettingContainer.DEFAULT_REFRESH_PERIOD
                val delayed = pref[firstLaunchDelayKey] ?: SettingContainer.FIRST_LAUNCH_DELAY
                _state.value = SettingContainer(periodic, delayed)
            }
        }
    }
}
