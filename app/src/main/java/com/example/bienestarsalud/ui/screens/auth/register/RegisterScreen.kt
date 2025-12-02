package com.example.bienestarsalud.ui.screens.auth.register

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.bienestarsalud.R
import com.example.bienestarsalud.ui.theme.PrimaryBlue
import com.example.bienestarsalud.ui.theme.PrimaryGreen
import com.example.bienestarsalud.ui.theme.TextPrimary
import com.example.bienestarsalud.ui.theme.TextSecondary
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    vm: RegisterViewModel = hiltViewModel(),
    onNavigateToLogin: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    val ui by vm.ui.collectAsState()
    var passwordVisible by remember { mutableStateOf(false) }

    if (ui.registered) {
        LaunchedEffect(Unit) { onNavigateToHome() }
    }

    // --- CONFIGURACIÓN GOOGLE ---
    val context = LocalContext.current
    val googleSignInClient = remember {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.web_client_id))
            .requestEmail()
            .build()
        GoogleSignIn.getClient(context, gso)
    }
    val googleLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                vm.signUpWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Manejar error silenciosamente o mostrarlo
            }
        }
    }
    // ----------------------------

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(colors = listOf(Color(0xFFE3F2FD), Color.White)))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))
            Box(
                modifier = Modifier.size(80.dp).background(PrimaryBlue, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, null, tint = Color.White, modifier = Modifier.size(40.dp))
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text("Crear Cuenta", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Text("Comienza tu viaje hacia el bienestar", fontSize = 14.sp, color = TextSecondary)
            Spacer(modifier = Modifier.height(32.dp))

            // NOMBRE COMPLETO
            OutlinedTextField(
                value = ui.fullName,
                onValueChange = vm::onFullNameChange,
                label = { Text("Nombre Completo") },
                placeholder = { Text("Juan Pérez") },
                leadingIcon = { Icon(Icons.Default.Person, null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = MaterialTheme.shapes.large,
                isError = ui.fullNameError != null,
                supportingText = { if (ui.fullNameError != null) Text(ui.fullNameError!!, color = MaterialTheme.colorScheme.error) }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // EMAIL
            OutlinedTextField(
                value = ui.email,
                onValueChange = vm::onEmailChange,
                label = { Text("Correo Electrónico") },
                placeholder = { Text("tu@email.com") },
                leadingIcon = { Icon(Icons.Default.Email, null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = MaterialTheme.shapes.large,
                isError = ui.emailError != null,
                supportingText = { if (ui.emailError != null) Text(ui.emailError!!, color = MaterialTheme.colorScheme.error) }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // PASSWORD
            OutlinedTextField(
                value = ui.password,
                onValueChange = vm::onPasswordChange,
                label = { Text("Contraseña") },
                leadingIcon = { Icon(Icons.Default.Lock, null) },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff, null)
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = MaterialTheme.shapes.large,
                isError = ui.passwordError != null,
                supportingText = { if (ui.passwordError != null) Text(ui.passwordError!!, color = MaterialTheme.colorScheme.error) }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // REQUISITOS PASSWORD
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                PasswordRequirement("Mínimo 8 caracteres", ui.hasMinLength)
                PasswordRequirement("Una mayúscula", ui.hasUppercase)
                PasswordRequirement("Un número", ui.hasNumber)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // TÉRMINOS
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(checked = ui.acceptTerms, onCheckedChange = vm::onTermsChange)
                    Text("Acepto los Términos y Condiciones", fontSize = 14.sp, color = TextSecondary)
                }
                if (ui.termsError != null) {
                    Text(ui.termsError!!, color = MaterialTheme.colorScheme.error, fontSize = 12.sp, modifier = Modifier.padding(start = 12.dp))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ERROR GENERAL
            if (ui.generalError != null) {
                Text(ui.generalError!!, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(bottom = 8.dp))
            }

            // BOTÓN REGISTRO
            Button(
                onClick = vm::signUp,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = !ui.loading,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                shape = MaterialTheme.shapes.large,
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                if (ui.loading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Crear Cuenta", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // DIVIDER
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Divider(modifier = Modifier.weight(1f))
                Text("  O regístrate con  ", fontSize = 12.sp, color = TextSecondary)
                Divider(modifier = Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(24.dp))

            // BOTONES SOCIALES (Google añadido)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedButton(
                    onClick = { googleLauncher.launch(googleSignInClient.signInIntent) },
                    modifier = Modifier.weight(1f),
                    shape = MaterialTheme.shapes.large
                ) {
                    Text("Google")
                }
                OutlinedButton(
                    onClick = { /* Facebook Logic */ },
                    modifier = Modifier.weight(1f),
                    shape = MaterialTheme.shapes.large
                ) {
                    Text("Facebook")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // LOGIN LINK
            Row {
                Text("¿Ya tienes cuenta? ", color = TextSecondary)
                Text("Inicia Sesión", color = PrimaryBlue, fontWeight = FontWeight.Bold, modifier = Modifier.clickable(onClick = onNavigateToLogin))
            }
        }
    }
}

@Composable
fun PasswordRequirement(text: String, satisfied: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            tint = if (satisfied) PrimaryGreen else Color.LightGray,
            modifier = Modifier.size(16.dp)
        )
        Text(text, fontSize = 12.sp, color = if (satisfied) PrimaryGreen else TextSecondary)
    }
}