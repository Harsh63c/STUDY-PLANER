with open('app/src/main/java/com/example/data/dao/Daos.kt', 'r') as f:
    content = f.read()

content = content.replace(
    'suspend fun getTaskById(id: Int): Task?',
    'suspend fun getTaskById(id: Int): Task?\n\n    @Query("SELECT * FROM tasks")\n    suspend fun getAllTasksSync(): List<Task>'
)

content = content.replace(
    'suspend fun getStudyPlanById(id: Int): StudyPlan?',
    'suspend fun getStudyPlanById(id: Int): StudyPlan?\n\n    @Query("SELECT * FROM study_plans")\n    suspend fun getAllStudyPlansSync(): List<StudyPlan>'
)

with open('app/src/main/java/com/example/data/dao/Daos.kt', 'w') as f:
    f.write(content)
