package dev.kdrag0n.android12ext.data

import dev.kdrag0n.android12ext.data.model.SettingsReport
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

// This corresponds to the patreon-dl-server instance at patreon.kdrag0n.dev
// TODO: refactor server-side endpoints into multiple services
interface PatreonDlService {
    // Opt-in settings telemetry
    @POST("v1/telemetry/report_settings")
    suspend fun reportSettings(@Body report: SettingsReport): Response<Unit>

    companion object {
        const val BASE_URL = "https://patreon.kdrag0n.dev/api/"
    }
}
