
//Validación captcha
function validarCaptcha() {
    let respuesta = document.getElementById("captcha").value;
    if (respuesta != 5) {
        let modal = new bootstrap.Modal(document.getElementById("captchaModal"));
        modal.show();

        return false;
    }
    return true;
}

//Boton ScrollUp
const scrollUpBtn = document.getElementById("scrollUpBtn");
if (scrollUpBtn) {
    window.addEventListener("scroll", () => {
        if (document.documentElement.scrollTop > 200) {
            scrollUpBtn.classList.remove("d-none");
        } else {
            scrollUpBtn.classList.add("d-none");
        }
    });
    scrollUpBtn.addEventListener("click", (e) => {
        e.preventDefault();
        window.scrollTo({ top: 0, behavior: "smooth" });
    });
}
// ================= CARRITO =================
const CLAVE_CARRITO = "carritoJuegos";

// Limpia el contador viejo (guardaba solo un número)
localStorage.removeItem("carrito");

function obtenerCarrito() {
    try {
        return JSON.parse(localStorage.getItem(CLAVE_CARRITO)) || [];
    } catch (error) {
        return [];
    }
}

function guardarCarrito(carrito) {
    localStorage.setItem(CLAVE_CARRITO, JSON.stringify(carrito));
}

function formatoSoles(n) {
    return "S/ " + n.toFixed(2);
}

// Burbuja roja de la barra: solo se ve si hay productos
function actualizarContador() {
    const contador = document.getElementById("contador-carrito");
    if (!contador) return;
    const total = obtenerCarrito().reduce((suma, j) => suma + j.cantidad, 0);
    contador.textContent = total;
    contador.classList.toggle("d-none", total === 0);
}

// Agrega el juego del botón presionado (usa data-nombre/data-imagen si existen,
// y si no, los lee de la tarjeta)
function agregarAlCarrito(boton) {
    const tarjeta = boton.closest(".card");
    const carrito = obtenerCarrito();
    const id = boton.dataset.id;
    if (!/^[0-9]+$/.test(id || "")) {
        window.alert("Este artículo todavía no está registrado en el catálogo de la tienda.");
        return;
    }
    const existente = carrito.find(j => j.id === id);

    if (existente) {
        if (existente.cantidad < 10) existente.cantidad++;
    } else {
        const nombre = boton.dataset.nombre
            || (tarjeta && tarjeta.querySelector(".card-title").textContent.trim());
        const imagen = boton.dataset.imagen
            || (tarjeta && tarjeta.querySelector("img").getAttribute("src"));

        carrito.push({
            id: id,
            nombre: nombre,
            imagen: imagen,
            precio: parseFloat(boton.dataset.precio),
            plataforma: boton.dataset.plataforma,
            cantidad: 1
        });
    }

    guardarCarrito(carrito);
    actualizarContador();
}

