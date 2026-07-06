package com.eventpass.android.core.navigation

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Process-wide, one-shot navigation signals for the bottom-tab host. Survives
 * the [MainTabsScreen] destination being popped and recreated (e.g. after the
 * Become-an-Organizer flow), which composition-scoped state can't.
 */
@Singleton
class TabNavSignals @Inject constructor() {

    /** Tab to land on the next time the tab host is (re)created, if any. */
    private var pendingStartTab: String? = null

    fun requestStartTab(tab: String) {
        pendingStartTab = tab
    }

    /** Returns the requested start tab once, then clears it. */
    fun consumeStartTab(): String? {
        val tab = pendingStartTab
        pendingStartTab = null
        return tab
    }
}
