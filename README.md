JAVA/GRADLE PROJECT USED AS AN ANDROID SHELL FOR AI SERVICES

Use IDE in order to change the file structure
from com/example/d308_mobile_application_development_android/
to   com/livewire/


PHASE 1 - Establish the new application architecture

Android UI
    |
ViewModel
    |
Repository
    |
AI Service


PHASE 2

                     USER
                      │
                      ▼
                 MainActivity
                      │
                 submitPrompt()
                      │
                      ▼
                MainViewModel
                      │
             repository.submitPrompt()
                      │
                      ▼
                MainRepository
                      │
                      │
                 returns result
                      │
                      ▼
                MainViewModel
                      │
                response.setValue()
                      │
                      ▼
                 LiveData
                      │
                      ▼
                MainActivity
                      │
                      ▼
                 TextView
