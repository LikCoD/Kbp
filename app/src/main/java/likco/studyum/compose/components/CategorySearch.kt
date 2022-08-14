package likco.studyum.compose.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import likco.studyum.models.Category
import likco.studyum.services.ScheduleService
import likco.studyum.services.UserService

@Composable
fun <I> CategorySearch(
    baseItems: List<I>,
    categories: List<Category>,
    filter: (Category?, String, I) -> Boolean,
    searchItem: @Composable BoxScope.(I) -> Unit,
    onSelect: (I) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = remember { mutableStateListOf<I>() }
    if (items.isEmpty())
        items.addAll(baseItems)

    var searchText by remember { mutableStateOf("") }
    var category by remember { mutableStateOf<Category?>(null) }

    Column(modifier.padding(horizontal = 15.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            SearchCategories(
                categories = categories,
                onClick = { c -> category = c },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 15.dp)
            )

            OutlinedTextField(
                value = searchText,
                onValueChange = { text -> searchText = text },
                label = { Text(text = "Search") },
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
                modifier = Modifier.width(150.dp)
            )
        }
        SearchItems(
            items = items.filter { filter(category, searchText, it) },
            searchItem = searchItem,
            onClick = onSelect,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun SearchCategories(
    categories: List<Category>,
    modifier: Modifier = Modifier,
    onClick: (Category?) -> Unit
) {
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    LazyRow(modifier) {
        items(categories) { category ->
            val activeColors = ButtonDefaults.buttonColors()
            val disabledColors = ButtonDefaults.buttonColors(
                backgroundColor = Color.Gray,
                contentColor = Color.White
            )

            Button(
                onClick = {
                    selectedCategory = if (selectedCategory == category) null else category
                    onClick(selectedCategory)
                },
                colors = if (selectedCategory == category) activeColors else disabledColors,
                modifier = Modifier.padding(horizontal = 5.dp)
            ) {
                Text(text = category.title)
            }
        }
    }
}

@Composable
private fun <I> SearchItems(
    items: List<I>,
    searchItem: @Composable BoxScope.(I) -> Unit,
    onClick: (I) -> Unit,
    modifier: Modifier,
) {
    LazyColumn(modifier) {
        items(items) {
            Box(modifier = Modifier.fillMaxWidth().clickable { onClick(it) }) {
                searchItem(it)
            }
        }
    }
}