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
                LazyColumn(modifier = Modifier.fillMaxSize().background(dark).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().height(80.dp).background(orange, RoundedCornerShape(20.dp)), contentAlignment = Alignment.Center) {
                            Text("HyperOS 3 Orange Studio Pro", fontWeight = FontWeight.Black, fontSize = 18.sp, color = Color.Black)
                        }
                    }
                    item {
                        Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = orange)) {
                            Column(Modifier.padding(20.dp)) {
                                Text("Welcome Fouad - Build SUCCESS", fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color.Black)
                                Text("All Systems Ready", color = Color.Black.copy(0.7f))
                            }
                        }
                    }
                    item { ItemCard("Super Icons","Orange rounded icons HyperOS 3") }
                    item { ItemCard("Control Center Blur","Blur + Orange accent") }
                    item { ItemCard("SystemUI Orange","Status bar & Volume panel") }
                    item { ItemCard("Boot Animation","Custom Orange boot") }
                    item { ItemCard("MTZ Builder","Build theme file") }
                    item {
                        Button(onClick = {}, modifier = Modifier.fillMaxWidth().height(56.dp), colors = ButtonDefaults.buttonColors(containerColor = orange), shape = RoundedCornerShape(16.dp)) {
                            Text("BUILD MTZ NOW", fontWeight = FontWeight.Black, color = Color.Black)
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun ItemCard(t:String, d:String){
    var c by remember { mutableStateOf(false) }
    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))) {
        Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically){
            Column(Modifier.weight(1f)){
                Text(t, color = Color.White, fontWeight = FontWeight.Bold)
                Text(d, color = Color.Gray, fontSize = 12.sp)
            }
            Switch(checked = c, onCheckedChange = {c=it}, colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFFFF6A00), checkedTrackColor = Color(0xFFFF6A00).copy(0.5f)))
        }
    }
}
