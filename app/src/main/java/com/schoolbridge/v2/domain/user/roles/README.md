// **Removed**: These concrete implementations belong in the backend
│   │       // TeacherImpl.kt, StudentImpl.kt, etc. are NOT in the mobile client project.
│   │       // The mobile client simply sends requests to the backend, which executes these behaviors.
│   │       // The 'User.kt' in the mobile client will only have properties reflecting its roles,
│   │       // and methods that *trigger* API calls, not perform the behavior directly.