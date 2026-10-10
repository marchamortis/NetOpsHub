package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.local.*
import com.example.ui.components.GlassCard
import com.example.ui.components.GlassToolbar
import com.example.ui.components.SearchInput
import com.example.ui.theme.LocalNetOpsColors

@Composable
fun SearchScreen(
    servers: List<ServerEntity>,
    articles: List<ArticleEntity>,
    ports: List<PortEntity>,
    onBack: () -> Unit
) {
    val tokens = LocalNetOpsColors.current
    var query by remember { mutableStateOf("") }

    val matchingServers = remember(servers, query) {
        if (query.isBlank()) emptyList() else servers.filter {
            it.name.contains(query, ignoreCase = true) ||
            it.ip.contains(query, ignoreCase = true) ||
            it.service.contains(query, ignoreCase = true) ||
            it.site.contains(query, ignoreCase = true)
        }.take(10)
    }

    val matchingArticles = remember(articles, query) {
        if (query.isBlank()) emptyList() else articles.filter {
            it.title.contains(query, ignoreCase = true) ||
            it.titleFa.contains(query, ignoreCase = true) ||
            it.summary.contains(query, ignoreCase = true) ||
            it.tags.contains(query, ignoreCase = true)
        }.take(10)
    }

    val matchingPorts = remember(ports, query) {
        if (query.isBlank()) emptyList() else ports.filter {
            it.portNumber.toString().contains(query) ||
            it.service.contains(query, ignoreCase = true) ||
            it.description.contains(query, ignoreCase = true)
        }.take(10)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        GlassToolbar(
            title = "Global Search",
            subtitle = "Query Servers, Articles & Ports",
            navigationIcon = {
                IconButton(onClick = onBack, modifier = Modifier.testTag("search_back_button")) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = tokens.textPrimary)
                }
            }
        )

        Box(modifier = Modifier.padding(16.dp)) {
            SearchInput(
                value = query,
                onValueChange = { query = it },
                placeholder = "Search OSPF, 53, RDP, Ardakan, 192.168.1.8..."
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            if (matchingServers.isNotEmpty()) {
                item {
                    Text("Matching Servers (${matchingServers.size})", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = tokens.primary))
                }
                items(matchingServers) { s ->
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Text(text = "${s.name} (${s.ip})", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = tokens.textPrimary))
                        Text(text = "Site: ${s.site} • Service: ${s.service}", style = MaterialTheme.typography.bodySmall.copy(color = tokens.textSecondary))
                    }
                }
            }

            if (matchingArticles.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Matching Academy Guides (${matchingArticles.size})", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = tokens.accent))
                }
                items(matchingArticles) { a ->
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Text(text = a.title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = tokens.textPrimary))
                        Text(text = a.summary, style = MaterialTheme.typography.bodySmall.copy(color = tokens.textSecondary))
                    }
                }
            }

            if (matchingPorts.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Matching Ports (${matchingPorts.size})", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = tokens.primary))
                }
                items(matchingPorts) { p ->
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Text(text = "Port ${p.portNumber} - ${p.service}", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = tokens.textPrimary))
                        Text(text = p.description, style = MaterialTheme.typography.bodySmall.copy(color = tokens.textSecondary))
                    }
                }
            }

            if (query.isNotBlank() && matchingServers.isEmpty() && matchingArticles.isEmpty() && matchingPorts.isEmpty()) {
                item {
                    Text("No matching results found for \"$query\"", color = tokens.textMuted, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}
