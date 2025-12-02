package com.example.bienestarsalud.ui.screens.settings

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.bienestarsalud.ui.theme.PrimaryBlue
import com.example.bienestarsalud.ui.theme.PrimaryGreen

@Composable
fun SettingsScreen(
    onLogout: () -> Unit,
    vm: SettingsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var showEditNameDialog by remember { mutableStateOf(false) }

    val isDark by vm.isDarkMode.collectAsState()
    val areNotifsEnabled by vm.notificationsEnabled.collectAsState()

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(), // <--- CAMBIO AQUÍ
        onResult = { uri ->
            if (uri != null) {
                vm.uploadImage(uri)
            }
        }
    )
    LaunchedEffect(vm.message) {
        vm.message?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            vm.clearMessage()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            // CAMBIO 1: Fondo dinámico (Gris claro en día, Gris oscuro en noche)
            .background(MaterialTheme.colorScheme.background)
    ) {
        // HEADER (Se mantiene igual porque los degradados suelen verse bien en ambos)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(colors = listOf(PrimaryGreen, PrimaryBlue)))
                .padding(top = 48.dp, bottom = 60.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(contentAlignment = Alignment.Center) {
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .clickable {
                            photoPickerLauncher.launch("image/*") // <--- Solo pide imágenes
                        },
                        contentAlignment = Alignment.Center
                    ) {
                        if (vm.profilePicUrl != null) {
                            AsyncImage(
                                model = vm.profilePicUrl,
                                contentDescription = "Foto de perfil",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = Color.LightGray,
                                modifier = Modifier.size(50.dp)
                            )
                        }
                        if (vm.isUploading) {
                            CircularProgressIndicator(modifier = Modifier.size(30.dp), color = PrimaryGreen)
                        }
                    }
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .offset(x = 4.dp, y = 4.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .padding(6.dp)
                    ) {
                        Icon(Icons.Default.CameraAlt, "Cambiar foto", tint = PrimaryBlue, modifier = Modifier.size(16.dp))
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(vm.userName, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text(vm.userEmail, fontSize = 14.sp, color = Color.White.copy(alpha = 0.8f))
            }
        }

        // CONTENIDO SCROLLABLE
        Column(
            modifier = Modifier
                .fillMaxSize()
                .offset(y = (-30).dp)
                .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                // CAMBIO 2: El fondo curvo también debe ser dinámico
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            SettingsSection("Cuenta") {
                SettingsItem(Icons.Default.Edit, "Editar Nombre") { showEditNameDialog = true }
                SettingsItem(Icons.Default.LockReset, "Cambiar Contraseña") { vm.resetPassword() }
            }

            SettingsSection("Preferencias") {
                // Notificaciones
                Row(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // CAMBIO 3: Tint del icono dinámico (o gris neutro)
                        Icon(Icons.Default.Notifications, null, tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                        Spacer(Modifier.width(12.dp))
                        // CAMBIO 4: Color de texto dinámico (Negro en día, Blanco en noche)
                        Text("Notificaciones", color = MaterialTheme.colorScheme.onSurface)
                    }
                    Switch(
                        checked = areNotifsEnabled,
                        onCheckedChange = { vm.toggleNotifications(areNotifsEnabled) },
                        colors = SwitchDefaults.colors(checkedTrackColor = PrimaryGreen)
                    )
                }

                // Modo Oscuro
                Row(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.DarkMode, null, tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                        Spacer(Modifier.width(12.dp))
                        Text("Modo Oscuro", color = MaterialTheme.colorScheme.onSurface)
                    }
                    Switch(
                        checked = isDark,
                        onCheckedChange = { vm.toggleDarkMode(isDark) },
                        colors = SwitchDefaults.colors(checkedTrackColor = PrimaryBlue)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón Cerrar Sesión (Colores fijos rojos están bien para alertas)
            Button(
                onClick = {
                    vm.signOut()
                    onLogout()
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEF2F2), contentColor = Color(0xFFDC2626))
            ) {
                Icon(Icons.Default.Logout, null, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text("Cerrar Sesión", fontWeight = FontWeight.Bold)
            }

            Text(
                text = "Versión 1.0.0",
                fontSize = 12.sp,
                // CAMBIO 5: Texto de versión dinámico
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth().wrapContentWidth(Alignment.CenterHorizontally).padding(top = 16.dp)
            )
        }
    }

    if (showEditNameDialog) {
        var tempName by remember { mutableStateOf(vm.userName) }
        AlertDialog(
            onDismissRequest = { showEditNameDialog = false },
            title = { Text("Editar Nombre") },
            text = {
                OutlinedTextField(
                    value = tempName,
                    onValueChange = { tempName = it },
                    label = { Text("Nuevo nombre") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    // CAMBIO 6: Colores del input dinámicos
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                        focusedBorderColor = PrimaryGreen,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                )
            },
            confirmButton = { Button(onClick = { vm.updateName(tempName); showEditNameDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)) { Text("Guardar") } },
            dismissButton = { TextButton(onClick = { showEditNameDialog = false }) { Text("Cancelar") } },
            containerColor = MaterialTheme.colorScheme.surface // Fondo del diálogo
        )
    }
}

// --- COMPONENTES AUXILIARES ADAPTADOS ---

@Composable
fun SettingsSection(title: String, content: @Composable () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        // CAMBIO 7: Color de tarjeta dinámico (Blanco en día, Gris oscuro en noche)
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(12.dp),
                // CAMBIO 8: Texto de título dinámico
                color = MaterialTheme.colorScheme.onSurface
            )
            content()
        }
    }
}

@Composable
fun SettingsItem(icon: ImageVector, title: String, onClick: () -> Unit) {
    TextButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // CAMBIO 9: Icono dinámico
                Icon(icon, null, tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                Spacer(Modifier.width(12.dp))
                // CAMBIO 10: Texto dinámico
                Text(title, color = MaterialTheme.colorScheme.onSurface)
            }
            Icon(Icons.Default.ChevronRight, null, tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
        }
    }
}