package de.fhe.ai.colivingpilot.di

/*
@Module
@InstallIn(SingletonComponent::class)
class AppModule() {

    @Provides
    @Singleton
    fun provideDatabase(app: Application,
                        callback: WgDatabase.Callback
    ) : WgDatabase {
        val db =
        Room.databaseBuilder(app, WgDatabase::class.java, "wg_database")
            .fallbackToDestructiveMigration()
            .addCallback(callback)
            .build()
        Log.d(CoLiPiApplication.LOG_TAG, "Database provided.")
        return db
    }

    @Provides
    fun provideTaskDao(db: WgDatabase) = db.taskDao()

    @Provides
    fun provideShoppingListItemDao(db: WgDatabase) = db.shoppingListItemDao()

    @Provides
    fun provideTaskAssignedUserDao(db: WgDatabase) = db.taskAssignedUserDao()

    @Provides
    fun provideUserDao(db: WgDatabase) = db.userDao()

    @Provides
    @Singleton
    fun provideRepository(
        userDao: UserDao,
        taskDao: TaskDao,
        shoppingListItemDao: ShoppingListItemDao,
        taskAssignedUserDao: TaskAssignedUserDao,

    ) = Repository(userDao, taskDao, shoppingListItemDao, taskAssignedUserDao)

    @Provides
    @Singleton
    fun provideApplicationScope() = CoroutineScope(SupervisorJob())
}

 */