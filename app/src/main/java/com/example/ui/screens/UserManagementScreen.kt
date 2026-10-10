package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.UserEntity
import com.example.data.model.UserRole
import com.example.ui.components.GlassCard
import com.example.ui.components.GlassToolbar
import com.example.ui.theme.LocalNetOpsColors
import com.example.ui.theme.StatusError
import com.example.ui.theme.StatusSuccess

@Composable
fun UserManagementScreen(
    users: List<UserEntity>,
    currentRole: UserRole,
    onCreateUser: (UserEntity) -> Unit,
    onUpdateRole: (String, String) -> Unit,
    onUpdateStatus: (String, Boolean) -> Unit,
    onUpdatePassword: ((String, String, Boolean) -> Unit)? = null,
    onDeleteUser: (UserEntity) -> Unit,
    onBack: () -> Unit
) {
    val tokens = LocalNetOpsColors.current
    val context = LocalContext.current
    var showCreateDialog by remember { mutableStateOf(false) }
    var selectedUserForRole by remember { mutableStateOf<UserEntity?>(null) }
    var selectedUserForPassword by remember { mutableStateOf<UserEntity?>(null) }
    var selectedUserForDelete by remember { mutableStateOf<UserEntity?>(null) }

    val isCreator = currentRole == UserRole.CREATOR
    val isAdmin = currentRole == UserRole.ADMIN || isCreator

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        GlassToolbar(
            title = stringResource(R.string.user_management),
            subtitle = "Active Role: ${currentRole.display} • Access Control (RBAC)",
            navigationIcon = {
                IconButton(onClick = onBack, modifier = Modifier.testTag("user_mgmt_back_button")) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = tokens.textPrimary)
                }
            },
            actions = {
                if (isAdmin) {
                    IconButton(
                        onClick = { showCreateDialog = true },
                        modifier = Modifier.testTag("open_create_user_button")
                    ) {
                        Icon(Icons.Default.PersonAdd, contentDescription = "Create User", tint = tokens.primary)
                    }
                }
            }
        )

        // Banner explaining Creator vs Admin privileges
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = tokens.glassSurface,
            border = BorderStroke(1.dp, tokens.glassBorder),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = if (isCreator) tokens.primary else tokens.accent,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = if (isCreator) "Full Creator System Control" else "Administrative Governance",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = tokens.textPrimary)
                    )
                    Text(
                        text = if (isCreator)
                            "You have unrestricted access to create, assign Creator role, activate/deactivate, and manage all users."
                        else
                            "Admins can create and manage Admin, Editor, and Viewer roles. Both Creator and Admin have full colleague user creation authority.",
                        style = MaterialTheme.typography.bodySmall.copy(color = tokens.textSecondary, fontSize = 11.sp)
                    )
                    if (isAdmin) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = { showCreateDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = if (isCreator) tokens.primary else tokens.accent),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("banner_create_user_button")
                        ) {
                            Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Create Colleague Account",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = tokens.background
                            )
                        }
                    }
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            items(users, key = { it.id }) { user ->
                UserCard(
                    user = user,
                    currentRole = currentRole,
                    onChangeRole = { selectedUserForRole = user },
                    onToggleStatus = { onUpdateStatus(user.id, !user.isActive) },
                    onResetPassword = { selectedUserForPassword = user },
                    onDelete = { selectedUserForDelete = user }
                )
            }
        }
    }

    // 1. Create User Dialog
    if (showCreateDialog) {
        CreateUserDialog(
            isCreator = isCreator,
            onDismiss = { showCreateDialog = false },
            onCreate = { newUser ->
                onCreateUser(newUser)
                showCreateDialog = false
                Toast.makeText(context, "User ${newUser.username} created", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // 2. Change Role Dialog
    if (selectedUserForRole != null) {
        ChangeRoleDialog(
            user = selectedUserForRole!!,
            isCreator = isCreator,
            onDismiss = { selectedUserForRole = null },
            onRoleSelected = { newRole ->
                onUpdateRole(selectedUserForRole!!.id, newRole)
                selectedUserForRole = null
                Toast.makeText(context, "Role updated to $newRole", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // 3. Reset Password Dialog
    if (selectedUserForPassword != null) {
        ResetPasswordDialog(
            user = selectedUserForPassword!!,
            onDismiss = { selectedUserForPassword = null },
            onResetSuccess = { newPass ->
                onUpdatePassword?.invoke(selectedUserForPassword!!.id, newPass, true)
                selectedUserForPassword = null
                Toast.makeText(context, "رمز عبور تغییر کرد (ورود با الزام تغییر رمز عبور در اولین ورود)", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // 4. Delete Confirmation Dialog
    if (selectedUserForDelete != null) {
        AlertDialog(
            onDismissRequest = { selectedUserForDelete = null },
            title = { Text("Confirm User Deletion", color = tokens.textPrimary) },
            text = { Text("Are you sure you want to delete user ${selectedUserForDelete!!.username}? This operation cannot be undone.", color = tokens.textSecondary) },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteUser(selectedUserForDelete!!)
                        selectedUserForDelete = null
                        Toast.makeText(context, "User deleted", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = tokens.error)
                ) {
                    Text("Delete", color = tokens.background)
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedUserForDelete = null }) {
                    Text("Cancel", color = tokens.textSecondary)
                }
            },
            containerColor = tokens.surface
        )
    }
}

@Composable
private fun UserCard(
    user: UserEntity,
    currentRole: UserRole,
    onChangeRole: () -> Unit,
    onToggleStatus: () -> Unit,
    onResetPassword: () -> Unit,
    onDelete: () -> Unit
) {
    val tokens = LocalNetOpsColors.current
    val isCreator = currentRole == UserRole.CREATOR
    val isAdmin = currentRole == UserRole.ADMIN || isCreator

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = user.displayName.ifBlank { user.username },
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = tokens.textPrimary
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = when (user.role) {
                            "Creator" -> tokens.primary.copy(alpha = 0.25f)
                            "Admin" -> tokens.accent.copy(alpha = 0.25f)
                            "Editor" -> tokens.secondary.copy(alpha = 0.25f)
                            else -> tokens.surface
                        },
                        border = BorderStroke(
                            1.dp,
                            when (user.role) {
                                "Creator" -> tokens.primary
                                "Admin" -> tokens.accent
                                "Editor" -> tokens.secondary
                                else -> tokens.glassBorder
                            }
                        )
                    ) {
                        Text(
                            text = user.role.uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                color = when (user.role) {
                                    "Creator" -> tokens.primary
                                    "Admin" -> tokens.accent
                                    "Editor" -> tokens.secondary
                                    else -> tokens.textSecondary
                                }
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "@${user.username} • ${user.email}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = FontFamily.Monospace,
                        color = tokens.textMuted
                    )
                )
            }

            // Status indicator
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = if (user.isActive) StatusSuccess.copy(alpha = 0.15f) else StatusError.copy(alpha = 0.15f),
                border = BorderStroke(1.dp, if (user.isActive) StatusSuccess else StatusError)
            ) {
                Text(
                    text = if (user.isActive) "ACTIVE" else "INACTIVE",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (user.isActive) StatusSuccess else StatusError
                    ),
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }

        if (isAdmin) {
            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = tokens.glassBorder.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(6.dp))

            // Action Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Change Role
                TextButton(onClick = onChangeRole) {
                    Icon(Icons.Default.ManageAccounts, contentDescription = null, tint = tokens.primary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Role", color = tokens.primary, fontSize = 12.sp)
                }

                // Activate / Deactivate Toggle
                TextButton(onClick = onToggleStatus) {
                    Icon(
                        imageVector = if (user.isActive) Icons.Default.Block else Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = if (user.isActive) tokens.warning else StatusSuccess,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (user.isActive) "Suspend" else "Activate", color = if (user.isActive) tokens.warning else StatusSuccess, fontSize = 12.sp)
                }

                // Reset Password
                TextButton(onClick = onResetPassword) {
                    Icon(Icons.Default.Key, contentDescription = null, tint = tokens.accent, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Password", color = tokens.accent, fontSize = 12.sp)
                }

                // Delete (Creator or Admin)
                if (isCreator && user.role != "Creator") {
                    IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = tokens.error, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun CreateUserDialog(
    isCreator: Boolean,
    onDismiss: () -> Unit,
    onCreate: (UserEntity) -> Unit
) {
    val tokens = LocalNetOpsColors.current
    var username by remember { mutableStateOf("") }
    var displayName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf(if (isCreator) "Admin" else "Viewer") }
    var isActive by remember { mutableStateOf(true) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    val availableRoles = if (isCreator) {
        listOf("Creator", "Admin", "Editor", "Viewer")
    } else {
        listOf("Admin", "Editor", "Viewer") // Admin cannot create Creator!
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.create_user), color = tokens.textPrimary, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text(stringResource(R.string.username)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("new_user_username")
                )
                OutlinedTextField(
                    value = displayName,
                    onValueChange = { displayName = it },
                    label = { Text(stringResource(R.string.display_name)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email (Optional)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text(stringResource(R.string.password)) },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("new_user_password")
                )
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text(stringResource(R.string.confirm_password)) },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(stringResource(R.string.user_role), style = MaterialTheme.typography.labelMedium.copy(color = tokens.textSecondary))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    availableRoles.forEach { role ->
                        FilterChip(
                            selected = selectedRole == role,
                            onClick = { selectedRole = role },
                            label = { Text(role) }
                        )
                    }
                }

                if (errorMsg != null) {
                    Text(errorMsg!!, color = tokens.error, style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (username.isBlank() || password.isBlank()) {
                        errorMsg = "Username and password required"
                        return@Button
                    }
                    if (password != confirmPassword) {
                        errorMsg = "Passwords do not match"
                        return@Button
                    }
                    val userEmail = if (email.isBlank()) "$username@netopshub.internal" else email
                    val userDisp = if (displayName.isBlank()) username else displayName

                    onCreate(
                        UserEntity(
                            id = "u_${username.lowercase()}_${System.currentTimeMillis()}",
                            username = username.trim().lowercase(),
                            displayName = userDisp,
                            email = userEmail,
                            role = selectedRole,
                            isActive = isActive,
                            // v2.0.1: plaintext travels once via TLS (plainPassword),
                            // Room stores the PBKDF2 hash only.
                            plainPassword = password.trim(),
                            mustChangePassword = true,
                            createdAt = System.currentTimeMillis()
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = tokens.primary),
                modifier = Modifier.testTag("submit_create_user_button")
            ) {
                Text("Create User", color = tokens.background, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel), color = tokens.textSecondary)
            }
        },
        containerColor = tokens.surface
    )
}

@Composable
private fun ChangeRoleDialog(
    user: UserEntity,
    isCreator: Boolean,
    onDismiss: () -> Unit,
    onRoleSelected: (String) -> Unit
) {
    val tokens = LocalNetOpsColors.current
    val availableRoles = if (isCreator) {
        listOf("Creator", "Admin", "Editor", "Viewer")
    } else {
        listOf("Admin", "Editor", "Viewer") // Admin cannot assign Creator!
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Change Role for @${user.username}", color = tokens.textPrimary) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Select new role (Current: ${user.role}):", color = tokens.textSecondary)
                availableRoles.forEach { role ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = user.role == role,
                            onClick = { onRoleSelected(role) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(role, color = tokens.textPrimary, fontWeight = FontWeight.Bold)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Close", color = tokens.primary) }
        },
        containerColor = tokens.surface
    )
}

@Composable
private fun ResetPasswordDialog(
    user: UserEntity,
    onDismiss: () -> Unit,
    onResetSuccess: (String) -> Unit
) {
    val tokens = LocalNetOpsColors.current
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Reset Password for @${user.username}", color = tokens.textPrimary) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text("New Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirm New Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                if (error != null) {
                    Text(error!!, color = tokens.error, style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (newPassword.isBlank() || newPassword != confirmPassword) {
                        error = "Passwords must match and cannot be empty"
                        return@Button
                    }
                    onResetSuccess(newPassword)
                },
                colors = ButtonDefaults.buttonColors(containerColor = tokens.primary)
            ) {
                Text("Set Password", color = tokens.background)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = tokens.textSecondary) }
        },
        containerColor = tokens.surface
    )
}
