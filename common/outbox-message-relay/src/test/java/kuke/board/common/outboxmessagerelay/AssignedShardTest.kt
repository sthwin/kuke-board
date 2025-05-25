package kuke.board.common.outboxmessagerelay

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class AssignedShardTest {

    @Test
    fun ofTest() {
        // given
        val shardCount = 64L
        val appList = listOf("appId1", "appId2", "appId3")

        // when
        val assignedShard1 = AssignedShard.of(
            appId = appList[0],
            appIds = appList,
            shardCount = shardCount
        )

        val assignedShard2 = AssignedShard.of(
            appId = appList[1],
            appIds = appList,
            shardCount = shardCount
        )

        val assignedShard3 = AssignedShard.of(
            appId = appList[2],
            appIds = appList,
            shardCount = shardCount
        )

        val assignedShard4 = AssignedShard.of(
            appId = "invalid",
            appIds = appList,
            shardCount = shardCount
        )

        // then
        val totalShards =
            listOf(
                assignedShard1.shards,
                assignedShard2.shards,
                assignedShard3.shards,
                assignedShard4.shards,
            )
                .flatten()


        assertEquals(shardCount, totalShards.size.toLong())

        (0 until shardCount).forEach { index ->
            assertEquals(index, totalShards[index.toInt()])
        }

        assertTrue { assignedShard4.shards.isEmpty() }
    }
}