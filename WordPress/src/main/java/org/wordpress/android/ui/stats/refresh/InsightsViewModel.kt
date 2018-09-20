package org.wordpress.android.ui.stats.refresh

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import kotlinx.coroutines.experimental.CoroutineScope
import kotlinx.coroutines.experimental.launch
import org.wordpress.android.modules.UI_SCOPE
import org.wordpress.android.ui.stats.refresh.InsightsItem.Type.NOT_IMPLEMENTED
import org.wordpress.android.ui.stats.refresh.InsightsUiState.ListStatus.DONE
import org.wordpress.android.ui.stats.refresh.InsightsUiState.ListStatus.FETCHING
import javax.inject.Inject
import javax.inject.Named

class InsightsViewModel
@Inject constructor(
    private val insightsDomain: InsightsDomain,
    @Named(UI_SCOPE) private val uiScope: CoroutineScope
) : ViewModel() {
    private val mutableData: MutableLiveData<InsightsUiState> = MutableLiveData()
    val data: LiveData<InsightsUiState> = mutableData
    fun start() {
        if (mutableData.value == null) {
            mutableData.value = InsightsUiState(status = FETCHING)
        }
        uiScope.launch {
            val loadedData = insightsDomain.loadInsightItems()
            mutableData.value = InsightsUiState(loadedData, DONE)
        }
    }
}

data class NotImplemented(val text: String) : InsightsItem(NOT_IMPLEMENTED)

data class InsightsUiState(val data: List<InsightsItem> = listOf(), val status: ListStatus) {
    enum class ListStatus {
        DONE,
        ERROR,
        FETCHING
    }
}