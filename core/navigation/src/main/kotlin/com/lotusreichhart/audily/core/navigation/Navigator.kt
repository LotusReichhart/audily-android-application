package com.lotusreichhart.audily.core.navigation

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation3.runtime.NavKey

/**
 * Navigator: Xử lý logic điều hướng giữa các màn hình trong ứng dụng.
 * Quản lý cả Top-Level destinations (như Home, Library) và Sub-Stack (các màn hình chi tiết).
 */
class Navigator(val state: NavigationState) {

    /**
     * Phương thức điều hướng chính.
     * - Nếu key đã là Top-Level đang hiển thị -> Xóa sạch các màn hình chi tiết (Sub-Stack).
     * - Nếu key thuộc danh sách Top-Level khác -> Chuyển sang Top-Level đó.
     * - Nếu là màn hình bình thường -> Thêm vào Sub-Stack hiện tại.
     */
    fun navigate(key: NavKey) {
        when (key) {
            state.currentTopLevelKey -> clearSubStack()
            in state.topLevelKeys -> goToTopLevel(key)
            else -> goToKey(key)
        }
    }

    /**
     * Quay lại màn hình trước đó.
     * - Nếu đang ở màn hình khởi đầu (Start Route) -> Báo lỗi vì không thể back thêm.
     * - Nếu đang ở màn hình Top-Level -> Xóa Top-Level hiện tại khỏi stack (thường sẽ thoát App nếu stack trống).
     * - Nếu đang ở màn hình chi tiết -> Xóa màn hình chi tiết cuối cùng khỏi Sub-Stack.
     */
    fun goBack() {
        when (state.currentKey) {
            state.startKey -> error("You cannot go back from the start route")
            state.currentTopLevelKey -> {
                state.topLevelStack.removeLastOrNull()
            }

            else -> state.currentSubStack.removeLastOrNull()
        }
    }

    /**
     * Thêm một màn hình mới vào Sub-Stack của Top-Level hiện tại.
     * Nếu màn hình đã tồn tại trong stack, nó sẽ được đưa lên đầu (Bring to front).
     */
    internal fun goToKey(key: NavKey) {
        state.currentSubStack.apply {
            remove(key)
            if (key is SingleInstanceKey) {
                removeAll { it::class == key::class }
            }
            add(key)
        }
    }

    /**
     * Chuyển đổi giữa các màn hình Top-Level (Tab chính).
     * - Nếu chuyển về màn hình khởi đầu (StartKey) -> Xóa toàn bộ stack để reset.
     * - Luôn đưa Top-Level đích lên đầu stack để hiển thị.
     */
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

    /**
     * Xóa sạch toàn bộ Sub-Stack của Top-Level hiện tại.
     * Giữ lại phần tử gốc (index 0) và xóa các màn hình chi tiết từ index 1 trở đi.
     * Giúp đưa người dùng quay lại màn hình gốc của Tab khi tap vào tab icon lần nữa.
     */
    internal fun clearSubStack() {
        state.currentSubStack.run {
            if (size > 1) subList(1, size).clear()
        }
    }
}

interface SingleInstanceKey : NavKey

val LocalNavigator = staticCompositionLocalOf<Navigator> {
    error("No Navigator provided")
}