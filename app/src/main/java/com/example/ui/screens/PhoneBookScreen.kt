package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.PhoneEntryEntity
import com.example.data.model.UserRole
import com.example.data.io.ExcelImporter
import com.example.ui.components.GlassCard
import com.example.ui.components.GlassToolbar
import com.example.ui.components.SearchInput
import com.example.ui.theme.LocalNetOpsColors

/**
 * Phone Book (دفترچه تلفن) - v2.0.1
 *
 * Fields: نام (First name) | نام خانوادگی (Last name) | شماره تماس (Phone)
 *         نام شرکت (Company) | شماره داخلی (Internal extension)
 * Creator/Admin can populate it either by importing an Excel/CSV file or by
 * adding contacts manually. Every other role has read-only access.
 */
@Composable
fun PhoneBookScreen(
    entries: List<PhoneEntryEntity>,
    userRole: UserRole,
    currentLang: String,
    onBack: () -> Unit,
    onCreateEntry: (PhoneEntryEntity) -> Unit,
    onUpdateEntry: (PhoneEntryEntity) -> Unit,
    onDeleteEntry: (PhoneEntryEntity) -> Unit,
    onImportEntries: (List<PhoneEntryEntity>, Boolean) -> Unit
) {
    val tokens = LocalNetOpsColors.current
    val context = LocalContext.current
    val isPersian = currentLang == "fa"

    var searchQuery by remember { mutableStateOf("") }
    var selectedCompany by remember { mutableStateOf("__all__") }
    var showEditDialog by remember { mutableStateOf(false) }
    var editingEntry by remember { mutableStateOf<PhoneEntryEntity?>(null) }
    var deleteCandidate by remember { mutableStateOf<PhoneEntryEntity?>(null) }
    var pendingImport by remember { mutableStateOf<List<PhoneEntryEntity>?>(null) }
    var pendingSkipped by remember { mutableStateOf(0) }

    val canManage = userRole == UserRole.CREATOR || userRole == UserRole.ADMIN
    val canEdit = userRole != UserRole.VIEWER

    // ------------------------- Excel / CSV Import -------------------------
    val filePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            try {
                val result = ExcelImporter.import(context, uri)
                if (result.entries.isEmpty()) {
                    Toast.makeText(
                        context,
                        if (isPersian) "هیچ مخاطب معتبری در فایل پیدا نشد" else "No valid contacts found in the file",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    pendingImport = result.entries
                    pendingSkipped = result.skipped
                }
            } catch (e: Exception) {
                Toast.makeText(
                    context,
                    (if (isPersian) "خطا در خواندن فایل: " else "Failed to read file: ") + e.message,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    // ------------------------- Filtering -------------------------
    val companies = remember(entries) {
        entries.map { it.company.trim() }.filter { it.isNotBlank() }.distinct().sorted()
    }
    val filtered = remember(entries, searchQuery, selectedCompany) {
        entries.filter { e ->
            val matchesCompany = selectedCompany == "__all__" || e.company.trim() == selectedCompany
            val q = searchQuery.trim()
            val matchesSearch = q.isBlank() ||
                e.firstName.contains(q, true) ||
                e.lastName.contains(q, true) ||
                e.phone.contains(q, true) ||
                e.company.contains(q, true) ||
                e.extension.contains(q, true)
            matchesCompany && matchesSearch
        }
    }

    // ------------------------- Dialogs -------------------------
    if (showEditDialog) {
        PhoneEntryFormDialog(
            initial = editingEntry,
            isPersian = isPersian,
            onDismiss = { showEditDialog = false; editingEntry = null },
            onSave = { entry ->
                if (editingEntry == null) onCreateEntry(entry) else onUpdateEntry(entry)
                showEditDialog = false
                editingEntry = null
            }
        )
    }

    if (deleteCandidate != null) {
        AlertDialog(
            onDismissRequest = { deleteCandidate = null },
            title = { Text(if (isPersian) "حذف مخاطب" else "Delete Contact", color = tokens.error, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "${deleteCandidate!!.firstName} ${deleteCandidate!!.lastName}" +
                        if (isPersian) " از دفترچه تلفن حذف شود؟" else " will be removed from the phone book.",
                    color = tokens.textPrimary
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    onDeleteEntry(deleteCandidate!!)
                    deleteCandidate = null
                }) { Text(if (isPersian) "حذف" else "Delete", color = tokens.error, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { deleteCandidate = null }) { Text(if (isPersian) "انصراف" else "Cancel", color = tokens.textMuted) }
            },
            containerColor = tokens.surface
        )
    }

    if (pendingImport != null) {
        AlertDialog(
            onDismissRequest = { pendingImport = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.UploadFile, contentDescription = null, tint = tokens.primary)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        if (isPersian) "تأیید ورود اطلاعات" else "Confirm Import",
                        fontWeight = FontWeight.Bold,
                        color = tokens.textPrimary
                    )
                }
            },
            text = {
                Column {
                    Text(
                        (if (isPersian) "تعداد " else "") + "${pendingImport!!.size}" +
                            (if (isPersian) " مخاطب آماده ورود به دفترچه تلفن است." else " contacts are ready to import."),
                        color = tokens.textPrimary
                    )
                    if (pendingSkipped > 0) {
                        Spacer(Modifier.height(4.dp))
                        Text(
                            (if (isPersian) "" else "") + "$pendingSkipped " +
                                (if (isPersian) "ردیف بدون نام نادیده گرفته شد." else "rows without a name were skipped."),
                            color = tokens.textMuted,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Spacer(Modifier.height(10.dp))
                    Text(
                        if (isPersian) "روش ورود را انتخاب کنید:" else "Choose import mode:",
                        style = MaterialTheme.typography.labelMedium.copy(color = tokens.primary, fontWeight = FontWeight.Bold)
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        if (isPersian) "• افزودن: مخاطبین جدید به لیست فعلی اضافه می‌شوند."
                        else "• Append: new contacts are added to the existing list.",
                        style = MaterialTheme.typography.bodySmall, color = tokens.textSecondary
                    )
                    Text(
                        if (isPersian) "• جایگزینی: کل دفترچه تلفن با فایل جدید بازنویسی می‌شود."
                        else "• Replace: the whole phone book is overwritten by the file.",
                        style = MaterialTheme.typography.bodySmall, color = tokens.textSecondary
                    )
                }
            },
            confirmButton = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            onImportEntries(pendingImport!!, false)
                            pendingImport = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = tokens.primary)
                    ) { Text(if (isPersian) "افزودن به لیست" else "Append") }
                    OutlinedButton(
                        onClick = {
                            onImportEntries(pendingImport!!, true)
                            pendingImport = null
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = tokens.error)
                    ) { Text(if (isPersian) "جایگزینی کامل" else "Replace all") }
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingImport = null }) { Text(if (isPersian) "انصراف" else "Cancel", color = tokens.textMuted) }
            },
            containerColor = tokens.surface
        )
    }

    // ------------------------- Screen -------------------------
    Scaffold(
        containerColor = Color.Transparent,
        floatingActionButton = {
            if (canManage) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Excel import FAB
                    ExtendedFloatingActionButton(
                        onClick = {
                            filePicker.launch("*/*")
                        },
                        containerColor = tokens.surface,
                        contentColor = tokens.primary,
                        icon = { Icon(Icons.Default.UploadFile, contentDescription = "Import Excel") },
                        text = { Text(if (isPersian) "اکسل" else "Excel", fontWeight = FontWeight.Bold) },
                        modifier = Modifier.testTag("phonebook_import_fab")
                    )
                    // Manual add FAB
                    ExtendedFloatingActionButton(
                        onClick = { editingEntry = null; showEditDialog = true },
                        containerColor = tokens.primary,
                        contentColor = tokens.background,
                        icon = { Icon(Icons.Default.PersonAdd, contentDescription = "Add Contact") },
                        text = { Text(if (isPersian) "مخاطب جدید" else "Add", fontWeight = FontWeight.Bold) },
                        modifier = Modifier.testTag("phonebook_add_fab")
                    )
                }
            } else if (canEdit) {
                FloatingActionButton(
                    onClick = { editingEntry = null; showEditDialog = true },
                    containerColor = tokens.primary,
                    contentColor = tokens.background,
                    modifier = Modifier.testTag("phonebook_add_fab")
                ) { Icon(Icons.Default.PersonAdd, contentDescription = "Add Contact") }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            GlassToolbar(
                title = if (isPersian) "دفترچه تلفن سازمانی" else "Corporate Phone Book",
                subtitle = if (isPersian)
                    "نام و نام خانوادگی • شماره تماس • شرکت • شماره داخلی"
                else "Name • Phone • Company • Internal Extension",
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("phonebook_back_button")) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = tokens.textPrimary)
                    }
                },
                actions = {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = tokens.glassSurface,
                        border = BorderStroke(1.dp, tokens.glassBorder)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.Contacts, contentDescription = null, tint = tokens.accent, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(
                                "${entries.size}",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    color = tokens.textPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            )
                        }
                    }
                }
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SearchInput(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = if (isPersian) "جستجوی نام، شماره، شرکت یا داخلی..."
                    else "Search name, phone, company or extension...",
                    modifier = Modifier.fillMaxWidth()
                )

                if (companies.isNotEmpty()) {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        item {
                            CompanyChip(
                                label = if (isPersian) "همه شرکت‌ها" else "All Companies",
                                selected = selectedCompany == "__all__",
                                isPersian = isPersian,
                                onClick = { selectedCompany = "__all__" }
                            )
                        }
                        items(companies) { company ->
                            CompanyChip(
                                label = company,
                                selected = selectedCompany == company,
                                isPersian = isPersian,
                                onClick = { selectedCompany = if (selectedCompany == company) "__all__" else company }
                            )
                        }
                    }
                }
            }

            if (filtered.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 60.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.ContactPhone,
                            contentDescription = null,
                            tint = tokens.textMuted,
                            modifier = Modifier.size(52.dp)
                        )
                        Spacer(Modifier.height(10.dp))
                        Text(
                            if (isPersian) "مخاطبی یافت نشد" else "No contacts found",
                            color = tokens.textMuted,
                            style = MaterialTheme.typography.titleSmall
                        )
                        if (canManage) {
                            Spacer(Modifier.height(4.dp))
                            Text(
                                if (isPersian)
                                    "با دکمه‌های پایین فایل اکسل را وارد کنید یا مخاطب دستی اضافه کنید"
                                else
                                    "Use the buttons below to import an Excel file or add a contact manually",
                                color = tokens.textMuted,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 96.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filtered, key = { it.id }) { entry ->
                        PhoneEntryCard(
                            entry = entry,
                            isPersian = isPersian,
                            canManage = canManage,
                            onEdit = { editingEntry = entry; showEditDialog = true },
                            onDelete = { deleteCandidate = entry }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CompanyChip(label: String, selected: Boolean, isPersian: Boolean, onClick: () -> Unit) {
    val tokens = LocalNetOpsColors.current
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label, fontSize = 12.sp) },
        leadingIcon = if (selected) {
            { Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(14.dp)) }
        } else null,
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = tokens.primary,
            selectedLabelColor = tokens.background,
            containerColor = tokens.glassSurface,
            labelColor = tokens.textPrimary
        )
    )
}

