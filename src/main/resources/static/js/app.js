var app = (function () {
    var selectedAuthor = null;
    var currentBlueprint = null;
    var isNewBlueprint = false; // Variable para rastrear si es un nuevo blueprint
    var points = [];
    var api = apiclient;

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
            $("#blueprint-title").text(`Current blueprint: ${bpname}`);
            currentBlueprint = bpname;
            isNewBlueprint = false; // No es un nuevo blueprint, ya existe en la base de datos
            points = blueprint.points;

            let canvas = document.getElementById("myCanvas");
            let ctx = canvas.getContext("2d");
            ctx.clearRect(0, 0, canvas.width, canvas.height);

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

        console.log("Guardando blueprint:", blueprintData);

        let url, requestType;

        if (isNewBlueprint) {
            url = `http://localhost:8080/blueprints`;
            requestType = "POST";
        } else {
            url = `http://localhost:8080/blueprints/${selectedAuthor}/${currentBlueprint}`;
            requestType = "PUT";
        }

        $.ajax({
            url: url,
            type: requestType,
            data: JSON.stringify(blueprintData),
            contentType: "application/json"
        }).then(() => {
            console.log("Blueprint guardado correctamente.");
            // Obtener lista actualizada de blueprints
            return $.get(`http://localhost:8080/blueprints/${selectedAuthor}`);
        }).then((blueprints) => {
            updateBlueprintsInfo(blueprints);
            isNewBlueprint = false; // Una vez guardado, ya no es un blueprint nuevo
        }).catch((error) => {
            console.error("Error al guardar el blueprint:", error);
        });
    }

    function createNewBlueprint() {
        if (!selectedAuthor) {
            console.log("Debe seleccionar un autor antes de crear un nuevo blueprint.");
            return;
        }

        let blueprintName = prompt("Ingrese el nombre del nuevo blueprint:");

        if (!blueprintName || blueprintName.trim() === "") {
            console.log("Nombre de blueprint inválido.");
            return;
        }

        // Limpiar canvas
        let canvas = document.getElementById("myCanvas");
        let ctx = canvas.getContext("2d");
        ctx.clearRect(0, 0, canvas.width, canvas.height);

        // Configurar el nuevo blueprint correctamente
        currentBlueprint = blueprintName.trim();
        isNewBlueprint = true;
        points = [];

        $("#blueprint-title").text(`Current blueprint: ${currentBlueprint}`);
    }

    function deleteBlueprint() {
        if (!selectedAuthor || !currentBlueprint) {
            console.log("No hay un blueprint seleccionado para eliminar.");
            return;
        }

        let confirmDelete = confirm(`¿Estás seguro de eliminar el blueprint "${currentBlueprint}"?`);
        if (!confirmDelete) return;

        let url = `http://localhost:8080/blueprints/${selectedAuthor}/${currentBlueprint}`;

        $.ajax({
            url: url,
            type: "DELETE",
            contentType: "application/json"
        }).then(() => {
            console.log("Blueprint eliminado correctamente.");
            currentBlueprint = null;
            points = [];

            // Limpiar el canvas
            let canvas = document.getElementById("myCanvas");
            let ctx = canvas.getContext("2d");
            ctx.clearRect(0, 0, canvas.width, canvas.height);

            $("#blueprint-title").text("");

            // Obtener lista actualizada de blueprints
            return $.get(`http://localhost:8080/blueprints/${selectedAuthor}`);
        }).then((blueprints) => {
            updateBlueprintsInfo(blueprints);
        }).catch((error) => {
            console.error("Error al eliminar el blueprint:", error);
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
                updateBlueprintsInfo(blueprints || []);
            });
        },
        drawBlueprint: drawBlueprint,
        saveOrUpdateBlueprint: saveOrUpdateBlueprint,
        createNewBlueprint: createNewBlueprint,
        deleteBlueprint: deleteBlueprint,
        initCanvasEvent: initCanvasEvent,
        setApi: function (newApi) {
            api = newApi;
        }
    };
})();

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

    $("#btn-create-blueprint").click(function () {
        app.createNewBlueprint();
    });
    $("#btn-delete-blueprint").click(function () {
        app.deleteBlueprint();
    });
    if (window.PointerEvent) {
        app.initCanvasEvent();
    }
});
