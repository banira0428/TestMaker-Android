package jp.gr.java_conf.foobar.testmaker.service

import android.content.Context
import android.content.SharedPreferences

class SharedPreferenceManager(context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("question", Context.MODE_PRIVATE)

    var numChoose: Int
        get() = sharedPreferences.getInt("num_choose", 3)
        set(num) = sharedPreferences.edit().putInt("num_choose", num)
                .apply()

    var numWrite: Int
        get() = sharedPreferences.getInt("num_write", 1)
        set(num) = sharedPreferences.edit().putInt("num_write", num)
                .apply()

    var auto: Boolean
        get() = sharedPreferences.getBoolean("auto", false)
        set(num) = sharedPreferences.edit().putBoolean("auto", num)
                .apply()

    var explanation: Boolean
        get() = sharedPreferences.getBoolean("explanation", false)
        set(f) = sharedPreferences.edit().putBoolean("explanation", f)
                .apply()

    var manual: Boolean
        get() = sharedPreferences.getBoolean("manual", false)
        set(f) = sharedPreferences.edit().putBoolean("manual", f)
                .apply()

    var refine: Boolean
        get() = sharedPreferences.getBoolean("refine", false)
        set(f) = sharedPreferences.edit().putBoolean("refine", f)
                .apply()

    var audio: Boolean
        get() = sharedPreferences.getBoolean("audio", false)
        set(f) = sharedPreferences.edit().putBoolean("audio", f)
                .apply()

    var reverse: Boolean
        get() = sharedPreferences.getBoolean("reverse", false)
        set(f) = sharedPreferences.edit().putBoolean("reverse", f)
                .apply()

    var confirmSave: Boolean
        get() = sharedPreferences.getBoolean("confirmSave", false)
        set(f) = sharedPreferences.edit().putBoolean("confirmSave", f)
                .apply()

    var sort: Int
        get() = sharedPreferences.getInt("sort", -1)
        set(i) = sharedPreferences.edit().putInt("sort", i).apply()

}