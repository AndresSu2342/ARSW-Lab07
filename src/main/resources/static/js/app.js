var app = (function () {
    var selectedAuthor = null;
    var currentBlueprint = null;
    var points = []; // Lista de puntos capturados
    var api = apiclient; // Cambia entre 'apimock' y 'apiclient' aquí

    function updateBlueprintsInfo(blueprints) {
        console.log("Datos recibidos de la API:", blueprints);

        if (!Array.isArray(blueprints) || blueprints.length === 0) {
            console.log("No hay planos para este autor.");
            $("#tabla-blueprints tbody").empty();
            $("#total").text("0");
            $("#autor-seleccionado").text("No blueprints found.");
            return;
        }

        $("#tabla-blueprints tbody").empty();

        blueprints.forEach(bp => {
            let row = `<tr>
                <td>${bp.name}</td>
                <td>${bp.points.length}</td>
                <td><button class="btn-draw" data-bpname="${bp.name}">Draw</button></td>
            </tr>`;
            $("#tabla-blueprints tbody").append(row);
        });

        let totalPoints = blueprints.reduce((sum, bp) => sum + bp.points.length, 0);
        $("#total").text(totalPoints);
        $("#autor-seleccionado").text(`${selectedAuthor}´s blueprints:`);

        $(".btn-draw").click(function () {
            let bpname = $(this).data("bpname");
            app.drawBlueprint(selectedAuthor, bpname);
        });
    }

    function drawBlueprint(author, bpname) {
        console.log(`Dibujando blueprint: ${bpname} de ${author}`);

        api.getBlueprintsByNameAndAuthor(author, bpname, function (blueprint) {
            if (!blueprint || !blueprint.points || blueprint.points.length === 0) {
                console.log("No se encontraron puntos en el blueprint.");
                return;
            }
            // Actualizamos el título y la variable actual
            $("#blueprint-title").text(`Current blueprint: ${bpname}`);
             currentBlueprint = bpname;

            // Guardamos los puntos obtenidos
            points = blueprint.points;

            // Limpiar el canvas antes de dibujar
            let canvas = document.getElementById("myCanvas");
            let ctx = canvas.getContext("2d");
            ctx.clearRect(0, 0, canvas.width, canvas.height);

            // Dibujar los puntos
            if (points.length > 0) {
                ctx.beginPath();
                ctx.moveTo(points[0].x, points[0].y);

                for (let i = 1; i < points.length; i++) {
                    ctx.lineTo(points[i].x, points[i].y);
                }
                ctx.stroke();
            }
        });
    }

    function saveOrUpdateBlueprint() {
        if (!selectedAuthor || !currentBlueprint) {
            console.log("No hay un blueprint seleccionado para guardar.");
            return;
        }

        let blueprintData = {
            author: selectedAuthor,
            name: currentBlueprint,
            points: points
        };

        console.log("Enviando actualización al servidor:", blueprintData);

        // 1. PUT al API para actualizar el blueprint
        $.ajax({
            url: `http://localhost:8080/blueprints/${selectedAuthor}/${currentBlueprint}`,
            type: 'PUT',
            data: JSON.stringify(blueprintData),
            contentType: "application/json"
        }).then(() => {
            console.log("Blueprint actualizado correctamente.");

            // 2. GET para actualizar la lista de blueprints
            return $.get(`http://localhost:8080/blueprints/${selectedAuthor}`);
        }).then((blueprints) => {
            console.log("Lista de blueprints actualizada:", blueprints);

            // 3. Actualizar la tabla y los puntos totales
            updateBlueprintsInfo(blueprints);
        }).catch((error) => {
            console.error("Error en la actualización del blueprint:", error);
        });
    }

    function initCanvasEvent() {
        let canvas = document.getElementById("myCanvas");
        let ctx = canvas.getContext("2d");

        function addPoint(event) {
             if (!currentBlueprint) {
                console.log("No se puede agregar puntos sin seleccionar un blueprint.");
                return;
            }

            let rect = canvas.getBoundingClientRect();
            let x = event.clientX - rect.left;
            let y = event.clientY - rect.top;

            console.log(`Punto agregado: (${x}, ${y})`);
            points.push({ x: x, y: y });

            // Dibujar la línea si hay al menos 2 puntos
            if (points.length > 1) {
                ctx.beginPath();
                ctx.moveTo(points[points.length - 2].x, points[points.length - 2].y);
                ctx.lineTo(x, y);
                ctx.stroke();
            }
        }

        canvas.addEventListener("pointerdown", addPoint, false);
    }

    return {
        getBlueprintsByAuthor: function (authname) {
            selectedAuthor = authname;
            console.log(`Solicitando planos para el autor: ${authname}`);

            api.getBlueprintsByAuthor(authname, function (blueprints) {
                if (!blueprints) {
                    console.log("El API devolvió un valor nulo o indefinido.");
                    updateBlueprintsInfo([]);
                } else {
                    updateBlueprintsInfo(blueprints);
                }
            });
        },
        drawBlueprint: drawBlueprint,
        saveOrUpdateBlueprint: saveOrUpdateBlueprint,
        initCanvasEvent: initCanvasEvent, // Exponemos la función
        setApi: function (newApi) {
            api = newApi; // Permite cambiar dinámicamente la fuente de datos
        }
    };
})();

// Inicializar el manejador de eventos cuando la página cargue
$(document).ready(function () {
    $("#btn-get-blueprints").click(function () {
        let autor = $("#input-autor").val().trim();
        if (autor !== "") {
            app.getBlueprintsByAuthor(autor);
        } else {
            console.log("Ingrese un autor válido.");
        }
    });
    $("#btn-save-blueprints").click(function () {
        app.saveOrUpdateBlueprint();
    });
    if (window.PointerEvent) {
        app.initCanvasEvent();
    }
});
