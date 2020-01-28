package dev.glycine.devtool

import dev.glycine.devtool.Config.developers
import hazae41.minecraft.kutils.bukkit.*
import org.bukkit.Bukkit
import org.bukkit.Bukkit.getTPS
import org.bukkit.ChatColor
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.scoreboard.DisplaySlot
import org.bukkit.scoreboard.Scoreboard
import java.text.DecimalFormat
import java.util.concurrent.TimeUnit

class DevTool : BukkitPlugin() {

    override fun onEnable() {
        init(Config)

        var viewCounter = 0

        fun newScoreboard(): Scoreboard {
            val board = Bukkit.getScoreboardManager().newScoreboard
            val obj = board.registerNewObjective("dev.glycine.devtool.DevTool", "dummy", "dev.glycine.devtool.DevTool")
            obj.displaySlot = DisplaySlot.SIDEBAR
            return board
        }

        fun Scoreboard.addLine(name: String): Scoreboard {
            registerNewTeam("$name Team").addEntry(name)
            getObjective("dev.glycine.devtool.DevTool")!!.getScore(name).score = viewCounter++
            return this
        }

        listen<PlayerJoinEvent> { e ->
            if (e.player.name in developers) {
                e.player.scoreboard = newScoreboard()
                    .addLine("TPS")
                    .addLine("POS")
                    .addLine("Time")
            }
        }

        listen<PlayerMoveEvent> { e ->
            if (e.player.name in developers) {
                val board1 = e.player.scoreboard
                board1.getEntryTeam("POS")?.suffix =
                    "${ChatColor.YELLOW}  x${e.player.location.x.toInt()} ${e.player.location.y.toInt()} ${e.player.location.z.toInt()}"
            }
        }

        schedule(period = 50, unit = TimeUnit.MICROSECONDS) {
            developers.forEach {
                val player = Bukkit.getPlayer(it)
                val board = player?.scoreboard
                board?.getEntryTeam("TPS")?.suffix = "${ChatColor.YELLOW}  ${DecimalFormat("0.0").format(getTPS()[0])}"

                board?.getEntryTeam("Time")?.suffix = "${ChatColor.YELLOW}   " + player?.world?.time.toString()
            }
        }

        info("&5 dev.glycine.devtool.DevTool Enabled!")
    }

    override fun onLoad() {
        val logo = """
                ___           _____            _ 
               /   \_____   _/__   \___   ___ | |
              / /\ / _ \ \ / / / /\/ _ \ / _ \| |
             / /_//  __/\ V / / / | (_) | (_) | |
            /___,' \___| \_/  \/   \___/ \___/|_|
                                                 
        """
        info("&5$logo")
    }

    override fun onDisable() {
        info("disabled!")
    }
}