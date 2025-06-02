package com.example.women_safety_app

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.women_safety_app.pages.BottomNavigationBar
import com.example.women_safety_app.pages.EmergencyContactScreen
import com.example.women_safety_app.pages.EmergencyNumbersScreen
import com.example.women_safety_app.pages.FakeCallScreen

import com.example.women_safety_app.pages.LoginPage
import com.example.women_safety_app.pages.MainScreen
import com.example.women_safety_app.pages.SelfDefenseScreen
import com.example.women_safety_app.pages.SettingsScreen
import com.example.women_safety_app.pages.SignupPage

@Composable
fun MyAppNavigation(modifier: Modifier, authViewModel: AuthViewModel){
    val navController = rememberNavController()


    val bottombarScreens = listOf("home", "contacts", "settings","fakecall","defence","helpline")

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

//    val showBottomBar = currentRoute in bottombarScreens
    var showBottomBar by remember { mutableStateOf(true) } //ADDED THIS

    val context = LocalContext.current
    val viewModel: SafetyViewModel = viewModel(factory = SafetyViewModelFactory(context))



    Scaffold(
        bottomBar = {
            if (showBottomBar){
                BottomNavigationBar(navController = navController)
            }
        }
    ) {innerPadding ->

        NavHost(
            modifier = Modifier.padding(innerPadding),
            navController = navController,
            startDestination = "home", builder = {

            composable("login"){
                LoginPage(modifier, navController,authViewModel)
            }
            composable("signup"){
                SignupPage(modifier, navController,authViewModel)
            }
            composable("home"){
                MainScreen(modifier, navController,authViewModel,viewModel)
            }

            composable("contacts") {
                EmergencyContactScreen(viewModel = viewModel)
            }

            composable ("settings"){
                SettingsScreen(modifier = modifier, navController = navController)
            }


                composable("fakecall") {
                    FakeCallScreen(
                        onFakeCallButtonPressed = {
                            showBottomBar = false // Hide the bottom bar when the fake call button is pressed
                        },
                        onFakeCallEnded = {
                            showBottomBar = true // Show the bottom bar when the fake call ends
                        }
                    )
                }

              composable("defence") {
                  SelfDefenseScreen()
              }

              composable("helpline") {
                  EmergencyNumbersScreen()
              }
        })
    }


}