package kuke.board.comment.service

class PageLimitCalculator {

    companion object {

        /**
         * @param movablePageCount 화면에 표시될 페이지 번호의 갯수. 10이면 1부 10번 페이지까지 이동할 수 있는 번호가 표시되는 것
         */
        fun calculatePageLimit(
            page: Long,
            pageSize: Long,
            movablePageCount: Long
        ): Long {
            return (((page - 1) / movablePageCount) + 1) * pageSize * movablePageCount + 1
        }
    }
}