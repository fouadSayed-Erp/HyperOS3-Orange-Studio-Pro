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
        val desc = "<MIUI-Theme><title>$name</title><designer>Fouad</designer></MIUI-Theme>"
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
        Column(Modifier.fillMaxSize().background(dark)){
            Box(Modifier.fillMaxWidth().height(50.dp).background(orange), contentAlignment=Alignment.Center){
                Text("HyperOS 3 Orange Studio Pro", fontWeight=FontWeight.Black, fontSize=13.sp, color=Color.Black)
            }
            Box(Modifier.weight(1f).fillMaxWidth()){
                when(tab){
                    0 -> PartsTab(allParts, dropped, { p -> if(!dropped.contains(p)) dropped=dropped+p }, { dropped=emptyList() }, ctx)
                    1 -> IconsTab(iconShape, {iconShape=it}, iconColor, {iconColor=it}, orange)
                    2 -> SystemTab(statusColor, {statusColor=it}, blur, {blur=it}, orange)
                    3 -> WallTab(brushes, {brushes=it}, selectedBrush, {selectedBrush=it}, orange)
                    4 -> PreviewTab(iconShape, iconColor, statusColor, selectedBrush, dark, orange)
                }
            }
            Row(Modifier.fillMaxWidth().height(60.dp).background(Color(0xFF1A1A1A)), verticalAlignment=Alignment.CenterVertically, horizontalArrangement=Arrangement.SpaceEvenly){
                TabBtn("🧩", "Parts", tab==0, {tab=0}, orange)
                TabBtn("🎨", "Icons", tab==1, {tab=1}, orange)
                TabBtn("📱", "System", tab==2, {tab=2}, orange)
                TabBtn("🖼️", "Wall", tab==3, {tab=3}, orange)
                TabBtn("👁️", "Preview", tab==4, {tab=4}, orange)
            }
        }
    }
}

@Composable
fun TabBtn(emoji:String, label:String, sel:Boolean, onClick:()->Unit, orange:Color){
    Column(Modifier.clickable{onClick()}.padding(4.dp), horizontalAlignment=Alignment.CenterHorizontally){
        Text(emoji, fontSize=16.sp)
        Text(label, color=if(sel) orange else Color.Gray, fontSize=7.sp, fontWeight=if(sel) FontWeight.Black else FontWeight.Normal)
    }
}

@Composable
fun PartsTab(parts:List<ThemePart>, dropped:List<ThemePart>, onAdd:(ThemePart)->Unit, onClear:()->Unit, ctx:Context){
    LazyColumn(Modifier.fillMaxSize().padding(10.dp), verticalArrangement=Arrangement.spacedBy(8.dp)){
        item{ Text("اضغط لاضافة العنصر", color=Color.White, fontSize=11.sp, fontWeight=FontWeight.Bold) }
        item{
            LazyRow(horizontalArrangement=Arrangement.spacedBy(6.dp)){
                items(parts){ p ->
                    Card(Modifier.width(68.dp).height(68.dp).clickable{ onAdd(p) }, shape=RoundedCornerShape(10.dp), colors=CardDefaults.cardColors(containerColor=Color(0xFF1E1E1E))){
                        Column(Modifier.fillMaxSize(), verticalArrangement=Arrangement.Center, horizontalAlignment=Alignment.CenterHorizontally){
                            Text(p.emoji, fontSize=16.sp)
                            Text(p.title, color=Color.White, fontSize=6.sp, fontWeight=FontWeight.Bold)
                        }
                    }
                }
            }
        }
        item{
            Card(Modifier.fillMaxWidth().height(100.dp).border(1.dp, Color(0xFFFF6A00), RoundedCornerShape(10.dp)), shape=RoundedCornerShape(10.dp), colors=CardDefaults.cardColors(containerColor=Color(0xFF151515))){
                Column(Modifier.padding(8.dp)){
                    Text("الثيم (${dropped.size})", color=Color(0xFFFF6A00), fontSize=9.sp, fontWeight=FontWeight.Black)
                    dropped.forEach{ Text("• ${it.emoji} ${it.title}", color=Color.White, fontSize=8.sp) }
                }
            }
        }
        item{
            Row(horizontalArrangement=Arrangement.spacedBy(6.dp)){
                Button(onClick={onClear()}, colors=ButtonDefaults.buttonColors(containerColor=Color.Gray)){ Text("Clear", fontSize=9.sp) }
                Button(onClick={
                    if(dropped.isEmpty()) Toast.makeText(ctx,"اضف عناصر", Toast.LENGTH_SHORT).show()
                    else { val f=buildRealMtz(ctx, dropped); Toast.makeText(ctx,"MTZ في Download", Toast.LENGTH_LONG).show() }
                }, modifier=Modifier.weight(1f), colors=ButtonDefaults.buttonColors(containerColor=Color(0xFFFF6A00))){ Text("BUILD MTZ (${dropped.size})", color=Color.Black, fontSize=9.sp, fontWeight=FontWeight.Black) }
            }
        }
    }
}

