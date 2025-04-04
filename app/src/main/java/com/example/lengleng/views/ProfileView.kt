package com.example.lengleng.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.lengleng.models.User
import com.example.lengleng.services.UserService
import java.util.*

@Composable
fun ProfileView(navController: NavController) {
    var user by remember { mutableStateOf<User?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var showEditProfile by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }
    val userService = UserService()

    LaunchedEffect(Unit) {
        isLoading = true
        user = userService.getCurrentUser()
        isLoading = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(48.dp)
                    .align(Alignment.CenterHorizontally)
            )
        } else if (user != null) {
            ProfileHeader(user = user!!)
            Spacer(modifier = Modifier.height(16.dp))
            ProfileStats(user = user!!)
            Spacer(modifier = Modifier.height(16.dp))
            ProfileActions(
                onEditProfile = { showEditProfile = true },
                onSettings = { showSettings = true },
                onLogout = {
                    userService.signOut()
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        } else {
            Text(
                text = "User not found",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    if (showEditProfile && user != null) {
        EditProfileDialog(
            user = user!!,
            onDismiss = { showEditProfile = false },
            onSave = { username, bio ->
                val updatedUser = user!!.copy(
                    username = username,
                    bio = bio
                )
                userService.updateUser(updatedUser)
                showEditProfile = false
            }
        )
    }

    if (showSettings) {
        SettingsDialog(
            onDismiss = { showSettings = false }
        )
    }
}

@Composable
fun ProfileHeader(user: User) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        // TODO: Add profile picture
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(100.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = user.username,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = MaterialTheme.typography.headlineMedium.fontWeight
        )

        Text(
            text = user.email,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        user.bio?.let { bio ->
            Text(
                text = bio,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
fun ProfileStats(user: User) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        StatItem(count = user.connections.size, label = "Friends")
        StatItem(count = user.polls.size, label = "Polls")
    }
}

@Composable
fun StatItem(count: Int, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = MaterialTheme.typography.titleLarge.fontWeight
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun ProfileActions(
    onEditProfile: () -> Unit,
    onSettings: () -> Unit,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Button(
            onClick = onEditProfile,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Edit, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Edit Profile")
        }

        Button(
            onClick = onSettings,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        ) {
            Icon(Icons.Default.Settings, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Settings")
        }

        Button(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = MaterialTheme.colorScheme.onError
            )
        ) {
            Icon(Icons.Default.Logout, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Logout")
        }
    }
}

@Composable
fun EditProfileDialog(
    user: User,
    onDismiss: () -> Unit,
    onSave: (String, String?) -> Unit
) {
    var username by remember { mutableStateOf(user.username) }
    var bio by remember { mutableStateOf(user.bio ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Profile") },
        text = {
            Column {
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )

                OutlinedTextField(
                    value = bio,
                    onValueChange = { bio = it },
                    label = { Text("Bio") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onSave(username, bio.takeIf { it.isNotBlank() }) },
                enabled = username.isNotBlank()
            ) {
                Text("Save")
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
fun SettingsDialog(
    onDismiss: () -> Unit
) {
    var notificationsEnabled by remember { mutableStateOf(true) }
    var darkModeEnabled by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Settings") },
        text = {
            Column {
                Text(
                    text = "Preferences",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Enable Notifications")
                    Switch(
                        checked = notificationsEnabled,
                        onCheckedChange = { notificationsEnabled = it }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Dark Mode")
                    Switch(
                        checked = darkModeEnabled,
                        onCheckedChange = { darkModeEnabled = it }
                    )
                }

                Text(
                    text = "About",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Version")
                    Text("1.0.0", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Done")
            }
        }
    )
} 