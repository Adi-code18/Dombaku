package com.app.dombaku.ui.theme.screens.pengurus

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.app.dombaku.data.model.User
import com.app.dombaku.ui.theme.*
import com.app.dombaku.ui.viewmodel.AuthUiState
import com.app.dombaku.ui.viewmodel.AuthViewModel

@Composable
fun AccountScreen(navController: NavController, authViewModel: AuthViewModel) {
    val uiState by authViewModel.uiState.collectAsState()
    val user = (uiState as? AuthUiState.Success)?.user
    
    var showEditDialog by remember { mutableStateOf(false) }

    if (showEditDialog && user != null) {
        EditProfileDialog(
            user = user,
            onDismiss = { showEditDialog = false },
            onSave = { updatedUser ->
                authViewModel.updateProfile(updatedUser)
                showEditDialog = false
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(20.dp))
        
        // Profile Header
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(GreenContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.Person, null, modifier = Modifier.size(60.dp), tint = GreenPrimary)
        }
        
        Spacer(Modifier.height(16.dp))
        
        Text(
            text = user?.nama ?: "Pengguna",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Text(
            text = "@${user?.username ?: "username"} • ${user?.role?.replaceFirstChar { it.uppercase() }}",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )
        
        Spacer(Modifier.height(32.dp))
        
        // Menu Options
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column {
                AccountMenuItem(
                    icon = Icons.Filled.Person,
                    label = "Edit Profil",
                    onClick = { showEditDialog = true }
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = DividerColor)
                AccountMenuItem(
                    icon = Icons.Filled.Logout,
                    label = "Keluar Akun",
                    color = Color.Red,
                    onClick = {
                        authViewModel.resetState()
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun EditProfileDialog(
    user: User,
    onDismiss: () -> Unit,
    onSave: (User) -> Unit
) {
    var nama by remember { mutableStateOf(user.nama) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Profil") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = nama,
                    onValueChange = { nama = it },
                    label = { Text("Nama") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(user.copy(nama = nama))
                },
                colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
            ) {
                Text("Simpan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}

@Composable
fun AccountMenuItem(
    icon: ImageVector,
    label: String,
    color: Color = TextPrimary,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, null, tint = if (color == Color.Red) color else GreenPrimary, modifier = Modifier.size(24.dp))
            Spacer(Modifier.width(16.dp))
            Text(label, modifier = Modifier.weight(1f), color = color, fontWeight = FontWeight.Medium)
            Icon(Icons.Filled.ChevronRight, null, tint = TextHint)
        }
    }
}
