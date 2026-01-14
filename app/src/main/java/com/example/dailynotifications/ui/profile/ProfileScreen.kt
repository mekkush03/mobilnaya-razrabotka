package com.example.dailynotifications.ui.profile

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.shadow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.dailynotifications.ui.theme.AppTypography

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val use24Hour by viewModel.use24Hour.collectAsState(initial = true)
    val notificationsEnabled by viewModel.notificationsEnabled.collectAsState(initial = true)
    val authState by viewModel.authState.collectAsState()
    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
                    .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(72.dp)
                        .align(Alignment.BottomCenter),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Profile",
                        style = AppTypography.titleLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Account", style = AppTypography.titleLarge, color = MaterialTheme.colorScheme.primary)
            if (!authState.isLoggedIn) {
                Button(
                    onClick = viewModel::onStartAuth,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(50)
                ) {
                    Text("Register to save profile", style = AppTypography.titleMedium, color = MaterialTheme.colorScheme.onPrimary)
                }
                Text(
                    "Create an account to sync reminders across devices.",
                    style = AppTypography.bodyLarge
                )
            } else {
                Text(
                    "User: ${authState.name ?: "User"}",
                    style = AppTypography.titleLarge
                )
                Text(
                    "Your profile information.",
                    style = AppTypography.bodyLarge
                )
                Text(
                    "Email: ${authState.email ?: "-"}",
                    style = AppTypography.titleMedium
                )
                Button(
                    onClick = viewModel::onLogout,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(6.dp, RoundedCornerShape(50)),
                    shape = RoundedCornerShape(50)
                ) {
                    Text("Logout", style = AppTypography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                }
            }

            Text("Settings", style = AppTypography.titleLarge, color = MaterialTheme.colorScheme.primary)
            Button(
                onClick = { viewModel.onNotificationsToggle(!notificationsEnabled) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (notificationsEnabled) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.surface
                    },
                    contentColor = if (notificationsEnabled) {
                        MaterialTheme.colorScheme.onPrimary
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .then(
                        if (!notificationsEnabled) {
                            Modifier.shadow(6.dp, RoundedCornerShape(50))
                        } else {
                            Modifier
                        }
                    ),
                shape = RoundedCornerShape(50)
            ) {
                Text(
                    if (notificationsEnabled) "Notifications: On" else "Notifications: Off",
                    style = AppTypography.titleMedium
                )
            }
            Text(
                "Turn off to pause all reminder alerts.",
                style = AppTypography.bodyLarge
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val selectedColor = MaterialTheme.colorScheme.primary
                val unselectedColor = MaterialTheme.colorScheme.surface
                val selectedContent = MaterialTheme.colorScheme.onPrimary
                val unselectedContent = MaterialTheme.colorScheme.onSurface
                val color12 by animateColorAsState(
                    targetValue = if (!use24Hour) selectedColor else unselectedColor,
                    label = "timeFormat12Bg"
                )
                val content12 by animateColorAsState(
                    targetValue = if (!use24Hour) selectedContent else unselectedContent,
                    label = "timeFormat12Fg"
                )
                val color24 by animateColorAsState(
                    targetValue = if (use24Hour) selectedColor else unselectedColor,
                    label = "timeFormat24Bg"
                )
                val content24 by animateColorAsState(
                    targetValue = if (use24Hour) selectedContent else unselectedContent,
                    label = "timeFormat24Fg"
                )
                Button(
                    onClick = { viewModel.onTimeFormatSelected(false) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = color12,
                        contentColor = content12
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .then(
                            if (!use24Hour) {
                                Modifier
                            } else {
                                Modifier.shadow(6.dp, RoundedCornerShape(50))
                            }
                        ),
                    shape = RoundedCornerShape(50)
                ) {
                    Text("12-hour", style = AppTypography.titleMedium)
                }
                Button(
                    onClick = { viewModel.onTimeFormatSelected(true) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = color24,
                        contentColor = content24
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .then(
                            if (use24Hour) {
                                Modifier
                            } else {
                                Modifier.shadow(6.dp, RoundedCornerShape(50))
                            }
                        ),
                    shape = RoundedCornerShape(50)
                ) {
                    Text("24-hour", style = AppTypography.titleMedium)
                }
            }
        }
    }
}
