package com.example.dailynotifications.ui

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.dailynotifications.ui.auth.AuthScreen
import com.example.dailynotifications.ui.auth.AuthViewModel
import com.example.dailynotifications.ui.list.ReminderListScreen
import com.example.dailynotifications.ui.profile.ProfileScreen
import com.example.dailynotifications.ui.remind.RemindCreationScreen
import com.example.dailynotifications.ui.theme.DailyNotificationsTheme

@Composable
fun AppRoot() {
    DailyNotificationsTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            RequestNotificationPermission()
            val authViewModel: AuthViewModel = hiltViewModel()
            val authState by authViewModel.authState.collectAsState()
            if (!authState.isLoggedIn && !authState.isGuest) {
                AuthScreen(authViewModel)
            } else {
                AppScaffold()
            }
        }
    }
}

@Composable
private fun AppScaffold() {
    val navController = rememberNavController()
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route

    Scaffold(
        bottomBar = {
            BottomBar(
                currentRoute = currentRoute,
                onListClick = {
                    navController.navigate(Screen.List.route) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onAddClick = {
                    navController.navigate(Screen.Create.route) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onProfileClick = {
                    navController.navigate(Screen.Profile.route) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    ) { padding ->
        AppNavHost(padding, navController)
    }
}

@Composable
private fun AppNavHost(
    padding: PaddingValues,
    navController: androidx.navigation.NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = Screen.List.route,
        modifier = Modifier.padding(padding)
    ) {
        composable(Screen.List.route) {
            ReminderListScreen(onEdit = { id ->
                navController.navigate("${Screen.Edit.route}/$id")
            })
        }
        composable(Screen.Create.route) { RemindCreationScreen() }
        composable("${Screen.Edit.route}/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id").orEmpty()
            RemindCreationScreen(
                editReminderId = id,
                onBack = { navController.navigate(Screen.List.route) }
            )
        }
        composable(Screen.Profile.route) { ProfileScreen() }
    }
}

@Composable
private fun BottomBar(
    currentRoute: String?,
    onListClick: () -> Unit,
    onAddClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    androidx.compose.material3.BottomAppBar(
        actions = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                IconButton(onClick = onListClick, modifier = Modifier.size(56.dp)) {
                    Icon(
                        Icons.Default.EventNote,
                        contentDescription = "Reminders",
                        tint = if (currentRoute == Screen.List.route) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        },
                        modifier = Modifier.size(28.dp)
                    )
                }
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFF6A2C))
                        .clickable(onClick = onAddClick),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Create reminder",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
                IconButton(onClick = onProfileClick, modifier = Modifier.size(56.dp)) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = "Profile",
                        tint = if (currentRoute == Screen.Profile.route) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        },
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    )
}

private enum class Screen(val route: String) {
    List("list"),
    Create("create"),
    Edit("edit"),
    Profile("profile")
}

@Composable
private fun RequestNotificationPermission() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
        return
    }
    val context = LocalContext.current
    if (
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED
    ) {
        return
    }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) {}
    LaunchedEffect(Unit) {
        launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }
}
