# Android con Kotlin - Corrutinas

*Este ejemplo muestra las siguientes características de [Corrutinas](https://developer.android.com/kotlin/coroutines?hl=es)*:

* Ejecutar una corrutina en un hilo en segundo plano
* Uso de corrutinas para la seguridad del hilo principal
* Control de excepciones

# Documentación

Cuando haces una solicitud de red en el hilo principal, este espera o se bloquea hasta que recibe una respuesta.

Para evitar que el hilo principal se bloquee, la solución es crear una nueva corrutina y ejecutar la solicitud en este hilo en segundo plano.

## viewModelScope

`viewModelScope` es un *CoroutineScope* predefinido que se incluye con las extensiones KTX de ViewModel.
Todas las corrutinas deben ejecutarse en un alcance y *CoroutineScope* administra una o más corrutinas relacionadas.

Una corrutina que se inicia con *viewModelScope*, se ejecuta en el alcance de ViewModel. Si se destruye el ViewModel porque el usuario se aleja de la pantalla, se cancela automáticamente viewModelScope, y todas las corrutinas en ejecución también se cancelan.

`launch` es una función que crea una corrutina y despacha la ejecución de sus funciones al *dispatcher* correspondiente.
Cuando no pasas un *Dispatcher* a launch, cualquier corrutina iniciada desde *viewModelScope* se ejecuta en el hilo principal.

`Dispatchers.IO` indica que esta corrutina debe ejecutarse en un hilo reservado para operaciones de E/S.


## withContext

Una función es segura para el hilo principal cuando no bloquea las actualizaciones de la IU en este hilo.

La función `withContext()` de la librería de corrutinas sirve para trasladar la ejecución de una corrutina a un hilo diferente.

`withContext(Dispatchers.IO)` traslada la ejecución de la corrutina a un hilo de E/S, lo que hace que una función de llamada sea segura y habilite la IU según sea necesario.

`suspend`, que es la forma en que Kotlin aplica una función desde una corrutina. Todas las funciones `suspend` deben ejecutarse en una corrutina.


## Resumen

La app llama a la función `login()` desde la capa *View* del hilo principal.
`launch` crea una nueva corrutina para realizar la solicitud de red en el hilo principal, y la corrutina comienza la ejecución.
Dentro de la corrutina, la llamada a `validateLogin()` ahora suspende la ejecución de la corrutina hasta que el bloque *withContext* de `validateLogin()` termina de ejecutarse.
Una vez que finaliza el bloque `withContext`, la corrutina de `login()` reanuda la ejecución en el hilo principal con el resultado de la solicitud de red.


## Attribution

This code was created by [arbems](https://github.com/arbems) in 2020.