# BienestarSalud

BienestarSalud es una aplicación móvil para Android que ayuda a los usuarios a monitorear y mejorar su bienestar. Desarrollada en **Kotlin** con **Jetpack Compose**, la app implementa arquitectura **MVVM** y utiliza **Hilt** para la inyección de dependencias, asegurando una estructura escalable y mantenible.

## Características

- **Login:** Pantalla de autenticación segura y moderna.
- **Dashboard:** Visualización y seguimiento de métricas de bienestar.
- **Registro diario:** Permite llevar control de hábitos y emociones.
- **Interfaz moderna:** UI desarrollada con Jetpack Compose y Material Design.
- **Arquitectura robusta:** Basada en MVVM y Hilt para mantener el código modular y testable.

## Tecnologías utilizadas

- **Kotlin**
- **Jetpack Compose**
- **MVVM**
- **Hilt**
- **Material Design**

## Estructura del Proyecto

```
BienestarSalud/
├── .idea/                              # Configuración de Android Studio
├── app/                                # Módulo principal de la aplicación
│   ├── build.gradle.kts
│   ├── proguard-rules.pro
│   └── src/
│       └── main/
│           └── java/
│               └── com/
│                   └── example/
│                       └── bienestarsalud/
│                           ├── BienestarApp.kt           # Entry point de la app
│                           ├── data/                    # Gestión de datos y modelos
│                           ├── di/                      # Configuración de Hilt (inyección de dependencias)
│                           ├── domain/                  # Lógica de negocio y entidades
│                           └── ui/                      # Interfaces y pantallas de usuario
├── build.gradle.kts
├── gradle/
├── gradle.properties
├── settings.gradle.kts
```

## Instalación y Ejecución

1. **Requisitos previos**
   - Android Studio Dolphin o superior
   - SDK Android 8.0 (API level 26) o superior

2. **Clona el repositorio**
   ```bash
   git clone https://github.com/Yelysz/BienestarSalud.git
   ```

3. **Abre el proyecto con Android Studio**
   - Importa y sincroniza Gradle.

4. **Ejecuta la app**
   - Selecciona un emulador o dispositivo físico y presiona **Run**.

## Contribuciones

Las contribuciones son bienvenidas. Puedes abrir Issues para sugerencias o errores, y Pull Requests para aportar código.

1. Haz un fork del repositorio
2. Crea una rama (`git checkout -b feature/mi-feature`)
3. Realiza tus cambios y haz commit (`git commit -m 'Descripción'`)
4. Envía un Pull Request

## Licencia

Este proyecto está bajo la licencia MIT. Consulta el archivo `LICENSE` para más detalles.

## Autora

- **Anyelys Torres**
- [GitHub perfil](https://github.com/Yelysz)

---

¡Gracias por apoyar el bienestar digital con tecnología!