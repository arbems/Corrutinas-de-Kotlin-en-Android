# Android con Kotlin - Corrutinas de Kotlin en Android

[Corrutinas](https://github.com/arbems/Corrutinas-de-Kotlin-en-Android/tree/master/Corrutinas)


# Documentación:

Una corrutina es un patrón de diseño de simultaneidad que puedes usar en Android para simplificar el código que se ejecuta de forma asíncrona.

En Android, las corrutinas ayudan a administrar tareas de larga duración que, de lo contrario, podrían bloquear el hilo principal y hacer que una app dejara de responder.

Las corrutinas son la solución recomendada para la **programación asíncrona en Android**. Por las siguientes razones:

* **Ligereza**: Puedes ejecutar muchas corrutinas en un solo subproceso debido a la compatibilidad con la **suspensión**, que no bloquea el subproceso en el que se ejecuta la corrutina. Ahora, la suspensión ahorra más memoria que el bloqueo y admite muchas operaciones simultáneas.

* **Menos fugas de memoria**: Usa la *simultaneidad estructurada* para ejecutar operaciones dentro de un alcance.

* **Compatibilidad con cancelación incorporada**: Se propaga automáticamente la cancelación a través de la jerarquía de corrutinas en ejecución.

* **Integración con Jetpack**: Muchas bibliotecas de Jetpack incluyen extensiones que proporcionan compatibilidad total con corrutinas. Además, algunas bibliotecas proporcionan su propio alcance de corrutina, que puedes usar para la simultaneidad estructurada.

Desde el punto de vista del rendimiento, las coroutines permiten la ejecución de miles y hasta millones de hilos concurrentemente con un uso de recursos eficiente haciendo más robusta la aplicación al ser más difícil de alcanzar un error que indique falta de memoria.


![scheme coroutines kotlin](https://raw.githubusercontent.com/arbems/Corrutinas-de-Kotlin-en-Android/master/0001.png)

# 1. Coroutine Context

Las corrutinas siempre se ejecutan en algún contexto representado por un valor del tipo [CoroutineContext](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.coroutines/-coroutine-context/), definido en la biblioteca estándar de Kotlin.

**CoroutineContext** es un conjunto indexado de instancias de [Element](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.coroutines/-coroutine-context/-element/), una mezcla entre un set y un map. <br>
Cada *Element* de este conjunto tiene una [Key](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.coroutines/-coroutine-context/-key.html).

**Keys** que nos sirven para obtener los cuatro *Element* de nuestro **CoroutineContext**:

| **Key**      |  **Element**      | **Descripción**  
| ------------- | ------------- | -------------
| [Job](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-job/index.html)                                                   |              |  Es responsable del ciclo de vida, la cancelación y las relaciones entre padres e hijos de la corrutina.
| [ContinuationInterceptor](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.coroutines/-continuation-interceptor/)                                            |              |  [CoroutineDispatcher](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-coroutine-dispatcher/index.html) que se asocia al contexto.
| [CoroutineExceptionHandler](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-coroutine-exception-handler/index.html)     |              |  Manejador de excepciones que se asocia al contexto.
| [CoroutineName](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-coroutine-name/index.html)                              |              |  Obtenemos el **nombre de la corrutina** a la que se asocia el contexto. Establecer un nombre es útil para efectos de depuración. 

El contexto actual de la corrutina está disponible a través de la propiedad `coroutineContext`.<br>
Por ejemplo para obtener *Job*:
```kotlin
val job: Job? = coroutineContext[Job] // coroutineContext[Key]

println("My context is: $coroutineContext")
```

El contexto de la corrutina es inmutable, pero puede agregar elementos a un contexto usando el operador `plus`.
Podemos combinar elementos de un contexto con los elementos de otro contexto gracias al operador `plus`, devolviendo un nuevo contexto que contiene los elementos combinados.

`Hay dos maneras de asignar un Context, en el alcance de la corrutina o en el constructor de la corrutina (launch, async, etc.)`

### 1.1. Job

Un [Job](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-job/index.html) es parte del contexto. Una corrutina en sí misma está representada por un *Job*. Es responsable del ciclo de vida, la cancelación y las relaciones entre padres e hijos de la corrutina.

```kotlin
interface Job : Element
```

Los **Jobs** se pueden organizar en jerarquías de padres e hijos donde la cancelación de un padre conduce a la cancelación inmediata de todos sus hijos de forma recursiva.<br>

Cuando se lanza una corrutina en el *CoroutineScope* de otra corrutina, hereda su contexto a través de *CoroutineScope.coroutineContext* y el trabajo de la nueva corrutina se convierte en un elemento secundario del trabajo de la corrutina principal. Cuando se cancela la corrutina principal, todos sus elementos secundarios también se cancelan de forma recursiva.

Sin embargo, cuando se utiliza [GlobalScope](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-global-scope/index.html) para iniciar una corrutina, no hay un padre para el trabajo de la nueva corrutina. Por lo tanto, no está vinculado al alcance desde el que se lanzó y funciona de forma independiente.

Una corrutina padre siempre espera la finalización de todos sus hijos. Un padre no tiene que rastrear explícitamente a todos los hijos que lanza, y no tiene que usar Job.join para esperarlos al final.

### 1.2. CoroutineDispatcher

Un **Dispatcher** de corrutina determina qué hilo o hilos utiliza la correspondiente corrutina para su ejecución.

[CoroutineDispatcher](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-coroutine-dispatcher/index.html) es la clase base que se usara con todas las implementaciones de [Dispatchers](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-dispatchers/index.html) de corrutina.

Por ejemplo *Dispatcher.IO*:

```kotlin
val IO: CoroutineDispatcher
```

Puede limitar la ejecución de corrutinas a un hilo específico, enviarlo a un grupo de hilos o dejar que se ejecute *unconfined*.

| **Nombre**      | **Hilo utilizado**    | **Nº máximo de hilos**        | **Útil**
| ------------- | ------------- | ---------------- | ---------------------------------------------------
| [Dispatchers.Default]()        | Grupo de hilos                       | Nº de CPU cores                    | Ejecutando código que use CPU
| [Dispatchers.IO]()             | Grupo de hilos                       | Por defecto: max(64, nº cpu cores) | Ejecutando codigo pesado de IO
| [Dispatchers.Main]()           | Principal                            | 1(normalmente)                     | Trabajando con elementos UI
| [Dispatchers.Unconfined]()     | Sin especificar                      |                                    | 
| [runBlocking { ... }]()        | Hilo actual (normalmente principal)  | 1                                  | Bloqueo y suspensión de bloque
| [runBlockingTest { ... }]()    | Hilo de Test                         | 1                                  | Unicamente en Test


* **Dispatchers.Default**: *CoroutineDispatcher* por defecto, que utilizan todos los constructores como launch, async, etc. si no se especifica un dispatcher ni ningún otro *ContinuationInterceptor* en su contexto. 
Utiliza un grupo común de hilos compartidos en segundo plano. Esta es una opción adecuada para corrutinas informáticas intensivas que consumen recursos de la CPU, como cálculos, algoritmos, etc.

* **Dispatchers.IO**: *CoroutineDispatcher* que está diseñado para descargar tareas de E/S de bloqueo a un grupo compartido de hilos. En general, todas las tareas que bloquearán el hilo mientras esperan la respuesta de otro sistema: peticiones al servidor, acceso a la base de datos, sitema de archivos, sensores etc.

* **Dispatchers.Main**: *CoroutineDispatcher* que se limita al hilo principal que opera con objetos de IU. Por lo general, estos *Dispatchers* son de un solo hilo.

* **Dispatchers.Unconfined**: *CoroutineDispatcher* que inicia una corrutina en el hilo del llamador, pero solo hasta el primer punto de suspensión. Después de la suspensión, reanuda la corrutina en el hilo que está totalmente determinada por la función de suspensión que se invocó. 
Es apropiado para corrutinas que no consumen tiempo de CPU ni actualizan ningún dato compartido (como la interfaz de usuario) confinado a un hilo específico. *Dispatchers.Unconfined* no debe usarse en código general.

**Elegir el Dispatcher incorrecto** puede reducir o anular la efectividad de la corrutina, a tener en cuenta para elegir *Dispatcher*:

* Si el código interactúa con los elementos de la interfaz de usuario, *Dispatchers.Main* es apropiado.
* Si el código es intensivo en CPU. Es decir, el código realiza cálculos (CPU), *Dispatchers.Default* es apropiado ya que está respaldado por un grupo de hilos con tantos hilos como núcleos de CPU.
* El código es intensivo en IO. Es decir, el código se comunica a través de la red / archivo (IO). *Dispatchers.IO* es apropiado.


### 1.3. CoroutineExceptionHandler

[CoroutineExceptionHandler](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-coroutine-exception-handler/index.html) es un elemento opcional en el contexto de corrutina para manejar excepciones no detectadas.

```kotlin
interface CoroutineExceptionHandler : Element
```

Normalmente, las excepciones no detectadas solo pueden resultar de las corrutinas *root* creadas con el constructor *launch*.

### 1.4. CoroutineName

[CoroutineName](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-coroutine-name/index.html) es el nombre de corrutina especificado por el usuario. Este nombre se utiliza en modo de depuración.

```kotlin
abstract class AbstractCoroutineContextElement : Element
```

```kotlin
data class CoroutineName : AbstractCoroutineContextElement
```

Los identificadores asignados automáticamente son buenos cuando las corrutinas se registran con frecuencia y solo necesita correlacionar los registros que provienen de la misma corrutina.

Sin embargo, cuando una corrutina está vinculada al procesamiento de una solicitud específica o al realizar alguna tarea específica en segundo plano, es mejor nombrarla explícitamente para fines de depuración. El elemento de contexto *CoroutineName* tiene el mismo propósito que el nombre del hilo. Se incluye en el nombre del hilo que está ejecutando esta corrutina cuando el modo de depuración está activado.


# 2. Coroutine Scope

*Coroutine Scope* define un alcance para nuevas corrutinas. Cada constructor de corrutinas (como launch, async, etc.) es una función de extensión de *CoroutineScope* y hereda su *coroutineContext* para propagar automáticamente todos sus elementos y su cancelación.

La interfaz [CoroutineScope](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-coroutine-scope/index.html) consta de una única propiedad de tipo *CoroutineContext*:

```kotlin
public interface CoroutineScope {
    public abstract val coroutineContext: kotlin.coroutines.CoroutineContext
}
```

Cada vez que se crea un nuevo *Coroutine Scope*, se crea un nuevo *Job* y se asocia con él. Cada corrutina creada con este alcance se convierte en el hijo de este *Job*. Así es como se crea una relación padre-hijo entre corrutinas.
Si alguna de las corrutinas arroja una excepción no controlada, su *Job* principal se cancela, lo que finalmente cancela todos sus elementos secundarios. Esto se llama **concurrencia estructurada**.


## 3. Coroutine Builder

Todos los constructores de corrutinas, como *launch* y *async*, aceptan un parámetro opcional de **CoroutineContext** que se puede utilizar para especificar explícitamente el [CoroutineDispatcher](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-coroutine-dispatcher/index.html) para la nueva corrutina y otros elementos de contexto como el [Job](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-job/index.html) de la corrutina, el [CoroutineExceptionHandler](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-coroutine-exception-handler/index.html) de la corrutina y el [CoroutineName](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-coroutine-name/index.html).

Funciones del constructor de corrutinas:

| **Nombre**      | **Resultado**    | **Scope**        | **Descripción**
| ------------- | ------------- | ---------------- | ---------------------------------------------------
| [launch](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/launch.html)        |  [Job](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-job/index.html)          | [CoroutineScope](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-coroutine-scope/index.html) | Lanza una corrutina que no tiene ningún resultado
| [async](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/async.html)       | [Deferred](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-deferred/index.html)    | [CoroutineScope](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-coroutine-scope/index.html) | Devuelve un solo valor con el resultado futuro
| [produce](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.channels/produce.html)     | [ReceiveChannel](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.channels/-receive-channel/index.html) | [ProducerScope](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.channels/-producer-scope/index.html)  | Produce un flujo de elementos.
| [runBlocking](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/run-blocking.html) | `T`           | [CoroutineScope](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-coroutine-scope/index.html) | Bloquea el hilo mientras se ejecuta la corrutina


### 3.1. launch

Lanza una nueva corrutina sin bloquear el hilo actual y devuelve una referencia a la corrutina como un *Job*. La corrutina se cancela cuando se cancela el *Job* resultante.
Si el contexto no tiene ningún *dispatcher* ni ningún otro *ContinuationInterceptor*, se utiliza *Dispatchers.Default*.

**launch** es una función de extensión de *CoroutineScope* y toma un *CoroutineContext* como parámetro:

```kotlin
fun CoroutineScope.launch(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit
): Job (source)
```

Realmente toma dos contextos de corrutina (una del parámetro y otro del *CoroutineScope*).
Estos se fusionan usando el operador `plus`, produciendo una unión de sus elementos, de modo que los elementos en el parámetro de contexto tienen prioridad sobre los elementos del *CoroutineScope*.

Podemos usar el operador (**+**) para definir el *conjunto de elementos* para un contexto:

```kotlin
launch(Dispatchers.Default + job + handleException + CoroutineName("test")) { }
```

### 3.2. async

Crea una corrutina y devuelve su resultado futuro como una implementación de [Deferred](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-deferred/index.html) que es un *Job* con un resultado exitoso o fallido del cálculo que se llevó a cabo.

```kotlin
fun <T> CoroutineScope.async(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> T
): Deferred<T> (source)
```

```kotlin
interface Deferred<out T> : Job
```

### 3.3. produce

[produce](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.channels/produce.html#:~:text=Launches%20a%20new%20coroutine%20to,elements%20produced%20by%20this%20coroutine.) lanza una nueva corrutina para producir un flujo de valores enviándolos a un canal y devuelve una referencia a la corrutina como un *ReceiveChannel*. Este objeto resultante se puede utilizar para recibir elementos producidos por esta corrutina.

### 3.4. runBlocking

[runBlocking](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/run-blocking.html) ejecuta una nueva corrutina y bloquea el hilo actual de forma ininterrumpida hasta su finalización. Esta función no debe utilizarse desde una corrutina. 
Está diseñado para conectar el código de bloqueo regular con las bibliotecas que están escritas en estilo de suspensión, para ser utilizadas en funciones principales y en pruebas.

```kotlin
fun main() = runBlocking { // this: CoroutineScope
    launch { // context of the parent, main runBlocking coroutine
        println("main runBlocking: I'm working in thread ${Thread.currentThread().name}")
    }
}
```

`Nota: No se suele usar en producción.`


## 4. Funciones de suspensión

Una *función de suspensión* es simplemente una función que se puede pausar y reanudar en un momento posterior. Pueden ejecutar una operación de larga duración y esperar a que se complete sin bloquear. 

La sintaxis de una función de suspensión es similar a la de una función regular, excepto por la adición de la palabra clave `suspend`. 

```kotlin
suspend fun doSomethingUsefulOne(): Int {
    delay(1000L) // pretend we are doing something useful here
    return 12
}
```

Funciones de suspensión de nivel superior:

| **Nombre**      | **Descripción**
| ------------- | -------------
| [delay]()             |  Non-blocking sleep
| [yield]()             | 
| [withContext]()       |  Cambia a un contexto diferente
| [withTimeout]()       | 
| [withTimeoutOrNull]() | 
| [awaitAll]()          | 
| [joinAll]()           | 





### 4.1. delay

Retrasa la rutina durante un tiempo determinado sin bloquear un hilo y la reanuda después de un tiempo especificado. 
Esta función de suspensión es cancelable. Si el trabajo de la corrutina actual se cancela o se completa mientras esta función de suspensión está esperando, esta función se reanuda inmediatamente con *CancellationException*.

#### 4.1.1. delay vs Thread.Sleep

La función `sleep` bloquea el hilo. La función `delay`, por el contrario, sí utiliza el modificador **suspend**, por lo que una llamada a esta función suspende el hilo.

`delay()` es como un `Thread.sleep()`, pero mejor: no bloquea un hilo, solo suspende la corrutina en sí.

```kotlin
fun main() = runBlocking {
    val list = listOf(11, 5, 3, 8, 1, 9, 6, 2)

    val time = measureTimeMillis {

        list.map { value ->
             async {
                 delay(2000L)
                 println("Processing value $value")
                 value
             }
        }.awaitAll()
    }

    println("Processed in $time ms")
} // print: Processed in 2019 ms
```

```kotlin
fun main() = runBlocking {
    val list = listOf(11, 5, 3, 8, 1, 9, 6, 2)

    val time = measureTimeMillis {

        list.map { value ->
             async {
                 Thread.sleep(2000L)
                 println("Processing value $value")
                 value
             }
        }.awaitAll()
    }

    println("Processed in $time ms")
} // print: Processed in 16028 ms
```

#### 4.1.2. Bloquear o suspender hilo

Bloquear un hilo significa que el hilo se mantendrá fuera de uso mientras este encuentre algo que lo bloquee. 
Por el contrario, suspender un hilo significa que el hilo estará libre y listo para ser usado en la ejecución de otras tareas mientras se encuentra a la espera de la liberación de un recurso.


### 4.2. withContext

Llama al bloque de suspensión especificado con un contexto de rutina determinado, lo suspende hasta que se completa y devuelve el resultado.



## Enlaces

[Corrutinas kotlin con componentes de la arquitectura](https://github.com/arbems/Android-with-Kotlin-Architecture-Components/tree/master/Corrutinas%20kotlin%20con%20componentes%20de%20la%20arquitectura)

## Attribution

This code was created by [arbems](https://github.com/arbems) in 2020.