package com.guilherme.volleybar.presentation

import com.guilherme.volleybar.domain.model.*
import com.guilherme.volleybar.domain.repository.GameRepository
import com.guilherme.volleybar.domain.usecase.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

sealed interface GameScreen { data object Home:GameScreen;data object Team:GameScreen;data class PlayerDetails(val id:String):GameScreen;data object Match:GameScreen;data object Result:GameScreen }
sealed interface GameAction { data class Navigate(val screen:GameScreen):GameAction;data class ToggleStarter(val id:String):GameAction;data class Upgrade(val id:String,val type:AttributeType):GameAction;data object StartMatch:GameAction;data object Rally:GameAction;data object ToggleAuto:GameAction;data object ToggleCompact:GameAction;data object ClearMessage:GameAction }
data class GameUiState(val progress:GameProgress?=null,val screen:GameScreen=GameScreen.Home,val result:MatchResult?=null,val loading:Boolean=true,val auto:Boolean=false,val compact:Boolean=false,val message:String?=null)

class GameStore(private val repository:GameRepository,private val engine:MatchEngine,private val scope:CoroutineScope) {
    private val mutable=MutableStateFlow(GameUiState());val state:StateFlow<GameUiState> = mutable.asStateFlow();private var autoJob:Job?=null
    init { scope.launch { val loaded=repository.load().getOrElse{GameProgress(InitialTeamFactory.create())};mutable.value=GameUiState(progress=loaded,loading=false,message=if(loaded.team.players.size==12)null else "Save inválido recriado.");persist(loaded) } }
    fun dispatch(a:GameAction){when(a){is GameAction.Navigate->mutable.update{it.copy(screen=a.screen)};is GameAction.ToggleStarter->updateTeam{LineupRules.toggle(it,a.id)};is GameAction.Upgrade->updateTeam{team->val p=team.players.firstOrNull{it.id==a.id}?:return@updateTeam Result.failure(IllegalArgumentException("Jogador não encontrado."));ProgressionRules.upgrade(p,a.type).map{up->team.copy(players=team.players.map{if(it.id==a.id)up else it})}};GameAction.StartMatch->start();GameAction.Rally->rally();GameAction.ToggleAuto->toggleAuto();GameAction.ToggleCompact->mutable.update{it.copy(compact=!it.compact)};GameAction.ClearMessage->mutable.update{it.copy(message=null)}}}
    private fun start(){val p=mutable.value.progress?:return;val error=LineupRules.validate(p.team);if(error!=null){mutable.update{it.copy(message=error)};return};val next=p.copy(currentMatch=engine.start(p));mutable.update{it.copy(progress=next,screen=GameScreen.Match,result=null)};persist(next)}
    private fun rally(){val p=mutable.value.progress?:return;val m=p.currentMatch?:return;val (next,tired)=engine.rally(m,p.team);var progress=p.copy(team=tired,currentMatch=next);if(next.finished){val won=next.homeSets>next.awaySets;val reward=ProgressionRules.reward(won);val mvp=tired.starters.maxBy{it.attributes.attack+it.attributes.defense};val before=tired.players.associate{it.id to it.level};val players=tired.players.map{player->val xp=(if(player.starter)reward.starterXp else reward.reserveXp)+(if(player.id==mvp.id)reward.mvpBonusXp else 0);ProgressionRules.addExperience(player.copy(energy=player.maxEnergy),xp)};val levels=players.filter{it.level>(before[it.id]?:it.level)}.map{it.name};progress=progress.copy(team=tired.copy(players=players),matchesPlayed=p.matchesPlayed+1,wins=p.wins+if(won)1 else 0,losses=p.losses+if(won)0 else 1,opponentLevel=next.opponent.averageLevel,teamTrainingPoints=p.teamTrainingPoints+reward.teamTrainingPoints,currentMatch=null);autoJob?.cancel();mutable.update{it.copy(progress=progress,result=MatchResult(won,next.sets,mvp.id,reward,levels),screen=GameScreen.Result,auto=false)} } else mutable.update{it.copy(progress=progress)};persist(progress)}
    private fun toggleAuto(){if(mutable.value.auto){autoJob?.cancel();mutable.update{it.copy(auto=false)}}else{mutable.update{it.copy(auto=true)};autoJob=scope.launch{while(isActive&&mutable.value.progress?.currentMatch!=null){delay(450);rally()}}}}
    private fun updateTeam(block:(Team)->Result<Team>){val p=mutable.value.progress?:return;block(p.team).onSuccess{val n=p.copy(team=it);mutable.update{s->s.copy(progress=n,message=null)};persist(n)}.onFailure{e->mutable.update{s->s.copy(message=e.message)}}}
    private fun persist(p:GameProgress)=scope.launch{repository.save(p).onFailure{e->println("VolleyBar save error: ${e.message}");mutable.update{it.copy(message="Não foi possível salvar o progresso.")}}}
}

