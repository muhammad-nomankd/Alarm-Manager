package com.example.alarmmanager.activities

import CreateTaskViewModel
import SignUp
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.alarmmanager.repositories.AuthRepository
import com.example.alarmmanager.screens.CreatTask
import com.example.alarmmanager.screens.HomeScreen
import com.example.alarmmanager.screens.TaskListScreen
import com.example.alarmmanager.viewmodels.AuthViewModel

class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = "home") {
                composable("home") { HomeScreen().HomeScreenUi(navController, this@MainActivity) }
                composable("signup") {
                    SignUp(
                        viewModel = AuthViewModel(AuthRepository()),
                        navController = navController
                    )
                }
                composable(
                    "createTask?taskId={taskId}&taskTitle={taskTitle}&taskDescription={taskDescription}&startDate={startDate}&endDate={endDate}&startTime={startTime}&endTime={endTime}&priority={priority}",
                    arguments = listOf(
                        navArgument("taskId") { nullable = true },
                        navArgument("taskTitle") { nullable = true },
                        navArgument("taskDescription") { nullable = true },
                        navArgument("startDate") { nullable = true },
                        navArgument("endDate") { nullable = true },
                        navArgument("startTime") { nullable = true },
                        navArgument("endTime") { nullable = true },
                        navArgument("priority") { nullable = true }
                    )
                ) { backStackEntry ->
                    val taskId = backStackEntry.arguments?.getString("taskId")
                    val taskTitle = backStackEntry.arguments?.getString("taskTitle")
                    val taskDescription = backStackEntry.arguments?.getString("taskDescription")
                    val startDate = backStackEntry.arguments?.getString("startDate")
                    val endDate = backStackEntry.arguments?.getString("endDate")
                    val startTime = backStackEntry.arguments?.getString("startTime")
                    val endTime = backStackEntry.arguments?.getString("endTime")
                    val priority = backStackEntry.arguments?.getString("priority")

                    CreatTask().CreateTaskcom(
                        navController,
                        CreateTaskViewModel(),
                        taskId,
                        taskTitle,
                        taskDescription,
                        startDate,
                        endDate,
                        startTime,
                        endTime,
                        priority
                    )
                }
                composable("taskListScreen") { TaskListScreen().TaskListScreen(navController) }
            }
        }
    }


}

