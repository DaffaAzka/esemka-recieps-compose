package com.example.esemkarecipe.ui.screen.recipe

import android.annotation.SuppressLint
import android.widget.Space
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.esemkarecipe.model.Recipe
import com.example.esemkarecipe.ui.component.ImageWithLoading

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeScreen(
    state: RecipeState,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Kembali") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
    ) {  innerPadding ->
        Box() {
            when {
                state.isLoading -> LoadingView()
                state.errorMessage != null -> ErrorView(
                    state.errorMessage
                )
                state.recipe == null -> EmptyView()
                else -> RecipeView(state.recipe)
            }
        }


    }


}

@Composable
private fun RecipeView(recipe: Recipe, modifier: Modifier = Modifier) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        ImageWithLoading(
            "http://10.0.2.2:5000/images/recipes/${recipe.image}",
        )


        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = recipe.title,
            style = MaterialTheme.typography.titleMedium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 8.dp)
        )


        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = recipe.description,
            textAlign = TextAlign.Start,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        ListIngredients(recipe.ingredients)

        ListSteps(recipe.steps)
    }

}

@Composable
fun ListIngredients(ingredients : List<String> ,modifier: Modifier = Modifier) {
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = "Ingredients:",
        style = MaterialTheme.typography.titleMedium,
        fontSize = 18.sp
        )
    LazyVerticalGrid(
        columns = GridCells.Fixed(1),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(ingredients) { ingredient ->
            Spacer(modifier = Modifier.height(8.dp))
            Text("- $ingredient")
        }
    }
}

@Composable
fun ListSteps(steps: List<String>, modifier: Modifier = Modifier) {
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = "Steps:",
        style = MaterialTheme.typography.titleMedium,
        fontSize = 18.sp
    )
    LazyVerticalGrid(
        columns = GridCells.Fixed(1),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        itemsIndexed(steps) { index, step ->
            Spacer(modifier = Modifier.height(8.dp))
            Text("${index + 1}. $step")
        }
    }
}

@Composable
private fun LoadingView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorView(message: String?) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message ?: "Unknown error",
            color = MaterialTheme.colorScheme.error
        )
    }
}

@Composable
private fun EmptyView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("No recipes found")
    }
}