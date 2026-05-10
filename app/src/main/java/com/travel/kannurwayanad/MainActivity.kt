package com.travel.kannurwayanad

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// --- DATA MODELS ---
data class Destination(
    val name: String,
    val timeSlot: String,
    val description: String,
    val icon: ImageVector,
    val mapsUrl: String,
    val rating: Float,
    val recentReview: String,
    val reviewAuthor: String
)

data class TripDay(
    val dayNumber: String,
    val date: String,
    val title: String,
    val weatherWarning: String,
    val destinations: List<Destination>
)

data class Message(val text: String, val isUser: Boolean)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(
                colorScheme = lightColorScheme(
                    primary = Color(0xFF0F9D58), // Malabar Palm Green
                    secondary = Color(0xFF4285F4), // Ocean Blue
                    background = Color(0xFFF1F5F9)
                )
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TravelAppScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TravelAppScreen() {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Itinerary", "Malabar AI", "Packing")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text("Malabar & Hills Guide", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 20.sp)
                        Text("May 16 - May 18, 2026", color = Color(0xFFE2E8F0), fontSize = 12.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                tabs.forEachIndexed { index, title ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        label = { Text(title) },
                        icon = {
                            Icon(
                                imageVector = when (index) {
                                    0 -> Icons.Default.Map
                                    1 -> Icons.Default.Face
                                    else -> Icons.Default.CheckCircle
                                },
                                contentDescription = title
                            )
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedTab) {
                0 -> ItineraryTab()
                1 -> ChatbotTab()
                2 -> PackingListTab()
            }
        }
    }
}

// --- TAB 1: ITINERARY WITH VISUALS AND RECENT REVIEWS ---
@Composable
fun ItineraryTab() {
    val context = LocalContext.current
    val tripDays = remember { getItineraryData() }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(tripDays) { day ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column {
                    // Day Title Banner with Visual Gradient
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(Color(0xFF0F9D58), Color(0xFF34A853))
                                )
                            )
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "DAY ${day.dayNumber} • ${day.date}",
                                    color = Color(0xFFE2E8F0),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = day.title,
                                    color = Color.White,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 18.sp
                                )
                            }
                            Icon(
                                imageVector = if (day.dayNumber == "1") Icons.Default.WbSunny else Icons.Default.Thermostat,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                    }

                    Column(modifier = Modifier.padding(16.dp)) {
                        // Weather warning box
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = "Alert",
                                    tint = Color(0xFFD97706),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = day.weatherWarning,
                                    color = Color(0xFF92400E),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Destinations
                        day.destinations.forEachIndexed { index, dest ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.Top
                            ) {
                                // Timeline indicator vertical line
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.width(32.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .background(
                                                MaterialTheme.colorScheme.secondary,
                                                RoundedCornerShape(12.dp)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = dest.icon,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                    if (index < day.destinations.size - 1) {
                                        Box(
                                            modifier = Modifier
                                                .width(2.dp)
                                                .height(180.dp)
                                                .background(Color(0xFFCBD5E1))
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                // Destination Info Column
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = dest.timeSlot,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Gray,
                                        fontSize = 12.sp
                                    )
                                    Text(
                                        text = dest.name,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 16.sp,
                                        color = Color(0xFF1E293B)
                                    )
                                    Text(
                                        text = dest.description,
                                        fontSize = 13.sp,
                                        color = Color(0xFF475569),
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    )

                                    // Maps Navigation Row
                                    Row(
                                        modifier = Modifier
                                            .clickable { launchGoogleMaps(context, dest.mapsUrl) }
                                            .padding(vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Navigation,
                                            contentDescription = "Nav",
                                            tint = MaterialTheme.colorScheme.secondary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Navigate on Maps",
                                            color = MaterialTheme.colorScheme.secondary,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                    }

                                    // Curated Recent 2026 Review Card
                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 8.dp, bottom = 16.dp)
                                    ) {
                                        Column(modifier = Modifier.padding(10.dp)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                repeat(5) { starIndex ->
                                                    Icon(
                                                        imageVector = Icons.Outlined.Star,
                                                        contentDescription = null,
                                                        tint = if (starIndex < dest.rating.toInt()) Color(0xFFF59E0B) else Color(0xFFCBD5E1),
                                                        modifier = Modifier.size(14.dp)
                                                    )
                                                }
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = "Recent Review",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 11.sp,
                                                    color = Color(0xFF64748B)
                                                )
                                            }
                                            Text(
                                                text = "\"${dest.recentReview}\"",
                                                fontSize = 11.sp,
                                                fontStyle = FontStyle.Italic,
                                                color = Color(0xFF334155),
                                                modifier = Modifier.padding(top = 4.dp)
                                            )
                                            Text(
                                                text = "— ${dest.reviewAuthor} (Visited 2026)",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF64748B),
                                                modifier = Modifier.align(Alignment.End)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- TAB 2: INTERACTIVE OFFLINE MALABAR AI CHATBOT ---
@Composable
fun ChatbotTab() {
    var textInput by remember { mutableStateOf("") }
    val messages = remember {
        mutableStateListOf(
            Message("Hello! I am your Malabar AI Companion. Ask me anything about your upcoming Kannur & Wayanad trip!", false)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF1F5F9))
    ) {
        // Chat History List
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(messages) { msg ->
                val alignment = if (msg.isUser) Alignment.End else Alignment.Start
                val bubbleColor = if (msg.isUser) Color(0xFF4285F4) else Color.White
                val textColor = if (msg.isUser) Color.White else Color(0xFF1E293B)

                Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = alignment) {
                    Box(
                        modifier = Modifier
                            .background(bubbleColor, RoundedCornerShape(12.dp))
                            .padding(12.dp)
                            .widthIn(max = 280.dp)
                    ) {
                        Text(text = msg.text, color = textColor, fontSize = 14.sp)
                    }
                }
            }
        }

        // Input Bar Area with context-matching offline engine
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = textInput,
                onValueChange = { textInput = it },
                placeholder = { Text("Ask about road, food, views...") },
                modifier = Modifier.weight(1f),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                )
            )
            IconButton(
                onClick = {
                    if (textInput.isNotBlank()) {
                        val query = textInput.trim()
                        messages.add(Message(query, true))
                        textInput = ""
                        
                        // Local Interactive Knowledge Decision Logic
                        val lowerQuery = query.lowercase()
                        val reply = when {
                            lowerQuery.contains("road") || lowerQuery.contains("
