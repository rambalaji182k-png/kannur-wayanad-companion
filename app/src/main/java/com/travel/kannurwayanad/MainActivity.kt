package com.travel.kannurwayanad

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// --- DATA STRUCTURES ---
data class Destination(
    val name: String,
    val timeSlot: String,
    val description: String,
    val icon: ImageVector,
    val mapsUrl: String
)

data class TripDay(
    val dayNumber: String,
    val date: String,
    val title: String,
    val weatherWarning: String,
    val destinations: List<Destination>
)

data class PackingItem(
    val name: String,
    val category: String,
    var isChecked: Boolean = false
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(
                colorScheme = lightColorScheme(
                    primary = Color(0xFF0F9D58), // Emerald Green
                    secondary = Color(0xFF4285F4), // Google Blue
                    background = Color(0xFFF8F9FA)
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
    val tabs = listOf("Itinerary", "Packing List", "Trip Info")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Malabar & Hills Companion", 
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    ) 
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
                                    0 -> Icons.Default.DateRange
                                    1 -> Icons.Default.CheckCircle
                                    else -> Icons.Default.Info
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
                1 -> PackingListTab()
                2 -> InfoTab()
            }
        }
    }
}

// --- TAB 1: ITINERARY ---
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
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Header Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Day ${day.dayNumber} - ${day.date}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = day.title,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Weather Warning Banner
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3CD)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Weather Alert",
                                tint = Color(0xFF856404),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = day.weatherWarning,
                                color = Color(0xFF856404),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    // Destinations Timeline
                    day.destinations.forEach { dest ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = dest.icon,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier
                                    .size(28.dp)
                                    .padding(top = 4.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = dest.timeSlot,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Gray
                                )
                                Text(
                                    text = dest.name,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = dest.description,
                                    fontSize = 13.sp,
                                    color = Color.DarkGray
                                )
                            }
                            IconButton(onClick = { launchGoogleMaps(context, dest.mapsUrl) }) {
                                Icon(
                                    imageVector = Icons.Default.Place,
                                    contentDescription = "Navigate",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- TAB 2: CHECKLIST ---
@Composable
fun PackingListTab() {
    val itemsState = remember {
        mutableStateListOf(
            PackingItem("Lightweight cotton wear", "Kannur Coast"),
            PackingItem("Sunscreen & sunglasses", "Kannur Coast"),
            PackingItem("Driving license / ID proofs", "Travel Essentials"),
            PackingItem("Waterproof jacket / umbrella", "Wayanad Hills"),
            PackingItem("Trekking/Hiking shoes (non-slip)", "Wayanad Hills"),
            PackingItem("Insect repellent", "Wayanad Hills"),
            PackingItem("Fast-charging power bank", "Travel Essentials"),
            PackingItem("First-Aid (motion sickness tablets)", "Travel Essentials")
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text(
                "Essentials Checklist",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        items(itemsState) { item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(8.dp))
                    .clickable {
                        val index = itemsState.indexOf(item)
                        itemsState[index] = item.copy(isChecked = !item.isChecked)
                    }
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = item.isChecked,
                    onCheckedChange = { isChecked ->
                        val index = itemsState.indexOf(item)
                        itemsState[index] = item.copy(isChecked = isChecked)
                    }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = item.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        textDecoration = if (item.isChecked) TextDecoration.LineThrough else TextDecoration.None,
                        color = if (item.isChecked) Color.Gray else Color.Unspecified
                    )
                    Text(
                        text = item.category,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// --- TAB 3: USEFUL INFO ---
@Composable
fun InfoTab() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "Essential Trip Guidelines",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "🚗 Driving Mountain Passes",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    "The route climbs from coastal Kannur to the high-altitude Wayanad plateau. Mountain curves require slow, steady control. Complete all ghat section climbs before 5:00 PM to avoid driving through evening fog or dense cloud covers.",
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "⛈️ Monsoon Safety Alerts",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    "Mid-May brings heavy pre-monsoon afternoon storms. Water levels at the waterfalls can surge suddenly. Always strictly adhere to local forestry guidelines and avoid stepping into deep pool currents.",
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

// --- HELPER METHODS ---
fun launchGoogleMaps(context: Context, url: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    context.startActivity(intent)
}

fun getItineraryData(): List<TripDay> {
    return listOf(
        TripDay(
            dayNumber = "1",
            date = "May 16",
            title = "Coastal History & Drives",
            weatherWarning = "High humidity (34°C). Remain hydrated and limit outdoor beach walks during afternoon peak heat hours.",
            destinations = listOf(
                Destination(
                    "St. Angelo Fort",
                    "10:00 AM - 11:30 AM",
                    "Explore the 16th-century sea-facing stone ramparts and panoramic bay views.",
                    Icons.Default.Home,
                    "https://www.google.com/maps/search/?api=1&query=St.+Angelo+Fort+Kannur"
                ),
                Destination(
                    "Arakkal Museum",
                    "11:30 AM - 12:30 PM",
                    "Discover the history, weaponry, and artifacts of Kerala's only Muslim royal family.",
                    Icons.Default.Menu,
                    "https://www.google.com/maps/search/?api=1&query=Arakkal+Museum+Kannur"
                ),
                Destination(
                    "Muzhappilangad Drive-In Beach",
                    "3:30 PM - 5:30 PM",
                    "Drive directly on India's longest drive-in sandy beach as waves break beside you.",
                    Icons.Default.PlayArrow,
                    "https://www.google.com/maps/search/?api=1&query=Muzhappilangad+Drive+In+Beach"
                )
            )
        ),
        TripDay(
            dayNumber = "2",
            date = "May 17",
            title = "Ascending the Western Ghats",
            weatherWarning = "Afternoon storms likely. Plan to navigate the winding ghat pass during the clear morning hours.",
            destinations = listOf(
                Destination(
                    "Kannur to Wayanad Roadtrip",
                    "7:30 AM - 11:30 AM",
                    "A scenic 4-hour drive climbing mountain ghat passes enveloped in deep green trees.",
                    Icons.Default.PlayArrow,
                    "https://www.google.com/maps/dir/Kannur/Wayanad"
                ),
                Destination(
                    "Pookode Lake",
                    "2:00 PM - 4:00 PM",
                    "Rent a pedal boat or trace the natural forest pathway wrapping around the shoreline.",
                    Icons.Default.LocationOn,
                    "https://www.google.com/maps/search/?api=1&query=Pookode+Lake"
                )
            )
        ),
        TripDay(
            dayNumber = "3",
            date = "May 18",
            title = "Misty Falls & Departure",
            weatherWarning = "Wet paths. Heavy shoes recommended for walking near the waterfall rocks.",
            destinations = listOf(
                Destination(
                    "Kanthanpara Waterfalls",
                    "9:30 AM - 11:30 AM",
                    "A beautiful 30-meter drop inside bamboo groves with flat, safe gravel access paths.",
                    Icons.Default.PlayArrow,
                    "https://www.google.com/maps/search/?api=1&query=Kanthanpara+Waterfalls"
                )
            )
        )
    )
}
