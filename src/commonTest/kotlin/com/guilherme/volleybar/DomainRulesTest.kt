package com.guilherme.volleybar
import com.guilherme.volleybar.domain.model.*
import com.guilherme.volleybar.domain.usecase.*
import kotlin.test.*

class DomainRulesTest {
    private val team=InitialTeamFactory.create()
    @Test fun initialTeamHasTwelveAndValidLineup(){assertEquals(12,team.players.size);assertEquals(6,team.starters.size);assertNull(LineupRules.validate(team))}
    @Test fun lineupRejectsTooManyAndInvalidComposition(){assertTrue(LineupRules.toggle(team,"p7").isFailure);val missing=team.copy(players=team.players.map{if(it.id=="p1")it.copy(starter=false)else it});assertNotNull(LineupRules.validate(missing))}
    @Test fun experienceLevelsAndKeepsOverflow(){val p=ProgressionRules.addExperience(team.players.first(),115);assertEquals(2,p.level);assertEquals(15,p.experience);assertEquals(1,p.trainingPoints)}
    @Test fun xpRequirementGrows(){val p=team.players.first();assertEquals(100,p.xpForNextLevel);assertEquals(150,p.copy(level=2).xpForNextLevel)}
    @Test fun upgradeUsesPointAndCapsAtHundred(){val p=team.players.first().copy(trainingPoints=1,attributes=team.players.first().attributes.copy(attack=99));val u=ProgressionRules.upgrade(p,AttributeType.ATTACK).getOrThrow();assertEquals(100,u.attributes.attack);assertEquals(0,u.trainingPoints);assertTrue(ProgressionRules.upgrade(u.copy(trainingPoints=1),AttributeType.ATTACK).isFailure)}
    @Test fun setRequiresFifteenAndTwoPointLead(){assertFalse(MatchRules.setWon(15,14));assertTrue(MatchRules.setWon(16,14));assertFalse(MatchRules.setWon(14,0));assertTrue(MatchRules.matchWon(2))}
    @Test fun rewardsDependOnResult(){assertEquals(100,ProgressionRules.reward(true).starterXp);assertEquals(25,ProgressionRules.reward(false).reserveXp);assertEquals(3,ProgressionRules.reward(true).teamTrainingPoints)}
    @Test fun rallyReducesEnergy(){val e=MatchEngine(FixedRandom(.99));val m=e.start(GameProgress(team));val (_,after)=e.rally(m,team);assertTrue(after.starters.all{it.energy<100});assertTrue(after.players.filterNot{it.starter}.all{it.energy==100})}
    @Test fun attributesInfluenceRally(){val engine=MatchEngine(FixedRandom(.5));val weak=team.copy(players=team.players.map{if(it.starter)it.copy(attributes=PlayerAttributes(1,1,1,1,1,1,1))else it});val strong=team.copy(players=team.players.map{if(it.starter)it.copy(attributes=PlayerAttributes(100,100,100,100,100,100,100))else it});val opponent=OpponentTeam("Test",1,60,60,60,60,1.0);assertFalse(engine.rally(VolleyballMatch(opponent),weak).first.lastPointHome);assertTrue(engine.rally(VolleyballMatch(opponent),strong).first.lastPointHome)}
    @Test fun opponentDifficultyProgressesGradually(){val factory=OpponentFactory();val first=factory.create(GameProgress(team));val later=factory.create(GameProgress(team.copy(players=team.players.map{it.copy(level=4)}),matchesPlayed=10,wins=6));assertTrue(later.averageLevel>first.averageLevel);assertTrue(later.difficulty>first.difficulty)}
    private class FixedRandom(private val d:Double):RandomProvider{override fun nextDouble()=d;override fun nextInt(from:Int,until:Int)=from}
}

