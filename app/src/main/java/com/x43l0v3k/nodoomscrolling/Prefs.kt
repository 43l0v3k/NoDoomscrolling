package com.x43l0v3k.nodoomscrolling

import android.content.Context

class Prefs(context: Context) {
    private val sp = context.getSharedPreferences("nds", Context.MODE_PRIVATE)

    var enabled: Boolean
        get() = sp.getBoolean("enabled", true)
        set(v) = sp.edit().putBoolean("enabled", v).apply()

    var diagnostic: Boolean
        get() = sp.getBoolean("diagnostic", false)
        set(v) = sp.edit().putBoolean("diagnostic", v).apply()

    var blocked: Int
        get() = sp.getInt("blocked", 0)
        set(v) = sp.edit().putInt("blocked", v).apply()

    var seen: Set<String>
        get() = sp.getStringSet("seen", emptySet()) ?: emptySet()
        set(v) = sp.edit().putStringSet("seen", HashSet(v)).apply()
}