@Composable
private fun PhoneEntryCard(
    entry: PhoneEntryEntity,
    isPersian: Boolean,
    canManage: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val tokens = LocalNetOpsColors.current
    val context = LocalContext.current
    val initials = remember(entry) {
        val f = entry.firstName.trim().firstOrNull()?.toString() ?: ""
        val l = entry.lastName.trim().firstOrNull()?.toString() ?: ""
        (f + l).uppercase().ifBlank { "?" }
    }

    GlassCard(modifier = Modifier.fillMaxWidth(), testTag = "phone_entry_card") {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Avatar with initials
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(tokens.primary.copy(alpha = 0.45f), tokens.accent.copy(alpha = 0.3f))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    initials,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = tokens.textPrimary
                    )
                )
            }
            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "${entry.firstName} ${entry.lastName}".trim(),
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = tokens.textPrimary
                    )
                )
                if (entry.company.isNotBlank()) {
                    Spacer(Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Business, contentDescription = null, tint = tokens.textMuted, modifier = Modifier.size(13.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(
                            entry.company,
                            style = MaterialTheme.typography.bodySmall.copy(color = tokens.textSecondary)
                        )
                    }
                }
                if (entry.phone.isNotBlank()) {
                    Spacer(Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Call, contentDescription = null, tint = tokens.textMuted, modifier = Modifier.size(13.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(
                            entry.phone,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = tokens.textSecondary,
                                fontFamily = FontFamily.Monospace
                            )
                        )
                    }
                }
            }

            // Extension badge
            if (entry.extension.isNotBlank()) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = tokens.primary.copy(alpha = 0.18f),
                    border = BorderStroke(1.dp, tokens.primary.copy(alpha = 0.5f))
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            if (isPersian) "داخلی" else "EXT",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = tokens.primary, fontSize = 9.sp, fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            entry.extension,
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = tokens.textPrimary,
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = FontFamily.Monospace
                            )
                        )
                    }
                }
            }

            Spacer(Modifier.width(6.dp))

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Call action
                if (entry.phone.isNotBlank()) {
                    IconButton(
                        onClick = {
                            try {
                                context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:${entry.phone}")))
                            } catch (_: Exception) {}
                        },
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(Icons.Default.PhoneInTalk, contentDescription = "Call", tint = com.example.ui.theme.TerminalGreen, modifier = Modifier.size(19.dp))
                    }
                }
                if (canManage) {
                    Row {
                        IconButton(onClick = onEdit, modifier = Modifier.size(30.dp)) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = tokens.textMuted, modifier = Modifier.size(16.dp))
                        }
                        IconButton(onClick = onDelete, modifier = Modifier.size(30.dp)) {
                            Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = tokens.error.copy(alpha = 0.75f), modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PhoneEntryFormDialog(
    initial: PhoneEntryEntity?,
    isPersian: Boolean,
    onDismiss: () -> Unit,
    onSave: (PhoneEntryEntity) -> Unit
) {
    val tokens = LocalNetOpsColors.current
    var firstName by remember { mutableStateOf(initial?.firstName ?: "") }
    var lastName by remember { mutableStateOf(initial?.lastName ?: "") }
    var phone by remember { mutableStateOf(initial?.phone ?: "") }
    var company by remember { mutableStateOf(initial?.company ?: "") }
    var extension by remember { mutableStateOf(initial?.extension ?: "") }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.ContactPhone, contentDescription = null, tint = tokens.primary)
                Spacer(Modifier.width(8.dp))
                Text(
                    if (initial == null)
                        (if (isPersian) "افزودن مخاطب جدید" else "Add New Contact")
                    else
                        (if (isPersian) "ویرایش مخاطب" else "Edit Contact"),
                    fontWeight = FontWeight.Bold,
                    color = tokens.textPrimary
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = firstName,
                    onValueChange = { firstName = it; errorMsg = null },
                    label = { Text(if (isPersian) "نام *" else "First Name *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    label = { Text(if (isPersian) "نام خانوادگی" else "Last Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text(if (isPersian) "شماره تماس" else "Phone Number") },
                    singleLine = true,
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = company,
                    onValueChange = { company = it },
                    label = { Text(if (isPersian) "نام شرکت" else "Company Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = extension,
                    onValueChange = { extension = it },
                    label = { Text(if (isPersian) "شماره داخلی" else "Internal Extension") },
                    singleLine = true,
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth()
                )
                if (errorMsg != null) {
                    Text(errorMsg!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (firstName.isBlank()) {
                        errorMsg = if (isPersian) "نام الزامی است" else "First name is required"
                        return@Button
                    }
                    onSave(
                        PhoneEntryEntity(
                            id = initial?.id ?: "ph_${System.currentTimeMillis()}_m",
                            firstName = firstName.trim(),
                            lastName = lastName.trim(),
                            phone = phone.trim(),
                            company = company.trim(),
                            extension = extension.trim(),
                            notes = initial?.notes ?: ""
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = tokens.primary)
            ) {
                Text(
                    if (initial == null) (if (isPersian) "افزودن" else "Add")
                    else (if (isPersian) "ذخیره" else "Save"),
                    color = tokens.background,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(if (isPersian) "انصراف" else "Cancel", color = tokens.textMuted) }
        },
        containerColor = tokens.surface
    )
}
