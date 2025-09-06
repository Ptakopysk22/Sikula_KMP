# Zachovej AutoService anotace (kvůli chybě, kterou zmiňuješ)
-dontwarn com.google.auto.service.AutoService
-dontwarn org.robolectric.**
-dontwarn com.google.auto.service.**
-dontwarn org.junit.**

# Zachovej všechny anotace (často potřebné pro multiplatformní kód)
-keepattributes *Annotation*

# Pokud používáš Koin nebo jinou DI knihovnu, přidej (příklad pro Koin):
-keep class org.koin.** { *; }
-dontwarn org.koin.**

# Pro Jetpack Compose – velmi důležité:
-keep class androidx.compose.** { *; }
-keep class androidx.activity.ComponentActivity { *; }
-keep class androidx.compose.runtime.** { *; }
-dontwarn androidx.compose.**

# Pokud používáš kotlinx.serialization:
-keep class kotlinx.serialization.** { *; }
-dontwarn kotlinx.serialization.**

# Pokud používáš kotlinx.coroutines:
-dontwarn kotlinx.coroutines.**

# Nepoužívej shrinking na některé třídy, pokud jsi v KMP a sdílíš common code:
-keep class cz.bosan.sikula_kmp.** { *; }