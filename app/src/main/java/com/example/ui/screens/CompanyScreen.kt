package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.RahkaranEntity
import com.example.data.local.ServerEntity
import com.example.data.local.SiteEntity
import com.example.data.local.VcenterEntity
import com.example.data.model.UserRole
import com.example.ui.components.GlassCard
import com.example.ui.components.GlassChip
import com.example.ui.components.GlassToolbar
import com.example.ui.components.SearchInput
import com.example.ui.theme.LocalNetOpsColors
import com.example.ui.theme.StatusError
import com.example.ui.theme.StatusSuccess

@Composable
fun CompanyScreen(
    servers: List<ServerEntity>,
    sites: List<SiteEntity>,
    vcenters: List<VcenterEntity>,
    rahkaran: List<RahkaranEntity>,
    userRole: UserRole,
    onBack: () -> Unit,
    onUpdateServer: ((ServerEntity, Boolean) -> Unit)? = null,
    onCreateServer: ((ServerEntity, Boolean) -> Unit)? = null,
    onSyncRealtime: (() -> Unit)? = null
) {
    val tokens = LocalNetOpsColors.current
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Servers, 1: Sites & vCenter, 2: Rahkaran ERP
    var searchQuery by remember { mutableStateOf("") }
    var selectedSiteFilter by remember { mutableStateOf("All") }
    var serverToEdit by remember { mutableStateOf<ServerEntity?>(null) }
    var showAddServerDialog by remember { mutableStateOf(false) }

    val isPermittedToEdit = userRole != UserRole.VIEWER

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        GlassToolbar(
            title = stringResource(R.string.section_company),
            subtitle = "Live Cloudflare D1 • Real-Time Sync Active",
            navigationIcon = {
                IconButton(onClick = onBack, modifier = Modifier.testTag("company_back_button")) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = tokens.textPrimary)
                }
            },
            actions = {
                // Live Cloudflare Sync button
                IconButton(
                    onClick = {
                        onSyncRealtime?.invoke()
                        Toast.makeText(context, "Checking Cloudflare D1 for real-time colleague updates...", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.testTag("company_sync_button")
                ) {
                    Icon(Icons.Default.CloudSync, contentDescription = "Sync Cloudflare", tint = tokens.primary)
                }

                if (isPermittedToEdit && onCreateServer != null) {
                    IconButton(
                        onClick = { showAddServerDialog = true },
                        modifier = Modifier.testTag("company_add_server_button")
                    ) {
                        Icon(Icons.Default.AddCircleOutline, contentDescription = "Add Server", tint = tokens.accent)
                    }
                }
            }
        )

        // Sub Tabs Row
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = tokens.glassSurface.copy(alpha = 0.5f),
            contentColor = tokens.primary,
            divider = { HorizontalDivider(color = tokens.glassBorder) }
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text(stringResource(R.string.tab_servers), fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.Dns, contentDescription = null, modifier = Modifier.size(18.dp)) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text(stringResource(R.string.tab_sites_vcenter), fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.Domain, contentDescription = null, modifier = Modifier.size(18.dp)) }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = { Text(stringResource(R.string.tab_rahkaran), fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.Hub, contentDescription = null, modifier = Modifier.size(18.dp)) }
            )
        }

        when (selectedTab) {
            0 -> ServersTabContent(
                servers = servers,
                searchQuery = searchQuery,
                onSearchChange = { searchQuery = it },
                selectedSite = selectedSiteFilter,
                onSiteSelect = { selectedSiteFilter = it },
                context = context,
                isPermittedToEdit = isPermittedToEdit,
                onEditServer = { serverToEdit = it }
            )
            1 -> SitesAndVcenterTabContent(
                sites = sites,
                vcenters = vcenters,
                servers = servers
            )
            2 -> RahkaranTabContent(
                rahkaranList = rahkaran,
                userRole = userRole,
                context = context
            )
        }

        // Edit Server Dialog (Colleagues real-time update)
        if (serverToEdit != null) {
            EditServerDialog(
                server = serverToEdit!!,
                onDismiss = { serverToEdit = null },
                onSave = { updated ->
                    onUpdateServer?.invoke(updated, true)
                    serverToEdit = null
                    Toast.makeText(context, "Server updated & synced to Cloudflare D1", Toast.LENGTH_SHORT).show()
                }
            )
        }

        // Add Server Dialog
        if (showAddServerDialog) {
            AddServerDialog(
                sites = sites,
                onDismiss = { showAddServerDialog = false },
                onAdd = { newServer ->
                    onCreateServer?.invoke(newServer, true)
                    showAddServerDialog = false
                    Toast.makeText(context, "Server added & pushed to Cloudflare D1", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
}

@Composable
private fun ServersTabContent(
    servers: List<ServerEntity>,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    selectedSite: String,
    onSiteSelect: (String) -> Unit,
    context: Context,
    isPermittedToEdit: Boolean = false,
    onEditServer: ((ServerEntity) -> Unit)? = null
) {
    val tokens = LocalNetOpsColors.current

    // Extract unique sites
    val siteList = remember(servers) {
        listOf("All") + servers.map { it.site }.distinct().sorted()
    }

    val filteredServers = remember(servers, searchQuery, selectedSite) {
        servers.filter { s ->
            val matchesSite = (selectedSite == "All" || s.site.equals(selectedSite, ignoreCase = true))
            val matchesSearch = if (searchQuery.isBlank()) true else {
                s.name.contains(searchQuery, ignoreCase = true) ||
                s.ip.contains(searchQuery, ignoreCase = true) ||
                s.service.contains(searchQuery, ignoreCase = true) ||
                s.serverHost.contains(searchQuery, ignoreCase = true) ||
                s.site.contains(searchQuery, ignoreCase = true) ||
                s.portRdp.contains(searchQuery, ignoreCase = true)
            }
            matchesSite && matchesSearch
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Search & Filter header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            SearchInput(
                value = searchQuery,
                onValueChange = onSearchChange,
                placeholder = stringResource(R.string.search_servers_hint)
            )

            // Horizontal Sites Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(siteList) { site ->
                    GlassChip(
                        text = site,
                        selected = site == selectedSite,
                        onClick = { onSiteSelect(site) }
                    )
                }
            }
        }

        Text(
            text = "Total Matching: ${filteredServers.size} servers",
            style = MaterialTheme.typography.labelSmall.copy(
                color = tokens.textMuted,
                fontFamily = FontFamily.Monospace
            ),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp)
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 32.dp)
        ) {
            items(filteredServers, key = { it.id }) { server ->
                ServerItemCard(
                    server = server,
                    context = context,
                    isPermittedToEdit = isPermittedToEdit,
                    onEdit = if (onEditServer != null && isPermittedToEdit) { { onEditServer(server) } } else null
                )
            }
        }
    }
}

