sed -i 's/viewModel.addStudyPlan(plan)/viewModel.addStudyPlan(plan) { insertedPlan -> scheduleAlarms(insertedPlan) }/g' app/src/main/java/com/example/ui/screens/studyplanner/StudyPlannerScreen.kt
