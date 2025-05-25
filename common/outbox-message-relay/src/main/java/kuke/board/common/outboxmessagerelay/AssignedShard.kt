package kuke.board.common.outboxmessagerelay

class AssignedShard {
    var shards = mutableListOf<Long>()

    companion object {
        /**
         * @param appId 지금 실행된 애플리케이션 아이디
         * @param appIds 먼저 실행되고 있는 애플리케이션 아이디들
         * @param shardCount 샤드의 갯수
         */
        fun of(
            appId: String,
            appIds: List<String>,
            shardCount: Long
        ): AssignedShard {
            val assignedShard = AssignedShard()
            assignedShard.shards = assign(
                appId,
                appIds,
                shardCount.toInt()
            )
            return assignedShard
        }

        private fun assign(
            appId: String,
            appIds: List<String>,
            shardCount: Int
        ): MutableList<Long> {
            val appIndex = findAppIndex(appId, appIds)
            if (appIndex == -1) {
                return mutableListOf()
            }

            val start = appIndex * shardCount / appIds.size
            val end = (appIndex + 1) * shardCount / appIds.size
            return (start.toLong() until end).toMutableList()
        }

        private fun findAppIndex(appId: String, appIds: List<String>): Int {
            return appIds.indexOfFirst { it == appId }
        }
    }
}