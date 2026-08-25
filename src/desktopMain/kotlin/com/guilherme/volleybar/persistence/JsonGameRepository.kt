package com.guilherme.volleybar.persistence
import com.guilherme.volleybar.domain.model.*
import com.guilherme.volleybar.domain.repository.GameRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString
import java.nio.file.*

class JsonGameRepository(private val path:Path=Paths.get(System.getProperty("user.home"),".volleybar","save.json")):GameRepository {
    private val json=Json{prettyPrint=true;ignoreUnknownKeys=true;encodeDefaults=true}
    override suspend fun load()=withContext(Dispatchers.IO){runCatching{if(!Files.exists(path))GameProgress(com.guilherme.volleybar.domain.usecase.InitialTeamFactory.create()) else json.decodeFromString<GameProgress>(Files.readString(path)).also{require(it.team.players.size==12)}}.recoverCatching{e->println("VolleyBar load error: ${e.message}");runCatching{Files.move(path,path.resolveSibling("save.corrupted.json"),StandardCopyOption.REPLACE_EXISTING)};GameProgress(com.guilherme.volleybar.domain.usecase.InitialTeamFactory.create())}}
    override suspend fun save(progress:GameProgress)=withContext(Dispatchers.IO){runCatching{Files.createDirectories(path.parent);val temp=path.resolveSibling("save.tmp");Files.writeString(temp,json.encodeToString(GameProgress.serializer(),progress));Files.move(temp,path,StandardCopyOption.REPLACE_EXISTING,StandardCopyOption.ATOMIC_MOVE);Unit}}
}
