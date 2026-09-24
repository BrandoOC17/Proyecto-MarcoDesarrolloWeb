
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
                <a href="videojuegos.html" class="btn btn-primary fw-bold enlace-azul">Ver videojuegos</a>
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

    modalDetalle.addEventListener('show.bs.modal', event => {
        // Botón que activó el modal
        const button = event.relatedTarget;

        // Extraer la información de los atributos data-*
        const nombre = button.getAttribute('data-nombre');
        const precio = button.getAttribute('data-precio');
        const tag = button.getAttribute('data-tag');
        const cantidad = button.getAttribute('data-cantidad');
        const img = button.getAttribute('data-img');

        // Inyectar los datos dentro de los elementos del modal
        modalDetalle.querySelector('#modal-nombre').textContent = nombre;
        modalDetalle.querySelector('#modal-precio').textContent = precio + " c/u";
        modalDetalle.querySelector('#modal-tag').textContent = tag;
        modalDetalle.querySelector('#modal-cantidad').textContent = cantidad;
        modalDetalle.querySelector('#modal-img').src = img;
    });
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