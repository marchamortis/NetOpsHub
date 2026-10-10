package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.local.ArticleEntity
import com.example.data.local.FavoriteEntity
import com.example.data.local.NoteEntity
import com.example.ui.components.GlassCard
import com.example.ui.components.GlassToolbar
import com.example.ui.theme.LocalNetOpsColors

@Composable
fun FavoritesNotesScreen(
    favorites: List<FavoriteEntity>,
    notes: List<NoteEntity>,
    allArticles: List<ArticleEntity>,
    onDeleteNote: (NoteEntity) -> Unit,
    onBack: () -> Unit
) {
    val tokens = LocalNetOpsColors.current
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Favorites, 1: Notes

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        GlassToolbar(
            title = "Saved Items & Notes",
            subtitle = "Personal Operator Workspace",
            navigationIcon = {
                IconButton(onClick = onBack, modifier = Modifier.testTag("fav_back_button")) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = tokens.textPrimary)
                }
            }
        )

        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = tokens.glassSurface,
            contentColor = tokens.primary
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text(stringResource(R.string.favorites)) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text(stringResource(R.string.notes)) }
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (selectedTab == 0) {
                if (favorites.isEmpty()) {
                    item {
                        Text("No bookmarked articles yet. Tap the bookmark icon on any guide in Network Academy to save it here.", color = tokens.textMuted)
                    }
                } else {
                    items(favorites) { fav ->
                        val article = allArticles.find { it.id == fav.articleId }
                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            Text(text = article?.title ?: "Saved Article", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = tokens.textPrimary))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = article?.summary ?: "", style = MaterialTheme.typography.bodySmall.copy(color = tokens.textSecondary))
                        }
                    }
                }
            } else {
                if (notes.isEmpty()) {
                    item {
                        Text("No operator notes yet. You can write personal checklists inside any guide.", color = tokens.textMuted)
                    }
                } else {
                    items(notes) { note ->
                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text(text = note.title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = tokens.primary))
                                IconButton(onClick = { onDeleteNote(note) }, modifier = Modifier.size(24.dp)) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = tokens.error, modifier = Modifier.size(16.dp))
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = note.content, style = MaterialTheme.typography.bodyMedium.copy(color = tokens.textPrimary))
                        }
                    }
                }
            }
        }
    }
}
