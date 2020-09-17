# Android con Kotlin - Corrutinas kotlin con componentes de la arquitectura

*Proyecto con códigos de ejemplo de [Corrutinas]() en Android con Kotlin.*

#### [Corrutinas en objeto ViewModel]()

#### [Corrutinas en objeto Lifecycle]()

#### [Suspender corrutinas optimizadas para ciclos de vida]()

#### [Corrutinas con LiveData](https://github.com/arbems/Android-with-Kotlin-Architecture-Components/tree/master/Corrutinas%20kotlin%20con%20componentes%20de%20la%20arquitectura/Usar%20corrutinas%20con%20LiveData)

# Documentación:

Las corrutinas de Kotlin proporcionan una API que te permite escribir código asíncrono. Con las corrutinas de Kotlin, puedes definir un *CoroutineScope*, lo que te ayuda a administrar cuándo deben ejecutarse las corrutinas. Cada operación asíncrona se ejecuta dentro de un alcance particular.

Los componentes de arquitectura proporcionan compatibilidad de primer nivel con las corrutinas para alcances lógicos de tu app, junto con una capa de interoperabilidad con LiveData.

# Ámbitos de corrutinas optimizados para ciclos de vida

## ViewModelScope

`viewModelScope` es un *CoroutineScope* predefinido que se incluye con las extensiones KTX de ViewModel.

Esta librería ofrece una función `viewModelScope()` que facilita el lanzamiento de corrutinas desde tu *ViewModel*. El *CoroutineScope* está vinculado a *Dispatchers.Main* y se cancela automáticamente cuando se borra el *ViewModel*. Puedes usar *viewModelScope()* en lugar de crear un nuevo alcance para cada ViewModel.

Todas las corrutinas deben ejecutarse en un alcance. *CoroutineScope* administra una o más corrutinas relacionadas.

Extensión de KTX:
`androidx.lifecycle:lifecycle-viewmodel-ktx:2.2.0`

Se define un **ViewModelScope** para cada objeto ViewModel de tu app. Si se borra ViewModel, se cancela automáticamente cualquier corrutina iniciada en este alcance.
Es útil para cuando tienes trabajos que se deben hacer solo si *ViewModel* está activo

Ejemplo que  lanza una corrutina que realiza una solicitud de red en un subproceso en segundo plano. 
La biblioteca maneja toda la configuración y la liberación del alcance correspondiente:

```kotlin
class MainViewModel : ViewModel() {
    // Realizar una solicitud de red sin bloquear el hilo de la interfaz de usuario
    private fun makeNetworkRequest() {
        // lanzar una corrutina en viewModelScope
        viewModelScope.launch  {
            remoteApi.slowFetch()
            // ...
        }
    }
    // No es necesario sobrescribir onCleared()
}
```

## LifecycleScope

*Lifecycle KTX* define un **LifecycleScope** para cada objeto Lifecycle. Se cancelan todas las corrutinas iniciadas en este alcance cuando se destruye el *Lifecycle*. Puedes acceder al *CoroutineScope* del Lifecycle mediante las propiedades *lifecycle.coroutineScope* o *lifecycleOwner.lifecycleScope*.

Extensión de KTX:
`androidx.lifecycle:lifecycle-runtime-ktx:2.2.0`

Ejemplo para usar lifecycleOwner.lifecycleScope para crear texto procesado previamente de forma asíncrona:

```kotlin
class MyFragment: Fragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            val params = TextViewCompat.getTextMetricsParams(textView)
            val precomputedText = withContext(Dispatchers.Default) {
                PrecomputedTextCompat.create(longTextContent, params)
            }
            TextViewCompat.setPrecomputedText(textView, precomputedText)
        }
    }
}
```

# Suspender corrutinas optimizadas para ciclos de vida

Aunque CoroutineScope proporciona una forma adecuada de cancelar automáticamente operaciones de larga duración, es posible que haya otros casos en los que quieras suspender la ejecución de un bloque de código, a menos que el Lifecycle esté en un estado determinado.

**Lifecycle** proporciona métodos adicionales: `lifecycle.whenCreated`, `lifecycle.whenStarted` y `lifecycle.whenResumed`. Se suspenderá cualquier ejecución de corrutina dentro de estos bloques si el Lifecycle no está al menos en el estado mínimo deseado.

Bloque de código que se ejecuta solamente cuando el Lifecycle asociado está al menos en el estado **STARTED**:

```kotlin
class MyFragment: Fragment {
    init { // Notice that we can safely launch in the constructor of the Fragment.
        lifecycleScope.launch {
            whenStarted {
                // The block inside will run only when Lifecycle is at least STARTED.
                // It will start executing when fragment is started and
                // can call other suspend methods.
                loadingView.visibility = View.VISIBLE
                val canAccess = withContext(Dispatchers.IO) {
                    checkUserAccess()
                }

                // When checkUserAccess returns, the next line is automatically
                // suspended if the Lifecycle is not *at least* STARTED.
                // We could safely run fragment transactions because we know the
                // code won't run unless the lifecycle is at least STARTED.
                loadingView.visibility = View.GONE
                if (canAccess == false) {
                    findNavController().popBackStack()
                } else {
                    showContent()
                }
            }

            // This line runs only after the whenStarted block above has completed.

        }
    }
}
```

Si el Lifecycle se destruye mientras una corrutina está activa mediante uno de los métodos when, se cancelará automáticamente la corrutina.
En el siguiente ejemplo, el bloque finally se ejecuta una vez que el estado de Lifecycle es **DESTROYED**:

```kotlin
class MyFragment: Fragment {
    init {
        lifecycleScope.launchWhenStarted {
            try {
                // Call some suspend functions.
            } finally {
                // This line might execute after Lifecycle is DESTROYED.
                if (lifecycle.state >= STARTED) {
                    // Here, since we've checked, it is safe to run any
                    // Fragment transactions.
                }
            }
        }
    }
}
```

`Nota: Ten en cuenta que, aunque la actividad se reinicie, no ocurrirá lo mismo con la corrutina.`

# Corrutinas con LiveData



## Enlaces

[Corrutinas de Kotlin en Android](https://github.com/arbems/Corrutinas-de-Kotlin-en-Android)

## Attribution

This code was created by [arbems](https://github.com/arbems) in 2020.