package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.ArticleEntity
import com.example.data.model.OnlineSource
import com.example.data.model.UserRole
import com.example.ui.components.CommandBox
import com.example.ui.components.CyberInfographicsSection
import com.example.ui.components.GlassCard
import com.example.ui.components.GlassChip
import com.example.ui.components.GlassToolbar
import com.example.ui.components.SearchInput
import com.example.ui.theme.LocalNetOpsColors
import com.example.ui.theme.StatusWarning
import org.json.JSONArray

data class AcademyCategoryUi(
    val slug: String,
    val nameRes: Int,
    val icon: ImageVector,
    val count: Int
)

@Composable
fun AcademyScreen(
    articles: List<ArticleEntity>,
    userRole: UserRole = UserRole.CREATOR,
    onNavigateToTools: (() -> Unit)? = null,
    onBack: () -> Unit,
    onToggleFavorite: (String, Boolean) -> Unit = { _, _ -> },
    favoritesIds: Set<String> = emptySet(),
    onSaveNote: (String, String, String) -> Unit = { _, _, _ -> },
    onSyncOnline: (() -> Unit)? = null,
    onCreateArticle: ((ArticleEntity, Boolean) -> Unit)? = null,
    onUpdateArticle: ((ArticleEntity, Boolean) -> Unit)? = null,
    onDeleteArticle: ((ArticleEntity, Boolean) -> Unit)? = null,
    cloudflareUrl: String = "https://netops-hub-api.mortavox.workers.dev/api/v1/",
    currentLang: String = "en"
) {
    val tokens = LocalNetOpsColors.current
    val context = LocalContext.current
    val isPersian = currentLang == "fa"

    var activeMode by remember { mutableIntStateOf(0) } // 0 = Visual Cyber Maps (CyberKid style), 1 = Master Runbooks
    var selectedCategory by remember { mutableStateOf<String?>("all") }
    var searchQuery by remember { mutableStateOf("") }
    var selectedArticle by remember { mutableStateOf<ArticleEntity?>(null) }
    var selectedDifficulty by remember { mutableStateOf("All") }
    var showOnlineSourcesModal by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var articleToEdit by remember { mutableStateOf<ArticleEntity?>(null) }
    var showDeleteConfirmDialog by remember { mutableStateOf<ArticleEntity?>(null) }

    val isPermittedToEdit = userRole != UserRole.VIEWER

    val categories = remember(articles) {
        listOf(
            AcademyCategoryUi("all", R.string.difficulty_all, Icons.Default.AllInclusive, articles.size),
            AcademyCategoryUi("windows-tricks", R.string.cat_windows_tricks, Icons.Default.Terminal, articles.count { it.categorySlug == "windows-tricks" }),
            AcademyCategoryUi("help-desk", R.string.cat_helpdesk, Icons.Default.SupportAgent, articles.count { it.categorySlug == "help-desk" }),
            AcademyCategoryUi("network-plus", R.string.cat_network_plus, Icons.Default.Lan, articles.count { it.categorySlug == "network-plus" }),
            AcademyCategoryUi("ccna", R.string.cat_ccna, Icons.Default.Router, articles.count { it.categorySlug == "ccna" }),
            AcademyCategoryUi("ccnp", R.string.cat_ccnp, Icons.Default.Hub, articles.count { it.categorySlug == "ccnp" }),
            AcademyCategoryUi("mikrotik", R.string.cat_mikrotik, Icons.Default.SettingsEthernet, articles.count { it.categorySlug == "mikrotik" }),
            AcademyCategoryUi("fortigate", R.string.cat_fortigate, Icons.Default.Security, articles.count { it.categorySlug == "fortigate" }),
            AcademyCategoryUi("windows-server", R.string.cat_windows_server, Icons.Default.Dns, articles.count { it.categorySlug == "windows-server" })
        )
    }

    // Article Details Dialog
    if (selectedArticle != null) {
        ArticleDetailDialog(
            article = selectedArticle!!,
            isFavorite = favoritesIds.contains(selectedArticle!!.id),
            isPermittedToEdit = isPermittedToEdit,
            onToggleFavorite = { isFav -> onToggleFavorite(selectedArticle!!.id, isFav) },
            onClose = { selectedArticle = null },
            onSaveNote = onSaveNote,
            onEdit = {
                articleToEdit = selectedArticle
                selectedArticle = null
                showEditDialog = true
            },
            onDelete = {
                val toDelete = selectedArticle
                selectedArticle = null
                showDeleteConfirmDialog = toDelete
            }
        )
    }

    // Delete Confirmation Dialog
    if (showDeleteConfirmDialog != null) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = null },
            title = { Text("Delete Knowledge Article", color = tokens.error, fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to delete '${showDeleteConfirmDialog!!.title}'? This will remove the article locally and from Cloudflare D1.", color = tokens.textPrimary) },
            confirmButton = {
                TextButton(
                    onClick = {
                        val art = showDeleteConfirmDialog!!
                        onDeleteArticle?.invoke(art, true)
                        Toast.makeText(context, "Article deleted", Toast.LENGTH_SHORT).show()
                        showDeleteConfirmDialog = null
                    }
                ) {
                    Text("Delete", color = tokens.error, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = null }) {
                    Text("Cancel", color = tokens.textMuted)
                }
            },
            containerColor = tokens.surface
        )
    }

    // Create / Edit Article Dialog
    if (showEditDialog) {
        ArticleFormDialog(
            initialArticle = articleToEdit,
            onDismiss = {
                showEditDialog = false
                articleToEdit = null
            },
            onSave = { article, pushToCloudflare ->
                if (articleToEdit == null) {
                    onCreateArticle?.invoke(article, pushToCloudflare)
                    Toast.makeText(context, "New article created and synced!", Toast.LENGTH_SHORT).show()
                } else {
                    onUpdateArticle?.invoke(article, pushToCloudflare)
                    Toast.makeText(context, "Article updated successfully!", Toast.LENGTH_SHORT).show()
                }
                showEditDialog = false
                articleToEdit = null
            }
        )
    }

    // Online Sources Modal
    if (showOnlineSourcesModal) {
        OnlineSourcesDialog(
            cloudflareUrl = cloudflareUrl,
            onDismiss = { showOnlineSourcesModal = false }
        )
    }

    Scaffold(
        floatingActionButton = {
            if (isPermittedToEdit) {
                FloatingActionButton(
                    onClick = {
                        articleToEdit = null
                        showEditDialog = true
                    },
                    containerColor = tokens.primary,
                    contentColor = tokens.background,
                    modifier = Modifier.testTag("academy_add_article_fab")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Technical Article")
                }
            }
        },
        containerColor = androidx.compose.ui.graphics.Color.Transparent
    ) { paddingVals ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingVals)
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            GlassToolbar(
                title = stringResource(R.string.section_academy),
                subtitle = if (isPersian) "پایگاه دانش ابری • مهندسی IT • نقشه‌های تصویری سایبر" else "Cloud Knowledge Base • IT Engineering • Cyber Infographics",
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("academy_back_button")) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = tokens.textPrimary)
                    }
                },
                actions = {
                    // Open Online Documentation Sources Portal
                    IconButton(
                        onClick = { showOnlineSourcesModal = true },
                        modifier = Modifier.testTag("academy_sources_button")
                    ) {
                        Icon(Icons.Default.Public, contentDescription = "Online Sources", tint = tokens.accent)
                    }

                    // Online Knowledge Sync button
                    IconButton(
                        onClick = {
                            onSyncOnline?.invoke()
                            Toast.makeText(context, "Synchronizing with Cloudflare & Online Repositories...", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.testTag("academy_sync_online_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudSync,
                            contentDescription = "Sync Online",
                            tint = tokens.primary
                        )
                    }
                }
            )

            // Primary Mode Selector: Visual Cyber Maps (CyberKid) vs Runbooks
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (activeMode == 0) tokens.primary else tokens.glassSurface,
                    border = BorderStroke(1.dp, if (activeMode == 0) tokens.primary else tokens.glassBorder),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { activeMode = 0 }
                        .testTag("academy_mode_infographics")
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = if (activeMode == 0) tokens.background else tokens.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isPersian) "اینفوگرافیک‌های امنیت و شبکه" else "Cyber Infographics & Maps",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (activeMode == 0) tokens.background else tokens.textPrimary
                            )
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (activeMode == 1) tokens.accent else tokens.glassSurface,
                    border = BorderStroke(1.dp, if (activeMode == 1) tokens.accent else tokens.glassBorder),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { activeMode = 1 }
                        .testTag("academy_mode_runbooks")
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.MenuBook,
                            contentDescription = null,
                            tint = if (activeMode == 1) tokens.background else tokens.accent,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isPersian) "پایگاه سناریوها و آموزش‌ها" else "Master Runbooks & Guides",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (activeMode == 1) tokens.background else tokens.textPrimary
                            )
                        )
                    }
                }
            }

            if (activeMode == 0) {
                // Infographics & Visual Posters (CyberKid Style)
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(top = 4.dp, bottom = 40.dp)
                ) {
                    item {
                        CyberInfographicsSection(isPersian = isPersian)
                    }
                }
            } else {
                // Search & Category Chips for Master Runbooks
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SearchInput(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = stringResource(R.string.search_academy_hint)
                    )

                    // Category Horizontal Chips with Real Counts
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(categories) { cat ->
                            val isSelected = selectedCategory == cat.slug
                            val label = "${stringResource(cat.nameRes)} (${cat.count})"
                            GlassChip(
                                text = label,
                                selected = isSelected,
                                onClick = { selectedCategory = cat.slug }
                            )
                        }
                    }
                }

                // Online Knowledge Hub Pill Banner
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = tokens.glassSurface,
                    border = BorderStroke(1.dp, tokens.glassBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { showOnlineSourcesModal = true }
                        ) {
                            Icon(Icons.Default.CloudQueue, contentDescription = null, tint = tokens.primary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Cloudflare D1 Online Sync • MS Learn • Cisco Docs",
                                style = MaterialTheme.typography.labelSmall.copy(color = tokens.textSecondary, fontSize = 11.sp),
                                maxLines = 1
                            )
                        }
                        TextButton(
                            onClick = {
                                onSyncOnline?.invoke()
                                Toast.makeText(context, "Knowledge base synced with Cloudflare D1", Toast.LENGTH_SHORT).show()
                            },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier.height(28.dp)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, tint = tokens.accent, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Sync Live", color = tokens.accent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Articles List
                val filteredArticles = remember(articles, selectedCategory, searchQuery, selectedDifficulty) {
                    articles.filter { a ->
                        val matchesCategory = (selectedCategory == "all" || a.categorySlug == selectedCategory)
                        val matchesSearch = if (searchQuery.isBlank()) true else {
                            a.title.contains(searchQuery, ignoreCase = true) ||
                            a.titleFa.contains(searchQuery, ignoreCase = true) ||
                            a.summary.contains(searchQuery, ignoreCase = true) ||
                            a.summaryFa.contains(searchQuery, ignoreCase = true) ||
                            a.content.contains(searchQuery, ignoreCase = true) ||
                            a.tags.contains(searchQuery, ignoreCase = true) ||
                            a.vendor.contains(searchQuery, ignoreCase = true)
                        }
                        matchesCategory && matchesSearch
                    }
                }

                if (filteredArticles.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.SearchOff, contentDescription = null, tint = tokens.textMuted, modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("No matching articles found", color = tokens.textMuted, fontWeight = FontWeight.SemiBold)
                            Text("Try searching another term or tap 'Sync Live' to update from online feeds.", color = tokens.textSecondary, fontSize = 12.sp)
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        items(filteredArticles, key = { it.id }) { article ->
                            ArticleSummaryCard(
                                article = article,
                                isFavorite = favoritesIds.contains(article.id),
                                onClick = { selectedArticle = article }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ArticleSummaryCard(
    article: ArticleEntity,
    isFavorite: Boolean,
    onClick: () -> Unit
) {
    val tokens = LocalNetOpsColors.current

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        contentPadding = PaddingValues(16.dp),
        onClick = onClick,
        testTag = "article_card_${article.id}"
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = tokens.surface,
                border = BorderStroke(0.5.dp, tokens.glassBorder)
            ) {
                Text(
                    text = article.categorySlug.uppercase().replace("-", " "),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = tokens.primary,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    ),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = when (article.difficulty.lowercase()) {
                        "fundamental" -> tokens.secondary.copy(alpha = 0.2f)
                        "advanced" -> tokens.error.copy(alpha = 0.2f)
                        else -> tokens.accent.copy(alpha = 0.2f)
                    }
                ) {
                    Text(
                        text = article.difficulty,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = when (article.difficulty.lowercase()) {
                                "fundamental" -> tokens.secondary
                                "advanced" -> tokens.error
                                else -> tokens.accent
                            },
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                if (isFavorite) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Default.Bookmark,
                        contentDescription = "Favorite",
                        tint = tokens.accent,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = article.title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = tokens.textPrimary
            )
        )

        if (article.titleFa.isNotBlank()) {
            Text(
                text = article.titleFa,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = tokens.textSecondary,
                    fontSize = 12.sp
                )
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = article.summary,
            style = MaterialTheme.typography.bodySmall.copy(
                color = tokens.textSecondary,
                lineHeight = 18.sp
            ),
            maxLines = 2
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Vendor & Tags
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Vendor: ${article.vendor}",
                style = MaterialTheme.typography.labelSmall.copy(color = tokens.textMuted)
            )

            Text(
                text = "Read Guide →",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = tokens.primary,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

@Composable
fun ArticleDetailDialog(
    article: ArticleEntity,
    isFavorite: Boolean,
    isPermittedToEdit: Boolean = false,
    onToggleFavorite: (Boolean) -> Unit,
    onClose: () -> Unit,
    onSaveNote: (String, String, String) -> Unit,
    onEdit: (() -> Unit)? = null,
    onDelete: (() -> Unit)? = null
) {
    val tokens = LocalNetOpsColors.current
    var showNoteInput by remember { mutableStateOf(false) }
    var noteContent by remember { mutableStateOf("") }
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onClose,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.92f),
        confirmButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (isPermittedToEdit && onEdit != null) {
                    TextButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = null, tint = tokens.accent, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Edit Online", color = tokens.accent, fontWeight = FontWeight.Bold)
                    }
                }
                if (isPermittedToEdit && onDelete != null) {
                    TextButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = null, tint = tokens.error, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Delete", color = tokens.error)
                    }
                }
                TextButton(onClick = onClose) {
                    Text("Close", color = tokens.primary, fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = {
            Row {
                IconButton(onClick = { onToggleFavorite(!isFavorite) }) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "Bookmark",
                        tint = tokens.accent
                    )
                }
                IconButton(onClick = { showNoteInput = !showNoteInput }) {
                    Icon(Icons.Default.NoteAdd, contentDescription = "Add Note", tint = tokens.textPrimary)
                }
            }
        },
        containerColor = tokens.surface,
        title = {
            Column {
                Text(
                    text = article.title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = tokens.textPrimary
                    )
                )
                if (article.vendor.isNotBlank()) {
                    Text(
                        text = "Vendor: ${article.vendor} • Difficulty: ${article.difficulty}",
                        style = MaterialTheme.typography.labelSmall.copy(color = tokens.primary)
                    )
                }
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Persian Translation Title
                if (article.titleFa.isNotBlank()) {
                    item {
                        Text(
                            text = article.titleFa,
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = tokens.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }

                // Summary
                item {
                    Text(
                        text = article.summary,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = tokens.textSecondary,
                            lineHeight = 20.sp
                        )
                    )
                }

                if (article.summaryFa.isNotBlank()) {
                    item {
                        Text(
                            text = article.summaryFa,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = tokens.textMuted,
                                lineHeight = 18.sp
                            )
                        )
                    }
                }

                // Personal Note Section
                if (showNoteInput) {
                    item {
                        GlassCard(
                            modifier = Modifier.fillMaxWidth(),
                            border = BorderStroke(1.dp, tokens.accent)
                        ) {
                            Text(
                                text = "Personal Note (Private to Operator):",
                                style = MaterialTheme.typography.labelSmall.copy(color = tokens.accent, fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(
                                value = noteContent,
                                onValueChange = { noteContent = it },
                                placeholder = { Text("Write personal checklist or observation...", color = tokens.textMuted) },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Button(
                                onClick = {
                                    if (noteContent.isNotBlank()) {
                                        onSaveNote(article.id, article.title, noteContent)
                                        showNoteInput = false
                                        Toast.makeText(context, "Note Saved", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = tokens.accent)
                            ) {
                                Text("Save Note", color = tokens.background)
                            }
                        }
                    }
                }

                // Technical Instructions
                item {
                    Text(
                        text = "Technical Instructions:",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = tokens.primary
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = article.content,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = tokens.textPrimary,
                            lineHeight = 22.sp
                        )
                    )
                }

                // Commands
                if (article.commandsJson.isNotBlank() && article.commandsJson != "[]") {
                    item {
                        Text(
                            text = "CLI & PowerShell Commands (Click to Copy):",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = tokens.accent
                            )
                        )
                    }

                    // Parse JSON commands safely
                    try {
                        val arr = JSONArray(article.commandsJson)
                        for (i in 0 until arr.length()) {
                            val obj = arr.getJSONObject(i)
                            val label = obj.optString("label", "Command")
                            val cmd = obj.optString("cmd", "")
                            item {
                                CommandBox(command = cmd, label = label)
                            }
                        }
                    } catch (e: Exception) {
                        item {
                            CommandBox(command = article.commandsJson, label = "Commands")
                        }
                    }
                }

                // Warnings
                if (article.warnings.isNotBlank()) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = StatusWarning.copy(alpha = 0.15f),
                            border = BorderStroke(1.dp, StatusWarning.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(Icons.Default.Warning, contentDescription = null, tint = StatusWarning, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = article.warnings,
                                    style = MaterialTheme.typography.bodySmall.copy(color = tokens.textPrimary)
                                )
                            }
                        }
                    }
                }

                // Verification & Expected Results
                if (article.verification.isNotBlank()) {
                    item {
                        Text(
                            text = "Verification Step:",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = tokens.primary)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        CommandBox(command = article.verification, label = "VERIFICATION CLI")
                    }
                }

                if (article.expectedResults.isNotBlank()) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = tokens.surface,
                            border = BorderStroke(0.5.dp, tokens.glassBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "Expected Result:",
                                    style = MaterialTheme.typography.labelSmall.copy(color = tokens.secondary, fontWeight = FontWeight.Bold)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = article.expectedResults,
                                    style = MaterialTheme.typography.bodySmall.copy(color = tokens.textPrimary)
                                )
                            }
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun OnlineSourcesDialog(
    cloudflareUrl: String,
    onDismiss: () -> Unit
) {
    val tokens = LocalNetOpsColors.current
    val context = LocalContext.current

    val sources = remember {
        listOf(
            OnlineSource(
                id = "src_ms_learn",
                name = "Microsoft Learn & TechNet",
                name_fa = "مرجع اسناد رسمی مایکروسافت",
                category = "Windows & Server",
                url = "https://learn.microsoft.com/en-us/windows-server/",
                description = "Official Windows Server, Active Directory, PowerShell, and Client OS documentation.",
                description_fa = "مستندات و کتابخانه‌های عیب‌یابی ویندوز سرور و اکتیو دایرکتوری"
            ),
            OnlineSource(
                id = "src_cisco",
                name = "Cisco Networking Documentation",
                name_fa = "پرتال رسمی شبکه سیسکو",
                category = "Routing & Switching",
                url = "https://www.cisco.com/c/en/us/support/index.html",
                description = "Configuration guides for IOS XE, Catalyst, Nexus, OSPF, BGP, and VLAN trunking.",
                description_fa = "راهنماهای پیکربندی و کامندهای روتر و سوئیچ‌های سیسکو"
            ),
            OnlineSource(
                id = "src_mikrotik",
                name = "MikroTik Documentation & Wiki",
                name_fa = "دانشنامه رسمی میکروتیک",
                category = "RouterOS & Firewall",
                url = "https://help.mikrotik.com/docs/",
                description = "Authoritative RouterOS v7 manuals, firewall mangle, queue trees, and WireGuard tunnels.",
                description_fa = "مستندات روتربورد، تنظیمات فایروال و مسیریابی پیشرفته میکروتیک"
            ),
            OnlineSource(
                id = "src_fortinet",
                name = "Fortinet FortiGuard Labs & Cookbook",
                name_fa = "مرجع سناریوهای فورتی‌گیت",
                category = "Enterprise Security",
                url = "https://docs.fortinet.com/",
                description = "FortiOS administration guides, SD-WAN steering, IPsec VPN, and threat prevention.",
                description_fa = "سناریوهای استقرار فایروال فورتی‌گیت، SD-WAN و وی‌پی‌ان‌های امن"
            ),
            OnlineSource(
                id = "src_wireshark",
                name = "Wireshark User Guide & Filter Spec",
                name_fa = "راهنمای فیلترهای وایرشارک",
                category = "Packet Analysis",
                url = "https://www.wireshark.org/docs/dfref/",
                description = "Official display filter reference and protocol decoder specifications.",
                description_fa = "مرجع رسمی فیلترهای پیشرفته تحلیل پکت‌های شبکه"
            ),
            OnlineSource(
                id = "src_vmware",
                name = "Broadcom VMware Knowledge Base",
                name_fa = "پایگاه دانش وی‌ام‌ویر (VMware KB)",
                category = "Virtualization & Cloud",
                url = "https://knowledge.broadcom.com/external/article?articleNumber=vmware",
                description = "vSphere, ESXi, vCenter troubleshooting, and vMotion migration diagnostics.",
                description_fa = "حل مشکلات و خطاهای هاست‌های ESXi و کلاستر vCenter"
            )
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Public, contentDescription = null, tint = tokens.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Authoritative Online Tech Portals", fontWeight = FontWeight.Bold, color = tokens.textPrimary)
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Text(
                        text = "Access official vendor documentation, live knowledge repositories, and configuration blueprints directly from your mobile device.",
                        style = MaterialTheme.typography.bodySmall.copy(color = tokens.textSecondary)
                    )
                }

                items(sources) { src ->
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = src.name,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = tokens.textPrimary)
                                )
                                if (!src.name_fa.isNullOrBlank()) {
                                    Text(
                                        text = src.name_fa,
                                        style = MaterialTheme.typography.bodySmall.copy(color = tokens.primary, fontSize = 11.sp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = src.description,
                                    style = MaterialTheme.typography.bodySmall.copy(color = tokens.textSecondary, fontSize = 11.sp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(src.url))
                                    context.startActivity(intent)
                                }
                            ) {
                                Icon(Icons.Default.OpenInNew, contentDescription = "Open", tint = tokens.accent)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Done", color = tokens.primary, fontWeight = FontWeight.Bold)
            }
        },
        containerColor = tokens.surface
    )
}

@Composable
fun ArticleFormDialog(
    initialArticle: ArticleEntity? = null,
    onDismiss: () -> Unit,
    onSave: (ArticleEntity, Boolean) -> Unit
) {
    val tokens = LocalNetOpsColors.current
    var title by remember { mutableStateOf(initialArticle?.title ?: "") }
    var titleFa by remember { mutableStateOf(initialArticle?.titleFa ?: "") }
    var categorySlug by remember { mutableStateOf(initialArticle?.categorySlug ?: "windows-tricks") }
    var difficulty by remember { mutableStateOf(initialArticle?.difficulty ?: "Intermediate") }
    var vendor by remember { mutableStateOf(initialArticle?.vendor ?: "Microsoft Windows") }
    var summary by remember { mutableStateOf(initialArticle?.summary ?: "") }
    var summaryFa by remember { mutableStateOf(initialArticle?.summaryFa ?: "") }
    var content by remember { mutableStateOf(initialArticle?.content ?: "") }
    var commandsText by remember { mutableStateOf(initialArticle?.commandsJson ?: "") }
    var warnings by remember { mutableStateOf(initialArticle?.warnings ?: "") }
    var verification by remember { mutableStateOf(initialArticle?.verification ?: "") }
    var pushToCloudflare by remember { mutableStateOf(true) }

    val categoryOptions = listOf(
        "windows-tricks" to "Windows Tricks & Hacks",
        "help-desk" to "Help Desk Troubleshooting",
        "network-plus" to "Network+ Fundamentals",
        "ccna" to "Cisco CCNA",
        "ccnp" to "Cisco CCNP",
        "mikrotik" to "MikroTik RouterOS",
        "fortigate" to "FortiGate Security",
        "windows-server" to "Windows Server & AD"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.92f),
        title = {
            Text(
                text = if (initialArticle == null) "Create Technical Article" else "Edit Article Online",
                fontWeight = FontWeight.Bold,
                color = tokens.primary
            )
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Title (English)*") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = titleFa,
                        onValueChange = { titleFa = it },
                        label = { Text("عنوان راهنما (فارسی)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    Text("Category", style = MaterialTheme.typography.labelSmall.copy(color = tokens.accent, fontWeight = FontWeight.Bold))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(categoryOptions) { (slug, label) ->
                            GlassChip(
                                text = label,
                                selected = categorySlug == slug,
                                onClick = { categorySlug = slug }
                            )
                        }
                    }
                }
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = vendor,
                            onValueChange = { vendor = it },
                            label = { Text("Vendor / Platform") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = difficulty,
                            onValueChange = { difficulty = it },
                            label = { Text("Difficulty") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                item {
                    OutlinedTextField(
                        value = summary,
                        onValueChange = { summary = it },
                        label = { Text("Summary (English)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = summaryFa,
                        onValueChange = { summaryFa = it },
                        label = { Text("خلاصه عیب‌یابی (فارسی)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = content,
                        onValueChange = { content = it },
                        label = { Text("Technical Content & Steps*") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 4
                    )
                }
                item {
                    OutlinedTextField(
                        value = commandsText,
                        onValueChange = { commandsText = it },
                        label = { Text("Commands (PowerShell / CLI / JSON)") },
                        placeholder = { Text("""[{"label": "Test", "cmd": "ipconfig"}]""") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )
                }
                item {
                    OutlinedTextField(
                        value = warnings,
                        onValueChange = { warnings = it },
                        label = { Text("Warning Notes (Optional)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = verification,
                        onValueChange = { verification = it },
                        label = { Text("Verification CLI (Optional)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = pushToCloudflare,
                            onCheckedChange = { pushToCloudflare = it }
                        )
                        Text(
                            text = "Sync immediately to Cloudflare D1 Backend",
                            color = tokens.textPrimary,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && content.isNotBlank()) {
                        val formattedCommands = if (commandsText.trim().startsWith("[")) {
                            commandsText
                        } else if (commandsText.isNotBlank()) {
                            """[{"label": "Command", "cmd": "${commandsText.replace("\"", "\\\"").replace("\n", "\\n")}"}]"""
                        } else {
                            "[]"
                        }
                        val entity = ArticleEntity(
                            id = initialArticle?.id ?: "art_${System.currentTimeMillis()}",
                            title = title,
                            titleFa = titleFa,
                            summary = summary,
                            summaryFa = summaryFa,
                            content = content,
                            categorySlug = categorySlug,
                            subcategory = initialArticle?.subcategory ?: "Custom",
                            vendor = vendor,
                            difficulty = difficulty,
                            tags = "$categorySlug, $vendor",
                            commandsJson = formattedCommands,
                            warnings = warnings,
                            expectedResults = initialArticle?.expectedResults ?: "",
                            verification = verification,
                            troubleshooting = initialArticle?.troubleshooting ?: ""
                        )
                        onSave(entity, pushToCloudflare)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = tokens.primary)
            ) {
                Text("Save Article", color = tokens.background, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = tokens.textMuted)
            }
        },
        containerColor = tokens.surface
    )
}
