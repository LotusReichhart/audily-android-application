package com.lotusreichhart.audily.core.navigation

import androidx.navigation3.runtime.NavKey

// Navigator: Xu ly logic dieu huong giua cac man hinh
class Navigator(val state: NavigationState) {
    // Dieu huong den mot key cu the
    fun navigate(key: NavKey) {
        when (key) {
            state.currentTopLevelKey -> clearSubStack()
            in state.topLevelKeys -> goToTopLevel(key)
            else -> goToKey(key)
        }
    }

    // Quay lai man hinh truoc do
    fun goBack() {
        when (state.currentKey) {
            state.startKey -> error("You cannot go back from the start route")
            state.currentTopLevelKey -> {
                state.topLevelStack.removeLastOrNull()
            }
            else -> state.currentSubStack.removeLastOrNull()
        }
    }

    // Di chuyen den mot key trong sub stack
    internal fun goToKey(key: NavKey) {
        state.currentSubStack.apply {
            remove(key)
            add(key)
        }
    }

    // Chuyen doi giua cac TopLevel key
    internal fun goToTopLevel(key: NavKey) {
        state.topLevelStack.apply {
            if (key == state.startKey) {
                clear()
            } else {
                remove(key)
            }
            add(key)
        }
    }

    // Xoa sach sub stack hien tai
    internal fun clearSubStack() {
        state.currentSubStack.run {
            if (size > 1) subList(1, size).clear()
        }
    }
}