@Composable
fun IconsTab(shape:Int, onShape:(Int)->Unit, color:Color, onColor:(Color)->Unit, orange:Color){
    LazyColumn(Modifier.fillMaxSize().padding(10.dp), verticalArrangement=Arrangement.spacedBy(10.dp)){
        item{ Text("تخصيص الايقونات", color=Color.White, fontWeight=FontWeight.Black, fontSize=13.sp) }
        item{
            Card(shape=RoundedCornerShape(12.dp), colors=CardDefaults.cardColors(containerColor=Color(0xFF1E1E1E))){
                Column(Modifier.padding(10.dp)){
                    Text("الشكل ${shape}dp", color=Color.Gray, fontSize=9.sp)
                    Slider(value=shape.toFloat(), onValueChange={onShape(it.toInt())}, valueRange=0f..50f, colors=SliderDefaults.colors(thumbColor=orange, activeTrackColor=orange))
                    Row(horizontalArrangement=Arrangement.spacedBy(5.dp)){
                        listOf(0,12,20,30,50).forEach{ r ->
                            Box(Modifier.size(38.dp).clip(RoundedCornerShape(r.dp)).background(color).clickable{onShape(r)}.border(1.dp, if(shape==r) Color.White else Color.Transparent, RoundedCornerShape(r.dp)), contentAlignment=Alignment.Center){ Text("📱", fontSize=12.sp) }
                        }
                    }
                }
            }
        }
        item{
            Card(shape=RoundedCornerShape(12.dp), colors=CardDefaults.cardColors(containerColor=Color(0xFF1E1E1E))){
                Column(Modifier.padding(10.dp)){
                    Text("اللون + توليد", color=Color.Gray, fontSize=9.sp)
                    Spacer(Modifier.height(6.dp))
                    LazyRow(horizontalArrangement=Arrangement.spacedBy(5.dp)){
                        items(listOf(Color(0xFFFF6A00), Color(0xFF00D1FF), Color(0xFF00FF88), Color(0xFFFF0055), Color.White)){ c ->
                            Box(Modifier.size(32.dp).clip(CircleShape).background(c).border(1.dp, if(color==c) Color.White else Color.Transparent, CircleShape).clickable{onColor(c)})
                        }
                    }
                    Spacer(Modifier.height(6.dp))
                    Button(onClick={ onColor(listOf(Color(0xFFFF6A00), Color(0xFF00D1FF), Color(0xFFFF0055), Color(0xFF00FF88)).random()) }, colors=ButtonDefaults.buttonColors(containerColor=orange)){ Text("🎨 توليد لون", color=Color.Black, fontSize=8.sp, fontWeight=FontWeight.Black) }
                }
            }
        }
    }
}

@Composable
fun SystemTab(sColor:Color, onSColor:(Color)->Unit, blur:Float, onBlur:(Float)->Unit, orange:Color){
    LazyColumn(Modifier.fillMaxSize().padding(10.dp), verticalArrangement=Arrangement.spacedBy(10.dp)){
        item{ Text("SystemUI & Lockscreen", color=Color.White, fontWeight=FontWeight.Black, fontSize=13.sp) }
        item{
            Card(shape=RoundedCornerShape(12.dp), colors=CardDefaults.cardColors(containerColor=Color(0xFF1E1E1E))){
                Column(Modifier.padding(10.dp)){
                    Text("Status Bar", color=orange, fontSize=10.sp, fontWeight=FontWeight.Bold)
                    Row(Modifier.fillMaxWidth().height(32.dp).clip(RoundedCornerShape(6.dp)).background(Color.Black).padding(6.dp), verticalAlignment=Alignment.CenterVertically, horizontalArrangement=Arrangement.SpaceBetween){
                        Text("9:41", color=Color.White, fontSize=10.sp)
                        Box(Modifier.size(12.dp).clip(CircleShape).background(sColor))
                    }
                    Spacer(Modifier.height(6.dp))
                    LazyRow(horizontalArrangement=Arrangement.spacedBy(4.dp)){
                        items(listOf(Color(0xFFFF6A00), Color.Black, Color(0xFF00D1FF), Color.White)){ c ->
                            Box(Modifier.size(28.dp).clip(CircleShape).background(c).border(1.dp, if(sColor==c) Color.White else Color.Gray, CircleShape).clickable{onSColor(c)})
                        }
                    }
                }
            }
        }
        item{
            Card(shape=RoundedCornerShape(12.dp), colors=CardDefaults.cardColors(containerColor=Color(0xFF1E1E1E))){
                Column(Modifier.padding(10.dp)){
                    Text("Blur ${blur.toInt()}%", color=Color.White, fontSize=9.sp)
                    Slider(value=blur, onValueChange={onBlur(it)}, valueRange=0f..100f, colors=SliderDefaults.colors(thumbColor=orange, activeTrackColor=orange))
                }
            }
        }
    }
}

