package com.yandex.practicum.middle_homework_4.data.setting_repository

data class SettingContainer(
    val periodic: Long,
    val delayed: Long
) {

    companion object {
        const val DEFAULT_REFRESH_PERIOD: Long = 15
        const val FIRST_LAUNCH_DELAY: Long = 10
        val initial: SettingContainer = SettingContainer(
            periodic = DEFAULT_REFRESH_PERIOD,
            delayed = FIRST_LAUNCH_DELAY
        )
    }
}