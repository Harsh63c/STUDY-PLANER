sed -i '/suspend fun insertTask(task: Task) = taskDao.insertTask(task)/c\
    suspend fun insertTask(task: Task) = taskDao.insertTask(task)\
    suspend fun getTaskById(id: Int) = taskDao.getTaskById(id)' app/src/main/java/com/example/data/repository/PlannerRepository.kt
