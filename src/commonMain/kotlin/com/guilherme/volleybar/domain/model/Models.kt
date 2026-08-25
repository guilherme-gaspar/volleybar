package com.guilherme.volleybar.domain.model

import kotlinx.serialization.Serializable

@Serializable enum class Position(val label: String) { SETTER("Levantador"), OPPOSITE("Oposto"), OUTSIDE("Ponteiro"), MIDDLE("Central"), LIBERO("Líbero") }
@Serializable enum class AttributeType(val label: String) { ATTACK("Ataque"), DEFENSE("Defesa"), SERVE("Saque"), BLOCK("Bloqueio"), SETTING("Levantamento"), SPEED("Velocidade"), STAMINA("Resistência") }

@Serializable data class PlayerAttributes(val attack:Int, val defense:Int, val serve:Int, val block:Int, val setting:Int, val speed:Int, val stamina:Int) {
    fun value(type: AttributeType)=when(type){AttributeType.ATTACK->attack;AttributeType.DEFENSE->defense;AttributeType.SERVE->serve;AttributeType.BLOCK->block;AttributeType.SETTING->setting;AttributeType.SPEED->speed;AttributeType.STAMINA->stamina}
    fun upgraded(type: AttributeType)=when(type){AttributeType.ATTACK->copy(attack=(attack+1).coerceAtMost(100));AttributeType.DEFENSE->copy(defense=(defense+1).coerceAtMost(100));AttributeType.SERVE->copy(serve=(serve+1).coerceAtMost(100));AttributeType.BLOCK->copy(block=(block+1).coerceAtMost(100));AttributeType.SETTING->copy(setting=(setting+1).coerceAtMost(100));AttributeType.SPEED->copy(speed=(speed+1).coerceAtMost(100));AttributeType.STAMINA->copy(stamina=(stamina+1).coerceAtMost(100))}
}
@Serializable data class Player(val id:String,val name:String,val number:Int,val position:Position,val level:Int=1,val experience:Int=0,val energy:Int=100,val maxEnergy:Int=100,val attributes:PlayerAttributes,val trainingPoints:Int=0,val starter:Boolean=false) { val xpForNextLevel:Int get()=100+(level-1)*50 }
@Serializable data class Team(val name:String="VolleyBar",val players:List<Player>) { val starters get()=players.filter(Player::starter); val averageLevel get()=players.map(Player::level).average() }
@Serializable data class OpponentTeam(val name:String,val averageLevel:Int,val attack:Int,val defense:Int,val serve:Int,val block:Int,val difficulty:Double)
@Serializable data class MatchSet(val homePoints:Int=0,val awayPoints:Int=0)
@Serializable data class VolleyballMatch(val opponent:OpponentTeam,val sets:List<MatchSet> = listOf(MatchSet()),val homeSets:Int=0,val awaySets:Int=0,val events:List<String> = emptyList(),val finished:Boolean=false,val lastPointHome:Boolean=true)
@Serializable data class Reward(val starterXp:Int,val reserveXp:Int,val teamTrainingPoints:Int,val mvpBonusXp:Int=30)
@Serializable data class MatchResult(val won:Boolean,val sets:List<MatchSet>,val mvpId:String,val reward:Reward,val levelUps:List<String>)
@Serializable data class GameProgress(val team:Team,val matchesPlayed:Int=0,val wins:Int=0,val losses:Int=0,val opponentLevel:Int=1,val teamTrainingPoints:Int=0,val currentMatch:VolleyballMatch?=null)

