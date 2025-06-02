package com.example.women_safety_app.pages

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext



import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight


@Composable
fun SelfDefenseScreen() {
    val context = LocalContext.current
    var showArticles by remember { mutableStateOf(true) } // Toggle state

    val articles = listOf(
        "Basic Street Safety for Women" to "https://www.instructables.com/Basic-Street-Safety-for-Women/",
        "Awareness: A Key to Women's Self-Defense" to "https://www.secondsight-ts.com/threat-assessment-blog/womens-self-defense?",
        "Self-Defense Techniques for Women" to "https://online.hilbert.edu/blog/self-defense-techniques-for-women/",
        "Eight Key Principles of Women's Self-Defense" to "https://www.divasfordefense.com/blogs/self-defense-articles-7/eight-key-principles-of-womens-self-defense",
        "Personal Safety for Women: Self-defense Goes Way Beyond the Physical" to "https://eap.partners.org/news_posts/personal-safety-for-women-self-defense-goes-way-beyond-the-physical/",
        "Why Women Need to Learn Self-Defense" to "https://effectiveselfdefense.com/blogs/news/why-women-need-to-learn-self-defense"


    )

    val videos = listOf(
        "The Most Powerful Self Defence Techniques" to "https://www.youtube.com/watch?v=OU9nlBKKPmI",
        "Pro's Guide to Situational Awareness" to "https://www.youtube.com/watch?v=HVn18rCZPzg",
        "3 Simple Self Defence Moves You Must Know" to "https://www.youtube.com/watch?v=UV78YzM-gGQ",
        "Essential Self-Defense Techniques For Women" to "https://www.youtube.com/watch?v=BUg0zlNcxFY",
        "Situational Awareness - Safety Training Video" to "https://www.youtube.com/watch?v=ejlXbOHBfpo",
        "Situational Awareness: How to Stay Alert and Safe in Any Situation" to "https://www.youtube.com/watch?v=1FMQ8kwUcSU"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Button(
            onClick = { showArticles = !showArticles },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF4382)) // Light peach background
        ) {
            Text(
                text = if (showArticles) "Switch to Videos" else "Switch to Articles",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if (showArticles) "Women Safety Articles" else "Self-Defense Videos",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(if (showArticles) articles else videos) { (title, url) ->
                WomenSafetyArticleCard(title, url, context)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun WomenSafetyArticleCard(title: String, url: String, context: Context) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .clickable { openWebPage(context, url) },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEDEB))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.DarkGray
            )
            Text(
                text = "Tap to view",
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
    }
}
// Function to open a web page in an external browser
fun openWebPage(context: android.content.Context, url: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    context.startActivity(intent)
}

