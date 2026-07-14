/* eslint-disable no-unused-vars */
let paginaVentas = 0;
let paginaPujas = 0;
const LIMITE = 8;

document.addEventListener("DOMContentLoaded", function () {
  pedirVentas();
  pedirPujas();
});

function cargarMasVentas() {
  paginaVentas++;
  pedirVentas();
}

function cargarMasPujas() {
  paginaPujas++;
  pedirPujas();
}

function pedirVentas() {
  const usuarioId = document.getElementById("session-usuario-id").value;
  if (!usuarioId) return;

  fetch(`/api/perfil/ventas?usuarioId=${usuarioId}&pagina=${paginaVentas}&limite=${LIMITE}`)
    .then(res => res.json())
    .then(subastas => {
      const contenedor = document.getElementById("lista-mis-ventas");
      const btn = document.getElementById("btn-cargar-ventas");

      if (paginaVentas === 0 && subastas.length === 0) {
        document.getElementById("msg-sin-ventas").classList.remove("d-none");
        return;
      }

      if (subastas.length < LIMITE) {
        btn.classList.add("d-none");
      } else {
        btn.classList.remove("d-none");
      }

      subastas.forEach(subasta => {
        let li = document.createElement("li");
        li.className = "profile-list-item";
        li.innerHTML = `
          <strong>${subasta.nombre}</strong>
          <p class="text-muted mb-2">Estado: ${subasta.estado}</p>
          <div class="d-flex gap-2 flex-wrap">
            ${armarTagEstadoVenta(subasta)}
            <a href="/detalle-subasta?id=${subasta.id}" class="btn btn-sm btn-warning ms-auto">Ver detalle</a>
          </div>
        `;
        contenedor.appendChild(li);
      });
    })
    .catch(err => console.error(err));
}

function pedirPujas() {
  const usuarioId = document.getElementById("session-usuario-id").value;
  if (!usuarioId) return;

  fetch(`/api/perfil/pujas?usuarioId=${usuarioId}&pagina=${paginaPujas}&limite=${LIMITE}`)
    .then(res => res.json())
    .then(subastas => {
      const contenedor = document.getElementById("lista-mis-pujas");
      const btn = document.getElementById("btn-cargar-pujas");

      if (paginaPujas === 0 && subastas.length === 0) {
        document.getElementById("msg-sin-pujas").classList.remove("d-none");
        return;
      }

      if (subastas.length < LIMITE) {
        btn.classList.add("d-none");
      } else {
        btn.classList.remove("d-none");
      }

      subastas.forEach(subasta => {
        let textoBoton = (subasta.estado === "ACTIVA" || subasta.estado === "CUENTA_ATRAS") ? "Ir a pujar" : "Ver detalle";
        let li = document.createElement("li");
        li.className = "profile-list-item";
        li.innerHTML = `
          <strong>${subasta.nombre}</strong>
          <p class="text-muted mb-2">Estado: ${subasta.estado}</p>
          <div class="d-flex gap-2 flex-wrap">
            ${armarTagEstadoPuja(subasta)}
            <a href="/detalle-subasta?id=${subasta.id}" class="btn btn-sm btn-primary ms-auto">${textoBoton}</a>
          </div>
        `;
        contenedor.appendChild(li);
      });
    })
    .catch(err => console.error(err));
}

function armarTagEstadoVenta(subasta) {
  if (subasta.estado === "ACTIVA" || subasta.estado === "CUENTA_ATRAS") {
    return "<span class=\"status-badge status-countdown\">En curso</span>";
  } else if (subasta.estado === "CERRADA" && subasta.vendido) {
    return "<span class=\"status-badge status-sold\">¡Vendido!</span>";
  }
  return "<span class=\"status-badge status-danger\">Sin ofertas</span>";
}

function armarTagEstadoPuja(subasta) {
  let tags = "";
  if (subasta.estado === "ACTIVA" || subasta.estado === "CUENTA_ATRAS") {
    tags += "<span class=\"status-badge status-countdown\">En juego</span> ";
    if (subasta.vasGanando) {
      tags += "<span class=\"status-badge status-winning\">Vas ganando</span>";
    } else {
      tags += "<span class=\"status-badge status-lost\">Superado</span>";
    }
  } else if (subasta.estado === "CERRADA") {
    if (subasta.ganaste) {
      tags += "<span class=\"status-badge status-winning\">¡Ganaste!</span>";
    } else {
      tags += "<span class=\"status-badge status-lost\">Perdiste</span>";
    }
  }
  return tags;
}