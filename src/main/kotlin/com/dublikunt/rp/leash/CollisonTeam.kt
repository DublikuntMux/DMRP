package com.dublikunt.rp.leash

import org.bukkit.Bukkit
import org.bukkit.scoreboard.Scoreboard
import org.bukkit.scoreboard.Team
import org.bukkit.scoreboard.Team.OptionStatus

class CollisionTeam {
    val board: Scoreboard
    val team: Team

    init {
        val manager = Bukkit.getScoreboardManager()
        val board = manager!!.newScoreboard
        val team = board.registerNewTeam("NoCollision")
        team.setOption(Team.Option.COLLISION_RULE, OptionStatus.NEVER)
        this.team = team
        this.board = board
    }
}
