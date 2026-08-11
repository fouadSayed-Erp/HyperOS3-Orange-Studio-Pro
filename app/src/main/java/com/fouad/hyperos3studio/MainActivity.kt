package com.fouad.hyperos3studio

@file:OptIn(ExperimentalMaterial3Api::class)

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
        val desc = "<?xml version=\"1.0\"?><MIUI-Theme><title>$name</title><designer>Fouad</designer></MIUI-Theme>"
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
    var blur by remember { mutableStateOf(50f) }
    var selectedBrush by remember { mutableStateOf<Brush?>(null) }
    var brushes by remember { mutableStateOf(listOf<Brush>()) }
    val ctx = LocalContext.current

    val allParts = listOf(
        ThemePart("icons","Icons","🎨"),
        ThemePart("wall","Wall","🖼️"),
        ThemePart("control","Control","🎛️"),
        ThemePart("systemui","SystemUI","📱"),
        ThemePart("boot","Boot","🚀"),
        ThemePart("lock","Lock","🔒")
    )
    var dropped by remember { mutableStateOf(listOf<ThemePart>()) }

    MaterialTheme(colorScheme = darkColorScheme(primary=orange, background=dark, surface=Color(0xFF1A1A1A))){
        Scaffold(
            topBar = { CenterAlignedTopAppBar(title={ Text("HyperOS 3 Orange Studio Pro", fontWeight=FontWeight.Black, fontSize=13.sp, color=Color.Black) }, colors=TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor=orange)) },
            bottomBar = {
                NavigationBar(containerColor=Color(0xFF1A1A1A)){
                    NavigationBarItem(selected=tab==0, onClick={tab=0}, icon={Text("🧩")}, label={Text("Parts", fontSize=7.sp)})
                    NavigationBarItem(selected=tab==1, onClick={tab=1}, icon={Text("🎨")}, label={Text("Icons", fontSize=7.sp)})
                    NavigationBarItem(selected=tab==2, onClick={tab=2}, icon={Text("📱")}, label={Text("System", fontSize=7.sp)})
                    NavigationBarItem(selected=tab==3, onClick={tab=3}, icon={Text("🖼️")}, label={Text("Wall", fontSize=7.sp)})
                    NavigationBarItem(selected=tab==4, onClick={tab=4}, icon={Text("👁️")}, label={Text("Preview", fontSize=7.sp)})
                }
            }
        ){ pad ->
            Box(Modifier.fillMaxSize().background(dark).padding(pad)){
                when(tab){
                    0 -> {
                        LazyColumn(Modifier.fillMaxSize().padding(10.dp), verticalArrangement=Arrangement.spacedBy(8.dp)){
                            item{ Text("اضغط على العنصر لاضافته - بديل السحب", color=Color.White, fontSize=11.sp, fontWeight=FontWeight.Bold) }
                            item{
                                LazyRow(horizontalArrangement=Arrangement.spacedBy(6.dp)){
                                    items(allParts){ part ->
                                        Card(
                                            Modifier.width(70.dp).height(70.dp).clickable{
                                                if(!dropped.contains(part)) dropped = dropped + part
                                                Toast.makeText(ctx, "${part.title} اضيف", Toast.LENGTH_SHORT).show()
                                            },
                                            shape=RoundedCornerShape(12.dp),
                                            colors=CardDefaults.cardColors(containerColor=Color(0xFF1E1E1E))
                                        ){
                                            Column(Modifier.fillMaxSize(), verticalArrangement=Arrangement.Center, horizontalAlignment=Alignment.CenterHorizontally){
                                                Text(part.emoji, fontSize=18.sp)
                                                Text(part.title, color=Color.White, fontSize=7.sp, fontWeight=FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                            item{
                                Card(Modifier.fillMaxWidth().height(110.dp).border(1.dp, orange, RoundedCornerShape(12.dp)), shape=RoundedCornerShape(12.dp), colors=CardDefaults.cardColors(containerColor=Color(0xFF151515))){
                                    Column(Modifier.padding(8.dp)){
                                        Text("الثيم الحالي (${dropped.size})", color=orange, fontSize=10.sp, fontWeight=FontWeight.Black)
                                        dropped.forEach{ Text("• ${it.emoji} ${it.title}", color=Color.White, fontSize=9.sp) }
                                        if(dropped.isEmpty()) Text("اضغط على العناصر فوق", color=Color.Gray, fontSize=8.sp)
                                    }
                                }
                            }
                            item{
                                Row(horizontalArrangement=Arrangement.spacedBy(6.dp)){
                                    Button(onClick={ dropped = emptyList() }, colors=ButtonDefaults.buttonColors(containerColor=Color.Gray)){ Text("Clear", fontSize=10.sp) }
                                    Button(onClick={
                                        if(dropped.isEmpty()) Toast.makeText(ctx,"اضف عناصر الاول", Toast.LENGTH_SHORT).show()
                                        else { val f=buildRealMtz(ctx, dropped); Toast.makeText(ctx,"✅ ${f.name} في Download", Toast.LENGTH_LONG).show() }
                                    }, modifier=Modifier.weight(1f), colors=ButtonDefaults.buttonColors(containerColor=orange)){ Text("BUILD MTZ (${dropped.size})", color=Color.Black, fontWeight=FontWeight.Black, fontSize=10.sp) }
                                }
                            }
                        }
                    }
                    1 -> {
                        LazyColumn(Modifier.fillMaxSize().padding(12.dp), verticalArrangement=Arrangement.spacedBy(10.dp)){
                            item{ Text("تخصيص الايقونات", color=Color.White, fontWeight=FontWeight.Black, fontSize=14.sp) }
                            item{
                                Card(shape=RoundedCornerShape(14.dp), colors=CardDefaults.cardColors(containerColor=Color(0xFF1E1E1E))){
                                    Column(Modifier.padding(12.dp)){
                                        Text("الشكل ${iconShape}dp", color=Color.Gray, fontSize=10.sp)
                                        Slider(value=iconShape.toFloat(), onValueChange={iconShape=it.toInt()}, valueRange=0f..50f, colors=SliderDefaults.colors(thumbColor=orange, activeTrackColor=orange))
                                        Row(horizontalArrangement=Arrangement.spacedBy(6.dp)){
                                            listOf(0,12,20,30,50).forEach{ r ->
                                                Box(Modifier.size(40.dp).clip(RoundedCornerShape(r.dp)).background(iconColor).clickable{ iconShape=r }.border(1.dp, if(iconShape==r) Color.White else Color.Transparent, RoundedCornerShape(r.dp)), contentAlignment=Alignment.Center){ Text("📱", fontSize=14.sp) }
                                            }
                                        }
                                    }
                                }
                            }
                            item{
                                Card(shape=RoundedCornerShape(14.dp), colors=CardDefaults.cardColors(containerColor=Color(0xFF1E1E1E))){
                                    Column(Modifier.padding(12.dp)){
                                        Text("اللون", color=Color.Gray, fontSize=10.sp)
                                        Spacer(Modifier.height(6.dp))
                                        LazyRow(horizontalArrangement=Arrangement.spacedBy(6.dp)){
                                            items(listOf(Color(0xFFFF6A00), Color(0xFF00D1FF), Color(0xFF00FF88), Color(0xFFFF0055), Color.White, Color(0xFF9C27B0))){ c ->
                                                Box(Modifier.size(34.dp).clip(CircleShape).background(c).border(2.dp, if(iconColor==c) Color.White else Color.Transparent, CircleShape).clickable{ iconColor=c })
                                            }
                                        }
                                        Spacer(Modifier.height(6.dp))
                                        Button(onClick={ iconColor = listOf(Color(0xFFFF6A00), Color(0xFF00D1FF), Color(0xFFFF0055), Color(0xFF00FF88)).random() }, colors=ButtonDefaults.buttonColors(containerColor=orange)){ Text("🎨 توليد لون", color=Color.Black, fontSize=9.sp, fontWeight=FontWeight.Black) }
                                    }
                                }
                            }
                            item{
                                Row(horizontalArrangement=Arrangement.spacedBy(6.dp)){
                                    Box(Modifier.size(56.dp).clip(RoundedCornerShape(iconShape.dp)).background(iconColor), contentAlignment=Alignment.Center){ Text("📞", fontSize=20.sp) }
                                    Box(Modifier.size(56.dp).clip(RoundedCornerShape(iconShape.dp)).background(iconColor), contentAlignment=Alignment.Center){ Text("💬", fontSize=20.sp) }
                                    Box(Modifier.size(56.dp).clip(RoundedCornerShape(iconShape.dp)).background(iconColor), contentAlignment=Alignment.Center){ Text("📷", fontSize=20.sp) }
                                    Box(Modifier.size(56.dp).clip(RoundedCornerShape(iconShape.dp)).background(iconColor), contentAlignment=Alignment.Center){ Text("⚙️", fontSize=20.sp) }
                                }
                            }
                        }
                    }
                    2 -> {
                        LazyColumn(Modifier.fillMaxSize().padding(12.dp), verticalArrangement=Arrangement.spacedBy(10.dp)){
                            item{ Text("SystemUI & Lock Screen", color=Color.White, fontWeight=FontWeight.Black, fontSize=14.sp) }
                            item{
                                Card(shape=RoundedCornerShape(14.dp), colors=CardDefaults.cardColors(containerColor=Color(0xFF1E1E1E))){
                                    Column(Modifier.padding(12.dp), verticalArrangement=Arrangement.spacedBy(6.dp)){
                                        Text("Status Bar", color=orange, fontSize=11.sp, fontWeight=FontWeight.Bold)
                                        Row(Modifier.fillMaxWidth().height(34.dp).clip(RoundedCornerShape(8.dp)).background(Color.Black).padding(8.dp), verticalAlignment=Alignment.CenterVertically, horizontalArrangement=Arrangement.SpaceBetween){
                                            Text("9:41", color=Color.White, fontSize=11.sp)
                                            Box(Modifier.size(14.dp).clip(CircleShape).background(statusColor))
                                        }
                                        LazyRow(horizontalArrangement=Arrangement.spacedBy(5.dp)){
                                            items(listOf(Color(0xFFFF6A00), Color.Black, Color(0xFF00D1FF), Color.White)){ c ->
                                                Box(Modifier.size(30.dp).clip(CircleShape).background(c).border(1.dp, if(statusColor==c) Color.White else Color.Gray, CircleShape).clickable{ statusColor=c })
                                            }
                                        }
                                    }
                                }
                            }
                            item{
                                Card(shape=RoundedCornerShape(14.dp), colors=CardDefaults.cardColors(containerColor=Color(0xFF1E1E1E))){
                                    Column(Modifier.padding(12.dp)){
                                        Text("Blur ${blur.toInt()}%", color=Color.White, fontSize=10.sp)
                                        Slider(value=blur, onValueChange={blur=it}, valueRange=0f..100f, colors=SliderDefaults.colors(thumbColor=orange, activeTrackColor=orange))
                                    }
                                }
                            }
                        }
                    }
                    3 -> {
                        LazyColumn(Modifier.fillMaxSize().padding(12.dp), verticalArrangement=Arrangement.spacedBy(10.dp)){
                            item{ Text("Wallpaper - توليد ثيمات", color=Color.White, fontWeight=FontWeight.Black, fontSize=14.sp) }
                            item{
                                Button(onClick={
                                    val list = listOf(
                                        Brush.linearGradient(listOf(Color(0xFFFF6A00), Color(0xFF1A1A1A))),
                                        Brush.linearGradient(listOf(Color(0xFF00D1FF), Color.Black)),
                                        Brush.linearGradient(listOf(Color(0xFFFF0055), Color(0xFFFF6A00))),
                                        Brush.linearGradient(listOf(Color(0xFF00FF88), Color.Black)),
                                        Brush.radialGradient(listOf(Color(0xFFFF6A00), Color.Black))
                                    )
                                    brushes = list
                                    selectedBrush = list.random()
                                }, modifier=Modifier.fillMaxWidth(), colors=ButtonDefaults.buttonColors(containerColor=orange)){ Text("🎨 توليد 5 خلفيات", color=Color.Black, fontWeight=FontWeight.Black, fontSize=11.sp) }
                            }
                            item{
                                if(brushes.isNotEmpty()){
                                    LazyRow(horizontalArrangement=Arrangement.spacedBy(6.dp)){
                                        items(brushes){ b ->
                                            Box(Modifier.size(70.dp).clip(RoundedCornerShape(10.dp)).background(b).border(2.dp, if(selectedBrush==b) orange else Color.Transparent, RoundedCornerShape(10.dp)).clickable{ selectedBrush=b })
                                        }
                                    }
                                }
                            }
                            item{
                                if(selectedBrush!=null){
                                    Box(Modifier.fillMaxWidth().height(180.dp).clip(RoundedCornerShape(14.dp)).background(selectedBrush!!), contentAlignment=Alignment.Center){
                                        Text("Lockscreen Wallpaper\n9:41", color=Color.White, fontWeight=FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                    4 -> {
                        LazyColumn(Modifier.fillMaxSize().padding(12.dp), verticalArrangement=Arrangement.spacedBy(10.dp)){
                            item{ Text("Live Preview", color=Color.White, fontWeight=FontWeight.Black, fontSize=13.sp) }
                            item{
                                Box(Modifier.fillMaxWidth().height(400.dp).clip(RoundedCornerShape(20.dp)).background(selectedBrush?: Brush.linearGradient(listOf(dark, Color(0xFF222222)))).border(2.dp, Color.Gray.copy(0.3f), RoundedCornerShape(20.dp))){
                                    Column(Modifier.fillMaxSize()){
                                        Row(Modifier.fillMaxWidth().height(30.dp).background(statusColor.copy(0.9f)).padding(horizontal=10.dp), verticalAlignment=Alignment.CenterVertically, horizontalArrangement=Arrangement.SpaceBetween){
                                            Text("9:41", color=Color.White, fontSize=11.sp, fontWeight=FontWeight.Bold)
                                            Text("39%", color=Color.White, fontSize=9.sp)
                                        }
                                        Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment=Alignment.Center){
                                            Column(horizontalAlignment=Alignment.CenterHorizontally){
                                                Text("Mon 12 May", color=Color.White.copy(0.7f), fontSize=10.sp)
                                                Text("9:41", color=Color.White, fontSize=28.sp, fontWeight=FontWeight.Black)
                                                Spacer(Modifier.height(10.dp))
                                                Box(Modifier.size(54.dp).clip(RoundedCornerShape(iconShape.dp)).background(iconColor), contentAlignment=Alignment.Center){ Text("🔒", fontSize=22.sp) }
                                            }
                                        }
                                        Row(Modifier.fillMaxWidth().height(60.dp).background(Color.Black.copy(0.5f)).padding(8.dp), horizontalArrangement=Arrangement.spacedBy(6.dp), verticalAlignment=Alignment.CenterVertically){
                                            Box(Modifier.size(44.dp).clip(RoundedCornerShape(iconShape.dp)).background(iconColor), contentAlignment=Alignment.Center){ Text("📞", fontSize=16.sp) }
                                            Box(Modifier.size(44.dp).clip(RoundedCornerShape(iconShape.dp)).background(iconColor), contentAlignment=Alignment.Center){ Text("💬", fontSize=16.sp) }
                                            Box(Modifier.size(44.dp).clip(RoundedCornerShape(iconShape.dp)).background(iconColor), contentAlignment=Alignment.Center){ Text("📷", fontSize=16.sp) }
                                            Box(Modifier.size(44.dp).clip(RoundedCornerShape(iconShape.dp)).background(iconColor), contentAlignment=Alignment.Center){ Text("⚙️", fontSize=16.sp) }
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
