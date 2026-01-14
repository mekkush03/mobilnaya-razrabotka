package com.example.dailynotifications.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.dailynotifications.ui.theme.AppTypography
import com.example.dailynotifications.ui.theme.LightColorScheme
import kotlinx.coroutines.flow.collectLatest

@Composable
fun AuthScreen(
    viewModel: AuthViewModel = hiltViewModel()
) {
    val colors = LightColorScheme
    var isLogin by remember { mutableStateOf(true) }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel) {
        viewModel.events.collectLatest { snackbarHostState.showSnackbar(it) }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            val statusBarPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp + statusBarPadding)
                    .background(colors.surface)
                    .padding(horizontal = 16.dp)
                    .padding(top = statusBarPadding + 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                OutlinedButton(
                    onClick = viewModel::onSkip,
                    shape = RoundedCornerShape(50),
                    border = ButtonDefaults.outlinedButtonBorder
                ) {
                    Text("Skip", style = AppTypography.titleMedium, color = colors.primary)
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.surface)
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
            Text(
                if (isLogin) "Login" else "Register",
                style = AppTypography.titleLarge,
                color = colors.primary
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { isLogin = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isLogin) colors.primary else colors.surface,
                        contentColor = if (isLogin) colors.onPrimary else colors.onSurface
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Login", style = AppTypography.titleMedium)
                }
                Button(
                    onClick = { isLogin = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (!isLogin) colors.primary else colors.surface,
                        contentColor = if (!isLogin) colors.onPrimary else colors.onSurface
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Register", style = AppTypography.titleMedium)
                }
            }

            if (!isLogin) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colors.primary,
                        unfocusedBorderColor = colors.outline
                    )
                )
            }

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colors.primary,
                    unfocusedBorderColor = colors.outline
                )
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colors.primary,
                    unfocusedBorderColor = colors.outline
                )
            )

            Button(
                onClick = {
                    if (isLogin) {
                        viewModel.onLogin(email, password)
                    } else {
                        viewModel.onRegister(name, email, password)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = colors.primary),
                shape = RoundedCornerShape(50),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    if (isLogin) "Login" else "Create account",
                    color = colors.onPrimary,
                    style = AppTypography.titleMedium
                )
            }
        }
        }
    }
}
