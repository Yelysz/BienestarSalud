package com.example.bienestarsalud.ui.screens.auth.login

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.bienestarsalud.R
import com.example.bienestarsalud.ui.theme.BienestarSaludTheme
import com.example.bienestarsalud.ui.theme.PrimaryGreen
import com.example.bienestarsalud.ui.theme.TextPrimary
import com.example.bienestarsalud.ui.theme.TextSecondary
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

@Composable
fun LoginScreen(
    vm: LoginViewModel = hiltViewModel(),
    onNavigateToRegister: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    val ui by vm.ui.collectAsState()

    // Navegar si el login es exitoso
    if (ui.loggedIn) {
        LaunchedEffect(Unit) { onNavigateToHome() }
    }

    // Configuración de Google Sign-In
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
                vm.signInWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                vm.onEmailChange("Error Google: ${e.message}")
            }
        }
    }

    LoginContent(
        ui = ui,
        onEmailChange = vm::onEmailChange,
        onPasswordChange = vm::onPasswordChange,
        onRememberMeChange = vm::onRememberMeChange,
        onSignInClick = vm::signIn,
        onForgotClick = vm::sendReset,
        onGoogleClick = { googleLauncher.launch(googleSignInClient.signInIntent) },
        onRegisterClick = onNavigateToRegister
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginContent(
    ui: LoginUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onRememberMeChange: (Boolean) -> Unit,
    onSignInClick: () -> Unit,
    onForgotClick: () -> Unit,
    onGoogleClick: () -> Unit,
    onRegisterClick: () -> Unit
) {
    var passwordVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(colors = listOf(Color(0xFFE8F5E9), Color.White)))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))
            // Logo
            Box(
                modifier = Modifier.size(80.dp).background(PrimaryGreen, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Favorite, null, tint = Color.White, modifier = Modifier.size(40.dp))
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text("¡Bienvenido!", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(modifier = Modifier.height(40.dp))

            // 1. EMAIL (Con error debajo)
            OutlinedTextField(
                value = ui.email,
                onValueChange = onEmailChange,
                label = { Text("Correo Electrónico") },
                placeholder = { Text("tu@email.com") },
                leadingIcon = { Icon(Icons.Default.Email, null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = MaterialTheme.shapes.large,
                isError = ui.emailError != null,
                supportingText = {
                    if (ui.emailError != null) {
                        Text(ui.emailError, color = MaterialTheme.colorScheme.error)
                    }
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 2. CONTRASEÑA (Con error debajo)
            OutlinedTextField(
                value = ui.password,
                onValueChange = onPasswordChange,
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
                supportingText = {
                    if (ui.passwordError != null) {
                        Text(ui.passwordError, color = MaterialTheme.colorScheme.error)
                    }
                }
            )

            // Sin Spacer para pegar el checkbox al input

            // 3. RECORDARME & OLVIDASTE
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-12).dp), // Subimos la fila visualmente
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = ui.rememberMe,
                        onCheckedChange = onRememberMeChange
                    )
                    Text("Recordarme", fontSize = 14.sp, color = TextSecondary)
                }
                Text("¿Olvidaste tu contraseña?", fontSize = 14.sp, color = PrimaryGreen, modifier = Modifier.clickable { onForgotClick() })
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 4. ERROR GENERAL DE SEGURIDAD
            if (ui.generalError != null) {
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                ) {
                    Text(
                        text = ui.generalError,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // BOTÓN LOGIN
            Button(
                onClick = onSignInClick,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = !ui.loading,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                shape = MaterialTheme.shapes.large
            ) {
                if (ui.loading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                else Text("Iniciar Sesión", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(24.dp))
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Divider(modifier = Modifier.weight(1f))
                Text("  O continúa con  ", fontSize = 12.sp, color = TextSecondary)
                Divider(modifier = Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(24.dp))

            // SOCIAL
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedButton(onClick = onGoogleClick, modifier = Modifier.weight(1f), shape = MaterialTheme.shapes.large) {
                    Text("Google")
                }
                OutlinedButton(onClick = {}, modifier = Modifier.weight(1f), shape = MaterialTheme.shapes.large) {
                    Text("Facebook")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // REGISTRO
            Row {
                Text("¿No tienes cuenta? ", color = TextSecondary)
                Text("Regístrate", color = PrimaryGreen, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { onRegisterClick() })
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    BienestarSaludTheme {
        LoginContent(
            ui = LoginUiState(email = "test@preview.com", emailError = "Formato inválido"),
            onEmailChange = {},
            onPasswordChange = {},
            onRememberMeChange = {},
            onSignInClick = {},
            onForgotClick = {},
            onGoogleClick = {},
            onRegisterClick = {}
        )
    }
}