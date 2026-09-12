sed -i '/fun getTasksForDateRange(startOfRange: Long, endOfRange: Long): Flow<List<Task>>/a \
    @Query("SELECT * FROM tasks WHERE id = :id")\
    suspend fun getTaskById(id: Int): Task?' app/src/main/java/com/example/data/dao/Daos.kt
