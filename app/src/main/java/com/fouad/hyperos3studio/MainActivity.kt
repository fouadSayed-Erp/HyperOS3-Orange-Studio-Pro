package com.fouad.hyperos3studio
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val orange = Color(0xFFFF6A00)
            val dark = Color(0xFF0F0F0F)
            MaterialTheme(colorScheme = darkColorScheme(primary = orange, background = dark, surface = Color(0xFF1A1A1A))) {
                var selectedTab by remember { mutableStateOf(0) }
                Scaffold(
                    topBar = {
                        CenterAlignedTopAppBar(
                            title = { Text("HyperOS 3 Orange Studio Pro", fontWeight = FontWeight.Black, color = Color.White) },
                            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = orange)
                        )
                    },
                    bottomBar = {
                        NavigationBar(containerColor = Color(0xFF1A1A1A)) {
                            NavigationBarItem(selected = selectedTab==0, onClick = {selectedTab=0}, icon = {Text("🎨")}, label = {Text("Themes")})
                            NavigationBarItem(selected = selectedTab==1, onClick = {selectedTab=1}, icon = {Text("⚙️")}, label = {Text("System")})
                            NavigationBarItem(selected = selectedTab==2, onClick = {selectedTab=2}, icon = {Text("🚀")}, label = {Text("MTZ")})
                        }
                    }
                ) { pad ->
                    LazyColumn(modifier = Modifier.fillMaxSize().background(dark).padding(pad).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        item {
                            Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = orange)) {
                                Column(Modifier.padding(20.dp)) {
                                    Text("Welcome Fouad", fontSize = 22.sp, fontWeight = FontWeight.Black, color = Color.Black)
                                    Text("HyperOS 3 Full Orange Engine Ready", color = Color.Black.copy(0.8f))
                                }
                            }
                        }
                        item { StudioCard("Super Icons","Orange rounded icons, HyperOS 3 style") }
                        item { StudioCard("Control Center Blur","Enable HyperOS 3 blur + Orange accent") }
                        item { StudioCard("SystemUI Orange","Status bar, Volume panel orange theme") }
                        item { StudioCard("Boot Animation","Custom Orange HyperOS boot") }
                        item { StudioCard("AOD Style","Always on Display Orange edition") }
                        item {
                            Button(onClick = {}, modifier = Modifier.fillMaxWidth().height(56.dp), colors = ButtonDefaults.buttonColors(containerColor = orange), shape = RoundedCornerShape(16.dp)) {
                                Text("BUILD MTZ NOW", fontWeight = FontWeight.Black, fontSize = 16.sp, color = Color.Black)
                            }
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun StudioCard(title:String, desc:String){
    var checked by remember { mutableStateOf(false) }
    Card(shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))) {
        Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween){
            Column(Modifier.weight(1f)){
                Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(desc, color = Color.Gray, fontSize = 12.sp)
            }
            Switch(checked = checked, onCheckedChange = {checked=it}, colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFFFF6A00), checkedTrackColor = Color(0xFFFF6A00).copy(0.5f)))
        }
    }
}