@Composable
private fun ServerItemCard(
    server: ServerEntity,
    context: Context,
    isPermittedToEdit: Boolean = false,
    onEdit: (() -> Unit)? = null
) {
    val tokens = LocalNetOpsColors.current
    val isOnline = server.status.equals("ok", ignoreCase = true)

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        contentPadding = PaddingValues(14.dp),
        testTag = "server_card_${server.name}"
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(if (isOnline) StatusSuccess else StatusError)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = server.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = tokens.textPrimary
                    )
                )
            }

            Surface(
                shape = RoundedCornerShape(6.dp),
                color = tokens.surface.copy(alpha = 0.8f),
                border = BorderStroke(0.5.dp, tokens.glassBorder)
            ) {
                Text(
                    text = server.site,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = tokens.primary,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // IP Address & RDP Port Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.SettingsEthernet,
                    contentDescription = null,
                    tint = tokens.accent,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "IP: ${server.ip}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = tokens.textPrimary,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (isPermittedToEdit && onEdit != null) {
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.size(28.dp).testTag("edit_server_${server.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Server",
                            tint = tokens.accent,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                }

                // Quick Copy IP Button
                if (server.ip.isNotBlank() && server.ip != "Needs Verification") {
                    IconButton(
                        onClick = {
                            val cb = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            cb.setPrimaryClip(ClipData.newPlainText("IP", server.ip))
                            Toast.makeText(context, "IP ${server.ip} copied", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy IP",
                            tint = tokens.primary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        // Service & Host Details
        if (server.service.isNotBlank()) {
            Text(
                text = "Service: ${server.service}",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = tokens.textSecondary
                )
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Host: ${server.serverHost}",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = tokens.textMuted,
                    fontFamily = FontFamily.Monospace
                )
            )
            Text(
                text = "RDP Port: ${server.portRdp}",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = tokens.accent,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

@Composable
private fun SitesAndVcenterTabContent(
    sites: List<SiteEntity>,
    vcenters: List<VcenterEntity>,
    servers: List<ServerEntity>
) {
    val tokens = LocalNetOpsColors.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        item {
            Text(
                text = "vCenter Infrastructure Management",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = tokens.primary
                )
            )
        }

        items(vcenters) { vc ->
            val hostCount = servers.count { it.site.equals(vc.site, ignoreCase = true) }
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, tokens.primary.copy(alpha = 0.5f))
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CloudQueue,
                        contentDescription = null,
                        tint = tokens.primary,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = vc.name,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = tokens.textPrimary
                            )
                        )
                        Text(
                            text = "IP: ${vc.ip} • Site: ${vc.site}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = tokens.textSecondary,
                                fontFamily = FontFamily.Monospace
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = vc.description,
                    style = MaterialTheme.typography.bodySmall.copy(color = tokens.textMuted)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Associated VMs: $hostCount systems",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = tokens.accent,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "All Operational Sites / Branches",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = tokens.accent
                )
            )
        }

        items(sites) { site ->
            val siteServers = servers.count { it.site.equals(site.name, ignoreCase = true) }
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = site.name,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = tokens.textPrimary
                            )
                        )
                        Text(
                            text = site.description,
                            style = MaterialTheme.typography.bodySmall.copy(color = tokens.textSecondary)
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = tokens.surface,
                        border = BorderStroke(0.5.dp, tokens.glassBorder)
                    ) {
                        Text(
                            text = "$siteServers servers",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = tokens.primary,
                                fontFamily = FontFamily.Monospace
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RahkaranTabContent(
    rahkaranList: List<RahkaranEntity>,
    userRole: UserRole,
    context: Context
) {
    val tokens = LocalNetOpsColors.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        item {
            Text(
                text = stringResource(R.string.rahkaran_title),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = tokens.primary
                )
            )
            Text(
                text = "Application & Web Endpoints (Authoritative Data from Link Rahkaran.pdf)",
                style = MaterialTheme.typography.bodySmall.copy(color = tokens.textSecondary)
            )
        }

        items(rahkaranList, key = { it.id }) { item ->
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                contentPadding = PaddingValues(16.dp),
                testTag = "rahkaran_card_${item.rowNo}"
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${item.rowNo}. ${item.nameFa}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = tokens.textPrimary
                        )
                    )

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = tokens.surface,
                        border = BorderStroke(0.5.dp, tokens.glassBorder)
                    ) {
                        Text(
                            text = "Port: ${item.portApp}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = tokens.accent,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Server IP: ${item.ip} • Web Port: ${item.portWeb}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = FontFamily.Monospace,
                        color = tokens.textSecondary
                    )
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Clickable Web URL
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(tokens.surface)
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.url,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = FontFamily.Monospace,
                            color = tokens.primary,
                            fontSize = 12.sp
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    Row {
                        // Copy URL
                        IconButton(
                            onClick = {
                                val cb = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                cb.setPrimaryClip(ClipData.newPlainText("URL", item.url))
                                Toast.makeText(context, "URL copied", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy URL", tint = tokens.textSecondary, modifier = Modifier.size(16.dp))
                        }

                        Spacer(modifier = Modifier.width(4.dp))

                        // Open Browser Link
                        IconButton(
                            onClick = {
                                try {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(item.url))
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Cannot open browser", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(Icons.Default.OpenInBrowser, contentDescription = "Open Link", tint = tokens.accent, modifier = Modifier.size(16.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Password Protection Notice (Strict least-privilege compliance)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = if (userRole == UserRole.ADMIN) tokens.primary else tokens.textMuted,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (userRole == UserRole.ADMIN) {
                            "Master Credential (Admin Access): Hamkaran Vault Configured"
                        } else {
                            stringResource(R.string.password_protected)
                        },
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (userRole == UserRole.ADMIN) tokens.primary else tokens.textMuted,
                            fontFamily = FontFamily.Monospace
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun EditServerDialog(
    server: ServerEntity,
    onDismiss: () -> Unit,
    onSave: (ServerEntity) -> Unit
) {
    val tokens = LocalNetOpsColors.current
    var name by remember { mutableStateOf(server.name) }
    var ip by remember { mutableStateOf(server.ip) }
    var service by remember { mutableStateOf(server.service) }
    var portRdp by remember { mutableStateOf(server.portRdp) }
    var status by remember { mutableStateOf(server.status) }
    var site by remember { mutableStateOf(server.site) }
    var os by remember { mutableStateOf(server.os) }
    var notes by remember { mutableStateOf(server.notes) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Dns, contentDescription = null, tint = tokens.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Edit Server: ${server.name}", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = ip,
                    onValueChange = { ip = it },
                    label = { Text("IP Address") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("edit_server_ip_input")
                )
                OutlinedTextField(
                    value = service,
                    onValueChange = { service = it },
                    label = { Text("Service / Role") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = portRdp,
                        onValueChange = { portRdp = it },
                        label = { Text("RDP Port") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = status,
                        onValueChange = { status = it },
                        label = { Text("Status (ok/warning/offline)") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = site,
                        onValueChange = { site = it },
                        label = { Text("Site") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = os,
                        onValueChange = { os = it },
                        label = { Text("Operating System") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes & Cloudflare Tag") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        server.copy(
                            name = name,
                            ip = ip,
                            service = service,
                            portRdp = portRdp,
                            status = status,
                            site = site,
                            os = os,
                            notes = notes
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = tokens.primary),
                modifier = Modifier.testTag("save_server_button")
            ) {
                Text("Save & Push to Cloudflare")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun AddServerDialog(
    sites: List<SiteEntity>,
    onDismiss: () -> Unit,
    onAdd: (ServerEntity) -> Unit
) {
    val tokens = LocalNetOpsColors.current
    var name by remember { mutableStateOf("") }
    var ip by remember { mutableStateOf("") }
    var service by remember { mutableStateOf("") }
    var portRdp by remember { mutableStateOf("3389") }
    var status by remember { mutableStateOf("ok") }
    var site by remember { mutableStateOf(sites.firstOrNull()?.name ?: "Central") }
    var os by remember { mutableStateOf("Windows Server 2022") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AddCircleOutline, contentDescription = null, tint = tokens.accent)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add New Server", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Server Name (e.g. SRV-APP-01)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("add_server_name_input")
                )
                OutlinedTextField(
                    value = ip,
                    onValueChange = { ip = it },
                    label = { Text("IP Address (e.g. 192.168.10.50)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("add_server_ip_input")
                )
                OutlinedTextField(
                    value = service,
                    onValueChange = { service = it },
                    label = { Text("Service Description") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = portRdp,
                        onValueChange = { portRdp = it },
                        label = { Text("RDP Port") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = site,
                        onValueChange = { site = it },
                        label = { Text("Site Location") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
                OutlinedTextField(
                    value = os,
                    onValueChange = { os = it },
                    label = { Text("OS (Windows / Linux)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && ip.isNotBlank()) {
                        val newServer = ServerEntity(
                            id = "srv_${System.currentTimeMillis()}",
                            serverNum = 900,
                            vcenter = "vCenter Central",
                            serverHost = "ESXi-Cluster",
                            name = name.trim(),
                            ip = ip.trim(),
                            service = service.trim(),
                            portRdp = portRdp.trim(),
                            status = status.trim(),
                            site = site.trim(),
                            os = os.trim(),
                            notes = notes.trim()
                        )
                        onAdd(newServer)
                    }
                },
                enabled = name.isNotBlank() && ip.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = tokens.accent),
                modifier = Modifier.testTag("submit_add_server_button")
            ) {
                Text("Add & Sync to Cloudflare")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
