package com.fouad.hyperos3studio

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import kotlin.math.roundToInt

data class ThemePart(val id:String, val title:String, val emoji:String)

fun buildRealMtz(context: Context, parts: List<ThemePart>): File {
    val themeName = "HyperOS_Orange_Fouad"
    val outFile = File(context.getExternalFilesDir(null), "$themeName.mtz")
    ZipOutputStream(FileOutputStream(outFile)).use { zip ->
        val desc = """<?xml version="1.0" encoding="utf-8"?><MIUI-Theme><title>$themeName</title><designer>Fouad</designer><author>Fouad</author><version>1.0</version><uiVersion>18</uiVersion></MIUI-Theme>"""
        zip.putNextEntry(ZipEntry("description.xml"))
        zip.write(desc.toByteArray())
        zip.closeEntry()
        zip.putNextEntry(ZipEntry("wallpaper/default_wallpaper.jpg"))
        zip.write(ByteArray(0))
        zip.closeEntry()
    }
    try { outFile.copyTo(File("/storage/emulated/0/Download/$themeName.mtz"), true) } catch(e:Exception){}
    return outFile
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val orange = Color(0xFFFF6A00)
            val dark = Color(0xFF0F0F0F)
            var selectedTab by remember { mutableStateOf(0) }
            var iconShape by remember { mutableStateOf(20) }
            var iconColor by remember { mutableStateOf(orange) }
            var systemBlur by remember { mutableStateOf(50f) }
            var statusColor by remember { mutableStateOf(orange) }
            var lockWallpaperUri by remember { mutableStateOf<Uri?>(null) }
            var generatedWallpapers by remember { mutableStateOf(listOf<Brush>()) }
            var selectedBrush by remember { mutableStateOf<Brush?>(null) }
            var draggingItem by remember { mutableStateOf<ThemePart?>(null) }
            var dragOffset by remember { mutableStateOf(Offset.Zero) }
            val context = LocalContext.current

            val sourceParts = listOf(
                ThemePart("icons","Super Icons","🎨"),
                ThemePart("wallpaper","Wallpaper","🖼️"),
                ThemePart("control_center","Control","🎛️"),
                ThemePart("systemui","SystemUI","📱"),
                ThemePart("boot","Boot Anim","🚀"),
                ThemePart("lockscreen","Lockscreen","🔒")
            )
            var droppedIcons by remember { mutableStateOf(listOf<ThemePart>()) }
            var droppedSystem by remember { mutableStateOf(listOf<ThemePart>()) }
            var droppedLock by remember { mutableStateOf(listOf<ThemePart>()) }
            var droppedBoot by remember { mutableStateOf(listOf<ThemePart>()) }
            var iconsRect by remember { mutableStateOf(Rect.Zero) }
            var systemRect by remember { mutableStateOf(Rect.Zero) }
            var lockRect by remember { mutableStateOf(Rect.Zero) }
            var bootRect by remember { mutableStateOf(Rect.Zero) }

            fun handleDrop(pos: Offset, item: ThemePart){
                when {
                    iconsRect.contains(pos) -> if(!droppedIcons.contains(item)) droppedIcons = droppedIcons + item
                    systemRect.contains(pos) -> if(!droppedSystem.contains(item)) droppedSystem = droppedSystem + item
                    lockRect.contains(pos) -> if(!droppedLock.contains(item)) droppedLock = droppedLock + item
                    bootRect.contains(pos) -> if(!droppedBoot.contains(item)) droppedBoot = droppedBoot + item
                }
            }

            val pickLockImage = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri -> if(uri!=null) lockWallpaperUri = uri }

            MaterialTheme(colorScheme = darkColorScheme(primary = orange, background = dark, surface = Color(0xFF1A1A1A))) {
                Scaffold(
                    topBar = {
                        CenterAlignedTopAppBar(
                            title = { Text("HyperOS 3 Orange Studio Pro", fontWeight = FontWeight.Black, fontSize = 14.sp, color = Color.Black) },
                            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = orange)
                        )
                    },
                    bottomBar = {
                        NavigationBar(containerColor = Color(0xFF1A1A1A)){
                            NavigationBarItem(selected = selectedTab==0, onClick = {selectedTab=0}, icon={Text("🖱️")}, label={Text("Drag", fontSize=9.sp)})
                            NavigationBarItem(selected = selectedTab==1, onClick = {selectedTab=1}, icon={Text("🎨")}, label={Text("Icons", fontSize=9.sp)})
                            NavigationBarItem(selected = selectedTab==2, onClick = {selectedTab=2}, icon={Text("📱")}, label={Text("SystemUI", fontSize=9.sp)})
                            NavigationBarItem(selected = selectedTab==3, onClick = {selectedTab=3}, icon={Text("🖼️")}, label={Text("Wall", fontSize=9.sp)})
                            NavigationBarItem(selected = selectedTab==4, onClick = {selectedTab=4}, icon={Text("👁️")}, label={Text("Preview", fontSize=9.sp)})
                        }
                    }
                ) { pad ->
                    Box(Modifier.fillMaxSize().background(dark).padding(pad)){
                        when(selectedTab){
                            0 -> {
                                LazyColumn(Modifier.fillMaxSize().padding(10.dp), verticalArrangement=Arrangement.spacedBy(8.dp)){
                                    item{ Text("اسحب لتكوين الثيم - Drag & Drop", color=Color.White, fontSize=11.sp, fontWeight=FontWeight.Bold) }
                                    item{
                                        LazyRow(horizontalArrangement=Arrangement.spacedBy(6.dp)){
                                            items(sourceParts){ part ->
                                                var startPos by remember { mutableStateOf(Offset.Zero) }
                                                Card(
                                                    Modifier.width(75.dp).height(75.dp)
                                                        .onGloballyPositioned{ val p=it.positionInWindow(); startPos=p+Offset(it.size.width/2f,it.size.height/2f) }
                                                        .pointerInput(part){
                                                            detectDragGestures(
                                                                onDragStart={ draggingItem=part; dragOffset=startPos },
                                                                onDragEnd={ draggingItem?.let{ handleDrop(dragOffset,it) }; draggingItem=null },
                                                                onDrag={ _, amt -> dragOffset+=amt }
                                                            )
                                                        },
                                                    shape=RoundedCornerShape(12.dp), colors=CardDefaults.cardColors(containerColor=Color(0xFF1E1E1E))
                                                ){ Column(Modifier.fillMaxSize(), verticalArrangement=Arrangement.Center, horizontalAlignment=Alignment.CenterHorizontally){ Text(part.emoji, fontSize=20.sp); Text(part.title, color=Color.White, fontSize=7.sp, fontWeight=FontWeight.Bold) } }
                                            }
                                        }
                                    }
                                    item{
                                        Row(Modifier.fillMaxWidth(), horizontalArrangement=Arrangement.spacedBy(6.dp)){
                                            DropBox("Icons Pack", droppedIcons, Modifier.weight(1f).onGloballyPositioned{ iconsRect=it.boundsInWindow() }, orange)
                                            DropBox("SystemUI", droppedSystem, Modifier.weight(1f).onGloballyPositioned{ systemRect=it.boundsInWindow() }, orange)
                                        }
                                    }
                                    item{
                                        Row(Modifier.fillMaxWidth(), horizontalArrangement=Arrangement.spacedBy(6.dp)){
                                            DropBox("Lockscreen", droppedLock, Modifier.weight(1f).onGloballyPositioned{ lockRect=it.boundsInWindow() }, orange)
                                            DropBox("Boot & Sound", droppedBoot, Modifier.weight(1f).onGloballyPositioned{ bootRect=it.boundsInWindow() }, orange)
                                        }
                                    }
                                    item{
                                        val total = droppedIcons.size + droppedSystem.size + droppedLock.size + droppedBoot.size
                                        val all = droppedIcons + droppedSystem + droppedLock + droppedBoot
                                        Button(onClick={
                                            if(all.isEmpty()) Toast.makeText(context,"اسحب عناصر الاول!", Toast.LENGTH_SHORT).show()
                                            else { val f=buildRealMtz(context, all); Toast.makeText(context,"✅ MTZ اتبنى: ${f.name} في Download", Toast.LENGTH_LONG).show() }
                                        }, modifier=Modifier.fillMaxWidth().height(50.dp), colors=ButtonDefaults.buttonColors(containerColor=orange), shape=RoundedCornerShape(12.dp)){ Text("BUILD MTZ NOW ($total)", color=Color.Black, fontWeight=FontWeight.Black, fontSize=12.sp) }
                                    }
                                }
                            }
                            1 -> {
                                LazyColumn(Modifier.fillMaxSize().padding(12.dp), verticalArrangement=Arrangement.spacedBy(12.dp)){
                                    item{ Text("تخصيص الايقونات", color=Color.White, fontWeight=FontWeight.Black, fontSize=14.sp) }
                                    item{
                                        Card(shape=RoundedCornerShape(16.dp), colors=CardDefaults.cardColors(containerColor=Color(0xFF1E1E1E))){
                                            Column(Modifier.padding(12.dp)){
                                                Text("شكل الايقونة: ${iconShape}dp", color=Color.Gray, fontSize=11.sp)
                                                Slider(value=iconShape.toFloat(), onValueChange={iconShape=it.toInt()}, valueRange=0f..50f, colors=SliderDefaults.colors(thumbColor=orange, activeTrackColor=orange))
                                                Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){
                                                    listOf(0,10,20,30,50).forEach{ r ->
                                                        Box(Modifier.size(45.dp).clip(RoundedCornerShape(r.dp)).background(iconColor).clickable{ iconShape=r }.border(2.dp, if(iconShape==r) Color.White else Color.Transparent, RoundedCornerShape(r.dp)), contentAlignment=Alignment.Center){ Text("📱", fontSize=18.sp) }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    item{
                                        Card(shape=RoundedCornerShape(16.dp), colors=CardDefaults.cardColors(containerColor=Color(0xFF1E1E1E))){
                                            Column(Modifier.padding(12.dp)){
                                                Text("لون الايقونات + توليد", color=Color.Gray, fontSize=11.sp)
                                                Spacer(Modifier.height(8.dp))
                                                LazyRow(horizontalArrangement=Arrangement.spacedBy(8.dp)){
                                                    items(listOf(Color(0xFFFF6A00), Color(0xFF00D1FF), Color(0xFF00FF88), Color(0xFFFF0055), Color.White, Color(0xFF9C27B0))){
                                                        Box(Modifier.size(38.dp).clip(CircleShape).background(it).border(2.dp, if(iconColor==it) Color.White else Color.Transparent, CircleShape).clickable{ iconColor=it })
                                                    }
                                                }
                                                Spacer(Modifier.height(8.dp))
                                                Button(onClick={ iconColor = listOf(Color(0xFFFF6A00), Color(0xFF00D1FF), Color(0xFFFF0055), Color(0xFF00FF88), Color(0xFF9C27B0)).random() }, colors=ButtonDefaults.buttonColors(containerColor=orange)){ Text("🎨 توليد لون عشوائي", color=Color.Black, fontSize=10.sp, fontWeight=FontWeight.Black) }
                                            }
                                        }
                                    }
                                    item{
                                        LazyVerticalGrid(columns=GridCells.Fixed(4), modifier=Modifier.height(220.dp), verticalArrangement=Arrangement.spacedBy(8.dp), horizontalArrangement=Arrangement.spacedBy(8.dp)){
                                            items(16){ idx ->
                                                Box(Modifier.size(60.dp).clip(RoundedCornerShape(iconShape.dp)).background(iconColor).border(1.dp, Color.Gray.copy(0.2f), RoundedCornerShape(iconShape.dp)), contentAlignment=Alignment.Center){ Text(listOf("📞","💬","📷","🎵","⚙️","📁","🌐","📧")[idx%8], fontSize=22.sp) }
                                            }
                                        }
                                    }
                                }
                            }
                            2 -> {
                                LazyColumn(Modifier.fillMaxSize().padding(12.dp), verticalArrangement=Arrangement.spacedBy(12.dp)){
                                    item{ Text("تخصيص SystemUI", color=Color.White, fontWeight=FontWeight.Black, fontSize=14.sp) }
                                    item{
                                        Card(shape=RoundedCornerShape(16.dp), colors=CardDefaults.cardColors(containerColor=Color(0xFF1E1E1E))){
                                            Column(Modifier.padding(12.dp), verticalArrangement=Arrangement.spacedBy(8.dp)){
                                                Text("Status Bar", color=orange, fontWeight=FontWeight.Bold, fontSize=12.sp)
                                                Row(Modifier.fillMaxWidth().height(40.dp).clip(RoundedCornerShape(8.dp)).background(Color.Black).padding(8.dp), verticalAlignment=Alignment.CenterVertically, horizontalArrangement=Arrangement.SpaceBetween){
                                                    Text("9:41", color=Color.White, fontSize=12.sp, fontWeight=FontWeight.Bold)
                                                    Box(Modifier.size(18.dp).clip(CircleShape).background(statusColor))
                                                }
                                                LazyRow(horizontalArrangement=Arrangement.spacedBy(6.dp)){
                                                    items(listOf(Color(0xFFFF6A00), Color.Transparent, Color.Black, Color(0xFF00D1FF), Color.White)){ c ->
                                                        Box(Modifier.size(35.dp).clip(CircleShape).background(c).border(2.dp, if(statusColor==c) Color.White else Color.Gray.copy(0.3f), CircleShape).clickable{ statusColor=c })
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    item{
                                        Card(shape=RoundedCornerShape(16.dp), colors=CardDefaults.cardColors(containerColor=Color(0xFF1E1E1E))){
                                            Column(Modifier.padding(12.dp)){
                                                Text("Control Center Blur: ${systemBlur.toInt()}%", color=Color.White, fontSize=11.sp)
                                                Slider(value=systemBlur, onValueChange={systemBlur=it}, valueRange=0f..100f, colors=SliderDefaults.colors(thumbColor=orange, activeTrackColor=orange))
                                                Box(Modifier.fillMaxWidth().height(70.dp).clip(RoundedCornerShape(12.dp)).background(Brush.linearGradient(listOf(statusColor.copy(0.8f), Color.Black.copy(0.5f)))), contentAlignment=Alignment.Center){ Text("Control Center Preview", color=Color.White, fontSize=10.sp) }
                                            }
                                        }
                                    }
                                    item{
                                        Button(onClick={ statusColor = listOf(Color(0xFFFF6A00), Color(0xFF00D1FF), Color(0xFF9C27B0), Color(0xFF4CAF50)).random(); systemBlur = (0..100).random().toFloat() }, modifier=Modifier.fillMaxWidth(), colors=ButtonDefaults.buttonColors(containerColor=orange)){ Text("✨ توليد SystemUI عشوائي", color=Color.Black, fontWeight=FontWeight.Black) }
                                    }
                                }
                            }
                            3 -> {
                                LazyColumn(Modifier.fillMaxSize().padding(12.dp), verticalArrangement=Arrangement.spacedBy(12.dp)){
                                    item{ Text("خلفيات Lockscreen & System", color=Color.White, fontWeight=FontWeight.Black, fontSize=14.sp) }
                                    item{
                                        Card(Modifier.fillMaxWidth().height(120.dp).clickable{ pickLockImage.launch("image/*") }, shape=RoundedCornerShape(14.dp), colors=CardDefaults.cardColors(containerColor=Color(0xFF1E1E1E))){
                                            Box(Modifier.fillMaxSize(), contentAlignment=Alignment.Center){
                                                if(lockWallpaperUri!=null) Text("✅ تم اختيار صورة Lockscreen", color=orange, fontSize=11.sp)
                                                else Column(horizontalAlignment=Alignment.CenterHorizontally){ Text("🔒", fontSize=28.sp); Text("اختر صورة Lockscreen من الاستديو", color=Color.Gray, fontSize=10.sp) }
                                            }
                                        }
                                    }
                                    item{
                                        Button(onClick={
                                            val brushes = listOf(
                                                Brush.linearGradient(listOf(Color(0xFFFF6A00), Color(0xFF1A1A1A))),
                                                Brush.linearGradient(listOf(Color(0xFF00D1FF), Color(0xFF0F0F0F))),
                                                Brush.linearGradient(listOf(Color(0xFFFF0055), Color(0xFFFF6A00))),
                                                Brush.linearGradient(listOf(Color(0xFF00FF88), Color.Black)),
                                                Brush.radialGradient(listOf(Color(0xFFFF6A00), Color.Black))
                                            )
                                            generatedWallpapers = brushes
                                            selectedBrush = brushes.random()
                                        }, modifier=Modifier.fillMaxWidth(), colors=ButtonDefaults.buttonColors(containerColor=orange)){ Text("🎨 توليد 5 خلفيات Gradient احترافية", color=Color.Black, fontWeight=FontWeight.Black, fontSize=11.sp) }
                                    }
                                    item{
                                        if(generatedWallpapers.isNotEmpty()){
                                            LazyRow(horizontalArrangement=Arrangement.spacedBy(8.dp)){
                                                items(generatedWallpapers){ brush ->
                                                    Box(Modifier.size(75.dp).clip(RoundedCornerShape(12.dp)).background(brush).border(2.dp, if(selectedBrush==brush) orange else Color.Transparent, RoundedCornerShape(12.dp)).clickable{ selectedBrush=brush })
                                                }
                                            }
                                        }
                                    }
                                    item{
                                        if(selectedBrush!=null){
                                            Box(Modifier.fillMaxWidth().height(180.dp).clip(RoundedCornerShape(16.dp)).background(selectedBrush!!), contentAlignment=Alignment.Center){ Text("Lockscreen Preview\n9:41\nMon 12 May", color=Color.White, fontWeight=FontWeight.Bold) }
                                        }
                                    }
                                }
                            }
                            4 -> {
                                LazyColumn(Modifier.fillMaxSize().padding(12.dp), verticalArrangement=Arrangement.spacedBy(12.dp)){
                                    item{ Text("معاينة مباشرة Live - كل تغييراتك هنا", color=Color.White, fontWeight=FontWeight.Black, fontSize=13.sp) }
                                    item{
                                        Box(Modifier.fillMaxWidth().height(420.dp).clip(RoundedCornerShape(24.dp)).background(selectedBrush?: Brush.linearGradient(listOf(dark, Color(0xFF1A1A1A)))).border(3.dp, Color.Gray.copy(0.3f), RoundedCornerShape(24.dp))){
                                            Column(Modifier.fillMaxSize()){
                                                Row(Modifier.fillMaxWidth().height(32.dp).background(statusColor.copy(0.9f)).padding(horizontal=12.dp), verticalAlignment=Alignment.CenterVertically, horizontalArrangement=Arrangement.SpaceBetween){
                                                    Text("9:41", color=Color.White, fontSize=12.sp, fontWeight=FontWeight.Bold)
                                                    Text("🔋 42%", color=Color.White, fontSize=10.sp)
                                                }
                                                Box(Modifier.fillMaxWidth().weight(1f), contentAlignment=Alignment.Center){
                                                    Column(horizontalAlignment=Alignment.CenterHorizontally){
                                                        Text("Mon, 12 May", color=Color.White.copy(0.7f), fontSize=12.sp)
                                                        Text("9:41", color=Color.White, fontSize=32.sp, fontWeight=FontWeight.Black)
                                                        Spacer(Modifier.height(12.dp))
                                                        Box(Modifier.size(60.dp).clip(RoundedCornerShape(iconShape.dp)).background(iconColor), contentAlignment=Alignment.Center){ Text("🔒", fontSize=28.sp) }
                                                    }
                                                }
                                                Box(Modifier.fillMaxWidth().height(80.dp).background(Color.Black.copy(0.6f)).padding(8.dp)){
                                                    LazyRow(horizontalArrangement=Arrangement.spacedBy(8.dp)){
                                                        items(6){ idx ->
                                                            Box(Modifier.size(50.dp).clip(RoundedCornerShape(iconShape.dp)).background(iconColor).border(1.dp, Color.White.copy(0.2f), RoundedCornerShape(iconShape.dp)), contentAlignment=Alignment.Center){ Text(listOf("📞","💬","📷","🎵","⚙️","🌐")[idx], fontSize=20.sp) }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    item{
                                        Card(shape=RoundedCornerShape(12.dp), colors=CardDefaults.cardColors(containerColor=Color(0xFF1E1E1E))){
                                            Column(Modifier.padding(12.dp)){
                                                Text("ملخص الثيم", color=orange, fontWeight=FontWeight.Bold, fontSize=12.sp)
                                                Text("• شكل الايقونة: ${iconShape}dp - لون: ${iconColor}", color=Color.Gray, fontSize=10.sp)
                                                Text("• Blur: ${systemBlur.toInt()}% - Status: ${statusColor}", color=Color.Gray, fontSize=10.sp)
                                                Text("• خلفية: ${if(lockWallpaperUri!=null) "صورة مخصصة" else if(selectedBrush!=null) "Gradient مولد" else "افتراضي"}", color=Color.Gray, fontSize=10.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        draggingItem?.let { item ->
                            Card(
                                Modifier.offset { androidx.compose.ui.unit.IntOffset(dragOffset.x.roundToInt(), dragOffset.y.roundToInt()) }.width(75.dp).height(75.dp).border(2.dp, Color.White, RoundedCornerShape(12.dp)),
                                shape=RoundedCornerShape(12.dp), colors=CardDefaults.cardColors(containerColor=orange)
                            ){
                                Column(Modifier.fillMaxSize(), verticalArrangement=Arrangement.Center, horizontalAlignment=Alignment.CenterHorizontally){ Text(item.emoji, fontSize=20.sp); Text(item.title, color=Color.Black, fontSize=7.sp, fontWeight=FontWeight.Black) }
                            }
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun DropBox(title:String, items:List<ThemePart>, modifier: Modifier, orange:Color){
    Card(modifier=modifier.height(100.dp).border(2.dp, if(items.isEmpty()) Color.Gray.copy(0.3f) else orange, RoundedCornerShape(14.dp)), shape=RoundedCornerShape(14.dp), colors=CardDefaults.cardColors(containerColor=if(items.isEmpty()) Color(0xFF151515) else Color(0xFF1E1E1E))) {
        Column(Modifier.fillMaxSize().padding(8.dp)){
            Text(title, color=if(items.isEmpty()) Color.Gray else orange, fontWeight=FontWeight.Black, fontSize=10.sp)
            if(items.isEmpty()) Box(Modifier.fillMaxSize(), contentAlignment=Alignment.Center){ Text("اسحب هنا", color=Color.Gray.copy(0.5f), fontSize=8.sp) }
            else Column{ items.take(2).forEach{ Text("• ${it.emoji} ${it.title}", color=Color.White, fontSize=9.sp) } }
        }
    }
}
