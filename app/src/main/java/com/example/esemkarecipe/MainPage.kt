package com.example.esemkarecipe

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.esemkarecipe.model.Category
import com.example.esemkarecipe.model.Recipe
import com.example.esemkarecipe.ui.screen.home.HomeScreen
import com.example.esemkarecipe.ui.screen.home.HomeViewModel
import com.example.esemkarecipe.ui.screen.login.LoginScreen
import com.example.esemkarecipe.ui.screen.login.LoginViewModel
import com.example.esemkarecipe.ui.screen.recipe.RecipeScreen
import com.example.esemkarecipe.ui.screen.recipe.RecipeViewModel
import com.example.esemkarecipe.ui.screen.recipes.RecipesScreen
import com.example.esemkarecipe.ui.screen.recipes.RecipesViewModel

sealed class Page {
    object Login : Page()
    object Home : Page()
    data class Recipes(val category: Category) : Page()
    data class Recipe(val recipe: com.example.esemkarecipe.model.Recipe) : Page()
}

@Composable
fun MainPage() {
    var currentPage by remember { mutableStateOf<Page>(Page.Login) }
    var navigationStack by remember { mutableStateOf<List<Page>>(emptyList()) }
    val context = LocalContext.current


    BackHandler(enabled = navigationStack.isNotEmpty()) {
        if (navigationStack.isNotEmpty()) {
            currentPage = navigationStack.last()
            navigationStack = navigationStack.dropLast(1)
        } else {
            (context as? Activity)?.finish()
        }
    }

    fun navigateTo(newPage: Page) {
        if (currentPage != Page.Login) {
            navigationStack = navigationStack + currentPage
        }
        currentPage = newPage
    }

    fun navigateBack() {
        if (navigationStack.isNotEmpty()) {
            currentPage = navigationStack.last()
            navigationStack = navigationStack.dropLast(1)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (currentPage is Page.Home) {
                BottomNavigationBar(
                    currentPage = currentPage,
                    onSelectedPage = { page ->
                        navigationStack = emptyList()
                        currentPage = page
                    }
                )
            }
        }
    ) { innerPadding ->
        when (val page = currentPage) {
            Page.Login -> {
                val loginViewModel: LoginViewModel = remember { LoginViewModel() }
                LoginScreen(
                    state = loginViewModel.state,
                    onEvent = loginViewModel::onEvent,
                    onNavigate = {
                        navigationStack = emptyList()
                        currentPage = Page.Home
                    }
                )
            }

            Page.Home -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp)
                ) {
                    val homeViewModel: HomeViewModel = remember { HomeViewModel() }
                    HomeScreen(
                        state = homeViewModel.state,
                        onCategoryClick = { category ->
                            navigateTo(Page.Recipes(category))
                        }
                    )
                }
            }

            is Page.Recipes -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp)
                ) {
                    val recipesViewModel: RecipesViewModel = remember(page.category.id) {
                        RecipesViewModel(categoryId = page.category.id)
                    }
                    RecipesScreen(
                        state = recipesViewModel.state,
                        categoryName = page.category.name,
                        onBackClick = {
                            navigateBack()
                        },
                        onRecipeClick = { recipe ->
                            navigateTo(Page.Recipe(recipe))
                        },
                        onEvent = recipesViewModel::onEvent
                    )
                }
            }

            is Page.Recipe -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp)
                ) {
                    val recipeViewModel: RecipeViewModel = remember(page.recipe.id) {
                        RecipeViewModel(
                            recipeId = page.recipe.id,
                        )
                    }

                    RecipeScreen(
                        state = recipeViewModel.state,
                        onBackClick = {
                            navigateBack()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(
    currentPage: Page,
    onSelectedPage: (Page) -> Unit
) {
    NavigationBar {
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Home"
                )
            },
            selected = currentPage is Page.Home,
            onClick = {
                if (currentPage !is Page.Home) {
                    onSelectedPage(Page.Home)
                }
            },
            label = { Text("Home") }
        )

    }
}