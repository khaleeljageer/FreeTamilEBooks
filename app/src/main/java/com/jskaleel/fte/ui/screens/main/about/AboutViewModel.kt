package com.jskaleel.fte.ui.screens.main.about

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Publish
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jskaleel.fte.core.model.ImageType
import com.jskaleel.fte.domain.model.About
import com.jskaleel.fte.domain.model.AboutItem
import com.jskaleel.fte.ui.screens.main.about.AboutNavigationState.OpenHtml
import com.jskaleel.fte.ui.screens.main.about.AboutNavigationState.OpenUrl
import com.jskaleel.fte.ui.utils.mutableNavigationState
import com.jskaleel.fte.ui.utils.navigate
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.yield

@HiltViewModel
class AboutViewModel @Inject constructor() : ViewModel() {
    var navigation by mutableNavigationState<AboutNavigationState>()
        private set
    private val mutex = Mutex()

    private val viewModelState = MutableStateFlow(AboutViewModelState())

    val uiState = viewModelState
        .map { it.toUiState() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = viewModelState.value.toUiState()
        )

    init {
        initMenus()
    }

    private fun initMenus() {
        viewModelScope.launch {
            viewModelState.update {
                it.copy(
                    menus = getAboutMenuItems()
                )
            }
        }
    }

    private fun getAboutMenuItems(): List<About> {
        return listOf(
            About(
                title = "திட்டம் பற்றி",
                items = listOf(
                    AboutItem(
                        label = "திட்டம் அறிமுகம்",
                        asset = "about_project.html",
                        icon = ImageType.Vector(Icons.Default.Info),
                        description = "திட்டத்தின் நோக்கம் மற்றும் பயன்கள்"
                    ),
                    AboutItem(
                        label = "தன்னார்வலர்கள்",
                        asset = "modern-team-html.html",
                        icon = ImageType.Vector(Icons.Default.Group),
                        description = "திட்டத்தின் தன்னார்வலர்கள்"
                    ),
                    AboutItem(
                        label = "படைப்புகளை வெளியிட",
                        asset = "publish_books.html",
                        icon = ImageType.Vector(Icons.Default.Publish),
                        description = "உங்கள் மின்னூல்களை வெளியிடுவதற்கான வழிமுறை"
                    ),
                )
            ),

            About(
                title = "இணைப்புகள்",
                items = listOf(
                    AboutItem(
                        label = "பொறுப்புத் துறப்பு",
                        asset = "disclaimer.html",
                        icon = ImageType.Vector(Icons.Default.Gavel),
                    ),
                    AboutItem(
                        label = "கணியம் அறக்கட்டளை",
                        url = "https://kaniyam.com",
                        icon = ImageType.Vector(Icons.Default.Public),
                    ),
                    AboutItem(
                        label = "நன்கொடை",
                        asset = "donate.html",
                        icon = ImageType.Vector(Icons.Default.VolunteerActivism),
                    ),
                    AboutItem(
                        label = "தொடர்பு",
                        asset = "contact.html",
                        icon = ImageType.Vector(Icons.Default.Email),
                    ),
                )
            ),
        )
    }

    fun onEvent(event: AboutEvent) {
        when (event) {
            is AboutEvent.ItemClicked -> handleItemClicked(
                title = event.title,
                type = event.type
            )
        }
    }

    private fun handleItemClicked(type: Type, title: String) {
        viewModelScope.launch {
            mutex.withLock {
                when (type) {
                    is Type.Url -> {
                        navigation = navigate(OpenUrl(type.url))
                    }

                    is Type.Asset -> {
                        navigation = navigate(
                            OpenHtml(
                                title = title,
                                path = type.path
                            )
                        )
                    }

                    Type.None -> {
                        // No action needed for Type.None
                    }
                }
            }
        }
    }

    private suspend inline fun <T> MutableStateFlow<T>.update(function: (T) -> T) {
        mutex.withLock {
            yield()
            this.value = function(this.value)
        }
    }
}

private data class AboutViewModelState(
    val menus: List<About> = emptyList()
) {
    fun toUiState(): AboutUiState {
        return AboutUiState(
            menus = menus.map { about ->
                AboutUiModel(
                    title = about.title,
                    items = about.items.map { item ->
                        AboutItemUiModel(
                            title = item.label,
                            type = if (item.url != null) {
                                Type.Url(url = item.url)
                            } else if (item.asset != null) {
                                Type.Asset(path = item.asset)
                            } else {
                                Type.None
                            },
                            icon = item.icon,
                            description = item.description
                        )
                    }
                )
            }
        )
    }
}

data class AboutUiState(
    val menus: List<AboutUiModel>
)

sealed interface AboutNavigationState {
    data class OpenUrl(val url: String) : AboutNavigationState
    data class OpenHtml(val title: String, val path: String) : AboutNavigationState
}

sealed interface AboutEvent {
    data class ItemClicked(val title: String, val type: Type) : AboutEvent
}
