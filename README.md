La aplicación permite la solicitud de peticiones de vigilancia para exámenes de las distintas asignaturas. Permite además administrar y exportar la información necesaria para el procesamiento de dichas peticiones. Para más información, consultar documento de requisitos `(requisitos.pdf)`

## Ejecución

La aplicación ha sido desarrollada con Spring Boot en Java, por lo que es necesario contar con una instalación de Java en cualquier sistema operativo. A continuación, ejecutar la clase PeticionesVigilanciaLCCApplication, situada en el directorio src/main/java/es/uma/lcc/peticionesvigilancialcc, y dirigirse a la ruta: `localhost:8080` en el navegador (esta ruta es la ruta predeterminada de Spring, y puede modificarse en el archivo `application.properties`, más información [aquí](https://www.javatpoint.com/spring-boot-properties).

Se ha incluido en la raíz del proyecto el archivo de prueba `usuarios.txt` que se puede usar para cargar usuarios en la aplicación mediante la importación de dicho archivo
