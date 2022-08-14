package likco.studyum.models

import androidx.compose.ui.graphics.vector.ImageVector

data class TopBarItem(
    val icon: ImageVector,
    val contentDescription: String,
    val onClick: () -> Unit
)
