package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.HelpdeskData
import com.example.data.local.HelpdeskIssue
import com.example.data.local.IssueSeverity
import com.example.data.local.TriageStep
import com.example.ui.components.CommandBox
import com.example.ui.components.GlassCard
import com.example.ui.components.GlassToolbar
import com.example.ui.components.HelpdeskVisualMapsSection
import com.example.ui.components.SearchInput
import com.example.ui.theme.*

@Composable
fun TroubleshootingHelpdeskScreen(
    currentLang: String = "en",
    onBack: () -> Unit
) {
    val tokens = LocalNetOpsColors.current
    val context = LocalContext.current
    val isPersian = currentLang == "fa"

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var selectedSeverity by remember { mutableStateOf<IssueSeverity?>(null) }
    var expandedIssueId by remember { mutableStateOf<String?>(null) }
    // v2.0.1: 0 = Visual Flow Maps (graphical), 1 = Runbook list (classic)
    var activeMode by remember { mutableIntStateOf(0) }

    val categories = remember { HelpdeskData.categories }
    val issues = remember { HelpdeskData.allIssues }

    val filteredIssues = issues.filter { issue ->
        val matchesCategory = selectedCategory == "All" || issue.category == selectedCategory
        val matchesSeverity = selectedSeverity == null || issue.severity == selectedSeverity
        val matchesSearch = searchQuery.isBlank() ||
                issue.title.contains(searchQuery, ignoreCase = true) ||
                issue.titleFa.contains(searchQuery, ignoreCase = true) ||
                issue.symptom.contains(searchQuery, ignoreCase = true) ||
                issue.symptomFa.contains(searchQuery, ignoreCase = true) ||
                issue.diagnosticCommand.contains(searchQuery, ignoreCase = true)
        matchesCategory && matchesSeverity && matchesSearch
    }

    Scaffold(
        topBar = {
            GlassToolbar(
                title = if (isPersian) "هلپ‌دسک و تریاژ عیب‌یابی NOC" else "NOC Troubleshooting Helpdesk",
                subtitle = if (isPersian) "عیب‌یابی، فلوچارت حل مسئله و رفع بحران" else "Incident Triage & Diagnostic Playbooks",
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("helpdesk_back_button")) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = tokens.textPrimary)
                    }
                }
            )
        },
        containerColor = Color.Transparent
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 32.dp)
        ) {
            // NOC Emergency Header Banner
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = tokens.glassSurface,
                    border = BorderStroke(1.dp, tokens.glassBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(tokens.error.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.MedicalServices,
                                        contentDescription = null,
                                        tint = tokens.error,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = if (isPersian) "مرکز مدیریت بحران و عیب‌یابی شبکه" else "NOC Incident Triage & Root Cause Center",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = tokens.textPrimary
                                        )
                                    )
                                    Text(
                                        text = if (isPersian) "دستورالعمل‌های حل فوری قطعی‌ها و خطاهای سازمانی" else "Step-by-step diagnostic workflows and verified resolution guides",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = tokens.textSecondary
                                        )
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Search Field
                        SearchInput(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = if (isPersian) "جستجوی علائم، ارورها یا دستورات عیب‌یابی..." else "Search symptoms, error codes, CLI commands...",
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // Severity & Category Filter Chips
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Category Chips
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(horizontal = 2.dp)
                    ) {
                        items(categories) { (catEn, catFa) ->
                            val isSelected = selectedCategory == catEn
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedCategory = catEn },
                                label = { Text(if (isPersian) catFa else catEn, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = tokens.primary,
                                    selectedLabelColor = tokens.background,
                                    containerColor = tokens.glassSurface,
                                    labelColor = tokens.textPrimary
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    borderColor = if (isSelected) tokens.primary else tokens.glassBorder,
                                    enabled = true,
                                    selected = isSelected
                                )
                            )
                        }
                    }

                    // Severity Quick Filter
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        IssueSeverity.values().forEach { sev ->
                            val isSelected = selectedSeverity == sev
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) sev.color.copy(alpha = 0.25f) else tokens.glassSurface,
                                border = BorderStroke(1.dp, if (isSelected) sev.color else tokens.glassBorder),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        selectedSeverity = if (selectedSeverity == sev) null else sev
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(sev.color)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isPersian) sev.labelFa else sev.label,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = if (isSelected) sev.color else tokens.textSecondary,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // v2.0.1 Mode Selector: Visual Flow Maps vs Classic Runbook List
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 2.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (activeMode == 0) tokens.primary else tokens.glassSurface,
                        border = BorderStroke(1.dp, if (activeMode == 0) tokens.primary else tokens.glassBorder),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { activeMode = 0 }
                            .testTag("helpdesk_mode_visual")
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 10.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.AccountTree,
                                contentDescription = null,
                                tint = if (activeMode == 0) tokens.background else tokens.textSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = if (isPersian) "نقشه‌های گرافیکی" else "Visual Flow Maps",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (activeMode == 0) tokens.background else tokens.textSecondary
                                )
                            )
                        }
                    }
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (activeMode == 1) tokens.primary else tokens.glassSurface,
                        border = BorderStroke(1.dp, if (activeMode == 1) tokens.primary else tokens.glassBorder),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { activeMode = 1 }
                            .testTag("helpdesk_mode_list")
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 10.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.FactCheck,
                                contentDescription = null,
                                tint = if (activeMode == 1) tokens.background else tokens.textSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = if (isPersian) "پرونده‌های تریاژ" else "Triage Runbooks",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (activeMode == 1) tokens.background else tokens.textSecondary
                                )
                            )
                        }
                    }
                }
            }

            // v2.0.1: graphical mode content
            if (activeMode == 0) {
                item {
                    HelpdeskVisualMapsSection(isPersian = isPersian)
                }
            }

            // Results Counter (classic list mode)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isPersian) "${filteredIssues.size} پرونده عیب‌یابی در دسترس" else "${filteredIssues.size} Diagnostic Runbooks Available",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = tokens.textMuted,
                            fontFamily = FontFamily.Monospace
                        )
                    )
                }
            }

            // Issues List (classic list mode only)
            items(filteredIssues.takeIf { activeMode == 1 } ?: emptyList(), key = { it.id }) { issue ->
                val isExpanded = expandedIssueId == issue.id

                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            expandedIssueId = if (isExpanded) null else issue.id
                        }
                        .testTag("helpdesk_issue_${issue.id}"),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        // Title + Severity Badge
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(issue.severity.color)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isPersian) issue.titleFa else issue.title,
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = tokens.textPrimary
                                    )
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = issue.severity.color.copy(alpha = 0.2f),
                                border = BorderStroke(1.dp, issue.severity.color.copy(alpha = 0.5f))
                            ) {
                                Text(
                                    text = if (isPersian) issue.severity.labelFa else issue.severity.label,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = issue.severity.color,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    ),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        // Symptom Box
                        Text(
                            text = if (isPersian) issue.symptomFa else issue.symptom,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = tokens.textSecondary,
                                lineHeight = 18.sp
                            )
                        )

                        // Expanded Diagnostic Details
                        AnimatedVisibility(visible = isExpanded) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Divider(color = tokens.glassBorder)

                                // Likely Root Causes
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(
                                        text = if (isPersian) "علل احتمالی بروز رخداد:" else "Likely Root Causes:",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = tokens.primary,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                    val causes = if (isPersian) issue.rootCausesFa else issue.rootCauses
                                    causes.forEach { cause ->
                                        Row(verticalAlignment = Alignment.Top) {
                                            Text("• ", color = tokens.primary, fontWeight = FontWeight.Bold)
                                            Text(cause, style = MaterialTheme.typography.bodySmall.copy(color = tokens.textSecondary, fontSize = 12.sp))
                                        }
                                    }
                                }

                                // Step-by-Step Triage
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(
                                        text = if (isPersian) "مراحل گام‌به‌گام عیب‌یابی (Triage Flow):" else "Triage Resolution Workflow:",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = tokens.accent,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                    issue.triageSteps.forEach { step ->
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = tokens.surface,
                                            border = BorderStroke(1.dp, tokens.glassBorder),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(8.dp),
                                                verticalAlignment = Alignment.Top
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(20.dp)
                                                        .clip(CircleShape)
                                                        .background(tokens.accent.copy(alpha = 0.2f)),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(
                                                        "${step.stepNum}",
                                                        color = tokens.accent,
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Column {
                                                    Text(
                                                        if (isPersian) step.titleFa else step.title,
                                                        style = MaterialTheme.typography.labelSmall.copy(
                                                            fontWeight = FontWeight.Bold,
                                                            color = tokens.textPrimary
                                                        )
                                                    )
                                                    Text(
                                                        if (isPersian) step.actionFa else step.action,
                                                        style = MaterialTheme.typography.bodySmall.copy(
                                                            color = tokens.textSecondary,
                                                            fontSize = 11.sp
                                                        )
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }

                                // Diagnostic Command with Copy
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(
                                        text = if (isPersian) "دستور تشخیصی ترمینال:" else "CLI Diagnostic Command:",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = TerminalGreen,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                    CommandBox(
                                        command = issue.diagnosticCommand,
                                        label = if (isPersian) "ترمینال تشخیص" else "Diagnostic CLI"
                                    )
                                }

                                // Verification Command
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(
                                        text = if (isPersian) "دستور اعتبارسنجی و تأیید رفع خطا:" else "Verification Command:",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = CyanAccent,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                    CommandBox(
                                        command = issue.verificationCommand,
                                        label = if (isPersian) "دستور اعتبارسنجی" else "Verification CLI"
                                    )
                                }

                                // Escalation Note
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = tokens.primary.copy(alpha = 0.1f),
                                    border = BorderStroke(1.dp, tokens.primary.copy(alpha = 0.3f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.Info, contentDescription = null, tint = tokens.primary, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = if (isPersian) issue.escalationNoteFa else issue.escalationNote,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = tokens.textPrimary,
                                                fontSize = 11.sp
                                            )
                                        )
                                    }
                                }
                            }
                        }

                        // Toggle Action Prompt
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isExpanded)
                                    (if (isPersian) "بستن جزئیات ▲" else "Collapse Details ▲")
                                else
                                    (if (isPersian) "مشاهده فلوچارت حل خطا و دستورات ▼" else "View Diagnostic Runbook ▼"),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = tokens.primary,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
