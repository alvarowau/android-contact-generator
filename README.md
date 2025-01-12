
# API Contact - Android Application

Esta aplicación permite generar contactos de manera dinámica en un dispositivo Android, solicitando permisos necesarios como el acceso y modificación de contactos y almacenamiento externo.

## Funcionalidades

- Generación de contactos a partir de un número ingresado por el usuario.
- Solicitud de permisos necesarios para acceder a los contactos y almacenamiento.
- Interacción con una API para obtener los datos de los contactos.

## Importante

- La aplicación puede tardar un poco en generar los contactos debido a que la API utilizada provee nombres que pueden contener caracteres especiales. Estos nombres serán descartados por la aplicación, por lo que no todos los nombres proporcionados por la API serán visibles en la interfaz.

## Permisos Requeridos

La aplicación solicita los siguientes permisos para funcionar correctamente:

- **Leer contactos** (`READ_CONTACTS`)
- **Escribir contactos** (`WRITE_CONTACTS`)
- **Leer almacenamiento externo** (`READ_EXTERNAL_STORAGE`)
- **Escribir almacenamiento externo** (`WRITE_EXTERNAL_STORAGE`)

## Cómo usar

1. Al abrir la aplicación, se solicita la autorización para los permisos mencionados.
2. Introduce un número en el campo de texto y presiona el botón para generar los contactos.
3. La aplicación mostrará un mensaje de éxito una vez que los contactos sean generados correctamente.

## Tecnología

- Android SDK
- Java
- API personalizada para manejar los contactos

## Notas

- La generación de contactos puede ser lenta debido a las características de la API.

## Colaboraciones

Si tienes alguna pregunta, sugerencia o deseas colaborar en el proyecto, no dudes en contactarme a través de mi correo: alvarobajo893@gmail.com.

