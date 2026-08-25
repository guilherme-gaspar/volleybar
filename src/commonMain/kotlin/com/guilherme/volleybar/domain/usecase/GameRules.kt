package com.guilherme.volleybar.domain.usecase

import com.guilherme.volleybar.domain.model.*
import kotlin.math.abs

interface RandomProvider { fun nextDouble():Double; fun nextInt(from:Int,until:Int):Int }
class DefaultRandomProvider:RandomProvider { override fun nextDouble()=kotlin.random.Random.nextDouble(); override fun nextInt(from:Int,until:Int)=kotlin.random.Random.nextInt(from,until) }

object LineupRules {
    fun validate(team:Team):String? { val s=team.starters; if(s.size!=6)return "Escolha exatamente 6 titulares."; val counts=s.groupingBy{it.position}.eachCount(); return if(counts[Position.SETTER]==1&&counts[Position.OPPOSITE]==1&&counts[Position.OUTSIDE]==2&&counts[Position.MIDDLE]==1&&counts[Position.LIBERO]==1)null else "Formação inválida: use 1 levantador, 1 oposto, 2 ponteiros, 1 central e 1 líbero." }
    fun toggle(team:Team,id:String):Result<Team> { val p=team.players.firstOrNull{it.id==id}?:return Result.failure(IllegalArgumentException("Jogador não encontrado.")); if(!p.starter&&team.starters.size>=6)return Result.failure(IllegalStateException("Já existem 6 titulares.")); return Result.success(team.copy(players=team.players.map{if(it.id==id)it.copy(starter=!it.starter)else it})) }
}
object ProgressionRules {
    fun addExperience(player:Player,amount:Int):Player { var p=player.copy(experience=player.experience+amount); while(p.experience>=p.xpForNextLevel){val needed=p.xpForNextLevel;p=p.copy(level=p.level+1,experience=p.experience-needed,trainingPoints=p.trainingPoints+1,maxEnergy=(p.maxEnergy+2).coerceAtMost(120),energy=(p.maxEnergy+2).coerceAtMost(120),attributes=p.attributes.copy(stamina=(p.attributes.stamina+1).coerceAtMost(100)))};return p }
    fun upgrade(player:Player,type:AttributeType):Result<Player> { if(player.trainingPoints<=0)return Result.failure(IllegalStateException("Sem pontos de treinamento.")); if(player.attributes.value(type)>=100)return Result.failure(IllegalStateException("Atributo já está no máximo.")); return Result.success(player.copy(attributes=player.attributes.upgraded(type),trainingPoints=player.trainingPoints-1)) }
    fun reward(won:Boolean)=if(won)Reward(100,40,3) else Reward(60,25,1)
}
object MatchRules { fun setWon(a:Int,b:Int)=a>=15&&a-b>=2; fun matchWon(sets:Int)=sets>=2 }

class OpponentFactory { private val names=listOf("Neon Spikers","Pixel Panthers","Turbo Setters","Byte Blockers","Sunset Servers","Glitch Falcons"); fun create(progress:GameProgress):OpponentTeam { val level=(1+progress.wins/2+progress.matchesPlayed/5+(progress.team.averageLevel-1)*.45).toInt().coerceAtLeast(1);val base=48+level*3;return OpponentTeam(names[progress.matchesPlayed%names.size],level,base+2,base,base-1,base+1,1.0+level*.06) } }

class MatchEngine(private val random:RandomProvider) {
    fun start(progress:GameProgress)=VolleyballMatch(OpponentFactory().create(progress),events=listOf("Apito inicial! Prepare o saque."))
    fun rally(match:VolleyballMatch,team:Team):Pair<VolleyballMatch,Team> {
        if(match.finished)return match to team
        val active=team.starters; val avg={f:(PlayerAttributes)->Int -> active.map{p->f(p.attributes)*(0.7+0.3*p.energy/p.maxEnergy.toDouble())}.average()}
        val home=avg{(it.attack+it.serve+it.setting+it.speed)/4}+avg{(it.defense+it.block)/2}*.45
        val away=(match.opponent.attack+match.opponent.serve+(match.opponent.defense+match.opponent.block)*.45)*match.opponent.difficulty
        val homePoint=home-away+(random.nextDouble()-.5)*34>=0
        var set=match.sets.last();set=if(homePoint)set.copy(homePoints=set.homePoints+1)else set.copy(awayPoints=set.awayPoints+1)
        var hs=match.homeSets;var asets=match.awaySets;var sets=match.sets.dropLast(1)+set
        val setEnded=MatchRules.setWon(set.homePoints,set.awayPoints)||MatchRules.setWon(set.awayPoints,set.homePoints)
        if(setEnded){if(set.homePoints>set.awayPoints)hs++ else asets++;if(!MatchRules.matchWon(hs)&&!MatchRules.matchWon(asets))sets=sets+MatchSet()}
        val finished=MatchRules.matchWon(hs)||MatchRules.matchWon(asets)
        val hero=active[random.nextInt(0,active.size)]
        val phrases=if(homePoint)listOf("${hero.name} dispara um ataque pixel perfeito!","Bloqueio firme! Ponto do VolleyBar!","Saque brilhante de ${hero.name}!") else listOf("A defesa rival encontra a linha.","O bloqueio adversário fecha a rede.","Ataque rival: ponto deles.")
        val event=phrases[random.nextInt(0,phrases.size)]+if(setEnded)" Fim do set!" else ""
        val tired=team.copy(players=team.players.map{if(it.starter)it.copy(energy=(it.energy-(1+(100-it.attributes.stamina)/35)).coerceAtLeast(0))else it})
        return match.copy(sets=sets,homeSets=hs,awaySets=asets,events=(match.events+event).takeLast(10),finished=finished,lastPointHome=homePoint) to tired
    }
}

object InitialTeamFactory {
    fun create():Team { val specs=listOf("Luna" to Position.SETTER,"Kai" to Position.OPPOSITE,"Maya" to Position.OUTSIDE,"Noah" to Position.OUTSIDE,"Theo" to Position.MIDDLE,"Iris" to Position.LIBERO,"Zoe" to Position.SETTER,"Leo" to Position.OPPOSITE,"Nina" to Position.OUTSIDE,"Dante" to Position.OUTSIDE,"Ayla" to Position.MIDDLE,"Finn" to Position.LIBERO);return Team(players=specs.mapIndexed{i,(n,pos)->Player("p${i+1}",n,i+1,pos,attributes=attributes(pos,i),starter=i<6)}) }
    private fun attributes(p:Position,i:Int):PlayerAttributes { val v=i%4;return when(p){Position.SETTER->PlayerAttributes(48+v,55,58,42,78,72,62);Position.OPPOSITE->PlayerAttributes(78,52,68,61,40,60,65);Position.OUTSIDE->PlayerAttributes(68+v,64,66,55,48,65,64);Position.MIDDLE->PlayerAttributes(65,50,55,80,42,52,70);Position.LIBERO->PlayerAttributes(35,82,52,25,58,78,72)} }
}
