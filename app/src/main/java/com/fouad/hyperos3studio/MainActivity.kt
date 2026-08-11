package com.fouad.hyperos3studio
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

data class ThemePart(val id:String, val title:String, val emoji:String)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val orange = Color(0xFFFF6A00)
            val dark = Color(0xFF0F0F0F)
            val context = LocalContext.current
            var draggingItem by remember { mutableStateOf<ThemePart?>(null) }
            var dragOffset by remember { mutableStateOf(Offset.Zero) }
            val sourceParts = listOf(
                ThemePart("icons","Super Icons","🎨"),
                ThemePart("cc","Control Center","🎛️"),
                ThemePart("status","SystemUI","📱"),
                ThemePart("boot","Boot Anim","🚀"),
                ThemePart("wall","Wallpaper","🖼️"),
                ThemePart("font","Font","🔤"),
                ThemePart("aod","AOD","⏰"),
                ThemePart("sound","Sounds","🔊")
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
            MaterialTheme(colorScheme = darkColorScheme(primary = orange, background = dark, surface = Color(0xFF1A1A1A))) {
                Box(Modifier.fillMaxSize().background(dark)) {
                    LazyColumn(Modifier.fillMaxSize().padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        item {
                            Box(Modifier.fillMaxWidth().height(70.dp).background(orange, RoundedCornerShape(20.dp)), contentAlignment = Alignment.Center){
                                Column(horizontalAlignment = Alignment.CenterHorizontally){
                                    Text("HyperOS 3 Orange Studio Pro", fontWeight = FontWeight.Black, fontSize = 15.sp, color = Color.Black)
                                    Text("DRAG & DROP - اسحب العناصر لتحت", fontSize = 10.sp, color = Color.Black.copy(0.7f), fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        item { Text("المكتبة - اضغط مطولا واسحب 👇", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp) }
                        item {
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)){
                                items(sourceParts){ part ->
                                    var startPos by remember { mutableStateOf(Offset.Zero) }
                                    Card(
                                        Modifier.width(85.dp).height(85.dp)
                                            .onGloballyPositioned { 
                                                val p = it.positionInWindow()
                                                startPos = p + Offset(it.size.width/2f, it.size.height/2f)
                                            }
                                            .pointerInput(part){
                                                detectDragGestures(
                                                    onDragStart = {
                                                        draggingItem = part
                                                        dragOffset = startPos
                                                    },
                                                    onDragEnd = {
                                                        draggingItem?.let { handleDrop(dragOffset, it) }
                                                        draggingItem = null
                                                    },
                                                    onDrag = { _, amt -> dragOffset += amt }
                                                )
                                            },
                                        shape = RoundedCornerShape(16.dp),
                                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
                                    ){
                                        Column(Modifier.fillMaxSize().padding(6.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally){
                                            Text(part.emoji, fontSize = 22.sp)
                                            Text(part.title, color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                        item {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)){
                                DropBox("Icons Pack", droppedIcons, Modifier.weight(1f).onGloballyPositioned { iconsRect = it.boundsInWindow() }, orange)
                                DropBox("SystemUI", droppedSystem, Modifier.weight(1f).onGloballyPositioned { systemRect = it.boundsInWindow() }, orange)
                            }
                        }
                        item {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)){
                                DropBox("Lockscreen", droppedLock, Modifier.weight(1f).onGloballyPositioned { lockRect = it.boundsInWindow() }, orange)
                                DropBox("Boot & Sound", droppedBoot, Modifier.weight(1f).onGloballyPositioned { bootRect = it.boundsInWindow() }, orange)
                            }
                        }
                        item {
                            val total = droppedIcons.size + droppedSystem.size + droppedLock.size + droppedBoot.size
                            Button(
                                onClick = {
                                    if(total==0) Toast.makeText(context,"اسحب عناصر الاول!", Toast.LENGTH_SHORT).show()
                                    else Toast.makeText(context,"تم بناء ثيم فيه $total عنصر - MTZ جاهز!", Toast.LENGTH_LONG).show()
                                },
                                modifier = Modifier.fillMaxWidth().height(56.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = orange),
                                shape = RoundedCornerShape(16.dp)
                            ){
                                Text("BUILD MTZ NOW ($total)", fontWeight = FontWeight.Black, color = Color.Black, fontSize = 13.sp)
                            }
                        }
                    }
                    draggingItem?.let { item ->
                        Card(
                            Modifier.offset { IntOffset(dragOffset.x.roundToInt(), dragOffset.y.roundToInt()) }.width(85.dp).height(85.dp).border(2.dp, Color.White, RoundedCornerShape(16.dp)),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = orange)
                        ){
                            Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally){
                                Text(item.emoji, fontSize = 22.sp)
                                Text(item.title, color = Color.Black, fontSize = 8.sp, fontWeight = FontWeight.Black)
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
    Card(modifier = modifier.height(125.dp).border(2.dp, if(items.isEmpty()) Color.Gray.copy(0.3f) else orange, RoundedCornerShape(18.dp)), shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = if(items.isEmpty()) Color(0xFF151515) else Color(0xFF1E1E1E))) {
        Column(Modifier.fillMaxSize().padding(10.dp)){
            Text(title, color = if(items.isEmpty()) Color.Gray else orange, fontWeight = FontWeight.Black, fontSize = 11.sp)
            Spacer(Modifier.height(6.dp))
            if(items.isEmpty()){
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center){ Text("اسحب هنا\nDrop Here", color = Color.Gray.copy(0.5f), fontSize = 9.sp) }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(3.dp)){
                    items.take(3).forEach { Text("• ${it.emoji} ${it.title}", color = Color.White, fontSize = 10.sp) }
                    if(items.size>3) Text("+${items.size-3}", color = orange, fontSize = 9.sp)
                }
            }
        }
    }
}