@Composable
fun WallTab(brushes:List<Brush>, onBrushes:(List<Brush>)->Unit, sel:Brush?, onSel:(Brush?)->Unit, orange:Color){
    LazyColumn(Modifier.fillMaxSize().padding(10.dp), verticalArrangement=Arrangement.spacedBy(10.dp)){
        item{ Text("Wallpaper توليد", color=Color.White, fontWeight=FontWeight.Black, fontSize=13.sp) }
        item{
            Button(onClick={
                val list = listOf(
                    Brush.linearGradient(listOf(Color(0xFFFF6A00), Color(0xFF1A1A1A))),
                    Brush.linearGradient(listOf(Color(0xFF00D1FF), Color.Black)),
                    Brush.linearGradient(listOf(Color(0xFFFF0055), Color(0xFFFF6A00))),
                    Brush.linearGradient(listOf(Color(0xFF00FF88), Color.Black)),
                    Brush.radialGradient(listOf(Color(0xFFFF6A00), Color.Black))
                )
                onBrushes(list)
                onSel(list.random())
            }, modifier=Modifier.fillMaxWidth(), colors=ButtonDefaults.buttonColors(containerColor=orange)){ Text("🎨 توليد 5 خلفيات", color=Color.Black, fontWeight=FontWeight.Black, fontSize=10.sp) }
        }
        item{
            if(brushes.isNotEmpty()){
                LazyRow(horizontalArrangement=Arrangement.spacedBy(5.dp)){
                    items(brushes){ b ->
                        Box(Modifier.size(65.dp).clip(RoundedCornerShape(8.dp)).background(b).border(2.dp, if(sel==b) orange else Color.Transparent, RoundedCornerShape(8.dp)).clickable{onSel(b)})
                    }
                }
            }
        }
        item{
            if(sel!=null){
                Box(Modifier.fillMaxWidth().height(160.dp).clip(RoundedCornerShape(12.dp)).background(sel), contentAlignment=Alignment.Center){
                    Text("Lockscreen\nWallpaper", color=Color.White, fontWeight=FontWeight.Bold, fontSize=12.sp)
                }
            }
        }
    }
}

@Composable
fun PreviewTab(shape:Int, color:Color, sColor:Color, brush:Brush?, dark:Color, orange:Color){
    LazyColumn(Modifier.fillMaxSize().padding(10.dp), verticalArrangement=Arrangement.spacedBy(10.dp)){
        item{ Text("Live Preview - معاينة مباشرة", color=Color.White, fontWeight=FontWeight.Black, fontSize=12.sp) }
        item{
            Box(Modifier.fillMaxWidth().height(380.dp).clip(RoundedCornerShape(18.dp)).background(brush?: Brush.linearGradient(listOf(dark, Color(0xFF222222)))).border(2.dp, Color.Gray.copy(0.3f), RoundedCornerShape(18.dp))){
                Column(Modifier.fillMaxSize()){
                    Row(Modifier.fillMaxWidth().height(28.dp).background(sColor.copy(0.9f)).padding(horizontal=8.dp), verticalAlignment=Alignment.CenterVertically, horizontalArrangement=Arrangement.SpaceBetween){
                        Text("9:41", color=Color.White, fontSize=10.sp, fontWeight=FontWeight.Bold)
                        Text("39%", color=Color.White, fontSize=8.sp)
                    }
                    Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment=Alignment.Center){
                        Column(horizontalAlignment=Alignment.CenterHorizontally){
                            Text("Mon 12 May", color=Color.White.copy(0.7f), fontSize=9.sp)
                            Text("9:41", color=Color.White, fontSize=26.sp, fontWeight=FontWeight.Black)
                            Spacer(Modifier.height(8.dp))
                            Box(Modifier.size(50.dp).clip(RoundedCornerShape(shape.dp)).background(color), contentAlignment=Alignment.Center){ Text("🔒", fontSize=20.sp) }
                        }
                    }
                    Row(Modifier.fillMaxWidth().height(54.dp).background(Color.Black.copy(0.5f)).padding(6.dp), horizontalArrangement=Arrangement.spacedBy(5.dp), verticalAlignment=Alignment.CenterVertically){
                        Box(Modifier.size(40.dp).clip(RoundedCornerShape(shape.dp)).background(color), contentAlignment=Alignment.Center){ Text("📞", fontSize=14.sp) }
                        Box(Modifier.size(40.dp).clip(RoundedCornerShape(shape.dp)).background(color), contentAlignment=Alignment.Center){ Text("💬", fontSize=14.sp) }
                        Box(Modifier.size(40.dp).clip(RoundedCornerShape(shape.dp)).background(color), contentAlignment=Alignment.Center){ Text("📷", fontSize=14.sp) }
                        Box(Modifier.size(40.dp).clip(RoundedCornerShape(shape.dp)).background(color), contentAlignment=Alignment.Center){ Text("⚙️", fontSize=14.sp) }
                    }
                }
            }
        }
    }
}
