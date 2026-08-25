package com.guilherme.volleybar.application
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import com.guilherme.volleybar.domain.usecase.*
import com.guilherme.volleybar.persistence.JsonGameRepository
import com.guilherme.volleybar.presentation.*
import com.guilherme.volleybar.presentation.ui.GameApp
import kotlinx.coroutines.*

fun main()=application{val scope=rememberCoroutineScope();val store=remember{GameStore(JsonGameRepository(),MatchEngine(DefaultRandomProvider()),scope)};val state by store.state.collectAsState();val windowState=rememberWindowState(width=1120.dp,height=760.dp);LaunchedEffect(state.compact){windowState.size=if(state.compact)androidx.compose.ui.unit.DpSize(620.dp,390.dp)else androidx.compose.ui.unit.DpSize(1120.dp,760.dp)};Window(onCloseRequest=::exitApplication,title="VolleyBar",state=windowState,alwaysOnTop=state.compact){GameApp(store)}}

