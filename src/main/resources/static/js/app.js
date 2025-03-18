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
            if (!blueprint || !blueprint.points) {
                console.log("No se encontraron puntos en el blueprint.");
                return;
            }

            $("#blueprint-title").text(`Current blueprint: ${bpname}`);
            currentBlueprint = bpname;
            points = blueprint.points; // Guardamos los puntos actuales

            let canvas = document.getElementById("myCanvas");
            let ctx = canvas.getContext("2d");
            ctx.clearRect(0, 0, canvas.width, canvas.height);

            ctx.beginPath();
            let points = blueprint.points;
            ctx.moveTo(points[0].x, points[0].y);

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

    function initCanvasEvent() {
        let canvas = document.getElementById("myCanvas");
        let ctx = canvas.getContext("2d");

        function addPoint(event) {
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

        canvas.addEventListener("pointerdown", addPoint);
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
    if (window.PointerEvent) {
        app.initCanvasEvent();
    }
});
