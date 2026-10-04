package com.x43l0v3k.nodoomscrolling

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Switch
import android.widget.TextView

class MainActivity : Activity() {

    private lateinit var prefs: Prefs
    private lateinit var status: TextView
    private lateinit var counter: TextView
    private lateinit var dump: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = Prefs(this)

        val pad = (16 * resources.displayMetrics.density).toInt()
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(pad, pad, pad, pad)
            fitsSystemWindows = true
        }

        status = TextView(this)
        counter = TextView(this).apply { setPadding(0, pad / 2, 0, pad / 2) }
        dump = TextView(this).apply { setTextIsSelectable(true) }

        val accessBtn = Button(this).apply {
            text = "Специальные возможности"
            setOnClickListener { startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)) }
        }
        val enabledSwitch = Switch(this).apply {
            text = "Блокировать шортсы"
            isChecked = prefs.enabled
            setOnCheckedChangeListener { _, c -> prefs.enabled = c }
        }
        val diagSwitch = Switch(this).apply {
            text = "Диагностика (собирать id элементов)"
            isChecked = prefs.diagnostic
            setOnCheckedChangeListener { _, c -> prefs.diagnostic = c }
        }
        val refreshBtn = Button(this).apply {
            text = "Обновить список"
            setOnClickListener { refresh() }
        }
        val clearBtn = Button(this).apply {
            text = "Очистить список"
            setOnClickListener { prefs.seen = emptySet(); refresh() }
        }

        root.addView(status)
        root.addView(accessBtn)
        root.addView(enabledSwitch)
        root.addView(counter)
        root.addView(diagSwitch)
        root.addView(refreshBtn)
        root.addView(clearBtn)
        root.addView(ScrollView(this).apply { addView(dump) })

        setContentView(root, ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        ))
    }

    override fun onResume() {
        super.onResume()
        refresh()
    }

    private fun refresh() {
        val on = Settings.Secure.getString(contentResolver, "enabled_accessibility_services")
            ?.contains(packageName) == true
        status.text = if (on) "Служба: включена" else "Служба: выключена, включи в спецвозможностях"
        counter.text = "Заблокировано: ${prefs.blocked}"
        dump.text = prefs.seen.sorted().joinToString("\n")
    }
}
