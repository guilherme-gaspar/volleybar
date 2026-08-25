package com.guilherme.volleybar.presentation.theme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
object VolleyColors { val Navy=Color(0xFF080C24);val Purple=Color(0xFF211747);val Cyan=Color(0xFF35D9FF);val Neon=Color(0xFF52F29A);val Pink=Color(0xFFFF4FA3);val Yellow=Color(0xFFFFDA57);val Text=Color(0xFFEAF6FF) }
@Composable fun VolleyBarTheme(content:@Composable()->Unit){MaterialTheme(colorScheme=darkColorScheme(primary=VolleyColors.Cyan,secondary=VolleyColors.Pink,tertiary=VolleyColors.Yellow,background=VolleyColors.Navy,surface=VolleyColors.Purple,onBackground=VolleyColors.Text,onSurface=VolleyColors.Text),typography=Typography(titleLarge=Typography().titleLarge.copy(letterSpacing=2.sp)),content=content)}