// Evita que caracteres como < > " rompan el HTML al dibujar los juegos
function escaparHTML(texto) {
    const div = document.createElement("div");
    div.textContent = texto;
    return div.innerHTML.replace(/"/g, "&quot;");
}

function renderCarrito() {
    const lista = document.getElementById("lista-carrito");
    if (!lista) return; // esta página no es el carrito

    const carrito = obtenerCarrito();

    if (carrito.length === 0) {
        lista.innerHTML = `
            <div class="text-center py-5">
                <i class="fa-solid fa-cart-shopping fs-1 text-muted mb-3"></i>
                <p class="mb-3 carrito-vacio">Tu carro está vacío</p>
                <a href="/videojuegos" class="btn btn-primary fw-bold enlace-azul">Ver videojuegos</a>
            </div>`;
    } else {
        lista.innerHTML = carrito.map(j => `
            <div class="item-carrito d-flex gap-3 py-3 border-top" data-id="${escaparHTML(j.id)}">
                <img class="item-img rounded" src="${escaparHTML(j.imagen)}" alt="${escaparHTML(j.nombre)}">
                <div class="flex-grow-1">
                    <div class="d-flex justify-content-between align-items-start gap-2">
                        <div>
                            <h6 class="text-uppercase mb-1">${escaparHTML(j.nombre)}</h6>
                            <p class="precio-unitario mb-1">${formatoSoles(j.precio)} c/u</p>
                            <div class="mt-2">
                                <span class="badge rounded-pill text-bg-light px-3 py-2">${escaparHTML(j.plataforma)}</span>
                            </div>
                        </div>
                        <div class="text-end">
                            <p class="fs-5 fw-bold mb-2 precio-item">${formatoSoles(j.precio * j.cantidad)}</p>
                            <button class="btn btn-link p-0 btn-eliminar" aria-label="Eliminar ${escaparHTML(j.nombre)}">
                                <i class="fa-regular fa-trash-can fs-5"></i>
                            </button>
                        </div>
                    </div>
                    <div class="d-flex flex-column align-items-end mt-2">
                        <div class="input-group input-group-sm cantidad-box">
                            <button class="btn btn-outline-secondary btn-menos" type="button" aria-label="Quitar una unidad">−</button>
                            <input type="text" class="form-control text-center cantidad" value="${j.cantidad}" readonly>
                            <button class="btn btn-outline-secondary btn-mas" type="button" aria-label="Agregar una unidad">+</button>
                        </div>
                    </div>
                </div>
            </div>`).join("");
    }

    actualizarResumen(carrito);
}

function actualizarResumen(carrito) {
    const total = carrito.reduce((suma, j) => suma + j.precio * j.cantidad, 0);
    const unidades = carrito.reduce((suma, j) => suma + j.cantidad, 0);

    document.getElementById("resumen-cantidad").textContent = unidades;
    document.getElementById("resumen-subtotal").textContent = formatoSoles(total);
    document.querySelectorAll(".resumen-total").forEach(el => el.textContent = formatoSoles(total));
    document.getElementById("btn-pagar").disabled = carrito.length === 0;

    // Lista de juegos dentro del resumen
    const listaResumen = document.getElementById("resumen-lista");
    listaResumen.innerHTML = carrito.map(j => `
        <div class="d-flex justify-content-between gap-3 py-1">
            <span class="resumen-nombre text-truncate">(${j.cantidad}) ${escaparHTML(j.nombre)}</span>
            <span class="text-nowrap">${formatoSoles(j.precio * j.cantidad)}</span>
        </div>`).join("");
}

// ================= EVENTOS =================
document.addEventListener("click", function (e) {
    // Botón "Agregar al carro" (tarjetas, detalle y carrusel)
    const botonAgregar = e.target.closest(".btn-agregar");
    if (botonAgregar) {
        agregarAlCarrito(botonAgregar);
        return;
    }

    // Botones +, − y papelera dentro del carrito
    const item = e.target.closest(".item-carrito");
    if (!item) return;

    let carrito = obtenerCarrito();
    const juego = carrito.find(j => j.id === item.dataset.id);
    if (!juego) return;

    if (e.target.closest(".btn-mas") && juego.cantidad < 10) juego.cantidad++;
    if (e.target.closest(".btn-menos") && juego.cantidad > 1) juego.cantidad--;
    if (e.target.closest(".btn-eliminar")) {
        carrito = carrito.filter(j => j.id !== juego.id);
    }

    guardarCarrito(carrito);
    actualizarContador();
    renderCarrito();
});

// Carrusel de accesorios: marca la miniatura activa y mueve la franja
const carruselAccesorios = document.getElementById("carruselAccesorios");
const franjaMiniaturas = document.getElementById("miniaturas");
if (carruselAccesorios && franjaMiniaturas) {
    carruselAccesorios.addEventListener("slide.bs.carousel", function (e) {
        const miniaturas = franjaMiniaturas.querySelectorAll(".miniatura");

        miniaturas.forEach((m, i) => {
            m.querySelector(".miniatura-btn").classList.toggle("active", i === e.to);
        });

        const activa = miniaturas[e.to];
        franjaMiniaturas.scrollTo({
            left: activa.offsetLeft - (franjaMiniaturas.clientWidth - activa.clientWidth) / 2,
            behavior: "smooth"
        });
    });
}
// ================= INICIO =================
document.addEventListener("DOMContentLoaded", function () {
    actualizarContador();
    renderCarrito();
});



document.addEventListener("DOMContentLoaded", () => {
    const modalDetalle = document.getElementById('modalDetalleProducto');

    if (modalDetalle) {
        modalDetalle.addEventListener('show.bs.modal', event => {
            const button = event.relatedTarget;

            const nombre = button.getAttribute('data-nombre');
            const precio = button.getAttribute('data-precio');
            const tag = button.getAttribute('data-tag');
            const cantidad = button.getAttribute('data-cantidad');
            const img = button.getAttribute('data-img');

            modalDetalle.querySelector('#modal-nombre').textContent = nombre;
            modalDetalle.querySelector('#modal-precio').textContent = precio + " c/u";
            modalDetalle.querySelector('#modal-tag').textContent = tag;
            modalDetalle.querySelector('#modal-cantidad').textContent = cantidad;
            modalDetalle.querySelector('#modal-img').src = img;
        });
    }
});




// Validación exclusiva para el formulario de Login del Administrador
document.addEventListener("DOMContentLoaded", () => {
    const formLogin = document.getElementById("form-login");

    if (formLogin) {
        formLogin.addEventListener("submit", function (event) {
            event.preventDefault();

            const correoIngresado = document.getElementById("correo").value;
            const contraIngresada = document.getElementById("contra").value;

            // Credenciales únicas requeridas
            const correoAdmin = "adminInfinity@gmail.com";
            const contraAdmin = "Keys2026";

            if (correoIngresado === correoAdmin && contraIngresada === contraAdmin) {
                // Redirige al panel de administración (estando en la misma carpeta templates)
                window.location.href = "/administracion";
            } else {
                alert("Acceso denegado: Correo o contraseña de administrador incorrectos.");
            }
        });
    }

    // Control inteligente para el botón "Admin" del menú de navegación
    const linkAdmin = document.getElementById("link-admin");

    if (linkAdmin) {
        linkAdmin.addEventListener("click", (e) => {
            e.preventDefault();
            const estaAutenticado = localStorage.getItem("adminAutenticado");

            if (estaAutenticado === "true") {
                // Apunta al controlador de Spring Boot sin .html
                window.location.href = "/administracion";
            } else {
                // Apunta a la ruta del login sin .html
                window.location.href = "/login";
            }
        });
    }
});

// ================= FILTRO DE NOTICIAS =================
document.addEventListener("DOMContentLoaded", () => {
    const filtros = document.querySelectorAll(".btn-filtro");
    if (filtros.length === 0) return; // esta página no es noticias

    const noticias = document.querySelectorAll(".noticia-item");
    const mensaje = document.getElementById("sin-noticias");

    filtros.forEach(boton => {
        boton.addEventListener("click", () => {
            const categoria = boton.dataset.categoria;
            let visibles = 0;

            filtros.forEach(b => b.classList.remove("active"));
            boton.classList.add("active");

            noticias.forEach(noticia => {
                const mostrar = categoria === "todas" || noticia.dataset.categoria === categoria;
                noticia.classList.toggle("d-none", !mostrar);
                if (mostrar) visibles++;
            });

            if (mensaje) mensaje.classList.toggle("d-none", visibles !== 0);
        });
    });
});


// ================= COMPRAS REALIZADAS =================
async function registrarCompra(metodoPago) {
    const carrito = obtenerCarrito();
    if (carrito.length === 0) return null;

    let confirmacionId = sessionStorage.getItem("confirmacionCompra");
    if (!confirmacionId) {
        confirmacionId = window.crypto && window.crypto.randomUUID
            ? window.crypto.randomUUID()
            : Date.now() + "-" + Math.random().toString(36).slice(2);
        sessionStorage.setItem("confirmacionCompra", confirmacionId);
    }

    const respuesta = await fetch("/compras/confirmar", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        credentials: "same-origin",
        body: JSON.stringify({
            metodoPago: metodoPago,
            confirmacionId: confirmacionId,
            productos: carrito.map(j => ({
                productoId: Number(j.id),
                cantidad: Number(j.cantidad)
            }))
        })
    });

    let resultado = {};
    try {
        resultado = await respuesta.json();
    } catch (error) {
        resultado = {};
    }
    if (!respuesta.ok) {
        const error = new Error(resultado.error || "No se pudo registrar la compra. Inténtalo de nuevo.");
        error.status = respuesta.status;
        throw error;
    }

    guardarCarrito([]);
    actualizarContador();
    sessionStorage.removeItem("confirmacionCompra");
    return resultado;
}

