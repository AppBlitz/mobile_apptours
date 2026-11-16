package co.edu.uniquindio.apptours

import android.app.Application
import co.edu.uniquindio.apptours.data.remote.firebase.seed.FirestoreSeeder
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class ApptoursApp : Application() {

    @Inject
    lateinit var seeder: FirestoreSeeder

    private val appScope = CoroutineScope(SupervisorJob())

    override fun onCreate() {
        super.onCreate()
        appScope.launch { seeder.seedIfNeeded() }
    }
}
