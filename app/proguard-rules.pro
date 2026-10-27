# Proguard rules for SuperBrix IA (BRIX360)
# Material 3 & Compose rules
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# OkHttp & Retrofit rules
-dontwarn okhttp3.**
-dontwarn okio.**
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions
-keepattributes *Annotation*

# Gson serialization rules
-keepattributes EnclosingMethod
-keepclassmembers enum * { *; }
-keep class com.google.gson.** { *; }
-keep class com.superbrix.ia.data.model.** { *; }
-keep class com.superbrix.ia.data.remote.** { *; }

# WorkManager
-keep class androidx.work.** { *; }

# Biometrics
-keep class androidx.biometric.** { *; }