document.addEventListener("DOMContentLoaded", () => {
    const btnPagar = document.getElementById("btn-pagar");
    if (!btnPagar) return; // esta página no es el carrito

    const modalElemento = document.getElementById("modal-pago");
    const modalPago = new bootstrap.Modal(modalElemento);
    const opcionesPago = document.getElementById("opciones-pago");
    const panelYape = document.getElementById("pago-yape");
    const panelTarjeta = document.getElementById("pago-tarjeta");
    const panelConfirmado = document.getElementById("pago-confirmado");
    const formularioTarjeta = document.getElementById("formulario-tarjeta");
    const alertaError = document.getElementById("error-pago");
    const mensajeError = document.getElementById("mensaje-error-pago");
    const enlaceLogin = alertaError.querySelector("a");
    let enviando = false;

    async function mostrarConfirmacion(metodoPago) {
        if (enviando) return;
        enviando = true;
        alertaError.classList.add("d-none");
        modalElemento.querySelectorAll("[data-confirmar-pago], #formulario-tarjeta button[type='submit']")
            .forEach(boton => boton.disabled = true);

        try {
            const compra = await registrarCompra(metodoPago);
            if (!compra) throw new Error("El carrito está vacío.");
            opcionesPago.classList.add("d-none");
            panelYape.classList.add("d-none");
            panelTarjeta.classList.add("d-none");
            panelConfirmado.classList.remove("d-none");
            document.getElementById("mensaje-pago-confirmado").textContent =
                "Tu pedido #" + compra.pedidoId + " fue registrado por " + formatoSoles(Number(compra.total))
                + ". En un proceso real, el comprobante se enviaría al correo registrado en un plazo máximo de 3 días. "
                + "Esta demostración no realiza cobros ni envía correos.";
        } catch (error) {
            mensajeError.textContent = error.message;
            enlaceLogin.classList.toggle("d-none", error.status !== 401);
            alertaError.classList.remove("d-none");
        } finally {
            enviando = false;
            modalElemento.querySelectorAll("[data-confirmar-pago], #formulario-tarjeta button[type='submit']")
                .forEach(boton => boton.disabled = false);
        }
    }

    btnPagar.addEventListener("click", () => {
        const total = document.getElementById("resumen-subtotal").textContent;
        document.getElementById("total-pago-yape").textContent = "Total: " + total;
        document.getElementById("total-pago-tarjeta").textContent = "Total: " + total;
        alertaError.classList.add("d-none");
        opcionesPago.classList.remove("d-none");
        panelYape.classList.add("d-none");
        panelTarjeta.classList.add("d-none");
        panelConfirmado.classList.add("d-none");
        formularioTarjeta.reset();
        formularioTarjeta.classList.remove("was-validated");
        modalPago.show();
    });

    modalElemento.addEventListener("click", (event) => {
        const metodo = event.target.closest("[data-metodo]");
        if (metodo) {
            opcionesPago.classList.add("d-none");
            panelYape.classList.toggle("d-none", metodo.dataset.metodo !== "Yape");
            panelTarjeta.classList.toggle("d-none", metodo.dataset.metodo !== "Tarjeta");
            return;
        }

        if (event.target.closest("[data-volver-pago]")) {
            panelYape.classList.add("d-none");
            panelTarjeta.classList.add("d-none");
            opcionesPago.classList.remove("d-none");
            return;
        }

        const confirmar = event.target.closest("[data-confirmar-pago]");
        if (confirmar) mostrarConfirmacion(confirmar.dataset.confirmarPago);
    });

    formularioTarjeta.addEventListener("submit", (event) => {
        event.preventDefault();
        formularioTarjeta.classList.add("was-validated");
        if (formularioTarjeta.checkValidity()) {
            mostrarConfirmacion("Tarjeta");
        }
    });

    modalElemento.addEventListener("hidden.bs.modal", () => {
        formularioTarjeta.reset();
        formularioTarjeta.classList.remove("was-validated");
    });
});


