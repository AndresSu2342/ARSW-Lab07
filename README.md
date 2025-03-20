#### Escuela Colombiana de Ingeniería
### Arquitecturas de Software

---

### Integrantes: Joan S. Acevedo Aguilar - Cesar A. Borray Suarez

---

#### Construción de un cliente 'grueso' con un API REST, HTML5, Javascript y CSS3. Parte II.


![](img/mock2.png)

1. Agregue al canvas de la página un manejador de eventos que permita capturar los 'clicks' realizados, bien sea a través del mouse, o a través de una pantalla táctil. Para esto, tenga en cuenta [este ejemplo de uso de los eventos de tipo 'PointerEvent'](https://mobiforge.com/design-development/html5-pointer-events-api-combining-touch-mouse-and-pen) (aún no soportado por todos los navegadores) para este fin. Recuerde que a diferencia del ejemplo anterior (donde el código JS está incrustado en la vista), se espera tener la inicialización de los manejadores de eventos correctamente modularizado, tal [como se muestra en este codepen](https://codepen.io/hcadavid/pen/BwWbrw).

	Para esto en nuestro app.js agregamos al momento de que la pagina cargue, que se inicialize nuestro manejador de eventos del canva con la condicion que el navegador si lo pueda soportar

    ![Image](https://github.com/user-attachments/assets/de2c4e93-b8f8-421a-b72a-798d7b8ec0b2)

	Y creamos la funcion privada initCanvasEvent, donde agregamos el AddEventListener para que lea cuando se le hace click en el canva, obtener sus coordenadas y dibujar el nuevo punto con su linea respectiva si llega a haber mas de un punto

    ![Image](https://github.com/user-attachments/assets/df22afdd-03d8-4c6b-a6f0-1aea9857e6af)

2. Agregue lo que haga falta en sus módulos para que cuando se capturen nuevos puntos en el canvas abierto (si no se ha seleccionado un canvas NO se debe hacer nada):
	1. Se agregue el punto al final de la secuencia de puntos del canvas actual (sólo en la memoria de la aplicación, AÚN NO EN EL API!).

	Para esto simplemente, creamos una nueva variable points donde se nos almacenara los puntos creados de forma local dentro de nuestro canva seleccionado

    ![Image](https://github.com/user-attachments/assets/609f2f00-7aa7-4814-8e12-f9503374af4d)

    2. Se repinte el dibujo.
   
	Para lograr repintarlo, debemos validar primeramente que no se pueda agregar puntos sin que haya un canva seleccionado, por lo que agregamos una variable donde tendremos el nombre del plano actual que se nos esta mostrando

    ![Image](https://github.com/user-attachments/assets/b1c15dfe-a310-4e17-b803-ac915feff735)

	Si esta variable al momento de inicializar el manejador de eventos es nula, podemos validar que no hay ningun plano seleccionado

	![Image](https://github.com/user-attachments/assets/285f5615-8143-48bd-8708-e08c90469dc3)
	
	Y por ultimo, solo hacemos que se dibuje la linea con respecto la punto anterior registrado de forma local

    ![Image](https://github.com/user-attachments/assets/b8835cfb-b1ea-4a42-a615-e0c78b02c6f4)

	Empezamos a probar su funcionamiento, seleccionando algun plano ya creado dentro de nuestro apiclient

    ![Image](https://github.com/user-attachments/assets/52055579-6f62-4ebc-9651-c3ab18d80388)

	Y vemos que al momento de realizar click dentro del canvas, se van creando los puntos con su respectiva linea

	![Image](https://github.com/user-attachments/assets/3e0858a4-cb01-4364-ab64-57a25f4b4d80)

3. Agregue el botón Save/Update. Respetando la arquitectura de módulos actual del cliente, haga que al oprimirse el botón:
	1. Se haga PUT al API, con el plano actualizado, en su recurso REST correspondiente.
	2. Se haga GET al recurso /blueprints, para obtener de nuevo todos los planos realizados.
	3. Se calculen nuevamente los puntos totales del usuario.

	Para lo anterior tenga en cuenta:

	* jQuery no tiene funciones para peticiones PUT o DELETE, por lo que es necesario 'configurarlas' manualmente a través de su API para AJAX. Por ejemplo, para hacer una peticion PUT a un recurso /myrecurso:

	```javascript
    return $.ajax({
        url: "/mirecurso",
        type: 'PUT',
        data: '{"prop1":1000,"prop2":"papas"}',
        contentType: "application/json"
    });
    
	```
	Para éste note que la propiedad 'data' del objeto enviado a $.ajax debe ser un objeto jSON (en formato de texto). Si el dato que quiere enviar es un objeto JavaScript, puede convertirlo a jSON con: 
	
	```javascript
	JSON.stringify(objetojavascript),
	```
	* Como en este caso se tienen tres operaciones basadas en _callbacks_, y que las mismas requieren realizarse en un orden específico, tenga en cuenta cómo usar las promesas de JavaScript [mediante alguno de los ejemplos disponibles](http://codepen.io/hcadavid/pen/jrwdgK).

	Dentro de nuestro html, creamos los 3 nuevos botones y los alienamos a como se nos muestran en el mockup (esto adelantando los siguientes puntos)

	![Image](https://github.com/user-attachments/assets/35fc2556-bbdd-48a8-84ac-5741fb9b6a45)

	Luego dentro de nuestro app.js, creamos una nueva funcion privada saveOrUpdateBlueprint que nos ayudara a implementar nuestro llamado al metodo PUT de la apiclient y asi mismo realizar el get del nuevo listado de planos atraves de promesas

    ![Image](https://github.com/user-attachments/assets/d3b049c6-540f-4a74-9c7c-6a903f14cf10)

	Probamos su funcionamiento, con un plano y agregamos unos cuantos puntos, le damos al boton y vemos que la cantidad de puntos en el plano seleccionado se actualiza, confirmandonos que si se guardo

    ![Image](https://github.com/user-attachments/assets/92e5e6de-e79a-4eed-a108-fd97f125c8cd)

	Volvemos a dibujar el mismo plano para volver a agregar mas puntos y que este si dibuje la linea como deberia, tambien permite seguir guardando y actualizando el plano y puntos totales

    ![Image](https://github.com/user-attachments/assets/800e68e8-7f4d-44ab-b9f3-41024c0d7cb0)

4. Agregue el botón 'Create new blueprint', de manera que cuando se oprima: 
	* Se borre el canvas actual.
	* Se solicite el nombre del nuevo 'blueprint' (usted decide la manera de hacerlo).
	
	Esta opción debe cambiar la manera como funciona la opción 'save/update', pues en este caso, al oprimirse la primera vez debe (igualmente, usando promesas):

	1. Hacer POST al recurso /blueprints, para crear el nuevo plano.
	2. Hacer GET a este mismo recurso, para actualizar el listado de planos y el puntaje del usuario.

	Primeramente, creamos nuestra funcion privada createNewBlueprint que se encargara de borrar el canvas actual y pedir el nombre del nuevo plano

    ![Image](https://github.com/user-attachments/assets/f9b5c0e9-46c9-4c18-bc2a-61614f81ed9b)

	Ahora como modificacion de la funcion de guardado, tenemos que atraves de una nueva variable isNewBlueprint nos permite saber si el plano actual es nuevo o existente para de esta forma asignar la url y el requestType correctas

    ![Image](https://github.com/user-attachments/assets/6baa511f-0146-46b5-8b6c-94cd2288673d)

	Probamos la creacion de un nuevo blueprint buscando un autor existente, dandole al boton, y dentro de un prompt ingresamos el nombre del nuevo plano

    ![Image](https://github.com/user-attachments/assets/a075bc53-a183-4cd3-933b-ff08334e7a15)

	Despues de esto tenemos nuestro canva limpio y empezamos a agregar puntos, posteriormente guardamos y vemos que la lista de los planos se actualiza y se encuentra el plano recien creado

	![Image](https://github.com/user-attachments/assets/3591ab55-71f5-4aa7-bed7-f4b0eb7bc9c7)

5. Agregue el botón 'DELETE', de manera que (también con promesas):
	* Borre el canvas.
	* Haga DELETE del recurso correspondiente.
	* Haga GET de los planos ahora disponibles.

	Para agregar esta funcionalidad, primero debemos implementar el método DELETE en cada una de las capas necesarias para que podamos eliminar planos

    ![Image](https://github.com/user-attachments/assets/5787e192-9fcc-4395-9874-cf8ba6f2d2b6)

	![Image](https://github.com/user-attachments/assets/b57090bb-7eb4-42b9-bacc-55b365223a9f)
	
	![Image](https://github.com/user-attachments/assets/675d6509-5aae-436a-8912-05804db2fabe)

	Posteriormente, volvemos a repetir el proceso de crear una nueva funcion privada deleteBlueprint(), donde volvemos a usar promesas y aplicamos el orden respectivo que se nos indica	
	
	![Image](https://github.com/user-attachments/assets/de8a4316-d244-4496-8fcc-e40a6fb44d51)

	Probamos y miramos que al darle al boton se nos pide una confirmacion que se agrego de forma adicional

    ![Image](https://github.com/user-attachments/assets/2c2dba34-a2c2-4795-8c6a-b345907d9a77)

	Aceptamos y vemos que se actualiza nuestra lista de planos donde ya no aparece el plano eliminado

   ![Image](https://github.com/user-attachments/assets/30561626-c57c-4161-9d47-e8d38974ee27)


