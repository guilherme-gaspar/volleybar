package com.guilherme.volleybar.domain.repository
import com.guilherme.volleybar.domain.model.GameProgress
interface GameRepository { suspend fun load(): Result<GameProgress>; suspend fun save(progress:GameProgress):Result<Unit> }

