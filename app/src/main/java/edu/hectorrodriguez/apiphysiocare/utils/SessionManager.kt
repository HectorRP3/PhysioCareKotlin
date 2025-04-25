package edu.hectorrodriguez.apiphysiocare.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name="settings")

class SessionManager(private val dataStore: DataStore<Preferences>){
    companion object{
        private val TOKEN_KEY = stringPreferencesKey("token")
        private val USERNAME_KEY = stringPreferencesKey("idUser")
        private val ROL_KEY = stringPreferencesKey("rol")
    }

    //Flujo de datos para la sesión. Devuelve un par con el token y el nombre de usuario
    val sessionFlowUser: Flow<Pair<String?, String?>> = dataStore.data.map { preferences ->
        preferences[TOKEN_KEY] to preferences[USERNAME_KEY]
    }

    //Flujo de datos para la sesión. Devuelve un par con el token y el nombre de usuario
    val sessionFlowRol: Flow<Pair<String?, String?>> = dataStore.data.map { preferences ->
        preferences[TOKEN_KEY] to preferences[ROL_KEY]
    }

    //Función para guardar los datos de la sesión
    suspend fun saveSession(token:String,idUser:String,rol:String){
        dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
            preferences[USERNAME_KEY] = idUser
            preferences[ROL_KEY] = rol
        }
    }

    //Funcion para limpiar la sesión. Barra los datos del fichero settings.
    suspend fun clearSession(){
        dataStore.edit{ it.clear() }
    }

}