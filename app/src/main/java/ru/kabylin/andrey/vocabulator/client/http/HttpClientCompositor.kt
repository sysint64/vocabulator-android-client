package ru.kabylin.andrey.vocabulator.client.http

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import ru.kabylin.andrey.vocabulator.compositors.ConfigCompositor
import ru.kabylin.andrey.vocabulator.compositors.MergeCompositor
import ru.kabylin.andrey.vocabulator.compositors.SchedulerCompositor

class HttpClientCompositor(val client: HttpClient) : ConfigCompositor() {

    override val config = MergeCompositor(
        SchedulerCompositor(
            Schedulers.single(),
            AndroidSchedulers.mainThread()
        ),
        HttpErrorsCompositor(client)
    )
}
