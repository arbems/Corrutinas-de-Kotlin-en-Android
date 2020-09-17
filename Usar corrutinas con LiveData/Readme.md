# Android con Kotlin - Usar corrutinas con LiveData

*Este ejemplo muestra las siguientes características de [Corrutinas con LiveData]()*:

* Uso de función de suspención

# Documentación

### LiveDataScope

Dependencias de KTX para liveData:

`androidx.lifecycle:lifecycle-livedata-ktx:2.2.0-alpha01`

Cuando usas LiveData, es posible que debas calcular valores de forma asíncrona. En estos casos, puedes usar la función de compilador de liveData para llamar a una función de `suspend`, que muestra el resultado como un objeto LiveData.

Aquí usa la función del compilador de liveData para llamar a **loadUser()** de forma asíncrona y, luego, usa **emit()** para emitir el resultado:
```kotlin
val user: LiveData<User> = liveData {
    val data = database.loadUser() // loadUser is a suspend function.
    emit(data)
}
```

## Attribution

This code was created by [arbems](https://github.com/arbems) in 2020.