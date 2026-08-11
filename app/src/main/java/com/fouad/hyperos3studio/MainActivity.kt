package com.fouad.hyperos3studio

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

data class ThemePart(val id:String, val title:String, val emoji:String)

fun buildRealMtz(context: Context, parts: List<ThemePart>): File {
    val name = "HyperOS_Orange_Fouad"
    val out = File(context.getExternalFilesDir(null), "$name.mtz")
    ZipOutputStream(FileOutputStream(out)).use { zip ->
        val desc = "<MIUI-Theme><title>$name</title></MIUI-Theme>"
        zip.putNextEntry(ZipEntry("description.xml"))
        zip.write(desc.toByteArray())
        zip.closeEntry()
    }
    try { out.copyTo(File("/storage/emulated/0/Download/$name.mtz"), true) } catch(e:Exception){}
    return out
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { StudioApp() }
    }
}

@Composable
fun StudioApp(){
    val orange = Color(0xFFFF6A00)
    val dark = Color(0xFF0F0F0F)
    var tab by remember { mutableStateOf(0) }
    var iconShape by remember { mutableStateOf(20) }
    var iconColor by remember { mutableStateOf(orange) }
    var statusColor by remember { mutableStateOf(orange) }
    var selectedBrush by remember { mutableStateOf<Brush?>(null) }
    var brushes by remember { mutableStateOf(listOf<Brush>()) }
    val ctx = LocalContext.current
    val allParts = listOf(ThemePart("icons","Icons","🎨"), ThemePart("wall","Wall","🖼️"), ThemePart("systemui","System","📱"), ThemePart("lock","Lock","🔒"))
    var dropped by remember { mutableStateOf(listOf<ThemePart>()) }

    MaterialTheme(colorScheme = darkColorScheme(primary=orange, background=dark)){
        Column(Modifier.fillMaxSize().background(dark)){
            Box(Modifier.fillMaxWidth().height(50.dp).background(orange), contentAlignment=Alignment.Center){
                Text("HyperOS 3 Orange Studio", fontWeight=FontWeight.Black, color=Color.Black, fontSize=13.sp)
            }
            Box(Modifier.weight(1f)){
                when(tab){
                    0 -> {
                        LazyColumn(Modifier.padding(10.dp), verticalArrangement=Arrangement.spacedBy(8.dp)){
                            item{ LazyRow(horizontalArrangement=Arrangement.spacedBy(6.dp)){ items(allParts){ p -> Card(Modifier.size(68.dp).clickable{ if(!dropped.contains(p)) dropped=dropped+p }, colors=CardDefaults.cardColors(containerColor=Color(0xFF1E1E1E))){ Column(Modifier.fillMaxSize(), verticalArrangement=Arrangement.Center, horizontalAlignment=Alignment.CenterHorizontally){ Text(p.emoji); Text(p.title, fontSize=7.sp, color=Color.White) } } } } }
                            item{ Text("المضاف: ${dropped.size}", color=Color.White, fontSize=10.sp) }
                            item{ Button(onClick={ if(dropped.isEmpty()) Toast.makeText(ctx,"اضف عناصر",Toast.LENGTH_SHORT).show() else { val f=buildRealMtz(ctx,dropped); Toast.makeText(ctx,"MTZ في Download",Toast.LENGTH_LONG).show() } }, colors=ButtonDefaults.buttonColors(containerColor=orange)){ Text("BUILD MTZ", color=Color.Black, fontWeight=FontWeight.Black) } }
                        }
                    }
                    1 -> {
                        Column(Modifier.padding(10.dp)){
                            Text("شكل الايقونة $iconShape", color=Color.White, fontSize=12.sp)
                            Slider(value=iconShape.toFloat(), onValueChange={iconShape=it.toInt()}, valueRange=0f..50f, colors=SliderDefaults.colors(thumbColor=orange, activeTrackColor=orange))
                            LazyRow(horizontalArrangement=Arrangement.spacedBy(6.dp)){ items(listOf(Color(0xFFFF6A00), Color(0xFF00D1FF), Color.White)){ c -> Box(Modifier.size(32.dp).clip(CircleShape).background(c).clickable{ iconColor=c }.border(1.dp, if(iconColor==c) Color.White else Color.Transparent, CircleShape)) } }
                            Spacer(Modifier.height(10.dp))
                            Box(Modifier.size(56.dp).clip(RoundedCornerShape(iconShape.dp)).background(iconColor), contentAlignment=Alignment.Center){ Text("📱") }
                        }
                    }
                    2 -> {
                        Column(Modifier.padding(10.dp)){
                            Text("Status Bar", color=orange, fontWeight=FontWeight.Bold)
                            Row(Modifier.fillMaxWidth().height(32.dp).background(Color.Black).padding(6.dp), horizontalArrangement=Arrangement.SpaceBetween){ Text("9:41", color=Color.White, fontSize=10.sp); Box(Modifier.size(12.dp).clip(CircleShape).background(statusColor)) }
                            LazyRow(horizontalArrangement=Arrangement.spacedBy(5.dp)){ items(listOf(Color(0xFFFF6A00), Color.Black, Color(0xFF00D1FF))){ c -> Box(Modifier.size(28.dp).clip(CircleShape).background(c).clickable{ statusColor=c }.border(1.dp, Color.Gray, CircleShape)) } }
                        }
                    }
                    3 -> {
                        Column(Modifier.padding(10.dp)){
                            Button(onClick={ val list = listOf(Brush.linearGradient(listOf(Color(0xFFFF6A00), Color.Black)), Brush.linearGradient(listOf(Color(0xFF00D1FF), Color.Black)), Brush.radialGradient(listOf(Color(0xFFFF6A00), Color.Black))); brushes=list; selectedBrush=list.random() }, colors=ButtonDefaults.buttonColors(containerColor=orange)){ Text("توليد خلفيات", color=Color.Black) }
                            Spacer(Modifier.height(8.dp))
                            if(brushes.isNotEmpty()){ LazyRow(horizontalArrangement=Arrangement.spacedBy(5.dp)){ items(brushes){ b -> Box(Modifier.size(60.dp).clip(RoundedCornerShape(8.dp)).background(b).clickable{ selectedBrush=b }) } } }
                            Spacer(Modifier.height(8.dp))
                            if(selectedBrush!=null) Box(Modifier.fillMaxWidth().height(140.dp).clip(RoundedCornerShape(12.dp)).background(selectedBrush!!))
                        }
                    }
                    4 -> {
                        Box(Modifier.fillMaxWidth().height(360.dp).padding(10.dp).clip(RoundedCornerShape(16.dp)).background(selectedBrush?: Brush.linearGradient(listOf(dark, Color(0xFF222222)))).border(1.dp, Color.Gray, RoundedCornerShape(16.dp))){
                            Column(Modifier.fillMaxSize()){
                                Row(Modifier.fillMaxWidth().height(28.dp).background(statusColor).padding(horizontal=8.dp), verticalAlignment=Alignment.CenterVertically, horizontalArrangement=Arrangement.SpaceBetween){ Text("9:41", color=Color.White, fontSize=10.sp); Text("39%", color=Color.White, fontSize=8.sp) }
                                Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment=Alignment.Center){ Box(Modifier.size(54.dp).clip(RoundedCornerShape(iconShape.dp)).background(iconColor), contentAlignment=Alignment.Center){ Text("🔒") } }
                            }
                        }
                    }
                }
            }
            Row(Modifier.fillMaxWidth().height(56.dp).background(Color(0xFF111)), horizontalArrangement=Arrangement.SpaceEvenly, verticalAlignment=Alignment.CenterVertically){
                listOf("🧩" to "Parts", "🎨" to "Icons", "📱" to "UI", "🖼️" to "Wall", "👁️" to "Prev").forEachIndexed{ i, pair -> Column(Modifier.clickable{tab=i}.padding(4.dp), horizontalAlignment=Alignment.CenterHorizontally){ Text(pair.first, fontSize=14.sp); Text(pair.second, fontSize=6.sp, color=if(tab==i) orange else Color.Gray) } }
            }
        }
    }
}
