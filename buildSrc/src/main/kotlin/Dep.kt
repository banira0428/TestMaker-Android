object Dep {
    const val compileSdkVersion = 31
    const val minSdkVersion = 23
    const val targetSdkVersion = 30

    const val kotlinVersion = "1.5.10"
    const val fragmentVersion = "1.3.2"
    const val navVersion = "2.3.3"
    const val hiltVersion = "2.41"

    object Hilt {
        const val android = "com.google.dagger:hilt-android:$hiltVersion"
        const val compiler = "com.google.dagger:hilt-compiler:$hiltVersion"
    }
}