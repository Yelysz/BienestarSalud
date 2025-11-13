package com.example.bienestarsalud.presentation.screens.auth.register

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
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.bienestarsalud.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    vm: RegisterViewModel = hiltViewModel(),
    onNavigateToLogin: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }


    val ui by vm.ui.collectAsState()

    if (ui.registered) {
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
                        Color(0xFFE3F2FD),
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
            Spacer(modifier = Modifier.height(40.dp))

            // Logo
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(PrimaryBlue, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Crear Cuenta",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Text(
                text = "Comienza tu viaje hacia el bienestar",
                fontSize = 14.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Full Name Field
            OutlinedTextField(
                value = ui.fullName,
                onValueChange = vm::onFullNameChange,
                label = { Text("Nombre Completo") },
                placeholder = { Text("Juan Pérez") },
                leadingIcon = {
                    Icon(Icons.Default.Person, contentDescription = null)
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = MaterialTheme.shapes.large
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Email Field
            OutlinedTextField(
                value = ui.email,
                onValueChange = vm::onEmailChange,
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

            // Password Field
            OutlinedTextField(
                value = ui.password,
                onValueChange = vm::onPasswordChange,
                label = { Text("Contraseña") },
                placeholder = { Text("Mínimo 8 caracteres") },
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

            Spacer(modifier = Modifier.height(12.dp))

            // Password Requirements
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                PasswordRequirement(
                    text = "Mínimo 8 caracteres",
                    satisfied = ui.hasMinLength
                )
                PasswordRequirement(
                    text = "Una mayúscula",
                    satisfied = ui.hasUppercase
                )
                PasswordRequirement(
                    text = "Un número",
                    satisfied = ui.hasNumber
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Terms Checkbox
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = ui.acceptTerms,
                    onCheckedChange = vm::onTermsChange
                )
                Text(
                    text = "Acepto los Términos y Condiciones",
                    fontSize = 14.sp,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Register Button
            Button(
                onClick = vm::signUp,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !ui.loading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryBlue
                ),
                shape = MaterialTheme.shapes.large,
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 4.dp
                )
            ) {
                Text(
                    text = "Crear Cuenta",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Login Link
            Row {
                Text(
                    text = "¿Ya tienes cuenta? ",
                    color = TextSecondary
                )
                Text(
                    text = "Inicia Sesión",
                    color = PrimaryBlue,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable(onClick = onNavigateToLogin)
                )
            }
        }
    }
}

@Composable
fun PasswordRequirement(text: String, satisfied: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            tint = if (satisfied) PrimaryGreen else Color.LightGray,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = text,
            fontSize = 12.sp,
            color = if (satisfied) PrimaryGreen else TextSecondary
        )
    }
}