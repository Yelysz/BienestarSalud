package com.example.bienestarsalud.presentation.screens.auth.login

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel // <-- ¡IMPORTANTE!
import com.example.bienestarsalud.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    vm: LoginViewModel = hiltViewModel(), // <-- 1. Inyecta el ViewModel
    onNavigateToRegister: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    // 2. Lee el estado (UI State) del ViewModel
    val ui by vm.ui.collectAsState()
    var passwordVisible by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(false) } // Este se puede quedar local

    // 3. Navega SÓLO si el VM dice que el login fue exitoso
    if (ui.loggedIn) {
        LaunchedEffect(Unit) {
            onNavigateToHome()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFE8F5E9),
                        Color.White
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            // Logo (sin cambios)
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(PrimaryGreen, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Títulos (sin cambios)
            Text(
                text = "¡Bienvenido!",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            // ...

            Spacer(modifier = Modifier.height(40.dp))

            // 4. Conecta los campos de texto al ViewModel
            OutlinedTextField(
                value = ui.email,
                onValueChange = vm::onEmailChange, // <-- Conectado
                label = { Text("Correo Electrónico") },
                placeholder = { Text("tu@email.com") },
                leadingIcon = {
                    Icon(Icons.Default.Email, contentDescription = null)
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = MaterialTheme.shapes.large
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = ui.password,
                onValueChange = vm::onPasswordChange, // <-- Conectado
                label = { Text("Contraseña") },
                placeholder = { Text("••••••••") },
                leadingIcon = {
                    Icon(Icons.Default.Lock, contentDescription = null)
                },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible)
                                Icons.Default.Visibility
                            else
                                Icons.Default.VisibilityOff,
                            contentDescription = null
                        )
                    }
                },
                visualTransformation = if (passwordVisible)
                    VisualTransformation.None
                else
                    PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = MaterialTheme.shapes.large
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Remember Me & Forgot Password
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = rememberMe,
                        onCheckedChange = { rememberMe = it }
                    )
                    Text(
                        text = "Recordarme",
                        fontSize = 14.sp,
                        color = TextSecondary
                    )
                }

                // 5. Conecta "Forgot Password" al ViewModel
                Text(
                    text = "¿Olvidaste tu contraseña?",
                    fontSize = 14.sp,
                    color = PrimaryGreen,
                    modifier = Modifier.clickable { vm.sendReset() } // <-- Conectado
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 6. Muestra el error si existe
            if (ui.error != null) {
                Text(
                    text = ui.error!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            // 7. Conecta el botón al ViewModel
            Button(
                onClick = vm::signIn, // <-- ¡CORREGIDO!
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                // Se deshabilita si está cargando
                enabled = !ui.loading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryGreen
                ),
                shape = MaterialTheme.shapes.large,
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 4.dp
                )
            ) {
                // Muestra un spinner si está cargando
                if (ui.loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White
                    )
                } else {
                    Text(
                        text = "Iniciar Sesión",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Divider (sin cambios)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Divider(modifier = Modifier.weight(1f))
                Text(
                    text = "  O continúa con  ",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                Divider(modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Social Login Buttons (sin cambios)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(
                    onClick = { /* TODO: Google Sign In */ },
                    modifier = Modifier.weight(1f),
                    shape = MaterialTheme.shapes.large
                ) {
                    Text("Google")
                }
                // ...
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Sign Up Link (sin cambios)
            Row {
                Text(
                    text = "¿No tienes cuenta? ",
                    color = TextSecondary
                )
                Text(
                    text = "Regístrate",
                    color = PrimaryGreen,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable(onClick = onNavigateToRegister)
                )
            }
        }
    }
}