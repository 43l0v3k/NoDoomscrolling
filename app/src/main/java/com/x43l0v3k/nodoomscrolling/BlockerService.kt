package com.x43l0v3k.nodoomscrolling

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class BlockerService : AccessibilityService() {

    private var lastBlock = 0L

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        val pkg = event.packageName?.toString() ?: return
        val prefs = Prefs(this)
        val root = rootInActiveWindow ?: return

        if (prefs.diagnostic) collectIds(root, pkg, prefs)
        if (!prefs.enabled) return

        if (pkg in WHOLE_APP) {
            block(prefs, GLOBAL_ACTION_HOME)
            return
        }
        val ids = SHORTS_IDS[pkg] ?: return
        if (ids.any { root.findAccessibilityNodeInfosByViewId("$pkg:id/$it").isNotEmpty() }) {
            block(prefs, GLOBAL_ACTION_BACK)
        }
    }

    private fun block(prefs: Prefs, action: Int) {
        val now = System.currentTimeMillis()
        if (now - lastBlock < 1000) return
        lastBlock = now
        performGlobalAction(action)
        prefs.blocked = prefs.blocked + 1
    }

    private fun collectIds(root: AccessibilityNodeInfo, pkg: String, prefs: Prefs) {
        val found = HashSet<String>(prefs.seen)
        fun walk(n: AccessibilityNodeInfo?, depth: Int) {
            if (n == null || depth > 25 || found.size > 400) return
            n.viewIdResourceName?.let { found.add(it) }
            for (i in 0 until n.childCount) walk(n.getChild(i), depth + 1)
        }
        walk(root, 0)
        prefs.seen = found
    }

    override fun onInterrupt() {}

    companion object {
        // приложения, где весь контент - короткие видео: выходим на рабочий стол
        val WHOLE_APP = setOf(
            "com.zhiliaoapp.musically",
            "com.ss.android.ugc.trill",
            "video.like"
        )

        // id элементов экрана шортсов. Пока угаданы, проверим диагностикой
        val SHORTS_IDS = mapOf(
            "com.google.android.youtube" to listOf("reel_recycler", "reel_player_page_container"),
            "com.instagram.android" to listOf("clips_viewer_view_pager", "clips_video_container")
        )
    }
}
