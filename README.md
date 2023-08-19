# API List
## Authentication
init()
getFirebaseUser()
signOut()
signInAnonymously()
signInGoogle()
linkAccountWithGoogle()
signUpWithEmail(String email, String password)
signInWithEmail(String email, String password)
linkAccountWithEmail(String email, String password)
sendEmailVerification()

## Realtime Database
databaseSetUserData(String collName, String jsonString)
databaseSetData(String collName, String jsonString)
databasePushUserData(String collName, String jsonString)
databasePushData(String collName, String jsonString)
databaseUpdateUserData(String collName, String jsonString)
databaseUpdateData(String collName, String jsonString)
databaseGetUserData(String collName)
databaseGetData(String collName)
databaseRemoveUserData(String collName)
databaseRemoveData(String collName)

## Firestore Databsae
firestoreSetUserData(String collName, String jsonString)
firestoreSetData(String collName, String jsonString, String docName)
firestoreAddData(String collName, String jsonString)
firestoreUpdateUserData(String collName, String jsonString)
firestoreUpdateData(String collName, String jsonString, String docName)
firestoreReadUserData(String collName)
firestoreReadData(String collName, String docName)
firestoreDeleteUserData(String collName)
firestoreDeleteData(String collName, String docName)

## Analytics
logEvent(String eventName, Dictionary params)
setUserProperty(String propertyName, String value)
testCrash()

## Remote Config
remoteConfigFetch()
remoteConfigActivate()
remoteConfigFetchAndActivate()
remoteConfigGetString(String key)
remoteConfigGetLong(String key)
remoteConfigGetBoolean(String key)
remoteConfigGetDouble(String key)