// ================= LISTA Y PAGINACIÓN DE COMPRAS (página Mis compras) =================

document.addEventListener("DOMContentLoaded", () => {
    const POR_PAGINA = 5;
    const lista = document.getElementById("lista-compras");
    const vacio = document.getElementById("sin-compras");
    const pag = document.getElementById("paginacion-compras");
    if (!lista) return;

    const compras = Array.from(lista.querySelectorAll(".compra-card"));

    if (compras.length === 0) {
        vacio.classList.remove("d-none");
        return;
    }

    vacio.classList.add("d-none");
    const totalPaginas = Math.ceil(compras.length / POR_PAGINA);

    function boton(contenido, pagina, deshabilitado, activo, etiqueta) {
        const li = document.createElement("li");
        li.className = "page-item" + (deshabilitado ? " disabled" : "") + (activo ? " active" : "");
        const a = document.createElement("a");
        a.className = "page-link";
        a.href = "#";
        a.innerHTML = contenido;
        if (etiqueta) a.setAttribute("aria-label", etiqueta);
        a.addEventListener("click", (e) => {
            e.preventDefault();
            if (!deshabilitado) mostrar(pagina);
        });
        li.appendChild(a);
        return li;
    }

    function mostrar(pagina) {
        const inicio = (pagina - 1) * POR_PAGINA;
        compras.forEach((compra, indice) => {
            compra.classList.toggle("d-none", indice < inicio || indice >= inicio + POR_PAGINA);
        });

        pag.innerHTML = "";
        pag.appendChild(boton('<i class="fa-solid fa-chevron-left"></i>', pagina - 1, pagina === 1, false, "Anterior"));
        for (let p = 1; p <= totalPaginas; p++) {
            pag.appendChild(boton(p, p, false, p === pagina));
        }
        pag.appendChild(boton('<i class="fa-solid fa-chevron-right"></i>', pagina + 1, pagina === totalPaginas, false, "Siguiente"));
    }

    mostrar(1);
});
