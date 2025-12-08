package io.branch.branchlinksimulator

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.branch.referral.Branch
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.junit.Test
import org.junit.runner.RunWith

val branchInitializationSignal = CompletableDeferred<Unit>()
private val applicationJob = SupervisorJob()
val applicationScope = CoroutineScope(Dispatchers.Main + applicationJob)
val exampleBranchKey = "key_live_hshD4wiPK2sSxfkZqkH30ggmyBfmGmD7"

@RunWith(AndroidJUnit4::class)
class BranchBackgroundInitTest {

    private val appContext: Context = ApplicationProvider.getApplicationContext()
    @Test
    fun branchGetInstanceTest() {
        suspend fun backgroundGetBranchAutoInstanceTest(context: Context, branchKey: String) {
            withContext(Dispatchers.IO) {
                Branch.getAutoInstance(context, branchKey)
            }
            branchInitializationSignal.complete(Unit)
        }

        applicationScope.launch {
            backgroundGetBranchAutoInstanceTest(appContext, exampleBranchKey)
        }
    }

}