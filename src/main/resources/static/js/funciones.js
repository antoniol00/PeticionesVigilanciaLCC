function muestra(elem) {
    var x = document.getElementById("warningBorrar");
    if (elem.value == "borra") {
        x.style.display = "block";
    }
    if (elem.value == "nueva") {
        x.style.display = "none";
    }
}

function valida() {
    let codigoAsignatura = document.getElementById("codigoAsignatura").value;
    let mes = document.getElementById("fechaHora").value.substring(5, 7);
    let numVigilantes = document.getElementById("numVigilantes").value;
    let profesoresExamen = document.getElementById("profesoresExamen").value;
    let profSugeridos = document.getElementById("profSugeridos").value;
    const d = new Date();
    let month = d.getMonth() + 1;
    var mensajeError = "";

    if (isNaN(codigoAsignatura)) {
        mensajeError += "+ El código de la asignatura proporcionado no es correcto.\n";
    }

    if (isNaN(numVigilantes) || numVigilantes < 0) {
        mensajeError += "+ El número de vigilantes proporcionado no es correcto.\n";
    }

    if (!profesoresExamen.match(/(\w+)(\s*\w+)*/) || (profSugeridos != "" && !profSugeridos.match(/(\w+)(\s*\w+)*/))) {
        mensajeError += "+ El formato de introducción de los profesores no es correcto.\n";
    }

    if (codigoAsignatura == "" || numVigilantes == "" || mes == "" || profesoresExamen == "") {
        mensajeError += "Debe rellenar todos los campos requeridos.";
    }

    if (mensajeError.length > 0) {
        alert(mensajeError);
        return false;
    }
    return true;
}

function validaEditar() {
    let codigoAsignatura = document.getElementById("codigoAsignaturaEditar").value;
    let mes = document.getElementById("fechaHoraEditar").value.substring(5, 7);
    let numVigilantes = document.getElementById("numVigilantesEditar").value;
    let profesoresExamen = document.getElementById("profesoresExamenEditar").value;
    let profSugeridos = document.getElementById("profSugeridosEditar").value;
    const d = new Date();
    let month = d.getMonth() + 1;
    var mensajeError = "";

    if (isNaN(codigoAsignatura)) {
        mensajeError += "+ El código de la asignatura proporcionado no es correcto.\n";
    }

    if (isNaN(numVigilantes) || numVigilantes < 0) {
        mensajeError += "+ El número de vigilantes proporcionado no es correcto.\n";
    }

    if (!profesoresExamen.match(/(\w+)(\s*\w+)*/) || (profSugeridos != "" && !profSugeridos.match(/(\w+)(\s*\w+)*/))) {
        mensajeError += "+ El formato de introducción de los profesores no es correcto.\n";
    }

    if (mensajeError.length > 0) {
        alert(mensajeError);
        return false;
    }
    return true;
}

document.getElementById("codigoAsignatura").addEventListener('change', compruebaCodigo);

function compruebaCodigo() {
    var x = document.getElementById("warningCodigoAsignatura");
    if (isNaN(this.value)) {
        x.style.display = "block";
        x.innerHTML = "El código de la asignatura proporcionado no es correcto." +
            " Debe introducir un valor numérico.";
    } else {
        x.style.display = "none";
    }
}

document.getElementById("numVigilantes").addEventListener('change', compruebaNumVigilantes);

function compruebaNumVigilantes() {
    var x = document.getElementById("warningNumVigilantes");
    if (isNaN(this.value) || this.value < 0) {
        x.style.display = "block";
        x.innerHTML = "El número de vigilantes proporcionado no es correcto." +
            " Debe introducir un valor numérico mayor o igual a 0.";
    } else {
        x.style.display = "none";
    }
}

function contenido(content) {
    var nueva = document.getElementById("contenedorNueva");
    var historial = document.getElementById("contenedorHistorial");
    var alertBorrar = document.getElementById("alertBorrar");
    var succesCrear = document.getElementById("successCrear");

    if (content == "nueva") {
        nueva.style.display = "block";
        historial.style.display = "none";
    }
    if (content == "historial") {
        nueva.style.display = "none";
        historial.style.display = "block";
        alertBorrar.style.display = "none";
        succesCrear.style.display = "none";
    }
}

function contenidoAdmin(content) {
    var agrega = document.getElementById("contenedorAgregar");
    var descarga = document.getElementById("contenedorDescarga");
    var gestion = document.getElementById("contenedorGestion");

    if (content == "agregarUsuarios") {
        agrega.style.display = "block";
        descarga.style.display = "none";
        gestion.style.display = "none";
    }
    if (content == "descargar") {
        agrega.style.display = "none";
        descarga.style.display = "block";
        gestion.style.display = "none";
    }
    if (content == "gestion") {
        agrega.style.display = "none";
        descarga.style.display = "none";
        gestion.style.display = "block";
    }
}

function borrado() {
    let inicio = document.getElementById("periodoFechaInicio").value;
    let fin = document.getElementById("periodoFechaFin").value;

    if(inicio == "" || fin == ""){
        alert("Introduzca la fecha de inicio y fin del nuevo período de solicitud.");
        return false;
    }

    return confirm("¿Está seguro que desea crear un nuevo período de solicitud?" +
        " Esta acción desactivará todas las peticiones creadas y borrará todos los profesores añadidos, teniendo" +
        "que importar esta información de nuevo para el próximo período de solicitud.");
}

function borrarPeticion() {
    return confirm("¿Está seguro que desea borrar esta petición?");
}

function copiarPeticion() {
    return confirm("Esta acción rellenará el formulario de nueva petición con los datos de esta," +
        " pudiendo modificar los que estime oportunos. ¿Está seguro que desea continuar?");
}
