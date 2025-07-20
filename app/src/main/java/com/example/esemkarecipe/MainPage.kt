package com.example.esemkarecipe

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.esemkarecipe.model.Category
import com.example.esemkarecipe.ui.screen.home.HomeScreen
import com.example.esemkarecipe.ui.screen.home.HomeViewModel
import com.example.esemkarecipe.ui.screen.login.LoginScreen
import com.example.esemkarecipe.ui.screen.login.LoginViewModel
import com.example.esemkarecipe.ui.screen.recipes.RecipesScreen
import com.example.esemkarecipe.ui.screen.recipes.RecipesViewModel

sealed class Page {
    object Login : Page()
    object Home : Page()
    data class Recipes(val category: Category) : Page()
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainPage() {
    val (currentPage, setCurrentPage) = remember { mutableStateOf<Page>(Page.Login) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (currentPage !is Page.Login && currentPage !is Page.Recipes) {
                BottomNavigationBar(currentPage = currentPage, onSelectedPage = setCurrentPage)
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
                        setCurrentPage(Page.Home)
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
                            setCurrentPage(Page.Recipes(category))
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
                            setCurrentPage(Page.Home)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(currentPage: Page, onSelectedPage: (Page) -> Unit) {
    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            selected = currentPage is Page.Home,
            onClick = { onSelectedPage(Page.Home) },
            label = { Text("Home") },
        )
    }
}