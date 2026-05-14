package com.taskflow.goals.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.taskflow.goals.ui.create.CreateGoalScreen
import com.taskflow.goals.ui.details.GoalDetailsScreen
import com.taskflow.goals.ui.home.HomeScreen
import com.taskflow.goals.ui.profile.ProfileScreen
import com.taskflow.goals.ui.proof.SubmitProofScreen

@Composable
fun GoalsNavHost(
    navController: NavHostController = rememberNavController(),
    onLogout: () -> Unit,
) {
    NavHost(
        navController = navController,
        startDestination = GoalsDestinations.HOME,
    ) {
        composable(GoalsDestinations.HOME) {
            HomeScreen(
                onCreateGoal = { navController.navigate(GoalsDestinations.CREATE) },
                onProfile = { navController.navigate(GoalsDestinations.PROFILE) },
                onGoalClick = { goalId -> navController.navigate(GoalsDestinations.details(goalId)) },
                onSendProof = { goalId -> navController.navigate(GoalsDestinations.proof(goalId)) },
            )
        }
        composable(GoalsDestinations.CREATE) {
            CreateGoalScreen(
                onBack = { navController.popBackStack() },
                onCreated = { navController.popBackStack() },
            )
        }
        composable(GoalsDestinations.PROFILE) {
            ProfileScreen(
                onBack = { navController.popBackStack() },
                onLogout = onLogout,
            )
        }
        composable(
            route = GoalsDestinations.DETAILS,
            arguments = listOf(navArgument("goalId") { type = NavType.StringType }),
        ) { entry ->
            val goalId = entry.arguments?.getString("goalId").orEmpty()
            GoalDetailsScreen(
                goalId = goalId,
                onBack = { navController.popBackStack() },
                onSubmitProof = { navController.navigate(GoalsDestinations.proof(goalId)) },
            )
        }
        composable(
            route = GoalsDestinations.PROOF,
            arguments = listOf(navArgument("goalId") { type = NavType.StringType }),
        ) { entry ->
            val goalId = entry.arguments?.getString("goalId").orEmpty()
            SubmitProofScreen(
                goalId = goalId,
                onBack = { navController.popBackStack() },
                onDone = { navController.popBackStack() },
            )
        }
    }
}
