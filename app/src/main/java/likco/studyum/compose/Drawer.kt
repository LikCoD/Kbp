package likco.studyum.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import likco.studyum.models.DrawerItem
import likco.studyum.models.User

@Composable
fun ColumnScope.Drawer(
    user: User,
    items: List<DrawerItem>,
    bottomItem: DrawerItem,
    onItemSelected: (DrawerItem) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .background(MaterialTheme.colors.primary)
            .padding(15.dp)
    ) {
        AsyncImage(
            model = user.picture,
            contentDescription = null,
            modifier = Modifier
                .size(100.dp)
                .clip(RoundedCornerShape(25.dp))
        )
        Spacer(modifier = Modifier.padding(horizontal = 10.dp))
        Text(text = "${user.login}\n${user.typeName}")
    }

    LazyColumn {
        items(items) {
            DrawerActionElement(it, MaterialTheme.colors.onSurface, onItemSelected)
        }
    }

    Spacer(modifier = Modifier.weight(1f))

    DrawerActionElement(bottomItem, MaterialTheme.colors.error, onItemSelected)
}

@Composable
fun DrawerActionElement(item: DrawerItem, color: Color, onClick: (DrawerItem) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clickable { onClick(item) }
            .height(50.dp)
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = item.contentDescription,
            tint = color
        )

        Spacer(modifier = Modifier.width(28.dp))

        Text(
            text = item.text,
            fontSize = 18.sp
        )
    }
}