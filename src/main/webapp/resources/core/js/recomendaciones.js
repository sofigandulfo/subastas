document.addEventListener("DOMContentLoaded", function () {
  fetch("/spring/api/recomendaciones")
    .then(function (res) {
      if (res.status === 401) {
        document.getElementById("seccion-recomendadas").style.display = "none";
        return null;
      }
      return res.json();
    })
    .then(function (subastas) {
      if (!subastas || subastas.length === 0) {
        document.getElementById("seccion-recomendadas").style.display = "none";
        return;
      }
      var contenedor = document.getElementById("contenedor-recomendadas");
      contenedor.innerHTML = "";
      subastas.forEach(function (subasta) {
        var col = document.createElement("div");
        col.className = "w3-third w3-margin-bottom";

        var card = document.createElement("div");
        card.className = "w3-card-4 w3-white w3-round-large";

        var header = document.createElement("div");
        header.className = "w3-container w3-blue w3-round-top";
        var titulo = document.createElement("h4");
        titulo.className = "w3-text-white";
        titulo.textContent = subasta.nombre;
        header.appendChild(titulo);

        var body = document.createElement("div");
        body.className = "w3-container w3-padding-16";

        var desc = document.createElement("p");
        desc.className = "w3-text-grey";
        desc.textContent = subasta.descripcion;

        var precio = document.createElement("p");
        precio.innerHTML = "<b>Precio actual:</b> $" + subasta.precioActual;

        var categoria = document.createElement("p");
        categoria.innerHTML = "<b>Categoría:</b> " + subasta.categoria;

        var link = document.createElement("a");
        link.href = "/spring/detalle-subasta?id=" + subasta.id;
        link.className = "w3-button w3-blue w3-round w3-block w3-hover-dark-grey w3-margin-top";
        link.textContent = "Ver detalle";

        body.appendChild(desc);
        body.appendChild(precio);
        body.appendChild(categoria);
        body.appendChild(link);
        card.appendChild(header);
        card.appendChild(body);
        col.appendChild(card);
        contenedor.appendChild(col);
      });
    })
    .catch(function () {
      document.getElementById("seccion-recomendadas").style.display = "none";
    });
});