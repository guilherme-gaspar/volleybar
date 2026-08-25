package com.guilherme.volleybar.presentation.ui
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.guilherme.volleybar.domain.model.*
import com.guilherme.volleybar.presentation.theme.VolleyColors

@Composable fun PixelButton(text:String,onClick:()->Unit,modifier:Modifier=Modifier,enabled:Boolean=true){Button(onClick=onClick,enabled=enabled,shape=RoundedCornerShape(3.dp),modifier=modifier.border(2.dp,VolleyColors.Cyan,RoundedCornerShape(3.dp))){Text(text.uppercase(),fontWeight=FontWeight.Black)}}
@Composable fun PixelPanel(modifier:Modifier=Modifier,content:@Composable ColumnScope.()->Unit){Column(modifier.background(VolleyColors.Purple,RoundedCornerShape(4.dp)).border(2.dp,Color(0xFF554B85),RoundedCornerShape(4.dp)).padding(12.dp),content=content)}
@Composable fun PixelProgress(value:Float,color:Color,modifier:Modifier=Modifier){Box(modifier.height(8.dp).background(Color(0xFF10152F))){Box(Modifier.fillMaxHeight().fillMaxWidth(value.coerceIn(0f,1f)).background(color))}}
@Composable fun PlayerSprite(player:Player,modifier:Modifier=Modifier){Canvas(modifier.size(58.dp)){val u=if(player.starter)VolleyColors.Cyan else VolleyColors.Pink;drawRect(Color(0xFFEDB98F),Offset(size.width*.35f,0f),Size(size.width*.3f,size.height*.25f));drawRect(Color(0xFF1A1738),Offset(size.width*.31f,0f),Size(size.width*.38f,size.height*.08f));drawRect(u,Offset(size.width*.25f,size.height*.25f),Size(size.width*.5f,size.height*.4f));drawRect(Color.White,Offset(size.width*.46f,size.height*.35f),Size(size.width*.08f,size.height*.12f));drawRect(Color(0xFFEDB98F),Offset(size.width*.12f,size.height*.3f),Size(size.width*.13f,size.height*.3f));drawRect(Color(0xFFEDB98F),Offset(size.width*.75f,size.height*.3f),Size(size.width*.13f,size.height*.3f));drawRect(u,Offset(size.width*.28f,size.height*.65f),Size(size.width*.16f,size.height*.3f));drawRect(u,Offset(size.width*.56f,size.height*.65f),Size(size.width*.16f,size.height*.3f))}}
@Composable fun PlayerCard(player:Player,onToggle:()->Unit,onDetails:()->Unit){PixelPanel(Modifier.width(225.dp)){Row(verticalAlignment=Alignment.CenterVertically){PlayerSprite(player);Spacer(Modifier.width(8.dp));Column{Text("#${player.number} ${player.name}",fontWeight=FontWeight.Bold,color=VolleyColors.Yellow);Text(player.position.label);Text("NÍVEL ${player.level} • ${if(player.starter)"TITULAR" else "BANCO"}",fontSize=11.sp,color=if(player.starter)VolleyColors.Neon else VolleyColors.Pink)}};Text("XP ${player.experience}/${player.xpForNextLevel}",fontSize=10.sp);PixelProgress(player.experience/player.xpForNextLevel.toFloat(),VolleyColors.Yellow,Modifier.fillMaxWidth());Text("ENERGIA ${player.energy}/${player.maxEnergy}",fontSize=10.sp);PixelProgress(player.energy/player.maxEnergy.toFloat(),VolleyColors.Neon,Modifier.fillMaxWidth());Row{TextButton(onClick=onToggle){Text(if(player.starter)"BANCO" else "ESCALAR")};TextButton(onClick=onDetails){Text("DETALHES")}}}}

@Composable fun VolleyballCourt(match:VolleyballMatch,modifier:Modifier=Modifier){Canvas(modifier.aspectRatio(1.9f).border(3.dp,VolleyColors.Cyan)){drawRect(Color(0xFF142855));drawRect(Color(0xFFD97945),Offset(size.width*.08f,size.height*.12f),Size(size.width*.84f,size.height*.76f));drawLine(Color.White,Offset(size.width/2,size.height*.12f),Offset(size.width/2,size.height*.88f),5f);drawLine(Color.White,Offset(size.width*.08f,size.height*.12f),Offset(size.width*.92f,size.height*.12f),3f);drawLine(Color.White,Offset(size.width*.08f,size.height*.88f),Offset(size.width*.92f,size.height*.88f),3f);for(side in 0..1)for(i in 0..5){val x=size.width*(if(side==0).22f+(i%2)*.16f else .62f+(i%2)*.16f);val y=size.height*(.25f+(i/2)*.25f);drawCircle(if(side==0)VolleyColors.Cyan else VolleyColors.Pink,10f,Offset(x,y))};val x=if(match.lastPointHome)size.width*.44f else size.width*.56f;drawCircle(VolleyColors.Yellow,8f,Offset(x,size.height*.35f))}}
