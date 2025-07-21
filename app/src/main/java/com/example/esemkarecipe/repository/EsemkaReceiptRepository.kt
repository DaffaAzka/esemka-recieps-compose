package com.example.esemkarecipe.repository

import com.example.esemkarecipe.model.User
import com.example.esemkarecipe.model.Recipe
import com.example.esemkarecipe.model.Category
import org.json.JSONObject
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit
import android.os.Handler
import android.os.Looper

data class LoginResponse(
    val success: Boolean,
    val message: String?,
    val user: User?
)

class EsemkaReceiptRepository {
    private companion object {
        const val BASE_URL = "http://10.0.2.2:5000/api"
        const val SIGN_IN_ENDPOINT = "$BASE_URL/sign-in"
        const val TIMEOUT_SECONDS = 30
    }

    fun signIn(username: String, password: String, callback: (LoginResponse) -> Unit) {
        Thread {
            try {
                val url = URL(SIGN_IN_ENDPOINT)
                val connection = url.openConnection() as HttpURLConnection

                connection.apply {
                    requestMethod = "POST"
                    setRequestProperty("Content-Type", "application/json")
                    setRequestProperty("Accept", "application/json")
                    doOutput = true
                    connectTimeout = TimeUnit.SECONDS.toMillis(TIMEOUT_SECONDS.toLong()).toInt()
                    readTimeout = TimeUnit.SECONDS.toMillis(TIMEOUT_SECONDS.toLong()).toInt()
                }

                val requestBody = JSONObject().apply {
                    put("username", username)
                    put("password", password)
                }.toString()

                connection.outputStream.use { outputStream ->
                    outputStream.write(requestBody.toByteArray(Charsets.UTF_8))
                    outputStream.flush()
                }

                val responseCode = connection.responseCode
                val responseBody = if (responseCode == HttpURLConnection.HTTP_OK) {
                    connection.inputStream.bufferedReader().use { it.readText() }
                } else {
                    connection.errorStream?.bufferedReader()?.use { it.readText() }
                        ?: "Unknown error occurred"
                }

                val result = try {
                    val json = JSONObject(responseBody)

                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        val user = try {
                            User(
                                id = json.getInt("id"),
                                username = json.getString("username"),
                                fullName = json.optString("fullName")
                            )
                        } catch (e: Exception) { null }

                        LoginResponse(success = true, message = "Login successful", user = user)
                    } else {
                        val errorMessage = json.optString("message", "Login failed")
                        LoginResponse(success = false, message = errorMessage, user = null)
                    }
                } catch (e: Exception) {
                    LoginResponse(success = false, message = "Failed to parse server response", user = null)
                }

                connection.disconnect()

                Handler(Looper.getMainLooper()).post {
                    callback(result)
                }

            } catch (e: SocketTimeoutException) {
                Handler(Looper.getMainLooper()).post {
                    callback(LoginResponse(
                        success = false,
                        message = "Connection timeout. Please check your internet connection.",
                        user = null
                    ))
                }
            } catch (e: IOException) {
                Handler(Looper.getMainLooper()).post {
                    callback(LoginResponse(
                        success = false,
                        message = "Network error. Please check your connection.",
                        user = null
                    ))
                }
            } catch (e: Exception) {
                Handler(Looper.getMainLooper()).post {
                    callback(LoginResponse(
                        success = false,
                        message = "An unexpected error occurred: ${e.message}",
                        user = null
                    ))
                }
            }
        }.start()
    }

    fun getCategories(callback: (List<Category>?, String?) -> Unit) {
        Thread {
            try {
                val url = URL("$BASE_URL/categories")
                val connection = url.openConnection() as HttpURLConnection
                connection.apply {
                    requestMethod = "GET"
                    setRequestProperty("Accept", "application/json")
                    connectTimeout = TimeUnit.SECONDS.toMillis(TIMEOUT_SECONDS.toLong()).toInt()
                    readTimeout = TimeUnit.SECONDS.toMillis(TIMEOUT_SECONDS.toLong()).toInt()
                }

                val responseCode = connection.responseCode
                val responseBody = if (responseCode == HttpURLConnection.HTTP_OK) {
                    connection.inputStream.bufferedReader().use { it.readText() }
                } else {
                    connection.errorStream?.bufferedReader()?.use { it.readText() }
                        ?: "Error: $responseCode"
                }

                val result = if (responseCode == HttpURLConnection.HTTP_OK) {
                    parseCategories(responseBody) to null
                } else {
                    null to "Failed to load categories ($responseCode)"
                }

                connection.disconnect()
                Handler(Looper.getMainLooper()).post {
                    callback(result.first, result.second)
                }
            } catch (e: Exception) {
                Handler(Looper.getMainLooper()).post {
                    callback(null, "Error: ${e.message}")
                }
            }
        }.start()
    }

    fun getRecipes(categoryId: Int, callback: (List<Recipe>?, String?) -> Unit) {
        Thread {
            try {
                val url = URL("$BASE_URL/recipes?categoryId=$categoryId")
                val connection = url.openConnection() as HttpURLConnection
                connection.apply {
                    requestMethod = "GET"
                    setRequestProperty("Accept", "application/json")
                    connectTimeout = TimeUnit.SECONDS.toMillis(TIMEOUT_SECONDS.toLong()).toInt()
                    readTimeout = TimeUnit.SECONDS.toMillis(TIMEOUT_SECONDS.toLong()).toInt()
                }

                val responseCode = connection.responseCode
                val responseBody = if (responseCode == HttpURLConnection.HTTP_OK) {
                    connection.inputStream.bufferedReader().use { it.readText() }
                } else {
                    connection.errorStream?.bufferedReader()?.use { it.readText() }
                        ?: "Error: $responseCode"
                }

                val result = if (responseCode == HttpURLConnection.HTTP_OK) {
                    parseRecipes(responseBody) to null
                } else {
                    null to "Failed to load recipes ($responseCode)"
                }

                connection.disconnect()
                Handler(Looper.getMainLooper()).post {
                    callback(result.first, result.second)
                }
            } catch (e: Exception) {
                Handler(Looper.getMainLooper()).post {
                    callback(null, "Error: ${e.message}")
                }
            }
        }.start()
    }
    fun getRecipe(recipeId: Int, callback: (Recipe?, String?) -> Unit) {
        Thread {
            try {
                val url = URL("$BASE_URL/recipes/detail/$recipeId")
                val connection = url.openConnection() as HttpURLConnection
                connection.apply {
                    requestMethod = "GET"
                    setRequestProperty("Accept", "application/json")
                    connectTimeout = TimeUnit.SECONDS.toMillis(TIMEOUT_SECONDS.toLong()).toInt()
                    readTimeout = TimeUnit.SECONDS.toMillis(TIMEOUT_SECONDS.toLong()).toInt()
                }

                val responseCode = connection.responseCode
                val responseBody = if (responseCode == HttpURLConnection.HTTP_OK) {
                    connection.inputStream.bufferedReader().use { it.readText() }
                } else {
                    connection.errorStream?.bufferedReader()?.use { it.readText() }
                        ?: "Error: $responseCode"
                }

                val result = if (responseCode == HttpURLConnection.HTTP_OK) {
                    parseRecipe(responseBody) to null
                } else {
                    null to "Failed to load recipe ($responseCode): $responseBody"
                }

                Handler(Looper.getMainLooper()).post {
                    callback(result.first, result.second)
                }

                connection.disconnect()
            } catch (e: Exception) {
                Handler(Looper.getMainLooper()).post {
                    callback(null, "Error: ${e.message}")
                }
            }
        }.start()
    }


    private fun parseCategories(json: String): List<Category> {
        val jsonArray = JSONArray(json)
        return List(jsonArray.length()) { i ->
            val obj = jsonArray.getJSONObject(i)
            Category(
                icon = obj.getString("icon"),
                id = obj.getInt("id"),
                name = obj.getString("name")
            )
        }
    }

    private fun parseRecipes(json: String): List<Recipe> {
        val jsonArray = JSONArray(json)
        return List(jsonArray.length()) { i ->
            val obj = jsonArray.getJSONObject(i)
            val categoryObj = obj.getJSONObject("category")

            val ingredientsArray = obj.getJSONArray("ingredients")
            val ingredients = List(ingredientsArray.length()) { j ->
                ingredientsArray.getString(j)
            }

            val stepsArray = obj.getJSONArray("steps")
            val steps = List(stepsArray.length()) { j ->
                stepsArray.getString(j)
            }

            Recipe(
                category = Category(
                    icon = categoryObj.getString("icon"),
                    id = categoryObj.getInt("id"),
                    name = categoryObj.getString("name")
                ),
                categoryId = obj.getInt("categoryId"),
                cookingTimeEstimate = obj.getInt("cookingTimeEstimate"),
                description = obj.getString("description"),
                id = obj.getInt("id"),
                image = obj.getString("image"),
                ingredients = ingredients,
                priceEstimate = obj.getInt("priceEstimate"),
                steps = steps,
                title = obj.getString("title")
            )
        }
    }
    private fun parseRecipe(json: String): Recipe {
        // Parse sebagai JSONObject, bukan JSONArray
        val obj = JSONObject(json) // Ubah dari JSONArray ke JSONObject
        val categoryObj = obj.getJSONObject("category")

        val ingredientsArray = obj.getJSONArray("ingredients")
        val ingredients = List(ingredientsArray.length()) { j ->
            ingredientsArray.getString(j)
        }

        val stepsArray = obj.getJSONArray("steps")
        val steps = List(stepsArray.length()) { j ->
            stepsArray.getString(j)
        }

        val recipe = Recipe(
            category = Category(
                icon = categoryObj.getString("icon"),
                id = categoryObj.getInt("id"),
                name = categoryObj.getString("name")
            ),
            categoryId = obj.getInt("categoryId"),
            cookingTimeEstimate = obj.getInt("cookingTimeEstimate"),
            description = obj.getString("description"),
            id = obj.getInt("id"),
            image = obj.getString("image"),
            ingredients = ingredients,
            priceEstimate = obj.getInt("priceEstimate"),
            steps = steps,
            title = obj.getString("title")
        )

        return recipe
    }
}