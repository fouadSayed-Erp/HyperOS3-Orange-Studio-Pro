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
import androidx.compose.ui.input.pointerInput
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

data class ThemePart(val id:String, val title:String, val emoji:String, val color:Color)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val orange = Color(0xFFFF6A00)
            val dark = Color(0xFF0F0F0F)
            val context = LocalContext.current
            
            var draggingItem by remember { mutableStateOf<ThemePart?>(null) }
            var dragOffset by remember { mutableStateOf(Offset.Zero) }
            var dragStart by remember { mutableStateOf(Offset.Zero) }
            
            val sourceParts = remember {
                listOf(
                    ThemePart("icons","Super Icons","🎨", Color(0xFF1E1E1E)),
                    ThemePart("cc","Control Center","🎛️", Color(0xFF1E1E1E)),
                    ThemePart("status","SystemUI Orange","📱", Color(0xFF1E1E1E)),
                    ThemePart("boot","Boot Animation","🚀", Color(0xFF1E1E1E)),
                    ThemePart("wall","Wallpaper","🖼️", Color(0xFF1E1E1E)),
                    ThemePart("font","Orange Font","🔤", Color(0xFF1E1E1E)),
                    ThemePart("aod","AOD Style","⏰", Color(0xFF1E1E1E)),
                    ThemePart("sound","Sounds","🔊", Color(0xFF1E1E1E))
                )
            }
            
            var droppedIcons by remember { mutableStateOf(listOf<ThemePart>()) }
            var droppedSystem by remember { mutableStateOf(listOf<ThemePart>()) }
            var droppedLockscreen by remember { mutableStateOf(listOf<ThemePart>()) }
            var droppedBoot by remember { mutableStateOf(listOf<ThemePart>()) }
            
            var iconsBounds by remember { mutableStateOf(Rect.Zero) }
            var systemBounds by remember { mutableStateOf(Rect.Zero) }
            var lockBounds by remember { mutableStateOf(Rect.Zero) }
            var bootBounds by remember { mutableStateOf(Rect.Zero) }

            fun handleDrop(pos: Offset, item: ThemePart){
                when {
                    iconsBounds.contains(pos) -> if(!droppedIcons.contains(item)) droppedIcons = droppedIcons + item
                    systemBounds.contains(pos) -> if(!droppedSystem.contains(item)) droppedSystem = droppedSystem + item
                    lockBounds.contains(pos) -> if(!droppedLockscreen.contains(item)) droppedLockscreen = droppedLockscreen + item
                    bootBounds.contains(pos) -> if(!droppedBoot.contains(item)) droppedBoot = droppedBoot + item
                }
            }

            MaterialTheme(colorScheme = darkColorScheme(primary = orange, background = dark, surface = Color(0xFF1A1A1A))) {
                Box(modifier = Modifier.fillMaxSize().background(dark)) {
                    LazyColumn(modifier = Modifier.fillMaxSize().padding(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().height(70.dp).background(orange, RoundedCornerShape(20.dp)), contentAlignment = Alignment.Center) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally){
                                    Text("HyperOS 3 Orange Studio Pro", fontWeight = FontWeight.Black, fontSize = 16.sp, color = Color.Black)
                                    Text("DRAG & DROP BUILDER", fontSize = 10.sp, color = Color.Black.copy(0.6f), fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        item {
                            Text("اسحب العناصر من هنا 👇", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        item {
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(sourceParts) { part ->
                                    var itemPos by remember { mutableStateOf(Offset.Zero) }
                                    Card(
                                        modifier = Modifier.width(90.dp).height(90.dp)
                                            .onGloballyPositioned { itemPos = it.boundsInWindow().center }
                                            .pointerInput(part) {
                                                detectDragGestures(
                                                    onDragStart = { offset ->
                                                        draggingItem = part
                                                        dragStart = itemPos
                                                        dragOffset = itemPos
                                                    },
                                                    onDragEnd = {
                                                        draggingItem?.let { handleDrop(dragOffset, it) }
                                                        draggingItem = null
                                                    },
                                                    onDrag = { _, dragAmount ->
                                                        dragOffset += dragAmount
                                                    }
                                                )
                                            },
                                        shape = RoundedCornerShape(16.dp),
                                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
                                    ) {
                                        Column(Modifier.fillMaxSize().padding(8.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally){
                                            Text(part.emoji, fontSize = 24.sp)
                                            Spacer(Modifier.height(4.dp))
                                            Text(part.title, color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold, maxLines = 2)
                                        }
                                    }
                                }
                            }
                        }
                        item {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)){
                                DropZone("Icons Pack", droppedIcons, Modifier.weight(1f).onGloballyPositioned { iconsBounds = it.boundsInWindow() }, orange)
                                DropZone("SystemUI", droppedSystem, Modifier.weight(1f).onGloballyPositioned { systemBounds = it.boundsInWindow() }, orange)
                            }
                        }
                        item {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)){
                                DropZone("Lockscreen", droppedLockscreen, Modifier.weight(1f).onGloballyPositioned { lockBounds = it.boundsInWindow() }, orange)
                                DropZone("Boot & Sound", droppedBoot, Modifier.weight(1f).onGloballyPositioned { bootBounds = it.boundsInWindow() }, orange)
                            }
                        }
                        item {
                            val total = droppedIcons.size + droppedSystem.size + droppedLockscreen.size + droppedBoot.size
                            Button(
                                onClick = {
                                    if(total==0) Toast.makeText(context,"اسحب عناصر الاول!", Toast.LENGTH_SHORT).show()
                                    else Toast.makeText(context,"ببني ثيم فيه $total عنصر ... MTZ جاهز!", Toast.LENGTH_LONG).show()
                                },
                                modifier = Modifier.fillMaxWidth().height(56.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = orange),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Text("BUILD MTZ NOW ($total) - اضغط بعد السحب", fontWeight = FontWeight.Black, color = Color.Black, fontSize = 12.sp)
                            }
                        }
                        item {
                            if(droppedIcons.isNotEmpty() || droppedSystem.isNotEmpty()){
                                Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))){
                                    Column(Modifier.padding(12.dp)){
                                        Text("الثيم الحالي:", color = orange, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        Text("Icons: ${droppedIcons.joinToString { it.title }}", color = Color.Gray, fontSize = 11.sp)
                                        Text("System: ${droppedSystem.joinToString { it.title }}", color = Color.Gray, fontSize = 11.sp)
                                        Text("Lock: ${droppedLockscreen.joinToString { it.title }}", color = Color.Gray, fontSize = 11.sp)
                                        Text("Boot: ${droppedBoot.joinToString { it.title }}", color = Color.Gray, fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                    draggingItem?.let { item ->
                        Card(
                            modifier = Modifier.offset { IntOffset(dragOffset.x.roundToInt(), dragOffset.y.roundToInt()) }.width(90.dp).height(90.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = orange)
                        ) {
                            Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally){
                                Text(item.emoji, fontSize = 24.sp)
                                Text(item.title, color = Color.Black, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun DropZone(title:String, items:List<ThemePart>, modifier: Modifier, orange:Color){
    Card(modifier = modifier.height(130.dp).border(2.dp, if(items.isEmpty()) Color.Gray.copy(0.3f) else orange, RoundedCornerShape(18.dp)), shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = if(items.isEmpty()) Color(0xFF151515) else Color(0xFF1E1E1E))) {
        Column(Modifier.fillMaxSize().padding(10.dp)){
            Text(title, color = if(items.isEmpty()) Color.Gray else orange, fontWeight = FontWeight.Black, fontSize = 12.sp)
            Spacer(Modifier.height(6.dp))
            if(items.isEmpty()){
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
                    Text("اسحب هنا\nDrop Here", color = Color.Gray.copy(0.5f), fontSize = 10.sp)
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)){
                    items.take(3).forEach { Text("• ${it.emoji} ${it.title}", color = Color.White, fontSize = 10.sp) }
                    if(items.size>3) Text("+${items.size-3} more", color = orange, fontSize = 9.sp)
                }
            }
        }
    }
}
